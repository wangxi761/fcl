package org.xiwang.fcl.services.format;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum FCLType {
	
	TEXT(0, "text"),
	TEXT_JSON(1, "text/json"),
	TEXT_YAML(2, "text/yaml"),
	NUMBER(3, "number"),
	BOOLEAN(4, "boolean"),
	OBJECT(5, "object"),
	ARRAY(6, "array"),
	;
	
	public static final String FCL_TYPE_ATTR = "fcl:type";
	
	private final int code;
	private final String mimeType;
	
	public static FCLType fromMimeType(String mimeType) {
		for (FCLType type : values()) {
			if (type.getMimeType().equals(mimeType)) {
				return type;
			}
		}
		return TEXT;
	}
}
