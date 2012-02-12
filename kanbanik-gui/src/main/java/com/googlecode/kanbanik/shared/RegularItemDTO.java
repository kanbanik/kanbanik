package com.googlecode.kanbanik.shared;

import java.util.ArrayList;
import java.util.List;


public class RegularItemDTO extends WorkflowItemDTO {

	private static final long serialVersionUID = 1869977773264756526L;
	
	private WorkflowItemPlaceDTO place;

	public void setPlace(WorkflowItemPlaceDTO place) {
		this.place = place;
	}

	@Override
	public List<WorkflowItemPlaceDTO> getPlaces() {
		List<WorkflowItemPlaceDTO> list = new ArrayList<WorkflowItemPlaceDTO>();
		list.add(place);
		return list;
	}
}
