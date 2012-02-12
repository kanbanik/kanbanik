package com.googlecode.kanbanik.client.modules.editworkflow.boards;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.kanbanik.client.KanbanikResources;
import com.googlecode.kanbanik.client.Modules;
import com.googlecode.kanbanik.client.messaging.Message;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.messaging.MessageListener;
import com.googlecode.kanbanik.client.modules.editworkflow.ConfigureWorkflowModule;
import com.googlecode.kanbanik.client.modules.editworkflow.projects.ProjectCreatingComponent;
import com.googlecode.kanbanik.client.modules.editworkflow.projects.ProjectsToBoardAdding;
import com.googlecode.kanbanik.client.modules.editworkflow.workflow.BoardDeletedMessage;
import com.googlecode.kanbanik.client.modules.editworkflow.workflow.BoardEditedMessage;
import com.googlecode.kanbanik.client.modules.lifecyclelisteners.ModulesLifecycleListener;
import com.googlecode.kanbanik.client.modules.lifecyclelisteners.ModulesLyfecycleListenerHandler;
import com.googlecode.kanbanik.shared.BoardDTO;
import com.googlecode.kanbanik.shared.ProjectDTO;

public class BoardsBox extends Composite {

	@UiField(provided=true)
	BoardsListBox boardsList;

	@UiField
	PushButton addBoardButton;
	
	@UiField
	PushButton deleteButton;
	
	@UiField
	PushButton editButton;

	@UiField
	PushButton addProjectButton;
	
	@UiField
	SimplePanel projectsToBoardAddingContainer;
	
	private ProjectsToBoardAdding projectToBoardAdding;

	private BoardDeletingComponent boardDeletingComponent;
	
	private BoardEditingComponent boardEditingComponent;
	
	private static int lastSelectedIndex = 0;
	
	interface MyUiBinder extends UiBinder<Widget, BoardsBox> {}
	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	public BoardsBox(ConfigureWorkflowModule configureWorkflowModule) {
		boardsList = new BoardsListBox(configureWorkflowModule);
		initWidget(uiBinder.createAndBindUi(this));
		
		deleteButton.setEnabled(false);
		editButton.setEnabled(false);
		addProjectButton.setEnabled(false);
		
		addBoardButton.getUpFace().setImage(new Image(KanbanikResources.INSTANCE.addButtonImage()));
		editButton.getUpFace().setImage(new Image(KanbanikResources.INSTANCE.editButtonImage()));
		deleteButton.getUpFace().setImage(new Image(KanbanikResources.INSTANCE.deleteButtonImage()));
		
		addProjectButton.getUpFace().setImage(new Image(KanbanikResources.INSTANCE.addButtonImage()));
		new BoardCreatingComponent(addBoardButton);
		boardDeletingComponent = new BoardDeletingComponent(deleteButton);
		boardEditingComponent = new BoardEditingComponent(editButton);
		new ProjectCreatingComponent(addProjectButton);
		
	}
	
	public void setBoards(List<BoardDTO> allBoards) {
		boardsList.setContent(allBoards);
	}

	class BoardsListBox extends ListBox implements ChangeHandler, MessageListener<BoardDTO>, ModulesLifecycleListener {

		private List<BoardDTO> boards;

		private BoardDTO selectedDTO = null;

		private ConfigureWorkflowModule configureWorkflowModule;

		public BoardsListBox(ConfigureWorkflowModule configureWorkflowModule) {
			this.configureWorkflowModule = configureWorkflowModule;
			addChangeHandler(this);
			new ModulesLyfecycleListenerHandler(Modules.CONFIGURE, this);
			MessageBus.registerListener(BoardCreatedMessage.class, this);
		}

		public void setContent(List<BoardDTO> boards) {
			int tmpSelectedBoard = lastSelectedIndex;
			clear();
			this.boards = boards;
			for (BoardDTO board : boards) {
				addItem(board.getName());
			}

			setupSelectedDTO();
			lastSelectedIndex = tmpSelectedBoard;
			resetButtonAvailability();
		}

