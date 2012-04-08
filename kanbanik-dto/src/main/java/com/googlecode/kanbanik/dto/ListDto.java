package com.googlecode.kanbanik.dto;

import java.util.ArrayList;
import java.util.List;

public class ListDto<T extends KanbanikDto> implements KanbanikDto {

	private static final long serialVersionUID = -425721713956605975L;

	private List<T> list = new ArrayList<T>();

	public List<T> getList() {
		return list;
	}
	
	public void addItem(T item) {
		list.add(item);
	}
	
}
