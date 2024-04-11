package org.xiwang.fcl.extensions;

import org.eclipse.lemminx.dom.DOMDocument;
import org.eclipse.lemminx.dom.DOMElement;
import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lemminx.services.extensions.completion.AttributeCompletionItem;
import org.eclipse.lemminx.services.extensions.completion.ICompletionParticipant;
import org.eclipse.lemminx.services.extensions.completion.ICompletionRequest;
import org.eclipse.lemminx.services.extensions.completion.ICompletionResponse;
import org.eclipse.lemminx.settings.SharedSettings;
import org.eclipse.lemminx.utils.StringUtils;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.MarkupContent;
import org.eclipse.lsp4j.MarkupKind;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.jsonrpc.CancelChecker;

import java.util.Collection;

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
			if (!hasAttribute(elementAtOffset, "xmlns:fcl") && !response.hasAttribute("xmlns:fcl")) { // "xmlns" completion
				createCompletionItem("xmlns:fcl", isSnippetsSupported, generateValue, editRange, null, null, null, response,
					request.getSharedSettings());
			}
		}
	}
	
	@Override
	public void onAttributeValue(String valuePrefix, ICompletionRequest request, ICompletionResponse response, CancelChecker cancelChecker) throws Exception {
	
	}
	
	@Override
	public void onDTDSystemId(String valuePrefix, ICompletionRequest request, ICompletionResponse response, CancelChecker cancelChecker) throws Exception {
	
	}
	
	private static void createCompletionItem(String attrName, boolean canSupportSnippet, boolean generateValue,
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
	
	private static boolean hasAttribute(DOMElement root, String prefix, String suffix) {
		return root.getAttributeNode(prefix, suffix) != null;
		
	}
	
	private static boolean hasAttribute(DOMElement root, String name) {
		return hasAttribute(root, null, name);
	}
}
