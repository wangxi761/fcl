package org.xiwang.fcl;

import org.eclipse.lemminx.XMLLanguageServer;
import org.eclipse.lemminx.XMLTextDocumentService;
import org.eclipse.lemminx.XMLWorkspaceService;
import org.eclipse.lemminx.client.ExtendedClientCapabilities;
import org.eclipse.lemminx.commons.ModelTextDocument;
import org.eclipse.lemminx.customservice.ActionableNotification;
import org.eclipse.lemminx.customservice.AutoCloseTagResponse;
import org.eclipse.lemminx.customservice.XMLLanguageClientAPI;
import org.eclipse.lemminx.dom.DOMDocument;
import org.eclipse.lemminx.extensions.contentmodel.settings.ContentModelSettings;
import org.eclipse.lemminx.extensions.contentmodel.settings.XMLValidationRootSettings;
import org.eclipse.lemminx.logs.LogHelper;
import org.eclipse.lemminx.services.XMLLanguageService;
import org.eclipse.lemminx.settings.*;
import org.eclipse.lemminx.settings.capabilities.InitializationOptionsExtendedClientCapabilities;
import org.eclipse.lemminx.settings.capabilities.ServerCapabilitiesInitializer;
import org.eclipse.lemminx.settings.capabilities.XMLCapabilityManager;
import org.eclipse.lemminx.telemetry.TelemetryManager;
import org.eclipse.lemminx.utils.FilesUtils;
import org.eclipse.lemminx.utils.platform.Platform;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.TextDocumentService;
import org.eclipse.lsp4j.services.WorkspaceService;
import org.xiwang.fcl.services.FCLLanguageService;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class FCLLanguageServer extends XMLLanguageServer {
	private static final Logger LOGGER = Logger.getLogger(XMLLanguageServer.class.getName());
	
	private final FCLLanguageService xmlLanguageService;
	private final XMLTextDocumentService xmlTextDocumentService;
	private final XMLWorkspaceService xmlWorkspaceService;
	private XMLLanguageClientAPI languageClient;
	private final ScheduledExecutorService delayer;
	private Integer parentProcessId;
	private XMLCapabilityManager capabilityManager;
	private TelemetryManager telemetryManager;
	
	public FCLLanguageServer() {
		xmlTextDocumentService = new XMLTextDocumentService(this);
		xmlWorkspaceService = new XMLWorkspaceService(this);
		
		xmlLanguageService = new FCLLanguageService();
		xmlLanguageService.setDocumentProvider(this);
		xmlLanguageService.setNotificationService(this);
		xmlLanguageService.setCommandService(xmlWorkspaceService);
		xmlLanguageService.setValidationService(this);
		
		delayer = Executors.newScheduledThreadPool(1);
		
	}
	
	@Override
	public CompletableFuture<InitializeResult> initialize(InitializeParams params) {
		Object initOptions = InitializationOptionsSettings.getSettings(params);
		Object xmlSettings = AllXMLSettings.getAllXMLSettings(initOptions);
		XMLGeneralClientSettings settings = XMLGeneralClientSettings.getGeneralXMLSettings(xmlSettings);
		
		LogHelper.initializeRootLogger(languageClient, settings == null ? null : settings.getLogs());
		
		LOGGER.info("Initializing XML Language server" + System.lineSeparator() + Platform.details());
		
		this.parentProcessId = params.getProcessId();
		
		xmlLanguageService.setTelemetryManager(getTelemetryManager());
		// Update XML language service extensions with InitializeParams
		xmlLanguageService.initializeParams(params);
		
		ExtendedClientCapabilities extendedClientCapabilities = InitializationOptionsExtendedClientCapabilities
			.getExtendedClientCapabilities(params);
		capabilityManager.setClientCapabilities(params.getCapabilities(), extendedClientCapabilities);
		
		xmlTextDocumentService.updateClientCapabilities(capabilityManager.getClientCapabilities().capabilities,
			capabilityManager.getClientCapabilities().getExtendedCapabilities());
		
		updateSettings(initOptions, false /* already configured logging */);
		
		ServerCapabilities nonDynamicServerCapabilities = ServerCapabilitiesInitializer.getNonDynamicServerCapabilities(
			capabilityManager.getClientCapabilities(), xmlTextDocumentService.isIncrementalSupport());
		
		return CompletableFuture.completedFuture(new InitializeResult(nonDynamicServerCapabilities));
	}
	
	/*
	 * Registers all capabilities that do not support client side preferences to
	 * turn on/off
	 *
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.lsp4j.services.LanguageServer#initialized(org.eclipse.lsp4j.
	 * InitializedParams)
	 */
	@Override
	public void initialized(InitializedParams params) {
		capabilityManager.initializeCapabilities();
		getTelemetryManager().onInitialized(params);
	}
	
	/**
	 * Update XML settings configured from the client.
	 *
	 * @param initOptions the XML settings
	 */
	@Override
	public synchronized void updateSettings(Object initOptions) {
		updateSettings(initOptions, true);
	}
	
	/**
	 * Update XML settings configured from the client.
	 *
	 * @param initOptions Settings the XML settings
	 * @param initLogs    whether to initialize the log handlers
	 */
	private synchronized void updateSettings(Object initOptions, boolean initLogs) {
		if (initOptions == null) {
			return;
		}
		// Update client settings
		Object initSettings = AllXMLSettings.getAllXMLSettings(initOptions);
		XMLGeneralClientSettings xmlClientSettings = XMLGeneralClientSettings.getGeneralXMLSettings(initSettings);
		if (xmlClientSettings != null) {
			if (initLogs) {
				// Update logs settings
				LogHelper.initializeRootLogger(languageClient, xmlClientSettings.getLogs());
			}
			
			XMLTelemetrySettings newTelemetry = xmlClientSettings.getTelemetry();
			if (newTelemetry != null) {
				getTelemetryManager().setEnabled(newTelemetry.isEnabled());
			}
			
			XMLFoldingSettings newFolding = xmlClientSettings.getFolding();
			if (newFolding != null) {
				xmlTextDocumentService.getSharedFoldingSettings().merge(newFolding);
			}
			// Update format settings
			XMLFormattingOptions formatterSettings = xmlClientSettings.getFormat();
			if (formatterSettings != null) {
				xmlTextDocumentService.getSharedFormattingSettings().merge(formatterSettings);
			}
			
			XMLCompletionSettings newCompletions = xmlClientSettings.getCompletion();
			if (newCompletions != null) {
				xmlTextDocumentService.updateCompletionSettings(newCompletions);
			}
			xmlTextDocumentService.getSharedSettings()
			                      .setLinkedEditingEnabled(xmlClientSettings.isLinkedEditingEnabled());
			
			XMLSymbolSettings newSymbols = xmlClientSettings.getSymbols();
			if (newSymbols != null) {
				xmlTextDocumentService.updateSymbolSettings(newSymbols);
			}
			
			XMLCodeLensSettings newCodeLens = xmlClientSettings.getCodeLens();
			if (newCodeLens != null) {
				xmlTextDocumentService.updateCodeLensSettings(newCodeLens);
			}
			
			XMLPreferences newPreferences = xmlClientSettings.getPreferences();
			if (newPreferences != null) {
				xmlTextDocumentService.updatePreferences(newPreferences);
			}
			
			ServerSettings serverSettings = xmlClientSettings.getServer();
			if (serverSettings != null) {
				String workDir = serverSettings.getNormalizedWorkDir();
				FilesUtils.setCachePathSetting(workDir);
			}
		}
		ContentModelSettings cmSettings = ContentModelSettings.getContentModelXMLSettings(initSettings);
		if (cmSettings != null) {
			XMLValidationRootSettings validationSettings = cmSettings.getValidation();
			xmlTextDocumentService.getValidationSettings().merge(validationSettings);
			
		}
		xmlTextDocumentService.updateSettings(initSettings);
	}
	
	@Override
	public CompletableFuture<Object> shutdown() {
		xmlLanguageService.dispose();
		if (capabilityManager.getClientCapabilities().shouldLanguageServerExitOnShutdown()) {
			delayer.schedule(() -> exit(0), 1, TimeUnit.SECONDS);
		}
		return CompletableFuture.completedFuture(null);
	}
	
	@Override
	public void exit() {
		exit(0);
	}
	
	@Override
	public void exit(int exitCode) {
		delayer.shutdown();
		System.exit(exitCode);
	}
	
	@Override
	public TextDocumentService getTextDocumentService() {
		return xmlTextDocumentService;
	}
	
	@Override
	public WorkspaceService getWorkspaceService() {
		return xmlWorkspaceService;
	}
	
	@Override
	public void setClient(LanguageClient languageClient) {
		this.languageClient = (XMLLanguageClientAPI) languageClient;
		capabilityManager = new XMLCapabilityManager(this.languageClient, xmlTextDocumentService);
		telemetryManager = new TelemetryManager(languageClient);
	}
	
	@Override
	public XMLLanguageClientAPI getLanguageClient() {
		return languageClient;
	}
	
	@Override
	public XMLLanguageService getXMLLanguageService() {
		return xmlLanguageService;
	}
	
	@Override
	public ScheduledFuture<?> schedule(Runnable command, int delay, TimeUnit unit) {
		return delayer.schedule(command, delay, unit);
	}
	
	@Override
	public long getParentProcessId() {
		return parentProcessId != null ? parentProcessId : 0;
	}
	
	@Override
	public CompletableFuture<AutoCloseTagResponse> closeTag(TextDocumentPositionParams params) {
		return xmlTextDocumentService.computeDOMAsync(params.getTextDocument(),
			(xmlDocument, cancelChecker) ->
				getXMLLanguageService().doAutoClose(
					xmlDocument, params.getPosition(), getSharedSettings().getCompletionSettings(), cancelChecker
				));
	}
	
	@Override
	public CompletableFuture<Position> matchingTagPosition(TextDocumentPositionParams params) {
		return xmlTextDocumentService.computeDOMAsync(params.getTextDocument(), (xmlDocument, cancelChecker) -> {
			return getXMLLanguageService().getMatchingTagPosition(xmlDocument, params.getPosition(), cancelChecker);
		});
	}
	
	@Override
	public DOMDocument getDocument(String uri) {
		ModelTextDocument<DOMDocument> document = xmlTextDocumentService.getDocument(uri);
		return document != null ? document.getModel() : null;
	}
	
	@Override
	public void sendNotification(String message, MessageType messageType, Command... commands) {
		SharedSettings sharedSettings = getSharedSettings();
		if (sharedSettings.isActionableNotificationSupport() && sharedSettings.isOpenSettingsCommandSupport()) {
			ActionableNotification notification = new ActionableNotification().withSeverity(messageType)
			                                                                  .withMessage(message).withCommands(Arrays.asList(commands));
			languageClient.actionableNotification(notification);
		} else {
			languageClient.showMessage(new MessageParams(messageType, message));
		}
	}
	
	@Override
	public SharedSettings getSharedSettings() {
		return xmlTextDocumentService.getSharedSettings();
	}
	
	@Override
	public Collection<DOMDocument> getAllDocuments() {
		return xmlTextDocumentService.allDocuments().stream() //
		                             .map(ModelTextDocument::getModel) //
		                             .filter(Objects::nonNull) //
		                             .collect(Collectors.toList());
	}
	
	@Override
	public void validate(DOMDocument document, Map<String, Object> validationArgs) {
		super.validate(document, validationArgs);
	}
	
	@Override
	public XMLCapabilityManager getCapabilityManager() {
		return capabilityManager;
	}
	
	
	@Override
	public TelemetryManager getTelemetryManager() {
		return telemetryManager;
	}
	
}
