package com.googlecode.kanbanik.client.modules.editworkflow.workflow;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.kanbanik.client.BoardStyle;
import com.googlecode.kanbanik.client.KanbanikResources;
import com.googlecode.kanbanik.client.api.Dtos;
import com.googlecode.kanbanik.client.components.board.TaskContainer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public abstract class BoardGuiBuilder {
	
	private static final BoardStyle style = KanbanikResources.INSTANCE.boardStyle();

    class WipLimitGuard {

        private Map<String, ExtendedWorkflowitem> idToWorkflowitem = new HashMap<String, ExtendedWorkflowitem>();

        public void taskAdded(String id) {
            doTaskChenged(id, true);
        }

        public void taskRemoved(String id) {
            doTaskChenged(id, false);
        }

        private void doTaskChenged(String id, boolean inc) {
            Stack<ExtendedWorkflowitem> path = doIncDec(id, inc);

            for (ExtendedWorkflowitem current : path) {
                // something has been changed
                if (current.isUnderWip() != current.isCurrentlyShowAsUnderWipLimit()) {
                    switchColorsTo(current.isUnderWip(), current);
                }
            }
        }

        private void switchColorsTo(boolean underWip, ExtendedWorkflowitem from) {
            from.switchToCorrectColor(underWip);

            for (ExtendedWorkflowitem child : from.children) {
                child.switchToCorrectColor(underWip);
                switchColorsTo(underWip, child);
            }
        }

        private Stack<ExtendedWorkflowitem> doIncDec(String id, boolean inc) {
            Stack<ExtendedWorkflowitem> path = new Stack<ExtendedWorkflowitem>();
            ExtendedWorkflowitem current = idToWorkflowitem.get(id);
            do {
                if (inc) {
                    current.inc();
                } else {
                    current.dec();
                }

                path.push(current);
                current = current.parent;
            } while (current != null);

            return path;
        }
    }

    class ExtendedWorkflowitem {

        private Dtos.WorkflowitemDto current;

        private ExtendedWorkflowitem parent;

        private List<ExtendedWorkflowitem> children;

        private TaskContainer taskContainer;

        private int numOfTasks;

        private boolean currentlyShowAsUnderWipLimit = true;

        public void inc() {
            numOfTasks++;
        }

        public boolean isUnderWip() {
            return current.getWipLimit() != -1 && current.getWipLimit() <= numOfTasks;
        }

        public boolean isCurrentlyShowAsUnderWipLimit() {
            return currentlyShowAsUnderWipLimit;
        }

        public void switchToCorrectColor(boolean wipCorrect) {
            taskContainer.setWipCorrect(wipCorrect);
            currentlyShowAsUnderWipLimit = wipCorrect;
        }

        public void dec() {
            numOfTasks--;
        }
    }

    private WipLimitGuard wipLimitGuard = new WipLimitGuard();

	public BoardGuiBuilder() {
		style.ensureInjected();
	}

	public void buildBoard(
            Dtos.BoardDto realBoard, // the fully built board containing the workflow etc
			Dtos.WorkflowDto workflow,
			Dtos.ProjectDto project,
			FlexTable table,
			PickupDragController dragController, 
			int row, 
			int column) {
		
		if (workflow == null) {
			return;
		}
		
		Dtos.WorkflowDto currentDto = workflow;
		
		for (Dtos.WorkflowitemDto currentItem : currentDto.getWorkflowitems()) {
			if (currentItem.getNestedWorkflow().getWorkflowitems().size() > 0) {
				FlexTable childTable = new FlexTable();
				
				setupBoard(childTable, workflow.getBoard());
				
				Widget workflowitemPlace = createWorkflowitemPlace(dragController, currentItem, project, childTable, workflow.getBoard());
				workflowitemPlace.addStyleName(style.board());
				table.setWidget(row, column, workflowitemPlace);
				buildBoard(realBoard, currentItem.getNestedWorkflow(), project, childTable, dragController, 0, 0);
			} else {
				Widget taskContainer = createWorkflowitemPlaceContentWidget(dragController, currentItem, project, realBoard);
				Widget workflowitemPlace = createWorkflowitemPlace(dragController, currentItem, project, taskContainer, workflow.getBoard());
				workflowitemPlace.addStyleName(style.board());
				table.setWidget(row, column, workflowitemPlace);
				setupBoard(table, workflow.getBoard());
			}

			if (currentItem.getItemType().equals(Dtos.ItemType.HORIZONTAL.getType())) {
				column++;
			} else if (currentItem.getItemType().equals(Dtos.ItemType.VERTICAL.getType())) {
				row++;
			} else {
				throw new IllegalStateException("Unsupported item type: '"
						+ currentItem.getItemType() + "'");
			}
		}

	}

	
	private void setupBoard(FlexTable table, Dtos.BoardDto board) {
        Dtos.WorkflowVerticalSizing sizing = Dtos.WorkflowVerticalSizing.from(board.getWorkflowVerticalSizing());
		if (sizing == Dtos.WorkflowVerticalSizing.BALANCED) {
			table.addStyleName("balanced-table");
		} else if (sizing == Dtos.WorkflowVerticalSizing.MIN_POSSIBLE) {
			table.addStyleName("not-balanced-table");
		}
		
		table.getElement().getStyle().setBackgroundColor("#FBFBFB");
		table.setWidth("100%");
	}
	
	protected abstract Widget createWorkflowitemPlaceContentWidget(PickupDragController dragController, Dtos.WorkflowitemDto currentItem, Dtos.ProjectDto project, Dtos.BoardDto board);
	
	protected abstract Widget createWorkflowitemPlace(PickupDragController dragController, Dtos.WorkflowitemDto currentItem, Dtos.ProjectDto project, Widget childTable, Dtos.BoardDto board);
}

