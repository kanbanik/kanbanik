package com.googlecode.kanbanik.client.components.board;

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
	
	interface MyUiBinder extends UiBinder<FlowPanel, TaskContainer> {}
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
	
	private int getTaskIndex(TaskDto task) {
		for (int i = 0; i < contentPanel.getWidgetCount(); i ++) {
			Widget widget = contentPanel.getWidget(i);
			if (widget instanceof TaskGui) {
				if (((TaskGui) widget).getDto().equals(task)) {
					return i;
				}
			}
		}
		
		return -1;
	}

	public void add(TaskGui task) {
		contentPanel.add(task);
	}

	public FlowPanel asFlowPanel() {
		return contentPanel;
	}
}
