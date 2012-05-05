package com.googlecode.kanbanik.client.modules.editworkflow.projects;

import java.util.ArrayList;
import java.util.List;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.drop.DropController;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.kanbanik.client.KanbanikAsyncCallback;
import com.googlecode.kanbanik.client.Modules;
import com.googlecode.kanbanik.client.ServerCommandInvokerManager;
import com.googlecode.kanbanik.client.messaging.Message;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.messaging.MessageListener;
import com.googlecode.kanbanik.client.modules.editworkflow.workflow.ProjectDeletedMessage;
import com.googlecode.kanbanik.client.modules.lifecyclelisteners.ModulesLifecycleListener;
import com.googlecode.kanbanik.client.modules.lifecyclelisteners.ModulesLyfecycleListenerHandler;
import com.googlecode.kanbanik.dto.BoardWithProjectsDto;
import com.googlecode.kanbanik.dto.ProjectDto;
import com.googlecode.kanbanik.dto.shell.SimpleParams;
import com.googlecode.kanbanik.dto.shell.VoidParams;
import com.googlecode.kanbanik.shared.ServerCommand;

public class ProjectsToBoardAdding extends Composite implements ModulesLifecycleListener {

	private static final String ON_BOARD = "RIGHT";

	private static final String TO_BE_ADDED = "LEFT";

	private BoardWithProjectsDto boardWithProjects;

	private List<ProjectDto> projects;

	@UiField
	FlowPanel toBeAdded;

	@UiField
	FlowPanel projectsOfBoard;

	// just because PickupDragController accepts only absolute panels
	@UiField
	AbsolutePanel panelWithDraggablePanels;

	private PickupDragController dragController;

	private ProjectChangedListener projectChangedListener = new ProjectChangedListener();
	
	interface MyUiBinder extends UiBinder<Widget, ProjectsToBoardAdding> {}
	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	
	public ProjectsToBoardAdding(BoardWithProjectsDto boardWithProjects, List<ProjectDto> allProjects) {
		super();
		this.boardWithProjects = boardWithProjects;
		this.projects = allProjects;

		new ModulesLyfecycleListenerHandler(Modules.CONFIGURE, this);
		
		initWidget(uiBinder.createAndBindUi(this));
		
		dragController = new PickupDragController(panelWithDraggablePanels, false);
		init();
		
		MessageBus.registerListener(ProjectAddedMessage.class, projectChangedListener);
		MessageBus.registerListener(ProjectDeletedMessage.class, projectChangedListener);
	}

	private void init() {
		projectsOfBoard.add(new Label(""));
		toBeAdded.add(new Label(""));
		DropController toBeAddedDropController = new ProjectMovedDropController(toBeAdded, TO_BE_ADDED, new LeftListener());
		dragController.registerDropController(toBeAddedDropController);
		DropController projectsOfBoardDropController = new ProjectMovedDropController(projectsOfBoard, ON_BOARD, new RightListener());
		dragController.registerDropController(projectsOfBoardDropController);


		for (ProjectDto project : projects) {
			if (contains(boardWithProjects.getProjectsOnBoard(), project)) {
				continue;
			}

			addProjectToPanel(toBeAdded, TO_BE_ADDED, project);
		}

		for (ProjectDto project : boardWithProjects.getProjectsOnBoard()) {
			addProjectToPanel(projectsOfBoard, ON_BOARD, project);
		}
	}

	private void addProjectToPanel(Panel panel, String position, ProjectDto project) {
		ProjectWidget projectWidget = new ProjectWidget(position, project);
		panel.add(projectWidget);
		dragController.makeDraggable(projectWidget);
	}

	private boolean contains(List<ProjectDto> projectsOnBoard, ProjectDto project) {
		for (ProjectDto onBoard : projectsOnBoard) {
			if (onBoard.equals(project)) { 
				return true;
			}
		}

		return false;
	}

	private void executeCommand(ServerCommand command, List<Widget> widgets) {
		List<ProjectDto> dtos = extractDTOs(widgets);
		final BoardWithProjectsDto toStore = new BoardWithProjectsDto(boardWithProjects.getBoard());
		toStore.setProjects(dtos);
		
		ServerCommandInvokerManager.getInvoker().<SimpleParams<BoardWithProjectsDto>, VoidParams> invokeCommand(
				command,
				new SimpleParams<BoardWithProjectsDto>(toStore),
				new KanbanikAsyncCallback<VoidParams>() {

					@Override
					public void success(VoidParams result) {
					}
				});
	}

	private List<ProjectDto> extractDTOs(List<Widget> widgets) {
		List<ProjectDto> dtos = new ArrayList<ProjectDto>();
		for (Widget widget : widgets) {
			if (widget instanceof ProjectWidget) {
				dtos.add(((ProjectWidget) widget).getDto());
			}
		}

		return dtos;
	}

	class LeftListener implements WidgetsDropListener {
		public void dropped(List<Widget> widgets) {
			executeCommand(ServerCommand.REMOVE_PROJECTS_FROM_BOARD, widgets);
		}
	}

	class RightListener implements WidgetsDropListener {
		public void dropped(List<Widget> widgets) {
			executeCommand(ServerCommand.ADD_PROJECTS_TO_BOARD, widgets);
		}
	}
	
	class ProjectChangedListener implements MessageListener<ProjectDto> {

		public void messageArrived(Message<ProjectDto> message) {
			if (message instanceof ProjectAddedMessage) {
				addProjectToPanel(toBeAdded, TO_BE_ADDED, message.getPayload());	
			} else if (message instanceof ProjectDeletedMessage) {
				removedProjectFromPanel(message.getPayload());
			}
		}

		private void removedProjectFromPanel(ProjectDto payload) {
			if (!removeIfPresent(toBeAdded, payload)) {
				removeIfPresent(projectsOfBoard, payload);
			}
		}

		private boolean removeIfPresent(FlowPanel panel, ProjectDto payload) {
			int toDelete = -1;
			for (int i = 0; i < panel.getWidgetCount(); i++) {
				if (panel.getWidget(i) instanceof ProjectWidget) {
					ProjectWidget widget = (ProjectWidget) panel.getWidget(i);
					if (widget.getDto().getId() == payload.getId()) {
						toDelete = i;
						break;
					}
				}
			}
			if (toDelete != -1) {
				return panel.remove(toDelete);
			}
			
			return false;
		}
		
	}
	
	public void activated() {
	}

	public void deactivated() {
		MessageBus.unregisterListener(ProjectAddedMessage.class, projectChangedListener);
		MessageBus.unregisterListener(ProjectDeletedMessage.class, projectChangedListener);
	}
}