		private void setupSelectedDTO() {
			if (boards == null) {
				// TODO handle this better, it means the boards has not been initialized
				return;
			}

			int index = getSelectedIndex();
			if (boards.size() != 0 && index >= 0 && index  < boards.size()) {
				selectedDTO = boards.get(index);	
			} else { 
				selectedDTO = null;
			}
			
			lastSelectedIndex = index;
			boardDeletingComponent.setBoardDto(selectedDTO);
			boardEditingComponent.setBoardDto(selectedDTO);
		}

		public void onChange(ChangeEvent event) {
			onChange();
		}

		void onChange() {
			setupSelectedDTO();
			configureWorkflowModule.selectedBoardChanged(selectedDTO);
			resetButtonAvailability();
		}

		private void resetButtonAvailability() {
			editButton.setEnabled(selectedDTO != null);
			deleteButton.setEnabled(selectedDTO != null);
			addProjectButton.setEnabled(selectedDTO != null);
		}

		public BoardDTO getSelectedBoard() {
			return selectedDTO;
		}

		public void messageArrived(Message<BoardDTO> message) {
			BoardDTO dto = message.getPayload();
			
			if (message instanceof BoardCreatedMessage) {
				addNewBoard(dto);	
			} else if (message instanceof BoardDeletedMessage) {
				removeBoard(dto);
			} else if (message instanceof BoardEditedMessage) {
				editBoard(dto);
			}
			
			
		}

		private void editBoard(BoardDTO dto) {
			int toEdit = idOfBoard(dto);
			boards.get(toEdit).setName(dto.getName());
			setItemText(toEdit, dto.getName());
			onChange();
		}

		private void removeBoard(BoardDTO dto) {
			int toRemove = idOfBoard(dto);
			boards.remove(toRemove);
			removeItem(toRemove);
			if (boards.size() > 0) {
				setSelectedIndex(0);
				onChange();
			} else {
				if (projectToBoardAdding != null) {
					projectsToBoardAddingContainer.remove(projectToBoardAdding);	
				}
				onChange();
			}
		}

		private int idOfBoard(BoardDTO dto) {
			int idOfBoard = -1;
			for (int i = 0; i < boards.size(); i++) {
				if (boards.get(i).getId() == dto.getId()) {
					idOfBoard = i;
					break;
				}
			}
			if (idOfBoard == -1) {
				throw new IllegalStateException("Did not find the board which has been deleted");
			}
			return idOfBoard;
		}

		private void addNewBoard(BoardDTO dto) {
			boards.add(dto);
			addItem(dto.getName());
			setSelectedIndex(boards.size()-1);
			onChange();
		}

		public void activated() {
			if (!MessageBus.listens(BoardCreatedMessage.class, this)) {
				MessageBus.registerListener(BoardCreatedMessage.class, this);	
			}
			
			if (!MessageBus.listens(BoardDeletedMessage.class, this)) {
				MessageBus.registerListener(BoardDeletedMessage.class, this);	
			}
			
			if (!MessageBus.listens(BoardEditedMessage.class, this)) {
				MessageBus.registerListener(BoardEditedMessage.class, this);	
			}
		}

		public void deactivated() {
			MessageBus.unregisterListener(BoardCreatedMessage.class, this);
			MessageBus.unregisterListener(BoardDeletedMessage.class, this);
			MessageBus.unregisterListener(BoardEditedMessage.class, this);
			new ModulesLyfecycleListenerHandler(Modules.CONFIGURE, this);
		}
	}
	
	public void editBoard(BoardDTO board, List<ProjectDTO> projects) {
		if (projectToBoardAdding != null) {
			projectsToBoardAddingContainer.remove(projectToBoardAdding);	
		}
		
		projectToBoardAdding = new ProjectsToBoardAdding(board, projects);
		projectsToBoardAddingContainer.add(projectToBoardAdding);
		if (boardsList.getSelectedIndex() != lastSelectedIndex) {
			boardsList.setSelectedIndex(lastSelectedIndex);
			boardsList.onChange();	
		}
	}

}