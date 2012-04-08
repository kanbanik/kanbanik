package com.googlecode.kanbanik.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.googlecode.kanbanik.client.services.ServerCommandInvoker;
import com.googlecode.kanbanik.client.services.ServerCommandInvokerAsync;
import com.googlecode.kanbanik.dto.BoardWithProjectsDto;
import com.googlecode.kanbanik.dto.ListDto;
import com.googlecode.kanbanik.dto.shell.SimpleParams;
import com.googlecode.kanbanik.dto.shell.VoidParams;
import com.googlecode.kanbanik.shared.ServerCommand;

public class KanbanikEntryPoint implements EntryPoint{
	
	final ServerCommandInvokerAsync serverCommandInvoker = GWT.create(ServerCommandInvoker.class);
	
	public void onModuleLoad() {

		serverCommandInvoker.<VoidParams, SimpleParams<ListDto<BoardWithProjectsDto>>>invokeCommand(
				ServerCommand.GET_ALL_BOARDS_WITH_PROJECTS, 
				new VoidParams(), 
				new KanbanikAsyncCallback<SimpleParams<ListDto<BoardWithProjectsDto>>>(){

			@Override
			public void success(SimpleParams<ListDto<BoardWithProjectsDto>> result) {
				buildBoard(RootPanel.get("mainSection"), result);
			}

		});
		
	}
	
	private void buildBoard(RootPanel rootPanel, SimpleParams<ListDto<BoardWithProjectsDto>> result) {
		FocusPanel panel1 = new FocusPanel();
		FocusPanel panel2 = new FocusPanel();
		
		FlexTable table = new FlexTable();
		table.setWidget(0, 0, panel1);
		table.setWidget(0, 1, panel2);
		
//		table.getCellFormatter().getElement(0, 0).setAttribute("colspan", "1");
		
		panel1.add(new Label("Hello"));
		panel2.add(new Label("World!"));
		RootPanel.get("mainSection").add(table);		
	}

}

