package com.googlecode.kanbanik.client.modules.editworkflow.workflow;


import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class WipLimitGuard {
    private Map<String, ExtendedWorkflowitem> idToWorkflowitem = new HashMap<String, ExtendedWorkflowitem>();

    public void taskCountChanged(String id, int size) {
        int currentNumOfTasks = idToWorkflowitem.get(id).getNumOfTasks();
        if (currentNumOfTasks == size) {
            return;
        }

        Stack<ExtendedWorkflowitem> path = doIncDec(id, size - currentNumOfTasks);

        for (ExtendedWorkflowitem current : path) {
            // something has been changed
            if (current.isUnderWip() != current.isCurrentlyShowAsUnderWipLimit()) {
                switchColorsTo(current.isUnderWip(), current);
            }
        }
    }

    private void switchColorsTo(boolean underWip, ExtendedWorkflowitem from) {
        if (underWip && !from.isUnderWip()) {
            // ignore this branch - here it should stay red
            return;
        }

        from.switchToCorrectColor(underWip);

        for (ExtendedWorkflowitem child : from.getChildren()) {
            if (!(underWip && !child.isUnderWip())) {
                child.switchToCorrectColor(underWip);
                switchColorsTo(underWip, child);
            }

        }
    }

    private Stack<ExtendedWorkflowitem> doIncDec(String id, int diff) {
        Stack<ExtendedWorkflowitem> path = new Stack<ExtendedWorkflowitem>();
        ExtendedWorkflowitem current = idToWorkflowitem.get(id);
        do {
            current.add(diff);

            path.push(current);
            current = current.getParent();
        } while (current != null);

        return path;
    }

    public void addItem(String id, ExtendedWorkflowitem item) {
        idToWorkflowitem.put(id, item);
    }

    public ExtendedWorkflowitem getItem(String id) {
        if (!idToWorkflowitem.containsKey(id)) {
            return null;

        }
        return idToWorkflowitem.get(id);
    }
}

