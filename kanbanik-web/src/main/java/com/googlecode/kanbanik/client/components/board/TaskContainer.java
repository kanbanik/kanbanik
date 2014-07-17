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
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.kanbanik.client.api.Dtos;
import com.googlecode.kanbanik.client.components.task.TaskGui;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.messaging.messages.task.ChangeTaskSelectionMessage;
import static com.googlecode.kanbanik.client.api.Dtos.TaskDto;

public class TaskContainer extends Composite {

	@UiField
	FlowPanel contentPanel;
	
	@UiField 
	Style style;

	public interface Style extends CssResource {
		
		String defaultContantPanelStyle();
		
		String fixedContantPanelStyle();
	}
	
	// task height + border + margin
	public static final int SIZE_OF_TASK = 78;
	
	interface MyUiBinder extends UiBinder<FlowPanel, TaskContainer> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	public TaskContainer(Dtos.BoardDto board, Dtos.WorkflowitemDto currentItem) {
		initWidget(uiBinder.createAndBindUi(this));
		setupSizing(board, currentItem);
		registerListeners();
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
			String height = (numOfTasks * SIZE_OF_TASK) + "px";
			setHeight(height);
		} else {
			addStyleName(style.defaultContantPanelStyle());
		}
		
	}

	public void removeTask(TaskDto task) {
		int widgetIndex = getTaskIndex(task);
		if (widgetIndex != -1) {
			contentPanel.remove(widgetIndex);
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

	public void add(TaskGui task) {

		BigDecimal taskOrder = asBigDecimal(task.getDto().getOrder());
		
		for (TaskDto currenTask : getTasks()) {
			BigDecimal currentOrder = asBigDecimal(currenTask.getOrder());
			if (taskOrder.compareTo(currentOrder) <= 0) {
				contentPanel.insert(task, getTaskIndex(currenTask));
				return;
			}
		}
		
		contentPanel.add(task);
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
}
