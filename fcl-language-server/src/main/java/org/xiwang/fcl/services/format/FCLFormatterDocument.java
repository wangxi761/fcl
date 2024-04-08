package org.xiwang.fcl.services.format;

import org.eclipse.lemminx.dom.DOMDocument;
import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lemminx.dom.DOMText;
import org.eclipse.lemminx.services.extensions.format.IFormatterParticipant;
import org.eclipse.lemminx.services.format.DOMTextFormatter;
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
	
	private final DOMTextFormatter textFormatter;
	
	public FCLFormatterDocument(DOMDocument xmlDocument, Range range, SharedSettings sharedSettings, Collection<IFormatterParticipant> formatterParticipants) {
		super(xmlDocument, range, sharedSettings, formatterParticipants);
		textFormatter = new FCLTextFormatter(this);
	}
	
	@Override
	public void format(DOMNode child, XMLFormattingConstraints parentConstraints, int start, int end, List<TextEdit> edits) {
		String attribute = child.getAttribute(FCLType.FCL_TYPE_ATTR);
		FCLType fclType = FCLType.fromMimeType(attribute);
		
		FCLFormattingConstraints fclFormattingConstraints = new FCLFormattingConstraints();
		fclFormattingConstraints.copyConstraints(parentConstraints);
		fclFormattingConstraints.setFclType(fclType);
		
		if (child.getNodeType() == Node.TEXT_NODE) {
			textFormatter.formatText((DOMText) child, parentConstraints, start, end, edits);
		} else {
			super.format(child, fclFormattingConstraints, start, end, edits);
		}
	}
	
	@Override
	public void formatChildren(DOMNode currentDOMNode, XMLFormattingConstraints parentConstraints, int start, int end, List<TextEdit> edits) {
		for (DOMNode child : currentDOMNode.getChildren()) {
			this.format(child, parentConstraints, start, end, edits);
		}
	}
}
