package com.googlecode.kanbanik.client.modules.editworkflow.workflow;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class WipLimitGuard {
    private Map<String, ExtendedWorkflowitem> idToWorkflowitem = new HashMap<>();

    public void taskCountChanged(String id, int size) {
        int currentNumOfTasks = idToWorkflowitem.get(id).getNumOfTasks();
        if (currentNumOfTasks == size) {
            return;
        }

        List<ExtendedWorkflowitem> path = doIncDec(id, size - currentNumOfTasks);

        // going from the highest parent to the lowest
        for (ExtendedWorkflowitem current : path) {
            if (!current.hasWipLimitSet()) {
                 continue;
            }

            if (current.isUnderWip() != current.isCurrentlyShowAsUnderWipLimit()) {
                switchColorsTo(current.isUnderWip(), current);
            }

            if (!current.isUnderWip()) {
                // this one is red, all it's children painted to red, the children can not override this setting, exiting loop
                break;
            }
        }
    }

    private void switchColorsTo(boolean underWip, ExtendedWorkflowitem from) {
        if (underWip && !from.isUnderWip()) {
            // try to paint green but the wip limit is owerrun here, ignore the whole branch
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

    private List<ExtendedWorkflowitem> doIncDec(String id, int diff) {
        List<ExtendedWorkflowitem> path = new ArrayList<>();
        ExtendedWorkflowitem current = idToWorkflowitem.get(id);
        do {
            current.add(diff);

            path.add(current);
            current = current.getParent();
        } while (current != null);

        // the first will be the parent-most workflowitem
        Collections.reverse(path);
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

