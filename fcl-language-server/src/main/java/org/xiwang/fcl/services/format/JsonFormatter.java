package org.xiwang.fcl.services.format;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonFormatter {
	private final Gson GSON;
	private JsonFormatter() {
		GSON = new GsonBuilder().setPrettyPrinting().create();
	}
	
	private static class JsonFormatterHolder {
		private static final JsonFormatter INSTANCE = new JsonFormatter();
	}
	
	public static JsonFormatter getInstance() {
		return JsonFormatterHolder.INSTANCE;
	}
	
	public String format(String json) {
		return GSON.toJson(GSON.fromJson(json, Object.class));
	}
}
