package org.xiwang.fcl.services;

import static org.junit.jupiter.api.Assertions.*;

import lombok.SneakyThrows;
import org.eclipse.lemminx.commons.TextDocument;
import org.eclipse.lemminx.dom.DOMDocument;
import org.eclipse.lemminx.dom.DOMParser;
import org.eclipse.lemminx.services.format.TextEditUtils;
import org.eclipse.lemminx.settings.SharedSettings;
import org.eclipse.lemminx.utils.IOUtils;
import org.eclipse.lsp4j.TextEdit;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.List;

class FCLLanguageServiceTest {
	
	
	@Test
	@SneakyThrows
	void format() {
		InputStream resourceStream = this.getClass().getClassLoader().getResourceAsStream("fcl/test1.fcl");
		assert resourceStream != null;
		TextDocument document = new TextDocument(IOUtils.convertStreamToString(resourceStream), "memory://test.fcl");
		document.setIncremental(true);
		DOMDocument xmlDocument = DOMParser.getInstance().parse(document, null);
		
		FCLLanguageService languageService = new FCLLanguageService();
		List<? extends TextEdit> format = languageService.format(xmlDocument, null, new SharedSettings());
		String result = TextEditUtils.applyEdits(document, format);
		System.out.println(result);
	}
}