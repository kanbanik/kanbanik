package com.googlecode.kanbanik.dto.shell;

import com.googlecode.kanbanik.dto.KanbanikDto;

public class SimpleParams<T extends KanbanikDto> implements Params, Result  {
	
	private static final long serialVersionUID = -5531973259119524758L;
	
	private T payload;

	public SimpleParams() {
		// because of GWT
	}
	
	public SimpleParams(T payload) {
		this.payload = payload;
	}

	public T getPayload() {
		return payload;
	}

}
