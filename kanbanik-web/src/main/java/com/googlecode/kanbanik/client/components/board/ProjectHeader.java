package com.googlecode.kanbanik.client.components.board;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.kanbanik.client.KanbanikResources;
import com.googlecode.kanbanik.client.components.task.TaskAddingComponent;
import com.googlecode.kanbanik.dto.BoardDto;
import com.googlecode.kanbanik.dto.ProjectDto;
import com.googlecode.kanbanik.dto.WorkflowitemDto;

public class ProjectHeader extends Composite {

	interface MyUiBinder extends UiBinder<Widget, ProjectHeader> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	@UiField
	Label projectName;
	
	@UiField
	PushButton addButton;
	
	public ProjectHeader(BoardDto board, ProjectDto project) {
		initWidget(uiBinder.createAndBindUi(this));

		projectName.setText(project.getName());
		addButton.getUpFace().setImage(new Image(KanbanikResources.INSTANCE.addButtonImage()));
		new TaskAddingComponent(project, getInputQueue(board.getRootWorkflowitem()), addButton);
	}

	
	private WorkflowitemDto getInputQueue(WorkflowitemDto root) {
		if (root == null) {
			return null;
		}
		if (root.getChild() == null) {
			return root;
		} else {
			return getInputQueue(root.getChild());
		}
	}
}
