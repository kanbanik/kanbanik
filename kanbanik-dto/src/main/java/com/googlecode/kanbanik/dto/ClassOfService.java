package com.googlecode.kanbanik.dto;

import java.io.Serializable;

public enum ClassOfService implements Serializable {
	EXPEDITE(0), FIXED_DELIVERY_DATE(1), STANDARD(2), INTANGIBLE(3);
	
	private int id;
	
	private ClassOfService(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	
	public static ClassOfService fromId(int id) {
		for (ClassOfService value : values()) {
			if (value.id == id) {
				return value;
			}
		}
		
		throw new IllegalArgumentException("Unsupported id: '" + id + "'");
	}
}
