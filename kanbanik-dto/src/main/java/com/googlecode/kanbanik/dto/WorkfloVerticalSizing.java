package com.googlecode.kanbanik.dto;

public enum WorkfloVerticalSizing {
	
	BALANCED(0),
	MIN_POSSIBLE(1);
	
	
	private int index;

	private WorkfloVerticalSizing(int index) {
		this.index = index;
		
	}

	public int getIndex() {
		return index;
	}
	
	public static WorkfloVerticalSizing fromId(int index) {
		for (WorkfloVerticalSizing sizing : values()) {
			if (sizing.index == index) {
				return sizing;
			}
		}
		
		return null;
	}
}
