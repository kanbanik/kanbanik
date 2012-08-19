package com.googlecode.kanbanik.dto;

import java.io.Serializable;

public enum ClassOfService implements Serializable {
	STANDARD(2), EXPEDITE(0), INTANGIBLE(3), FIXED_DELIVERY_DATE(1);
	
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
