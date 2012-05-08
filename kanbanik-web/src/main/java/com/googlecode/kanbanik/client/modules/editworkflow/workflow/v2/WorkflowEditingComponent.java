package com.googlecode.kanbanik.client.modules.editworkflow.workflow.v2;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.kanbanik.dto.BoardWithProjectsDto;
import com.googlecode.kanbanik.dto.ProjectDto;
import com.googlecode.kanbanik.dto.WorkflowitemDto;

public class WorkflowEditingComponent extends Composite {
	
	interface MyUiBinder extends UiBinder<Widget, WorkflowEditingComponent> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	
	@UiField
	Panel board;
	
	@UiField
	Panel palette;
	
	WorkflowEditBoardGuiBuilder boardBuilder = new WorkflowEditBoardGuiBuilder();
	
	public WorkflowEditingComponent(BoardWithProjectsDto boardWithProjects) {
		initWidget(uiBinder.createAndBindUi(this));
		FlexTable table = new FlexTable();
		table.setBorderWidth(1);
		boardBuilder.buildBoard(
				boardWithProjects.getBoard().getRootWorkflowitem(),
				null,
				table,
				null,
				0,
				0
		);
		
		board.add(table);
	}
	
	
	
	class WorkflowEditBoardGuiBuilder extends BoardGuiBuilder {

		@Override
		protected Widget createWorkflowitemPlaceContentWidget(PickupDragController dragController, WorkflowitemDto currentItem, ProjectDto project) {
			// will than handle the drag/drop
			return new SimplePanel();
		}

		@Override
		protected Widget createWorkflowitemPlace(
				PickupDragController dragController,
				WorkflowitemDto currentItem, 
				ProjectDto project,
				Widget childTable) {
			
			return new WorkflowitemWidget(currentItem, childTable);
		}
		
	}
}
