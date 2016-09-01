package com.googlecode.kanbanik.client.components.board;

import com.allen_sauer.gwt.dnd.client.DragController;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.view.client.ListDataProvider;
import com.googlecode.kanbanik.client.api.Dtos;
import com.googlecode.kanbanik.client.components.filter.BoardsFilter;
import com.googlecode.kanbanik.client.modules.editworkflow.workflow.WipLimitGuard;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TableTaskContainer extends Composite implements TaskContainer {

    @UiField
    FlowPanel contentPanel;

    TextBox searchBox;

    private List<Dtos.TaskDto> realList = new ArrayList<>();

    private CellTable<Dtos.TaskDto> table = new CellTable<>();

    private ListDataProvider<Dtos.TaskDto> dataProvider;

    interface MyUiBinder extends UiBinder<FlowPanel, TableTaskContainer> {
    }

    private static TableTaskContainer.MyUiBinder uiBinder = GWT.create(TableTaskContainer.MyUiBinder.class);

    public TableTaskContainer(Dtos.BoardDto board, Dtos.WorkflowitemDto currentItem) {
        Column<Dtos.TaskDto, String> nameColumn = new Column<Dtos.TaskDto, String>(new TableTaskCell()) {
            @Override
            public String getValue(Dtos.TaskDto taskDto) {
                return taskDto.getName();
            }
        };

        nameColumn.setFieldUpdater(new FieldUpdater<Dtos.TaskDto, String>() {
            @Override
            public void update(int index, Dtos.TaskDto object, String value) {
                String x = "";
            }
        });

        nameColumn.setSortable(true);
        table.addColumn(nameColumn, "Name");

        dataProvider = new ListDataProvider<>();
        dataProvider.addDataDisplay(table);

        ColumnSortEvent.ListHandler<Dtos.TaskDto> columnSortHandler = new ColumnSortEvent.ListHandler<>(
                dataProvider.getList());
        columnSortHandler.setComparator(nameColumn,
                new Comparator<Dtos.TaskDto>() {
                    public int compare(Dtos.TaskDto o1, Dtos.TaskDto o2) {
                        if (o1 == o2) {
                            return 0;
                        }

                        // Compare the name columns.
                        if (o1 != null) {
                            return (o2 != null) ? o1.getName().compareTo(o2.getName()) : 1;
                        }
                        return -1;
                    }
                });
        table.addColumnSortHandler(columnSortHandler);
        table.getColumnSortList().push(nameColumn);
        table.setWidth("500px");

        initWidget(uiBinder.createAndBindUi(this));

        searchBox = new TextBox();
        searchBox.setWidth("492px");
        searchBox.getElement().setPropertyString("placeholder", "search...");
        searchBox.addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                updateFilter();
            }
        });


        contentPanel.add(searchBox);
        contentPanel.add(table);
    }

    private void updateFilter() {
        dataProvider.getList().clear();
        String searchText = searchBox.getText() != null ? searchBox.getText() : "";
        for (Dtos.TaskDto task : realList) {
            if (task.getName().startsWith(searchText)) {
                dataProvider.getList().add(task);
            }
        }
    }

    @Override
    public FlowPanel asFlowPanel() {
        return contentPanel;
    }

    @Override
    public List<Dtos.TaskDto> getTasks() {
        // todo return a copy of the list instead
        return realList;
    }

    @Override
    public int getTaskIndex(Dtos.TaskDto dto) {
        return 0;
    }

    @Override
    public void removeTask(Dtos.TaskDto taskDto, boolean partOfMove) {

    }

    @Override
    public boolean containsTask(Dtos.TaskDto taskDto) {
        return false;
    }

    @Override
    public HasVisibility add(Dtos.TaskDto taskDto, BoardsFilter filter, DragController dragController) {

        dataProvider.getList().add(taskDto);
        realList.add(taskDto);

        return new HasVisibility() {
            @Override
            public boolean isVisible() {
                return true;
            }

            @Override
            public void setVisible(boolean visible) {

            }
        };
    }

    @Override
    public void setWipLimitGuard(WipLimitGuard wipLimitGuard) {

    }

    @Override
    public void setWipCorrect(boolean wipCorrect) {

    }
}
