package org.xiwang.fcl.services.format;

import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import lombok.SneakyThrows;

public class YamlFormatter {
	private final YAMLMapper MAPPER;
	
	private YamlFormatter() {
		MAPPER = new YAMLMapper();
		MAPPER.configure(YAMLGenerator.Feature.WRITE_DOC_START_MARKER, false);
	}
	
	private static class YamlFormatterHolder {
		private static final YamlFormatter INSTANCE = new YamlFormatter();
	}
	
	public static YamlFormatter getInstance() {
		return YamlFormatterHolder.INSTANCE;
	}
	
	@SneakyThrows
	public String format(String yaml) {
		return MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(MAPPER.readTree(yaml));
	}
}
