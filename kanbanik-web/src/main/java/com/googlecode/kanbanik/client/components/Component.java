package com.googlecode.kanbanik.client.components;

import com.google.gwt.event.dom.client.HasClickHandlers;

public interface Component<T> {
	
	void setup(HasClickHandlers clickHandler, String title);
	
	void setDto(T dto);
}
