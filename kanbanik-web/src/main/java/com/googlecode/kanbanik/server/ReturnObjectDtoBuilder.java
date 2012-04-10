package com.googlecode.kanbanik.server;

import com.googlecode.kanbanik.ReturnObject;
import com.googlecode.kanbanik.shared.ReturnObjectDTO;


public class ReturnObjectDtoBuilder {
	
	public ReturnObjectDTO build(ReturnObject returnObject) {
		ReturnObjectDTO retDTO = new ReturnObjectDTO();
		retDTO.setOK(returnObject.isOK());
		retDTO.setMessage(returnObject.getMessage());
		return retDTO;
	}
}
