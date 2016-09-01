package com.googlecode.kanbanik.client.components.board;

import com.allen_sauer.gwt.dnd.client.DragController;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.kanbanik.client.api.Dtos;
import com.googlecode.kanbanik.client.components.filter.BoardsFilter;
import com.googlecode.kanbanik.client.modules.editworkflow.workflow.WipLimitGuard;

import java.util.List;

public interface TaskContainer {


    FlowPanel asFlowPanel();

    List<Dtos.TaskDto> getTasks();

    int getTaskIndex(Dtos.TaskDto dto);

    Widget asWidget();

    void removeTask(Dtos.TaskDto taskDto, boolean partOfMove);

    boolean containsTask(Dtos.TaskDto taskDto);


    HasVisibility add(Dtos.TaskDto taskDto, BoardsFilter filter, DragController dragController);

    void setWipLimitGuard(WipLimitGuard wipLimitGuard);

    void setWipCorrect(boolean wipCorrect);
}
