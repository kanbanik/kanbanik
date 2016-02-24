package com.googlecode.kanbanik.client.modules.editworkflow.boards;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.kanbanik.client.KanbanikResources;
import com.googlecode.kanbanik.client.Modules;
import com.googlecode.kanbanik.client.api.DtoFactory;
import com.googlecode.kanbanik.client.api.Dtos;
import com.googlecode.kanbanik.client.components.ListBoxWithAddEditDelete;
import com.googlecode.kanbanik.client.messaging.Message;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.messaging.MessageListener;
import com.googlecode.kanbanik.client.messaging.messages.board.BoardChangedMessage;
import com.googlecode.kanbanik.client.messaging.messages.board.BoardCreatedMessage;
import com.googlecode.kanbanik.client.messaging.messages.board.BoardDeletedMessage;
import com.googlecode.kanbanik.client.messaging.messages.board.BoardEditedMessage;
import com.googlecode.kanbanik.client.messaging.messages.board.BoardRefreshedMessage;
import com.googlecode.kanbanik.client.modules.ConfigureWorkflowModule;
import com.googlecode.kanbanik.client.modules.editworkflow.classofservice.ClassOfServicesListManager;
import com.googlecode.kanbanik.client.modules.editworkflow.projects.ProjectCreatingComponent;
import com.googlecode.kanbanik.client.modules.editworkflow.projects.ProjectsToBoardAdding;
import com.googlecode.kanbanik.client.modules.lifecyclelisteners.ModulesLifecycleListener;
import com.googlecode.kanbanik.client.modules.lifecyclelisteners.ModulesLyfecycleListenerHandler;

public class BoardsBox extends Composite {

	@UiField(provided=true)
	ListBoxWithAddEditDelete<Dtos.BoardWithProjectsDto> boardsList;

	@UiField
	PushButton addProjectButton;
	
	@UiField
	SimplePanel projectsToBoardAddingContainer;
	
	@UiField(provided=true)
	ListBoxWithAddEditDelete<Dtos.ClassOfServiceDto> classOfServiceList;
	
	private ProjectsToBoardAdding projectToBoardAdding;
	
	private ClassOfServicesListManager classOfServicesListManager = new ClassOfServicesListManager();
	
	interface MyUiBinder extends UiBinder<Widget, BoardsBox> {}
	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	
	public BoardsBox(final ConfigureWorkflowModule configureWorkflowModule) {
		
		class IdProvider implements ListBoxWithAddEditDelete.IdProvider<Dtos.BoardWithProjectsDto> {

			@Override
			public String getId(Dtos.BoardWithProjectsDto dto) {
				return dto.getBoard().getId();
			}
			
		}
		
		class LabelProvider implements ListBoxWithAddEditDelete.LabelProvider<Dtos.BoardWithProjectsDto> {

			@Override
			public String getLabel(Dtos.BoardWithProjectsDto dto) {
				return dto.getBoard().getName();
			}
			
		}
		
		class Refresher implements ListBoxWithAddEditDelete.Refresher<Dtos.BoardWithProjectsDto> {

			@Override
			public void refrehs(List<Dtos.BoardWithProjectsDto> items,
					Dtos.BoardWithProjectsDto newItem, int index) {
				items.get(index).setBoard(newItem.getBoard());
			}
			
		}
		
		class OnChangeListener implements ListBoxWithAddEditDelete.OnChangeListener<Dtos.BoardWithProjectsDto> {

			@Override
			public void onChanged(List<Dtos.BoardWithProjectsDto> items,
					Dtos.BoardWithProjectsDto selectedItem) {
				if (items.isEmpty()) {
					if (projectToBoardAdding != null) {
						projectToBoardAdding.disable();
					}
				} else {
					 configureWorkflowModule.selectedBoardChanged(selectedItem);
				}
				
				classOfServicesListManager.selectedBoardChanged(selectedItem == null ? null : selectedItem.getBoard());
			}
			
		}
		
		boardsList = new ListBoxWithAddEditDelete<Dtos.BoardWithProjectsDto>(
				"Boards",
				new IdProvider(), 
				new LabelProvider(),
				new BoardCreatingComponent(),
				new BoardEditingComponent(),
				new BoardDeletingComponent(),
				new Refresher());
		
		classOfServiceList = classOfServicesListManager.create();
		
		boardsList.setOnChangeListener(new OnChangeListener());
		
		new MessageListeners();
		
		initWidget(uiBinder.createAndBindUi(this));
		
		addProjectButton.setEnabled(true);
		addProjectButton.getUpFace().setImage(new Image(KanbanikResources.INSTANCE.addButtonImage()));
		new ProjectCreatingComponent(addProjectButton);
		
	}
	
	public void selectedBoardChanged(Dtos.BoardDto board) {
		classOfServicesListManager.selectedBoardChanged(board);
	}
	
	public void setBoards(List<Dtos.BoardWithProjectsDto> allBoards) {
		boardsList.setContent(allBoards);
	}
	
	class MessageListeners implements MessageListener<Dtos.BoardDto>, ModulesLifecycleListener {

		public MessageListeners() {
			new ModulesLyfecycleListenerHandler(Modules.CONFIGURE, this);
			MessageBus.registerListener(BoardCreatedMessage.class, this);
			MessageBus.registerListener(BoardChangedMessage.class, this);
		}

		public void messageArrived(Message<Dtos.BoardDto> message) {
			Dtos.BoardDto dto = message.getPayload();
            Dtos.BoardWithProjectsDto boardWithProjectsDto = DtoFactory.boardWithProjectsDto();
            boardWithProjectsDto.setBoard(dto);

			if (message instanceof BoardCreatedMessage) {
				boardsList.addNewItem(boardWithProjectsDto);
			} else if (message instanceof BoardDeletedMessage) {
				boardsList.removeItem(boardWithProjectsDto);
			} else if (message instanceof BoardEditedMessage) {
				boardsList.editItem(boardWithProjectsDto);
			} else if (message instanceof BoardRefreshedMessage) {
				boardsList.refresh(boardWithProjectsDto);
			} else if (message instanceof BoardChangedMessage) {
				boardsList.refresh(boardWithProjectsDto);
			}
			
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
			
			if (!MessageBus.listens(BoardRefreshedMessage.class, this)) {
				MessageBus.registerListener(BoardRefreshedMessage.class, this);	
			}
			
			if (!MessageBus.listens(BoardChangedMessage.class, this)) {
				MessageBus.registerListener(BoardChangedMessage.class, this);	
			} 	
			
		}

		public void deactivated() {
			MessageBus.unregisterListener(BoardCreatedMessage.class, this);
			MessageBus.unregisterListener(BoardDeletedMessage.class, this);
			MessageBus.unregisterListener(BoardEditedMessage.class, this);
			MessageBus.unregisterListener(BoardRefreshedMessage.class, this);
			MessageBus.unregisterListener(BoardChangedMessage.class, this);
            new ModulesLyfecycleListenerHandler(Modules.CONFIGURE, this);
		}

	}
	
	public void editBoard(Dtos.BoardWithProjectsDto boardWithProjects, List<Dtos.ProjectDto> allProjects) {
		if (projectToBoardAdding != null) {
			projectsToBoardAddingContainer.remove(projectToBoardAdding);	
		}
		
		projectToBoardAdding = new ProjectsToBoardAdding(boardWithProjects, allProjects);
		projectsToBoardAddingContainer.add(projectToBoardAdding);
	}

}

