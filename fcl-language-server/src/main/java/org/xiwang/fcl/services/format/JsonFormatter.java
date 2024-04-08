package org.xiwang.fcl.services.format;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

public class JsonFormatter {
	private final ObjectMapper MAPPER;
	private JsonFormatter() {
		MAPPER = new ObjectMapper();
	}
	
	private static class JsonFormatterHolder {
		private static final JsonFormatter INSTANCE = new JsonFormatter();
	}
	
	public static JsonFormatter getInstance() {
		return JsonFormatterHolder.INSTANCE;
	}
	
	@SneakyThrows
	public String format(String json) {
		return MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(MAPPER.readTree(json));
	}
}
