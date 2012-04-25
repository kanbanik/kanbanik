package com.googlecode.kanbanik.client.components.board;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.kanbanik.client.model.TaskGui;
import com.googlecode.kanbanik.dto.TaskDto;

public class TaskContainer extends FlowPanel {

	public TaskContainer() {
		setSize("200px", "200px");
	}

	public void removeTask(TaskDto task) {
		for (int i = 0; i < getWidgetCount(); i ++) {
			Widget widget = getWidget(i);
			if (widget instanceof TaskGui) {
				if (((TaskGui) widget).getDto().equals(task)) {
					remove(i);
				}
			}
		}
		
	}
}
