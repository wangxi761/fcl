package org.xiwang.fcl.services.format;

import lombok.SneakyThrows;
import org.eclipse.lemminx.commons.TextDocument;
import org.eclipse.lemminx.dom.DOMDocument;
import org.eclipse.lemminx.dom.DOMElement;
import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lemminx.dom.DOMText;
import org.eclipse.lemminx.services.extensions.format.IFormatterParticipant;
import org.eclipse.lemminx.services.format.TextEditUtils;
import org.eclipse.lemminx.services.format.XMLFormatterDocument;
import org.eclipse.lemminx.services.format.XMLFormattingConstraints;
import org.eclipse.lemminx.settings.SharedSettings;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.TextEdit;
import org.w3c.dom.Node;

import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

public class FCLFormatterDocument extends XMLFormatterDocument {
	
	private static final Logger LOGGER = Logger.getLogger(FCLFormatterDocument.class.getName());
	
	private final FCLTextFormatter textFormatter;
	
	private final TextDocument textDocument;
	
	private final SharedSettings sharedSettings;
	
	@SneakyThrows
	public FCLFormatterDocument(DOMDocument xmlDocument, Range range, SharedSettings sharedSettings, Collection<IFormatterParticipant> formatterParticipants) {
		super(xmlDocument, range, sharedSettings, formatterParticipants);
		this.textDocument = xmlDocument.getTextDocument();
		this.textFormatter = new FCLTextFormatter(this);
		this.sharedSettings = sharedSettings;
	}
	
	@Override
	public void format(DOMNode child, XMLFormattingConstraints parentConstraints, int start, int end, List<TextEdit> edits) {
		if (child.getNodeType() != Node.TEXT_NODE) {
			super.format(child, parentConstraints, start, end, edits);
			return;
		}
		DOMElement parentElement = child.getParentElement();
		if (parentElement == null) {
			return;
		}
		String attribute = parentElement.getAttribute(FCLType.FCL_TYPE_ATTR);
		if (attribute == null) {
			return;
		}
		FCLType fclType = FCLType.fromMimeType(attribute);
		FCLFormattingConstraints fclFormattingConstraints = new FCLFormattingConstraints();
		fclFormattingConstraints.copyConstraints(parentConstraints);
		fclFormattingConstraints.setFclType(fclType);
		textFormatter.formatText((DOMText) child, fclFormattingConstraints, start, end, edits);
	}
	
	@SneakyThrows
	public void createTextEditIfNeeded(int from, int to, String expectedContent, List<TextEdit> edits) {
		TextEdit edit = TextEditUtils.createTextEditIfNeeded(from, to, expectedContent, textDocument);
		if (edit != null) {
			edits.add(edit);
		}
	}
	
	public int getTabSize() {
		return sharedSettings.getFormattingSettings().getTabSize();
	}
	
}
