package com.googlecode.kanbanik.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.kanbanik.client.services.ServerCommandInvoker;
import com.googlecode.kanbanik.client.services.ServerCommandInvokerAsync;
import com.googlecode.kanbanik.dto.BoardDto;
import com.googlecode.kanbanik.dto.BoardWithProjectsDto;
import com.googlecode.kanbanik.dto.ListDto;
import com.googlecode.kanbanik.dto.ProjectDto;
import com.googlecode.kanbanik.dto.WorkflowitemDto;
import com.googlecode.kanbanik.dto.shell.SimpleParams;
import com.googlecode.kanbanik.dto.shell.VoidParams;
import com.googlecode.kanbanik.shared.ServerCommand;

public class KanbanikEntryPoint implements EntryPoint {

	final ServerCommandInvokerAsync serverCommandInvoker = GWT
			.create(ServerCommandInvoker.class);

	public void onModuleLoad() {

		serverCommandInvoker
				.<VoidParams, SimpleParams<ListDto<BoardWithProjectsDto>>> invokeCommand(
						ServerCommand.GET_ALL_BOARDS_WITH_PROJECTS,
						new VoidParams(),
						new KanbanikAsyncCallback<SimpleParams<ListDto<BoardWithProjectsDto>>>() {

							@Override
							public void success(
									SimpleParams<ListDto<BoardWithProjectsDto>> result) {
								buildBoard(RootPanel.get("mainSection"), result);
							}

						});

	}

	private void buildBoard(RootPanel rootPanel,
			SimpleParams<ListDto<BoardWithProjectsDto>> result) {
		VerticalPanel panel = new VerticalPanel();

		for (BoardWithProjectsDto boardWithProjects: result.getPayload().getList()) {
			BoardDto board = boardWithProjects.getBoard();

			int row = 0;
			for (ProjectDto project: boardWithProjects.getProjectsOnBoard()) {
				FlexTable table = new FlexTable();
				table.setBorderWidth(1);
				table.setWidget(row, 0, new Label(project.getName()));
				buildBoard(board.getRootWorkflowitem(), table, row, 0);
				panel.add(table);
				row ++;
			}
			
		}

		RootPanel.get("mainSection").add(panel);
		// table.getCellFormatter().getElement(0, 0).setAttribute("colspan",
		// "1");

	}

	private void buildBoard(WorkflowitemDto workflowitem, FlexTable table,
			int row, int column) {
		if (workflowitem == null) {
			return;
		}
		WorkflowitemDto currentItem = workflowitem;

		while (true) {
			if (currentItem.getChild() != null) {
				FlexTable childTable = new FlexTable();
				childTable.setBorderWidth(1);
				table.setWidget(row, column, new WorkflowitemPlace(new Label(
						currentItem.getName()), childTable));
				buildBoard(currentItem.getChild(), childTable, 0, 0);
			} else {
				table.setWidget(row, column, new WorkflowitemPlace(new Label(
						currentItem.getName()), new FocusPanel()));
			}

			currentItem = currentItem.getNextItem();
			if (currentItem == null) {
				break;
			}

			column++;
		}

	}

}

class WorkflowitemPlace extends VerticalPanel {

	public WorkflowitemPlace(Widget header, Widget body) {
		super();
		add(header);
		add(body);
	}

}
