package com.googlecode.kanbanik.client.managers;

import com.googlecode.kanbanik.client.api.DtoFactory;
import com.googlecode.kanbanik.client.security.CurrentUser;

import java.util.ArrayList;
import java.util.List;

import static com.googlecode.kanbanik.client.api.Dtos.ClassOfServiceDto;

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
        ClassOfServiceDto defaultClassOfService = DtoFactory.classOfServiceDto();
        defaultClassOfService.setColour("92c1f0");
        defaultClassOfService.setName("Default Class Of Service");
        defaultClassOfService.setId(null);
        defaultClassOfService.setSessionId(CurrentUser.getInstance().getSessionId());
        defaultClassOfService.setVersion(0);
		return defaultClassOfService;
	}

	private void addClassOfService(ClassOfServiceDto dto) {
		classesOfServices.add(dto);
	}
}
