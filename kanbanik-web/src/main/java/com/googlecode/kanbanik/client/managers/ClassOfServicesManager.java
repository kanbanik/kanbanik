package com.googlecode.kanbanik.client.managers;

import java.util.ArrayList;
import java.util.List;

import com.googlecode.kanbanik.dto.ClassOfServiceDto;

public class ClassOfServicesManager {

	private static final ClassOfServicesManager INSTANCE = new ClassOfServicesManager();
	
	private List<ClassOfServiceDto> classesOfServices = new ArrayList<ClassOfServiceDto>();
	
	public static ClassOfServicesManager getInstance() {
		return INSTANCE;
	}
	
	public List<ClassOfServiceDto> getAll() {
		
		List<ClassOfServiceDto> merged = new ArrayList<ClassOfServiceDto>();
		merged.addAll(classesOfServices);
		
		if (merged.size() == 0) {
			merged.add(getDefaultClassOfService());
		}
		
		return merged;
	}
	
	public void setClassesOfServices(List<ClassOfServiceDto> dtos) {
		classesOfServices = new ArrayList<ClassOfServiceDto>(); 
		for (ClassOfServiceDto dto : dtos) {
			addClassOfService(dto);
		}
	}

	public ClassOfServiceDto getDefaultClassOfService() {
		return new DefaultClassOfService();
	}

	private void addClassOfService(ClassOfServiceDto dto) {
		classesOfServices.add(dto);
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
