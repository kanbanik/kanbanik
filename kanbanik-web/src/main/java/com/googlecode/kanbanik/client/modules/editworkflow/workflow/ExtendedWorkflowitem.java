package com.googlecode.kanbanik.client.modules.editworkflow.workflow;

import com.googlecode.kanbanik.client.api.Dtos;
import com.googlecode.kanbanik.client.components.board.TaskContainer;

import java.util.List;

public class ExtendedWorkflowitem {

    private Dtos.WorkflowitemDto current;

    private ExtendedWorkflowitem parent;

    private List<ExtendedWorkflowitem> children;

    private TaskContainer taskContainer;

    private int numOfTasks;

    private boolean currentlyShowAsUnderWipLimit = true;

    public ExtendedWorkflowitem(Dtos.WorkflowitemDto current, ExtendedWorkflowitem parent) {
        this.current = current;
        this.parent = parent;
    }

    // if added a negative number, than it is decrementation
    public void add(int diff) {
        numOfTasks += diff;
    }

    public boolean isUnderWip() {
        return current.getWipLimit() == -1 || current.getWipLimit() >= numOfTasks;
    }

    public boolean isCurrentlyShowAsUnderWipLimit() {
        return currentlyShowAsUnderWipLimit;
    }

    public void switchToCorrectColor(boolean wipCorrect) {
        if (taskContainer != null) {
            taskContainer.setWipCorrect(wipCorrect);
        }

        currentlyShowAsUnderWipLimit = wipCorrect;
    }

    public ExtendedWorkflowitem getParent() {
        return parent;
    }

    public List<ExtendedWorkflowitem> getChildren() {
        return children;
    }

    public void setChildren(List<ExtendedWorkflowitem> children) {
        this.children = children;
    }

    public void setTaskContainer(TaskContainer taskContainer) {
        this.taskContainer = taskContainer;
    }

    public int getNumOfTasks() {
        return numOfTasks;
    }

    public Dtos.WorkflowitemDto getCurrent() {
        return current;
    }
}
