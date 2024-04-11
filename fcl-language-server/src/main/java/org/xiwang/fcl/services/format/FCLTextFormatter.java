package org.xiwang.fcl.services.format;

import org.eclipse.lemminx.dom.DOMText;
import org.eclipse.lemminx.services.format.DOMTextFormatter;
import org.eclipse.lemminx.services.format.XMLFormattingConstraints;
import org.eclipse.lsp4j.TextEdit;
import org.xiwang.fcl.services.FCLType;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FCLTextFormatter extends DOMTextFormatter {
	private final FCLFormatterDocument formatterDocument;
	
	public FCLTextFormatter(FCLFormatterDocument formatterDocument) {
		super(formatterDocument);
		this.formatterDocument = formatterDocument;
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
		String formattedJson = JsonFormatter.getInstance().format(textNode.getTextContent());
		formatterDocument.createTextEditIfNeeded(textNode.getStart(), textNode.getEnd(), appendIndentSpaceToFormattedContent(formattedJson, parentConstraints.getIndentLevel()), edits);
	}
	
	private void formatYamlText(DOMText textNode, XMLFormattingConstraints parentConstraints, int start, int end, List<TextEdit> edits) {
		String formattedYaml = YamlFormatter.getInstance().format(textNode.getTextContent());
		formatterDocument.createTextEditIfNeeded(textNode.getStart(), textNode.getEnd(), appendIndentSpaceToFormattedContent(formattedYaml, parentConstraints.getIndentLevel()), edits);
	}
	
	private String appendIndentSpaceToFormattedContent(String formattedContent, int indentLevel) {
		String indentFormattedContent = Arrays.stream(formattedContent.split("\n"))
		                                      .map(line -> generateIdent(indentLevel) + line)
		                                      .collect(Collectors.joining("\n"));
		return "\n%s\n%s".formatted(indentFormattedContent, generateIdent(indentLevel - 1));
	}
	
	private String generateIdent(int indentLevel) {
		if (formatterDocument.isInsertSpaces()) {
			return " ".repeat(formatterDocument.getTabSize()).repeat(indentLevel);
		} else {
			return "\t".repeat(indentLevel);
		}
	}
}
