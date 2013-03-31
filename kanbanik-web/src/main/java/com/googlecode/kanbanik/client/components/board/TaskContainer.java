package com.googlecode.kanbanik.client.components.board;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.kanbanik.client.components.task.TaskGui;
import com.googlecode.kanbanik.dto.TaskDto;

public class TaskContainer extends Composite {

	@UiField
	FlowPanel contentPanel;

	interface MyUiBinder extends UiBinder<FlowPanel, TaskContainer> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	public TaskContainer() {
		initWidget(uiBinder.createAndBindUi(this));
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
				if (((TaskGui) widget).getDto().equals(task)) {
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
		int nextTaskIndex = getNextTaskIndex(asBigDecimal(task.getDto().getOrder()));
		contentPanel.insert(task, nextTaskIndex);
	}

	private int getNextTaskIndex(BigDecimal order) {
		// TODO this is not correct
		for (TaskDto currenTask : getTasks()) {
			BigDecimal currentOrder = asBigDecimal(currenTask.getOrder());
			if (order.compareTo(currentOrder) > 0) {
				return getTaskIndex(currenTask) + 1;
			}
		}
		
		return 0;
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
