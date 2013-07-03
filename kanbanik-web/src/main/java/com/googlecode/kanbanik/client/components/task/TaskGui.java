package com.googlecode.kanbanik.client.components.task;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.kanbanik.client.KanbanikResources;
import com.googlecode.kanbanik.client.managers.ClassOfServicesManager;
import com.googlecode.kanbanik.client.managers.UsersManager;
import com.googlecode.kanbanik.client.messaging.Message;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.messaging.MessageListener;
import com.googlecode.kanbanik.client.messaging.messages.task.TaskChangedMessage;
import com.googlecode.kanbanik.client.messaging.messages.task.TaskEditedMessage;
import com.googlecode.kanbanik.dto.TaskDto;

public class TaskGui extends Composite implements MessageListener<TaskDto>, ClickHandler {

	private static final String DESCRIPTION_WIDTH_WITH_PICTURE = "119px";

	private static final String DESCRIPTION_WIDTH_WITHOUT_PICTURE = "160px";
	
	private static final String DESCRIPTION_WIDTH_WITH_PICTURE_SELECTED = "103px";
	
	private static final String DESCRIPTION_WIDTH_WITHOUT_PICTURE_SELECTED = "147px";

	private boolean hasPicture = false;
	
	@UiField
	FocusPanel header;

	@UiField
	Label ticketIdLabel;
	
	@UiField
	Label dueDateLabel;
	
	@UiField(provided = true)
	TextArea nameLabel;
	
	@UiField
	PushButton editButton;
	
	@UiField
	PushButton deleteButton;
	
	@UiField
	FlowPanel assigneePicturePlace;
	
	@UiField
	FlowPanel descriptionContainer;
	
	@UiField
	FocusPanel wholePanel;
	
	HandlerRegistration imageHandle;
	
	private TaskDto taskDto;
	
	private boolean isSelected = false;
	
	@UiField 
	Style style;
	
	public interface Style extends CssResource {
		
		String selected();
		
		String unselected();
	}
	
	interface MyUiBinder extends UiBinder<Widget, TaskGui> {}
	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	
	public TaskGui(TaskDto taskDto) {
		
		nameLabel = new ClickHandlingTextArea();
		
		initWidget(uiBinder.createAndBindUi(this));
		
		editButton.getUpFace().setImage(new Image(KanbanikResources.INSTANCE.editButtonImage()));
		deleteButton.getUpFace().setImage(new Image(KanbanikResources.INSTANCE.deleteButtonImage()));
		
		this.taskDto = taskDto;
		MessageBus.registerListener(TaskEditedMessage.class, this);
		MessageBus.registerListener(TaskChangedMessage.class, this);
		
		new TaskEditingComponent(this, editButton);
		new TaskDeletingComponent(this, deleteButton);
		
		setupAccordingDto(taskDto);
		
		wholePanel.addClickHandler(this);
		nameLabel.addDomHandler(this, ClickEvent.getType());
	}
	
	public void setupAccordingDto(TaskDto taskDto) {
		header.setStyleName("task-class-of-service");
		header.getElement().getStyle().setBackgroundColor(getColorOf(taskDto));
		ticketIdLabel.setText(taskDto.getTicketId());
		nameLabel.setText(taskDto.getName());
		nameLabel.setTitle(taskDto.getName());

		boolean showingPictureEnabled = taskDto.getWorkflowitem().getParentWorkflow().getBoard().isShowUserPictureEnabled();
		boolean hasAssignee = taskDto.getAssignee() != null;
		boolean assigneeHasPictue = hasAssignee && taskDto.getAssignee().getPictureUrl() != null && !"".equals(taskDto.getAssignee().getPictureUrl());

		// ahh this is sooo ugly! Need to find a way to do it using pure CSS in the future
		if (hasAssignee && assigneeHasPictue && showingPictureEnabled) {
			if (imageHandle != null) {
				imageHandle.removeHandler();
			}
			
			Image picture = UsersManager.getInstance().getPictureFor(taskDto.getAssignee());
			imageHandle = picture.addClickHandler(this);
			assigneePicturePlace.clear();
			assigneePicturePlace.add(picture);
			assigneePicturePlace.setTitle(taskDto.getAssignee().getRealName());
			assigneePicturePlace.getElement().getStyle().setDisplay(Display.BLOCK);
			if (isSelected) {
				descriptionContainer.setWidth(DESCRIPTION_WIDTH_WITH_PICTURE_SELECTED);
			} else {
				descriptionContainer.setWidth(DESCRIPTION_WIDTH_WITH_PICTURE);
			}
			picture.addClickHandler(this);
			hasPicture = true;
		} else {
			assigneePicturePlace.getElement().getStyle().setDisplay(Display.NONE);
			if (isSelected) {
				descriptionContainer.setWidth(DESCRIPTION_WIDTH_WITHOUT_PICTURE_SELECTED);
			} else {
				descriptionContainer.setWidth(DESCRIPTION_WIDTH_WITHOUT_PICTURE);
			}
			hasPicture = false;
		}
		
		setupDueDate(taskDto.getDueDate());
		
	}

