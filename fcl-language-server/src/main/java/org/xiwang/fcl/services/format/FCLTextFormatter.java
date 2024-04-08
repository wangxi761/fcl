package org.xiwang.fcl.services.format;

import org.eclipse.lemminx.dom.DOMText;
import org.eclipse.lemminx.services.format.DOMTextFormatter;
import org.eclipse.lemminx.services.format.XMLFormatterDocument;
import org.eclipse.lemminx.services.format.XMLFormattingConstraints;
import org.eclipse.lsp4j.TextEdit;

import java.util.List;

public class FCLTextFormatter extends DOMTextFormatter {
	
	public FCLTextFormatter(XMLFormatterDocument formatterDocument) {
		super(formatterDocument);
	}
	
	@Override
	public void formatText(DOMText textNode, XMLFormattingConstraints parentConstraints, int start, int end, List<TextEdit> edits) {
		if (parentConstraints instanceof FCLFormattingConstraints fclFormattingConstraints) {
			switch (fclFormattingConstraints.getFclType()) {
				case FCLType.TEXT_JSON -> formatJsonText(textNode, parentConstraints, start, end, edits);
				case FCLType.TEXT_YAML -> formatYamlText(textNode, parentConstraints, start, end, edits);
				default -> super.formatText(textNode, parentConstraints, start, end, edits);
			}
		} else {
			super.formatText(textNode, parentConstraints, start, end, edits);
		}
	}
	
	private void formatJsonText(DOMText textNode, XMLFormattingConstraints parentConstraints, int start, int end, List<TextEdit> edits) {
		// Format JSON text
	}
	
	private void formatYamlText(DOMText textNode, XMLFormattingConstraints parentConstraints, int start, int end, List<TextEdit> edits) {
		// Format YAML text
	}
}
