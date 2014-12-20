package com.googlecode.kanbanik.client.components.task;

import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.*;
import com.googlecode.kanbanik.client.KanbanikResources;
import com.googlecode.kanbanik.client.Modules;
import com.googlecode.kanbanik.client.api.DtoFactory;
import com.googlecode.kanbanik.client.api.Dtos;
import com.googlecode.kanbanik.client.components.filter.BoardsFilter;
import com.googlecode.kanbanik.client.managers.ClassOfServicesManager;
import com.googlecode.kanbanik.client.managers.UsersManager;
import com.googlecode.kanbanik.client.messaging.Message;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.messaging.MessageListener;
import com.googlecode.kanbanik.client.messaging.messages.task.*;
import com.googlecode.kanbanik.client.messaging.messages.task.ChangeTaskSelectionMessage.ChangeTaskSelectionParams;
import com.googlecode.kanbanik.client.modules.lifecyclelisteners.ModulesLifecycleListener;
import com.googlecode.kanbanik.client.modules.lifecyclelisteners.ModulesLyfecycleListenerHandler;
import static com.googlecode.kanbanik.client.api.Dtos.TaskDto;

public class TaskGui extends Composite implements MessageListener<TaskDto>, ModulesLifecycleListener, ClickHandler {
	
	@UiField
	FocusPanel header;

	@UiField
	Label ticketIdLabel;
	
	@UiField
	HTML dueDateLabel;
	
	@UiField
	Label nameLabel;

    @UiField
    TextArea nameLabelTextArea;

	@UiField
	PushButton editButton;
	
	@UiField
	PushButton deleteButton;
	
	@UiField
	FlowPanel assigneePicturePlace;
	
	@UiField
	FocusPanel wholePanel;

    @UiField
    HTMLPanel contentContainer;

	@UiField
	FlowPanel tagsPanel;

	HandlerRegistration imageHandle;
	
	private TaskDto taskDto;

    private BoardsFilter filter;

	private boolean isSelected = false;

    private boolean isShown = true;

	@UiField
	Style style;
	
	private static final TaskGuiTemplates TEMPLATE = GWT.create(TaskGuiTemplates.class);

    public interface TaskGuiTemplates extends SafeHtmlTemplates {
	     @Template("<div class=\"{0}\">{1}</div>")
	     SafeHtml messageWithLink(String style, String msg);
	   }

	
	private TaskSelectionChangeListener taskSelectionChangeListener = new TaskSelectionChangeListener();

    private TaskFilterChangeListener taskFilterChangeListener = new TaskFilterChangeListener();
	
	public interface Style extends CssResource {
		
		String selected();
		
		String unselected();
		
		String missedStyle();

		String tagStyle();
	}
	
