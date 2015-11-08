package com.googlecode.kanbanik.client.managers;

import com.googlecode.kanbanik.client.api.DtoFactory;
import com.googlecode.kanbanik.client.api.Dtos;
import com.googlecode.kanbanik.client.messaging.Message;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.messaging.MessageListener;
import com.googlecode.kanbanik.client.messaging.messages.task.TaskAddedMessage;
import com.googlecode.kanbanik.client.messaging.messages.task.TaskEditedMessage;
import com.googlecode.kanbanik.client.security.CurrentUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.googlecode.kanbanik.client.api.Dtos.ClassOfServiceDto;

public class ClassOfServicesManager implements MessageListener<Dtos.TaskDto> {

	private static final ClassOfServicesManager INSTANCE = new ClassOfServicesManager();
	
	private List<ClassOfServiceDto> classesOfServices = new ArrayList<ClassOfServiceDto>();

    private ClassOfServiceChangedListener listener;

	private ClassOfServicesManager() {
		MessageBus.registerListener(TaskAddedMessage.class, this);
		MessageBus.registerListener(TaskEditedMessage.class, this);
	}
	
	public static ClassOfServicesManager getInstance() {
		return INSTANCE;
	}

    public List<ClassOfServiceDto> getAll() {
        if (classesOfServices == null) {
            return new ArrayList<>();
        }
        Collections.sort(classesOfServices, new Comparator<ClassOfServiceDto>() {
            @Override
            public int compare(ClassOfServiceDto a, ClassOfServiceDto b) {
                if (a.getName() == null || b.getName() == null) {
                    // should not happen so just to make sure I will not fail on NPE
                    return 0;

                }

                return a.getName().compareTo(b.getName());
            }
        });
        return new ArrayList<>(classesOfServices);
    }


    public List<ClassOfServiceDto> getAllWithNone() {
		
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

	@Override
	public void messageArrived(Message<Dtos.TaskDto> message) {
        if (message == null || message.getPayload() == null) {
            return;
        }

        Dtos.TaskDto task = message.getPayload();
        Dtos.ClassOfServiceDto classOfServiceDto = task.getClassOfService();

        if (classOfServiceDto == null || classOfServiceDto.getId() == null) {
            return;
        }

        if (!contains(classOfServiceDto.getId())) {
            classesOfServices.add(classOfServiceDto);

            if (listener != null) {
                listener.added(classOfServiceDto);
            }
        }

	}

    private boolean contains(String id) {
        for (Dtos.ClassOfServiceDto classOfServiceDto : classesOfServices) {
            if (id.equals(classOfServiceDto.getId())) {
                return true;
            }
        }

        return false;
    }

    public void setListener(ClassOfServiceChangedListener listener) {
        this.listener = listener;
    }

    public interface ClassOfServiceChangedListener {
        void added(Dtos.ClassOfServiceDto classOfServiceDto);
    }
}
