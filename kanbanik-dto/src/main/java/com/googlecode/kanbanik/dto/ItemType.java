package com.googlecode.kanbanik.dto;

public enum ItemType {
	HORIZONTAL("H"), VERTICAL("V");

	private String stringType;

	private ItemType(String stringType) {
		this.stringType = stringType;
	}

	public static ItemType asItemType(String stringType) {
		if ("H".equals(stringType)) {
			return HORIZONTAL;
		} else if ("V".equals(stringType)) {
			return VERTICAL;
		}

		throw new IllegalArgumentException(
				"Not supported workflowitem type: '" + stringType + "'");
	}

	public String asStringValue() {
		return stringType;
	}
}
