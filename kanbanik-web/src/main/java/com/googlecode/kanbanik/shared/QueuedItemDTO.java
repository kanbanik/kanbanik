package com.googlecode.kanbanik.shared;

import java.util.ArrayList;
import java.util.List;

public class QueuedItemDTO extends WorkflowItemDTO {

	private static final long serialVersionUID = -3648970798751216048L;

	private List<WorkflowItemPlaceDTO> places = new ArrayList<WorkflowItemPlaceDTO>();

	public List<WorkflowItemPlaceDTO> getPlaces() {
		return places;
	}
	
	public void addPlace(WorkflowItemPlaceDTO place) {
		if (places == null) {
			places = new ArrayList<WorkflowItemPlaceDTO>();
		}
		
		places.add(place);
	}
}
