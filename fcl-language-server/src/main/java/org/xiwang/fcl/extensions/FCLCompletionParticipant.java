package org.xiwang.fcl.extensions;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import org.eclipse.lemminx.dom.DOMAttr;
import org.eclipse.lemminx.dom.DOMDocument;
import org.eclipse.lemminx.dom.DOMElement;
import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lemminx.extensions.contentmodel.participants.completion.AttributeValueCompletionResolver;
import org.eclipse.lemminx.extensions.contentmodel.utils.XMLGenerator;
import org.eclipse.lemminx.services.data.DataEntryField;
import org.eclipse.lemminx.services.extensions.completion.AttributeCompletionItem;
import org.eclipse.lemminx.services.extensions.completion.ICompletionParticipant;
import org.eclipse.lemminx.services.extensions.completion.ICompletionRequest;
import org.eclipse.lemminx.services.extensions.completion.ICompletionResponse;
import org.eclipse.lemminx.settings.SharedSettings;
import org.eclipse.lemminx.utils.StringUtils;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.CancelChecker;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.xiwang.fcl.services.FCLConst;
import org.xiwang.fcl.services.FCLType;

import java.util.*;
import java.util.stream.Collectors;

public class FCLCompletionParticipant implements ICompletionParticipant {
	
	@Override
	public void onTagOpen(ICompletionRequest completionRequest, ICompletionResponse completionResponse, CancelChecker cancelChecker) throws Exception {
	
	}
	
	@Override
	public void onXMLContent(ICompletionRequest request, ICompletionResponse response, CancelChecker cancelChecker) throws Exception {
	
	}
	
	@Override
	public void onAttributeName(boolean generateValue, ICompletionRequest request, ICompletionResponse response, CancelChecker cancelChecker) throws Exception {
		Range editRange = request.getReplaceRange();
		DOMDocument document = request.getXMLDocument();
		DOMElement rootElement = document.getDocumentElement();
		int offset = document.offsetAt(editRange.getStart());
		if (rootElement == null || offset <= rootElement.getStart() || offset >= rootElement.getEnd()) {
			return;
		}
		boolean inRootElement = false;
		DOMNode nodeAtOffset = document.findNodeAt(offset);
		DOMElement elementAtOffset;
		if (nodeAtOffset != null && nodeAtOffset.isElement()) {
			elementAtOffset = (DOMElement) nodeAtOffset;
		} else {
			return;
		}
		if (rootElement.equals(nodeAtOffset)) {
			inRootElement = true;
		}
		
		boolean isSnippetsSupported = request.isCompletionSnippetsSupported();
		if (inRootElement) {
			if (!hasAttribute(elementAtOffset, FCLConst.FCL_NAMESPACE) && !response.hasAttribute(FCLConst.FCL_NAMESPACE)) { // "xmlns" completion
				createAttrCompletionItem(FCLConst.FCL_NAMESPACE, isSnippetsSupported, generateValue, editRange, null, null, null, response,
					request.getSharedSettings());
			}
			return;
		}
		
		if (!hasAttribute(elementAtOffset, FCLConst.FCL_TYPE_ATTR)) {
			createAttrCompletionItem(FCLConst.FCL_TYPE_ATTR, isSnippetsSupported, generateValue, editRange, null, null, null, response,
				request.getSharedSettings());
			return;
		}
	}
	
	@Override
	public void onAttributeValue(String valuePrefix, ICompletionRequest request, ICompletionResponse response, CancelChecker cancelChecker) throws Exception {
		DOMAttr currentAttribute = request.getCurrentAttribute();
		if (currentAttribute == null) {
			return;
		}
		Range editRange = request.getReplaceRange();
		String attributeName = currentAttribute.getName();
		switch (attributeName) {
			case FCLConst.XMLNS, FCLConst.FCL_NAMESPACE -> {
				List<String> backupList = Lists.newArrayList(FCLConst.FCL_NAMESPACE_URI);
				createAttrValueCompletionItem(backupList, editRange, request, response, request.getSharedSettings());
			}
			case FCLConst.FCL_TYPE_ATTR -> {
				List<String> backupList = Arrays.stream(FCLType.values()).map(FCLType::getMimeType).toList();
				createAttrValueCompletionItem(backupList, editRange, request, response, request.getSharedSettings());
			}
		}
	}
	
	@Override
	public void onDTDSystemId(String valuePrefix, ICompletionRequest request, ICompletionResponse response, CancelChecker cancelChecker) throws Exception {
	
	}
	
	private static void createAttrCompletionItem(String attrName, boolean canSupportSnippet, boolean generateValue,
	                                             Range editRange, String defaultValue, Collection<String> enumerationValues, String documentation,
	                                             ICompletionResponse response, SharedSettings sharedSettings) {
		CompletionItem item = new AttributeCompletionItem(attrName, canSupportSnippet, editRange, generateValue,
			defaultValue, enumerationValues, sharedSettings);
		MarkupContent markup = new MarkupContent();
		markup.setKind(MarkupKind.MARKDOWN);
		markup.setValue(StringUtils.getDefaultString(documentation));
		item.setDocumentation(markup);
		response.addCompletionItem(item);
	}
	
	private static void createAttrValueCompletionItem(Collection<String> values, Range editRange, ICompletionRequest request, ICompletionResponse response, SharedSettings sharedSettings) {
		for (String value : values) {
			CompletionItem item = new CompletionItem();
			item.setLabel(value);
			item.setKind(CompletionItemKind.Value);
			item.setTextEdit(Either.forLeft(new TextEdit(editRange, value)));
			if (request.isResolveDocumentationSupported()) {
				addResolveData(request, item, AttributeValueCompletionResolver.PARTICIPANT_ID);
			}
			response.addCompletionItem(item);
		}
	}
	
	private static void addResolveData(ICompletionRequest request, CompletionItem item, String participantId) {
		JsonObject data = DataEntryField.createCompletionData(request, participantId);
		item.setData(data);
	}
	
	private static boolean hasAttribute(DOMElement root, String prefix, String suffix) {
		return root.getAttributeNode(prefix, suffix) != null;
		
	}
	
	private static boolean hasAttribute(DOMElement root, String name) {
		return hasAttribute(root, null, name);
	}
}
