package org.xiwang.fcl.services;

import com.google.common.collect.Lists;
import org.eclipse.lemminx.commons.BadLocationException;
import org.eclipse.lemminx.dom.DOMDocument;
import org.eclipse.lemminx.services.extensions.XMLExtensionsRegistry;
import org.eclipse.lemminx.services.extensions.format.IFormatterParticipant;
import org.eclipse.lemminx.services.format.XMLFormatterDocument;
import org.eclipse.lemminx.services.format.XMLFormatterDocumentOld;
import org.eclipse.lemminx.settings.SharedSettings;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.TextEdit;
import org.xiwang.fcl.services.format.FCLFormatterDocument;

import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FCLFormatter {
	private static final Logger LOGGER = Logger.getLogger(FCLFormatter.class.getName());
	
	private final XMLExtensionsRegistry extensionsRegistry;
	
	public FCLFormatter(XMLExtensionsRegistry extensionsRegistry) {
		this.extensionsRegistry = extensionsRegistry;
	}
	
	
	public List<? extends TextEdit> format(DOMDocument xmlDocument, Range range, SharedSettings sharedSettings) {
		try {
			if (sharedSettings.getFormattingSettings().isLegacy()) {
				XMLFormatterDocumentOld formatterDocument = new XMLFormatterDocumentOld(xmlDocument.getTextDocument(),
					range, sharedSettings, getFormatterParticipants());
				return formatterDocument.format();
			}
			FCLFormatterDocument formatterDocument = new FCLFormatterDocument(xmlDocument, range,
				sharedSettings, Lists.newArrayList());
			return formatterDocument.format();
		} catch (BadLocationException e) {
			LOGGER.log(Level.SEVERE, "Formatting failed due to BadLocation", e);
		}
		return null;
	}
	
	/**
	 * Returns list of {@link IFormatterParticipant}.
	 *
	 * @return list of {@link IFormatterParticipant}.
	 */
	private Collection<IFormatterParticipant> getFormatterParticipants() {
		return extensionsRegistry.getFormatterParticipants();
	}
	
}
