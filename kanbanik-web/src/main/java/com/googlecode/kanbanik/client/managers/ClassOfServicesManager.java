package com.googlecode.kanbanik.client.managers;

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
		return new DefaultClassOfService();
	}

	private void addClassOfService(ClassOfServiceDto dto) {
		classesOfServices.add(dto);
	}
	
	class DefaultClassOfService implements ClassOfServiceDto {

		private static final long serialVersionUID = 157065282059901799L;

        @Override
        public String getColour() {
            return "92c1f0";
        }

        @Override
        public String getName() {
            return "Default Class Of Service";
        }

        @Override
        public String getSessionId() {
            return CurrentUser.getInstance().getSessionId();
        }

        @Override
        public String getId() {
            return null;
        }

        @Override
        public void setId(String id) {

        }

        @Override
        public void setName(String name) {

        }

        @Override
        public String getDescription() {
            return null;
        }

        @Override
        public void setDescription(String description) {

        }

        @Override
        public void setColour(String colour) {

        }

        @Override
        public int getVersion() {
            return 0;
        }

        @Override
        public void setVersion(int version) {

        }

        @Override
        public String getCommandName() {
            return null;
        }

        @Override
        public void setCommandName(String commandName) {

        }

        @Override
        public void setSessionId(String sessionId) {

        }
    }
	
}
