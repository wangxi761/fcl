package org.xiwang.fcl.services.snippets;

import org.eclipse.lemminx.commons.snippets.ISnippetRegistryLoader;
import org.eclipse.lemminx.commons.snippets.SnippetRegistry;
import org.eclipse.lemminx.services.snippets.NewFileSnippetContext;

public class FCLSnippetRegistryLoader implements ISnippetRegistryLoader {
	@Override
	public void load(SnippetRegistry registry) throws Exception {
		registry.registerSnippets(FCLSnippetRegistryLoader.class.getResourceAsStream("new-fcl-snippets.json"), NewFileSnippetContext.XML_CONTEXT);
	}
}
