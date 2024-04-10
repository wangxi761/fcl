package org.xiwang.fcl;

import org.eclipse.lemminx.customservice.XMLLanguageClientAPI;
import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.jsonrpc.MessageConsumer;
import org.eclipse.lsp4j.launch.LSPLauncher;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.LanguageServer;
import org.xiwang.fcl.commons.LoggingParentProcessWatcher;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;

public class FCLServerLauncher {
	public static void main(String[] args) {
		
		final String HTTP_PROXY_HOST = System.getenv("HTTP_PROXY_HOST");
		final String HTTP_PROXY_PORT = System.getenv("HTTP_PROXY_PORT");
		final String HTTP_PROXY_USERNAME = System.getenv("HTTP_PROXY_USERNAME");
		final String HTTP_PROXY_PASSWORD = System.getenv("HTTP_PROXY_PASSWORD");
		final boolean LEMMINX_DEBUG = System.getenv("LEMMINX_DEBUG") != null;
		
		if (HTTP_PROXY_HOST != null && HTTP_PROXY_PORT != null) {
			System.setProperty("http.proxyHost", HTTP_PROXY_HOST);
			System.setProperty("http.proxyPort", HTTP_PROXY_PORT);
			System.setProperty("https.proxyHost", HTTP_PROXY_HOST);
			System.setProperty("https.proxyPort", HTTP_PROXY_PORT);
		}
		
		if (HTTP_PROXY_USERNAME != null && HTTP_PROXY_PASSWORD != null) {
			System.setProperty("http.proxyUser", HTTP_PROXY_USERNAME);
			System.setProperty("http.proxyPassword", HTTP_PROXY_PASSWORD);
		}
		
		final String username = System.getProperty("http.proxyUser");
		final String password = System.getProperty("http.proxyPassword");
		
		if (username != null && password != null) {
			Authenticator.setDefault(new Authenticator() {
				
				@Override
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(username, password.toCharArray());
				}
				
			});
		}
		InputStream in = System.in;
		PrintStream out = System.out;
		System.setIn(new NoOpInputStream());
		System.setOut(new NoOpPrintStream());
		if (!LEMMINX_DEBUG) {
			System.setErr(new NoOpPrintStream());
		}
		launch(in, out);
	}
	
	
	public static Future<?> launch(InputStream in, OutputStream out) {
		FCLLanguageServer server = new FCLLanguageServer();
		Function<MessageConsumer, MessageConsumer> wrapper;
		if ("false".equals(System.getProperty("watchParentProcess"))) {
			wrapper = it -> it;
		} else {
			wrapper = new LoggingParentProcessWatcher(server);
		}
		Launcher<LanguageClient> launcher = createServerLauncher(server, in, out, Executors.newCachedThreadPool(), wrapper);
		server.setClient(launcher.getRemoteProxy());
		return launcher.startListening();
	}
	
	private static Launcher<LanguageClient> createServerLauncher(LanguageServer server, InputStream in, OutputStream out,
	                                                             ExecutorService executorService, Function<MessageConsumer, MessageConsumer> wrapper) {
		return new LSPLauncher.Builder<LanguageClient>().
			setLocalService(server)
			.setRemoteInterface(XMLLanguageClientAPI.class) // Set client as XML language client
			.setInput(in)
			.setOutput(out)
			.setExecutorService(executorService)
			.wrapMessages(wrapper)
			.create();
	}
	
}
