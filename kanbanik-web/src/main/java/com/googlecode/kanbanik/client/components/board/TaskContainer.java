package com.googlecode.kanbanik.client.components.board;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.kanbanik.client.api.Dtos;
import com.googlecode.kanbanik.client.components.task.TaskGui;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.messaging.messages.task.ChangeTaskSelectionMessage;
import com.googlecode.kanbanik.client.modules.editworkflow.workflow.WipLimitGuard;

import static com.googlecode.kanbanik.client.api.Dtos.TaskDto;

public class TaskContainer extends Composite {

	@UiField(provided = true)
	FlowPanel contentPanel;
	
	@UiField 
	Style style;

    private WipLimitGuard wipLimitGuard;

    private Dtos.WorkflowitemDto currentItem;

    public interface Style extends CssResource {
		
		String defaultContantPanelStyle();
		
		String fixedContantPanelStyle();

        String inWip();

        String wipOverrun();
	}

	interface MyUiBinder extends UiBinder<FlowPanel, TaskContainer> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	public TaskContainer(Dtos.BoardDto board, final Dtos.WorkflowitemDto currentItem) {
        this.currentItem = currentItem;

        contentPanel = new FlowPanel() {
            @Override
            protected void insert(Widget child, com.google.gwt.user.client.Element container, int beforeIndex, boolean domInsert) {
                super.insert(child, container, beforeIndex, domInsert);
                if (child instanceof TaskGui) {
                    wipLimitGuard.taskCountChanged(currentItem.getId(), getTasks().size());
                }
            }

            @Override
            public boolean remove(Widget child) {
                boolean res = super.remove(child);

                if (res && child instanceof TaskGui) {
                    wipLimitGuard.taskCountChanged(currentItem.getId(), getTasks().size());
                }

                return res;
            }

        };

        initWidget(uiBinder.createAndBindUi(this));
		setupSizing(board, currentItem);
		registerListeners();
	}

    public void setWipCorrect(boolean wipCorrect) {
        if (wipCorrect) {
            contentPanel.getElement().replaceClassName(style.wipOverrun(), style.inWip());
        } else {
            contentPanel.getElement().replaceClassName(style.inWip(), style.wipOverrun());
        }
    }

	private void registerListeners() {
		contentPanel.addDomHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				MessageBus.sendMessage(ChangeTaskSelectionMessage.deselectAll(this));
			}
		}, ClickEvent.getType());
	}

	private void setupSizing(Dtos.BoardDto board, Dtos.WorkflowitemDto currentItem) {
		boolean fixedSizeOnWorkflowitem = currentItem.getVerticalSize() != -1;
		if (fixedSizeOnWorkflowitem) {
			int numOfTasks = currentItem.getVerticalSize();
			addStyleName(style.fixedContantPanelStyle());
			String height = (numOfTasks) + "px";
			setHeight(height);
		} else {
			addStyleName(style.defaultContantPanelStyle());
		}
		
	}

	public boolean containsTask(TaskDto task) {
		return getTaskIndex(task) != -1;
	}

	public int getTaskIndex(TaskDto task) {
		for (int i = 0; i < contentPanel.getWidgetCount(); i++) {
			Widget widget = contentPanel.getWidget(i);
			if (widget instanceof TaskGui) {
				if (((TaskGui) widget).getDto().getId().equals(task.getId())) {
					return i;
				}
			}
		}

		return -1;
	}

	public List<TaskDto> getTasks() {
		List<TaskDto> res = new ArrayList<TaskDto>();

		for (int i = 0; i < contentPanel.getWidgetCount(); i++) {
			Widget widget = contentPanel.getWidget(i);
			if (widget instanceof TaskGui) {
				res.add(((TaskGui) widget).getDto());
			}
		}

		return res;
	}

    public TaskDto getTaskById(String id) {
        if (id == null) {
            return null;
        }

        for (TaskDto task : getTasks()) {
            if (id.equals(task.getId())) {
                return task;
            }
        }

        return null;
    }

	public void add(TaskGui task) {
		BigDecimal taskOrder = asBigDecimal(task.getDto().getOrder());

		for (TaskDto currenTask : getTasks()) {
			BigDecimal currentOrder = asBigDecimal(currenTask.getOrder());
			if (taskOrder.compareTo(currentOrder) <= 0) {
				contentPanel.insert(task, getTaskIndex(currenTask));
				return;
			}
		}

        contentPanel.insert(task, 0);
	}

    public void removeTask(TaskDto task) {
        int widgetIndex = getTaskIndex(task);
        if (widgetIndex != -1) {
            contentPanel.remove(widgetIndex);
        }
    }

	private BigDecimal asBigDecimal(String string) {
		if (string == null || "".equals(string)) {
			string = "0";
		}

		return new BigDecimal(string);
	}

	public FlowPanel asFlowPanel() {
		return contentPanel;
	}

    public void setWipLimitGuard(WipLimitGuard wipLimitGuard) {
        this.wipLimitGuard = wipLimitGuard;
    }
}