	interface MyUiBinder extends UiBinder<Widget, TaskGui> {}
	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);


    private Dtos.BoardDto boardDto;

    public TaskGui(TaskDto taskDto, Dtos.BoardDto boardDto) {

        nameLabelTextArea = new ClickHandlingTextArea();
        this.boardDto = boardDto;

        initWidget(uiBinder.createAndBindUi(this));
		
		editButton.getUpFace().setImage(new Image(KanbanikResources.INSTANCE.editButtonImage()));
		deleteButton.getUpFace().setImage(new Image(KanbanikResources.INSTANCE.deleteButtonImage()));
		
		this.taskDto = taskDto;
		MessageBus.registerListener(TaskEditedMessage.class, this);
        MessageBus.registerListener(FilterChangedMessage.class, taskFilterChangeListener);
		MessageBus.registerListener(TaskChangedMessage.class, this);
		MessageBus.registerListener(TaskDeletedMessage.class, this);
		MessageBus.registerListener(ChangeTaskSelectionMessage.class, taskSelectionChangeListener);
		MessageBus.registerListener(GetSelectedTasksRequestMessage.class, this);
		new ModulesLyfecycleListenerHandler(Modules.BOARDS, this);
		
		new TaskEditingComponent(this, editButton, boardDto);
		new TaskDeletingComponent(this, deleteButton);
		
		setupAccordingDto(taskDto);
		
		wholePanel.addClickHandler(this);
	}
	
	public void setupAccordingDto(TaskDto taskDto) {
		header.setStyleName("task-class-of-service");
		header.getElement().getStyle().setBackgroundColor(getColorOf(taskDto));
        contentContainer.getElement().getStyle().setBackgroundColor(getColorOf(taskDto));
		ticketIdLabel.setText(taskDto.getTicketId());
		nameLabel.setText(taskDto.getName());
		nameLabel.setTitle(taskDto.getName());
        nameLabelTextArea.setText(taskDto.getName());
        nameLabelTextArea.setTitle(taskDto.getName());

        if (boardDto.isFixedSizeShortDescription()) {
            nameLabel.getElement().getStyle().setDisplay(Display.NONE);
            nameLabelTextArea.getElement().getStyle().setDisplay(Display.BLOCK);
        }


		boolean showingPictureEnabled = boardDto.isShowUserPictureEnabled();
		boolean hasAssignee = taskDto.getAssignee() != null;
		boolean assigneeHasPictue = hasAssignee && taskDto.getAssignee().getPictureUrl() != null && !"".equals(taskDto.getAssignee().getPictureUrl());

		if (hasAssignee && assigneeHasPictue && showingPictureEnabled) {
			if (imageHandle != null) {
				imageHandle.removeHandler();
			}

            Dtos.UserDto newUser = DtoFactory.userDto();
            newUser.setPictureUrl(taskDto.getAssignee().getPictureUrl());

			Image picture = UsersManager.getInstance().getPictureFor(newUser);
			imageHandle = picture.addClickHandler(this);
			assigneePicturePlace.clear();
			assigneePicturePlace.add(picture);
			assigneePicturePlace.setTitle(taskDto.getAssignee().getRealName());
			assigneePicturePlace.getElement().getStyle().setDisplay(Display.BLOCK);
            nameLabel.setWidth("84px");
            nameLabelTextArea.setWidth("73px");
			picture.addClickHandler(this);
		} else {
			assigneePicturePlace.getElement().getStyle().setDisplay(Display.NONE);
            nameLabel.setWidth("130px");
            nameLabelTextArea.setWidth("120px");
		}

		setupDueDate(taskDto.getDueDate());

		setupTags(taskDto.getTaskTags());
	}

	private void setupTags(List<Dtos.TaskTag> tags) {
		if (tags == null) {
			return;
		}

		for (Dtos.TaskTag tag : tags) {
			tagsPanel.add(renderTag(tag));
		}
	}

	private Widget renderTag(Dtos.TaskTag tag) {
		String pictureUrl = tag.getPictureUrl();
		if (pictureUrl == null || "".equals(pictureUrl)) {
			FlowPanel tagPanel = new FlowPanel();
			tagPanel.addStyleName(style.tagStyle());
			tagPanel.add(new Label(tag.getDescription()));
			// todo read this from the tag
			tagPanel.getElement().getStyle().setBackgroundColor("red");
			return tagPanel;
		} else {
			Image tagImage = new Image();
			tagImage.setUrl(pictureUrl);
			tagImage.setAltText(tagImage.getTitle());
			return tagImage;
		}
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
	    	dueDateLabel.setTitle("Due date deadline ("+dueDateText+") is already missed!");
	    	dueDateLabel.setHTML(TEMPLATE.messageWithLink(style.missedStyle(), "missed!"));
	    	return;
	    }
	    
	    if (diff == 0) {
	    	dueDateLabel.setTitle("Due date deadline ("+dueDateText+") is today!");
	    	dueDateLabel.setText("today");
	    	return;
	    }
	    
	    dueDateLabel.setTitle("Due date deadline ("+dueDateText+") is in " + diff + " days.");
	    if (diff > 365) {
	    	dueDateLabel.setText(" > year");
	    	return;
	    }
	    
	    if (diff > 31) {
	    	dueDateLabel.setText(" > month");
	    	return;
	    }
	    
	    if (diff > 7) {
	    	dueDateLabel.setText(" > week");
	    	return;
	    }
	    
	    if (diff == 1) {
	    	dueDateLabel.setText("tomorrow");
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
		if (message instanceof GetSelectedTasksRequestMessage) {
			if (isSelected) {
				MessageBus.sendMessage(new GetSelectedTasksRsponseMessage(getDto(), this));
			}
		} else if ((message instanceof TaskEditedMessage) || message instanceof TaskChangedMessage) {
			doTaskChanged(message);
		} else if (message instanceof TaskDeletedMessage) {
			if (message.getPayload().equals(getDto())) {
				unregisterListeners();	
			}
        }
	}

	private void doTaskChanged(Message<TaskDto> message) {
		TaskDto payload = message.getPayload();
		if (payload.getId().equals(taskDto.getId())) {
			this.taskDto = payload;
			setupAccordingDto(payload);
            reevaluateFilter();
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
				doClick(event.getCtrlKey());
				event.stopPropagation();
				event.preventDefault();
				setFocus(false);
				return;
			} else {
				super.onBrowserEvent(event);	
			}
		}
		
	}
	
	@Override
	public void onClick(ClickEvent event) {
		event.stopPropagation();
        event.preventDefault();
        doClick(event.isControlKeyDown());
	}
	
	private void doClick(boolean ctrlDown) {
		if (ctrlDown) {
			setSelected(!isSelected);
		} else {
			ChangeTaskSelectionParams params = new ChangeTaskSelectionParams(false, true, false, getDto());
			MessageBus.sendMessage(new ChangeTaskSelectionMessage(params, this));
			setSelected(true);
		}
	}
	
	private void setSelected(boolean selected) {
		if (selected) {
			wholePanel.addStyleName(style.selected());
			wholePanel.removeStyleName(style.unselected());
		} else {
			wholePanel.removeStyleName(style.selected());
			wholePanel.addStyleName(style.unselected());
		}
		
		isSelected = selected;
	}

	@Override
	public void activated() {
		if (!MessageBus.listens(TaskEditedMessage.class, this)) {
			MessageBus.registerListener(TaskEditedMessage.class, this);	
		}

        if (!MessageBus.listens(FilterChangedMessage.class, taskFilterChangeListener)) {
			MessageBus.registerListener(FilterChangedMessage.class, taskFilterChangeListener);
		}

		if (!MessageBus.listens(TaskChangedMessage.class, this)) {
			MessageBus.registerListener(TaskChangedMessage.class, this);	
		}
		
		if (!MessageBus.listens(TaskDeletedMessage.class, this)) {
			MessageBus.registerListener(TaskDeletedMessage.class, this);	
		}
		
		if (!MessageBus.listens(ChangeTaskSelectionMessage.class, taskSelectionChangeListener)) {
			MessageBus.registerListener(ChangeTaskSelectionMessage.class, taskSelectionChangeListener);	
		}
		
		if (!MessageBus.listens(GetSelectedTasksRequestMessage.class, this)) {
			MessageBus.registerListener(GetSelectedTasksRequestMessage.class, this);	
		}
	}

	@Override
	public void deactivated() {
		unregisterListeners();
	}

	private void unregisterListeners() {
		MessageBus.unregisterListener(TaskEditedMessage.class, this);
        MessageBus.unregisterListener(FilterChangedMessage.class, taskFilterChangeListener);
		MessageBus.unregisterListener(TaskChangedMessage.class, this);
		MessageBus.unregisterListener(TaskDeletedMessage.class, this);
		MessageBus.unregisterListener(ChangeTaskSelectionMessage.class, taskSelectionChangeListener);
		MessageBus.unregisterListener(GetSelectedTasksRequestMessage.class, this);
	}

    class TaskFilterChangeListener implements MessageListener<BoardsFilter> {

        @Override
        public void messageArrived(Message<BoardsFilter> message) {
            if (message.getPayload() == null) {
                return;
            }

            filter = message.getPayload();

            reevaluateFilter();
        }
    }

    public void beforeRemove(boolean partOfMove) {
        if (partOfMove) {
            // no need, the task is still present and will be re-evaluated
            return;
        }
        if (!isVisible() && filter != null) {
            // currently the only reason, but to be on the safe side adding the explicit check
            if (!filter.checkOnlyIfTaskMatches(taskDto)) {
                filter.onHiddenFieldRemoved();
            }
        }
    }

    public void reevaluateFilter() {
        boolean visible = filter == null || filter.taskMatches(taskDto, isVisible());
        setVisible(visible);
    }

    public void setFilter(BoardsFilter filter) {
        this.filter = filter;
    }

    class TaskSelectionChangeListener implements MessageListener<ChangeTaskSelectionParams> {

		@Override
		public void messageArrived(Message<ChangeTaskSelectionParams> message) {
			ChangeTaskSelectionParams params = message.getPayload();
			boolean forAll = params.isAll();
			boolean toMe = !forAll && params.getTask().equals(getDto());
			boolean ignoreMe = !params.isApplyToYourself() && message.getSource() == TaskGui.this;
			
			if ((forAll || toMe) && !ignoreMe) {
				setSelected(params.isSelect());	
			}
			
		}
		
	}


}
