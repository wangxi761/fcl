package org.xiwang.fcl.services;

import static org.junit.jupiter.api.Assertions.*;

import lombok.SneakyThrows;
import org.eclipse.lemminx.commons.TextDocument;
import org.eclipse.lemminx.dom.DOMDocument;
import org.eclipse.lemminx.dom.DOMParser;
import org.eclipse.lemminx.services.format.TextEditUtils;
import org.eclipse.lemminx.settings.SharedSettings;
import org.eclipse.lsp4j.TextEdit;
import org.junit.jupiter.api.Test;

import java.util.List;

class FCLLanguageServiceTest {
	
	
	@Test
	@SneakyThrows
	void format() {
		TextDocument document = new TextDocument("""
			<test>
				<test1>test</test1>
	            <test2 fcl:type="text/json">
         {
     "field": "src_plat",
     "value": "huya"
                         }
				</test2>
			</test>
			""", "memory://test.fcl");
		document.setIncremental(true);
		DOMDocument xmlDocument = DOMParser.getInstance().parse(document, null);
		
		FCLLanguageService languageService=new FCLLanguageService();
		List<? extends TextEdit> format = languageService.format(xmlDocument, null, new SharedSettings());
		String result = TextEditUtils.applyEdits(document, format);
		System.out.println(result);
	}
}