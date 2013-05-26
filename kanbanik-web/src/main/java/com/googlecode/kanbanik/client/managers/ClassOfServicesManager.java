package com.googlecode.kanbanik.client.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.googlecode.kanbanik.dto.BoardDto;
import com.googlecode.kanbanik.dto.ClassOfServiceDto;

public class ClassOfServicesManager {

	private static final ClassOfServicesManager INSTANCE = new ClassOfServicesManager();
	
	private Map<BoardDto, List<ClassOfServiceDto>> boardToClassOfServices = new HashMap<BoardDto, List<ClassOfServiceDto>>();
	
	public static ClassOfServicesManager getInstance() {
		return INSTANCE;
	}
	
	public List<ClassOfServiceDto> getForBoard(BoardDto board) {
		
		List<ClassOfServiceDto> forBoard = boardToClassOfServices.get(board);
		List<ClassOfServiceDto> shared = boardToClassOfServices.get(null);
		if (forBoard == null) {
			forBoard = new ArrayList<ClassOfServiceDto>();
		}
		
		if (shared == null) {
			shared = new ArrayList<ClassOfServiceDto>();
		}

		List<ClassOfServiceDto> merged = new ArrayList<ClassOfServiceDto>();
		merged.addAll(forBoard);
		merged.addAll(shared);
		
		return merged;
	}
	
	public void setClassesOfServices(List<ClassOfServiceDto> dtos) {
		boardToClassOfServices = new HashMap<BoardDto, List<ClassOfServiceDto>>(); 
		for (ClassOfServiceDto dto : dtos) {
			addClassOfService(dto);
		}
	}

	public ClassOfServiceDto getDefaultClassOfService() {
		return new DefaultClassOfService();
	}

	private void addClassOfService(ClassOfServiceDto dto) {
		if (!boardToClassOfServices.containsKey(dto.getBoard())) {
			boardToClassOfServices.put(dto.getBoard(), new ArrayList<ClassOfServiceDto>());
		}
		
		boardToClassOfServices.get(dto.getBoard()).add(dto);
	}
	
	class DefaultClassOfService extends ClassOfServiceDto {

		private static final long serialVersionUID = 157065282059901799L;
		
		@Override
		public String getName() {
			return "Default Class Of Service";
		}
		
		@Override
		public String getColour() {
			return "92c1f0";
		}
	}
	
}
