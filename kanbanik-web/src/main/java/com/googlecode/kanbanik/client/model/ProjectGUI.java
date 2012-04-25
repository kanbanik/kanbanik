package com.googlecode.kanbanik.client.model;

import java.util.HashMap;
import java.util.Map;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.drop.DropController;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.kanbanik.client.KanbanikResources;
import com.googlecode.kanbanik.client.components.task.TaskAddingComponent;
import com.googlecode.kanbanik.client.components.task.TaskCreationSavedMessage;
import com.googlecode.kanbanik.client.components.task.TaskDeletionSavedMessage;
import com.googlecode.kanbanik.client.components.task.TaskMovingDropController;
import com.googlecode.kanbanik.client.messaging.Message;
import com.googlecode.kanbanik.client.messaging.MessageBus;
import com.googlecode.kanbanik.client.messaging.MessageListener;
import com.googlecode.kanbanik.shared.ProjectDTO;
import com.googlecode.kanbanik.shared.TaskDTO;

public class ProjectGUI extends HorizontalPanel implements MessageListener<TaskDTO> {

	// TODO add reference to board so it will retrieve workflow (place descriptors) from it
	private Map<WorkflowItemGUI, VerticalPanel> taskHolders = new HashMap<WorkflowItemGUI, VerticalPanel>();

	// just because PickupDragController accepts only absolute panels
	private AbsolutePanel panelWithDraggablePanels = new AbsolutePanel();

	private PickupDragController dragController = new PickupDragController(panelWithDraggablePanels, false);

	private int id;

	private WorkflowGUI workflow;

	private ProjectDTO projectDTO;

	public ProjectGUI(ProjectDTO project, WorkflowGUI workflow) {
		super();

		this.projectDTO = project;

		setStyleName("project-gui");

		MessageBus.registerListener(TaskCreationSavedMessage.class, this);
		MessageBus.registerListener(TaskDeletionSavedMessage.class, this);

		this.id = project.getId();
		this.workflow = workflow;

		HorizontalPanel horizontalPanelWithDraggables = new HorizontalPanel();
		horizontalPanelWithDraggables.setStyleName("horizontal-panel-with-draggables");
		panelWithDraggablePanels.add(horizontalPanelWithDraggables);

		addProjectHeader(project.getName());
		add(panelWithDraggablePanels);
		for (WorkflowItemGUI workflowItem : workflow.getWorkflowItems()) {
			VerticalPanel taskHolder = new HackLabelPresentEnsuringPanel();
			taskHolder.setStyleName(workflowItem.getCssName());
			taskHolders.put(workflowItem, taskHolder);

			DropController dropController = new TaskMovingDropController(taskHolder, workflowItem);
			dragController.registerDropController(dropController);
			horizontalPanelWithDraggables.add(taskHolder);
		}
	}

	// this is a hack - the empty panel is not shown, so at least something has to be there
	class HackLabelPresentEnsuringPanel extends VerticalPanel {

		class MarkedHackLable extends Label {

			public MarkedHackLable() {
				super();
				setHeight("100px");
			}
			
		};
		
		private Label hackLabel;
		
		int numOfFields = 0;
		
		public HackLabelPresentEnsuringPanel() {
			super();
			hackLabel = new MarkedHackLable();
			add(hackLabel);
		}

		@Override
		public void insert(Widget widget, int beforeIndex) {
			super.insert(widget, beforeIndex);

			somethingAdded(widget);			
		}


		@Override
		public void add(Widget widget) {
			super.add(widget);

			somethingAdded(widget);
		}

		private void somethingAdded(Widget widget) {
			if (! (widget instanceof MarkedHackLable)) {
				if (widget instanceof TaskGui) {
					numOfFields++;	
					super.remove(hackLabel);
				}
				
			}

		}

		@Override
		public boolean remove(Widget widget) {
			boolean removed = super.remove(widget);

			if (widget instanceof MarkedHackLable) {
				return removed;
			}
			
			if (widget instanceof TaskGui) {
				numOfFields --;
				if (removed) {
					if (numOfFields == 0) {
						hackLabel = new MarkedHackLable();
						add(hackLabel);
					}
				}	
			}

			return removed;
		}
	}

	private void addProjectHeader(String name) {
		Panel projectHeaderPanel = new VerticalPanel();
		projectHeaderPanel.setStyleName("project-name");
		Label projectNameLabel = new Label(name);
		projectNameLabel.setStyleName("project-name-label");
		projectHeaderPanel.add(projectNameLabel);
		
		PushButton addButton = new PushButton();
		addButton.getUpFace().setImage(new Image(KanbanikResources.INSTANCE.addButtonImage()));
		addButton.setTitle("Add new task");
		addButton.setStyleName("centered-label");
		
		new TaskAddingComponent(projectDTO, workflow.getInputQueue().getDto(), addButton);

		projectHeaderPanel.add(addButton);
		add(projectHeaderPanel);
	}

	public void addTask(TaskGui task, WorkflowItemGUI workflowItem) {
//		task.setProject(this);
//		task.setWorkflowItem(workflowItem);
		taskHolders.get(workflowItem).add(task);
		setSizes();
		dragController.makeDraggable(task, task.getHeader());
	}
	
	private void deleteTask(TaskDTO task) {
		for (VerticalPanel taskHolder : taskHolders.values()) {
			for (int i = 0; i < taskHolder.getWidgetCount(); i++) {
//				if ((taskHolder.getWidget(i) instanceof TaskGui) && ((TaskGui)taskHolder.getWidget(i)).getDto().getId() == task.getId()) {
//					taskHolder.remove(taskHolder.getWidget(i));
//					break;
//				}
			}
		}
		
		setSizes();
	}

	private void setSizes() {
		for (VerticalPanel panel : taskHolders.values()) {
			panel.setHeight("100%");
		}
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void messageArrived(Message<TaskDTO> message) {
	/*	if (!message.getPayload().getProject().equals(projectDTO)) {
			return;
		}

		// this project but not on this board
		if (!isInThisBoard(message.getPayload())) {
			return;
		}
		
		if (message instanceof TaskCreationSavedMessage) {
//			addTask(new TaskGui(message.getPayload()), workflow.getInputQueue());
		} else if (message instanceof TaskDeletionSavedMessage) {
			deleteTask(message.getPayload());
		}
		*/
	}
	
	private boolean isInThisBoard(TaskDTO task) {
		int taskPlaceId = task.getPlace().getId();
		for (WorkflowItemGUI workflowitem : workflow.getWorkflowItems()) { 
			if (workflowitem.getDto().getId() == taskPlaceId) {
				return true;
			}
		}
		
		return false;		
	}

}
