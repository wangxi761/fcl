package org.xiwang.fcl.services;

import org.eclipse.lemminx.dom.DOMDocument;
import org.eclipse.lemminx.services.XMLLanguageService;
import org.eclipse.lemminx.settings.SharedSettings;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.TextEdit;
import org.xiwang.fcl.services.format.FCLFormatter;

import java.util.List;

public class FCLLanguageService extends XMLLanguageService {
	
	private final FCLFormatter formatter;
	
	public FCLLanguageService() {
		this.formatter = new FCLFormatter(this);
	}
	
	@Override
	public List<? extends TextEdit> format(DOMDocument xmlDocument, Range range, SharedSettings sharedSettings) {
		return formatter.format(xmlDocument, range, sharedSettings);
	}
}
