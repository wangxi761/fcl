package org.xiwang.fcl.extensions;

import org.eclipse.lemminx.services.extensions.IXMLExtension;
import org.eclipse.lemminx.services.extensions.XMLExtensionsRegistry;
import org.eclipse.lemminx.services.extensions.save.ISaveContext;
import org.eclipse.lsp4j.InitializeParams;

public class FCLExtension implements IXMLExtension {
	
	private final FCLCompletionParticipant completionParticipant = new FCLCompletionParticipant();
	
	
	@Override
	public void start(InitializeParams params, XMLExtensionsRegistry registry) {
		registry.registerCompletionParticipant(completionParticipant);
	}
	
	@Override
	public void stop(XMLExtensionsRegistry registry) {
		registry.unregisterCompletionParticipant(completionParticipant);
	}
	
	@Override
	public void doSave(ISaveContext context) {
	
	}
	
	
	
}
