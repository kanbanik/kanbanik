package com.googlecode.kanbanik.client.modules.editworkflow;

import java.util.List;

import com.google.gwt.user.client.ui.VerticalPanel;
import com.googlecode.kanbanik.client.modules.editworkflow.workflow.EditableWorkflow;
import com.googlecode.kanbanik.shared.BoardDTO;
import com.googlecode.kanbanik.shared.ProjectDTO;

public class EditableBoard extends VerticalPanel {
	
	public EditableBoard(BoardDTO board, List<ProjectDTO> projects) {
		add(new EditableWorkflow(board));
	}
}