	private void setupDueDate(String dueDate) {
		if (dueDate == null || "".equals(dueDate)) {
			dueDateLabel.setVisible(false);
			return;
		}
		
		Date date = null;
		try {
			date = DateTimeFormat.getFormat(PredefinedFormat.DATE_SHORT).parse(dueDate);
		} catch(IllegalArgumentException e) {
			dueDateLabel.setVisible(false);
			return;
		}

		dueDateLabel.setVisible(true);
		setupHumanReadableDueDateText(dueDate, date);
	}

	@SuppressWarnings("deprecation")
	private void setupHumanReadableDueDateText(String dueDateText, Date dueDate) {
		Date nowDate = new Date();
		// comparing days only - not care about the time
		nowDate.setMinutes(0);
		nowDate.setHours(0);
		nowDate.setSeconds(0);
		
		final long DAY_MILLIS = 1000 * 60 * 60 * 24;
		
	    long day1 = dueDate.getTime() / DAY_MILLIS;
	    long day2 = nowDate.getTime() / DAY_MILLIS;
	    long diff = day1 - day2;
	    
	    if (diff < 0) {
	    	dueDateLabel.setTitle("Due date deadline ("+dueDateText+") already missed");
	    	dueDateLabel.setText("missed!");
	    	return;
	    }
	    
	    if (diff == 0) {
	    	dueDateLabel.setTitle("Due date deadline is today!");
	    	dueDateLabel.setText("today");
	    	return;
	    }
	    
	    dueDateLabel.setTitle("Due date deadline is " + dueDateText + " (in " + diff + " days)");
	    if (diff > 31) {
	    	dueDateLabel.setText(" > month");
	    	return;
	    }
	    
	    if (diff > 7) {
	    	dueDateLabel.setText(" > week");
	    	return;
	    }
	    
	    if (diff == 1) {
	    	dueDateLabel.setText("1 day");
	    	return;
	    }
	    
    	dueDateLabel.setText(diff + " days");
	}

	private String getColorOf(TaskDto taskDto) {
		if (taskDto.getClassOfService() == null) {
			return "#" + ClassOfServicesManager.getInstance().getDefaultClassOfService().getColour();
		}
		
		return "#" + taskDto.getClassOfService().getColour();
	}

	public FocusPanel getHeader() {
		return header;
	}

	public TaskDto getDto() {
		return taskDto;
	}

	public void messageArrived(Message<TaskDto> message) {
		TaskDto payload = message.getPayload();
		if (payload.getId().equals(taskDto.getId())) {
			this.taskDto = payload;
			setupAccordingDto(payload);
		}
	}

	class ClickHandlingTextArea extends TextArea { 
		public ClickHandlingTextArea() {
			super();
			
			sinkEvents(Event.ONCLICK);
			setEnabled(true);
		}
		
		@Override
		public void onBrowserEvent(Event event) {
			if (DOM.eventGetType(event) == Event.ONCLICK) {
				click();
				super.onBrowserEvent(event);
				setFocus(false);
				return;
			}
			super.onBrowserEvent(event);
		}
		
	}
	
	@Override
	public void onClick(ClickEvent event) {
		click();
	}
	
	private void click() {
		if (isSelected) {
			wholePanel.removeStyleName(style.selected());
			wholePanel.addStyleName(style.unselected());
			if (hasPicture) {
				descriptionContainer.setWidth(DESCRIPTION_WIDTH_WITH_PICTURE);
			} else {
				descriptionContainer.setWidth(DESCRIPTION_WIDTH_WITHOUT_PICTURE);
			}
		} else {
			wholePanel.addStyleName(style.selected());
			wholePanel.removeStyleName(style.unselected());
			if (hasPicture) {
				descriptionContainer.setWidth(DESCRIPTION_WIDTH_WITH_PICTURE_SELECTED);
			} else {
				descriptionContainer.setWidth(DESCRIPTION_WIDTH_WITHOUT_PICTURE_SELECTED);
			}
		}
		
		isSelected = !isSelected;
	}
}
