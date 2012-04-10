package com.googlecode.kanbanik.client.modules;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.googlecode.kanbanik.client.KanbanikAsyncCallback;
import com.googlecode.kanbanik.client.KanbanikServerCaller;
import com.googlecode.kanbanik.client.components.task.TaskChangedMessage;
import com.googlecode.kanbanik.client.components.task.TaskDeleteRequestedMessage;
import com.googlecode.kanbanik.client.components.task.TaskSaver;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.model.BoardGui;
import com.googlecode.kanbanik.client.model.BoardsGUI;
import com.googlecode.kanbanik.client.model.WorkflowGUI;
import com.googlecode.kanbanik.client.model.WorkflowItemGUI;
import com.googlecode.kanbanik.client.services.KanbanikService;
import com.googlecode.kanbanik.client.services.KanbanikServiceAsync;
import com.googlecode.kanbanik.shared.BoardDTO;
import com.googlecode.kanbanik.shared.KanbanikDTO;
import com.googlecode.kanbanik.shared.WorkflowItemDTO;
import com.googlecode.kanbanik.shared.WorkflowItemPlaceDTO;

public class BoardsModule implements KanbanikModule {

	private static final KanbanikServiceAsync kanbanikService = GWT.create(KanbanikService.class);

	private Map<WorkflowItemPlaceDTO, WorkflowItemGUI> dtoPlaceToPlace = new HashMap<WorkflowItemPlaceDTO, WorkflowItemGUI>();

	private BoardsGUI boards;

	static {
		TaskSaver taskSaver = new TaskSaver(kanbanikService);
		MessageBus.registerListener(TaskChangedMessage.class, taskSaver);
		MessageBus.registerListener(TaskDeleteRequestedMessage.class, taskSaver);
	}
	
	private void fillTables(KanbanikDTO kanbanik, ModuleInitializeCallback callback) {
		initPlaceMapping(kanbanik);
		boards = new BoardsGUI();

		for (BoardDTO boardDTO : kanbanik.getBoards()) {
			if (boardDTO.getProjects().size() == 0) {
				// don't show projects without boards
				continue;
			}

			WorkflowGUI workflow = new WorkflowGUI(boardDTO.getWorkflow().getWorkflowItems(), dtoPlaceToPlace);
			BoardGui board = new BoardGui(boardDTO, workflow, dtoPlaceToPlace);
			boards.addBoard(board);
		}

		callback.initialized(boards);
	}

	private void initPlaceMapping(KanbanikDTO kanbanik) {
		for (BoardDTO board : kanbanik.getBoards()) {
			if (board.getProjects().size() == 0) {
				continue;
			}
			for (WorkflowItemDTO workflowItem : board.getWorkflow().getWorkflowItems()) {
				for (WorkflowItemPlaceDTO place : workflowItem.getPlaces()) {
					dtoPlaceToPlace.put(place, new WorkflowItemGUI(place));
				}
			}
		}
	}

	public void initialize(final ModuleInitializeCallback callback) {
		new KanbanikServerCaller(
				new Runnable() {

					public void run() {
						kanbanikService.loadKanbanikData(new KanbanikAsyncCallback<KanbanikDTO>() {

							@Override
							public void success(KanbanikDTO kanbanik) {
								fillTables(kanbanik, callback);
							}

						});				
					}
				}
		);
	}
}
