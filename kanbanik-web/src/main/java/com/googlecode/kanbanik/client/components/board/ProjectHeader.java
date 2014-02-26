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
import com.googlecode.kanbanik.client.api.Dtos;
import com.googlecode.kanbanik.client.components.task.TaskAddingComponent;

public class ProjectHeader extends Composite {

	interface MyUiBinder extends UiBinder<Widget, ProjectHeader> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	@UiField
	Label projectName;
	
	@UiField
	PushButton addButton;
	
	public ProjectHeader(Dtos.BoardDto board, Dtos.ProjectDto project) {
		initWidget(uiBinder.createAndBindUi(this));

		projectName.setText(project.getName());
		Dtos.WorkflowitemDto rootDto = board.getWorkflow().getWorkflowitems().size() > 0 ? board.getWorkflow().getWorkflowitems().get(0) : null;
		
		if (rootDto != null) {
			addButton.getUpFace().setImage(new Image(KanbanikResources.INSTANCE.addButtonImage()));	
		} else {
			// the board has no workflow, disable add button
			addButton.setEnabled(false);
			addButton.setTitle("It is not possible to add a task to a board when the board has no workflow.");
			addButton.getUpFace().setImage(new Image(KanbanikResources.INSTANCE.addDisabledButtonImage()));
		}
		
		new TaskAddingComponent(project, getInputQueue(rootDto), addButton, board);
	}

	
	private Dtos.WorkflowitemDto getInputQueue(Dtos.WorkflowitemDto root) {
		if (root == null) {
			return null;
		}
		
		
		if (root.getNestedWorkflow().getWorkflowitems().size() == 0) {
			return root;
		} else {
			return getInputQueue(root.getNestedWorkflow().getWorkflowitems().get(0));
		}
	}
	
}
