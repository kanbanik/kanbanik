package com.googlecode.kanbanik.client.components.board;

import com.allen_sauer.gwt.dnd.client.DragController;
import com.allen_sauer.gwt.dnd.client.drop.DropController;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.AbstractCellTable;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.RowStyles;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;
import com.googlecode.kanbanik.client.api.Dtos;
import com.googlecode.kanbanik.client.components.PanelContainingDialog;
import com.googlecode.kanbanik.client.components.filter.BoardsFilter;
import com.googlecode.kanbanik.client.components.task.TaskEditingComponent;
import com.googlecode.kanbanik.client.modules.editworkflow.workflow.WipLimitGuard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TableTaskContainer extends Composite implements TaskContainer {

    @UiField
    Style style;

    public interface Style extends CssResource {
        String oddRow();

        String evenRow();
    }

    @UiField
    FlowPanel contentPanel;

    @UiField
    TextBox searchBox;

    @UiField
    PushButton optionsButton;

    @UiField
    FlowPanel taskDetailsPanel;

    // this needs to be here because some of the tasks can be hidden but still present
    private List<Dtos.TaskDto> realList = new ArrayList<>();

    private CellTable<Dtos.TaskDto> table;

    private ListDataProvider<Dtos.TaskDto> dataProvider;

    private DropController dropController;

    interface MyUiBinder extends UiBinder<FlowPanel, TableTaskContainer> {
    }

    private static TableTaskContainer.MyUiBinder uiBinder = GWT.create(TableTaskContainer.MyUiBinder.class);

    private Map<String, Column<Dtos.TaskDto, String>> tagColumns = new HashMap<>();

    public TableTaskContainer(final Dtos.BoardDto board, Dtos.WorkflowitemDto currentItem) {
        Column<Dtos.TaskDto, String> nameColumn = new Column<Dtos.TaskDto, String>(new TableTaskCell()) {
            @Override
            public String getValue(Dtos.TaskDto taskDto) {
                return taskDto.getName();
            }
        };

        nameColumn.setFieldUpdater(new FieldUpdater<Dtos.TaskDto, String>() {
            @Override
            public void update(int index, Dtos.TaskDto object, String value) {

            }
        });

        ProvidesKey<Dtos.TaskDto> keyProvider = new ProvidesKey<Dtos.TaskDto>() {
            @Override
            public Object getKey(Dtos.TaskDto item) {
                return item == null ? null : item.getId();
            }
        };
        table = new CellTable<>(keyProvider);

        final SingleSelectionModel<Dtos.TaskDto> selectionModel = new SingleSelectionModel<>(keyProvider);
        table.setSelectionModel(selectionModel);

        nameColumn.setSortable(true);
        table.getSelectionModel().addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                Dtos.TaskDto selected = selectionModel.getSelectedObject();
                if (selected == null) {
                    taskDetailsPanel.clear();
                    taskDetailsPanel.setVisible(false);
                } else {
                    TaskEditingComponent taskEditingComponent = new TaskEditingComponent(selected, null, board);
                    taskEditingComponent.initialize();
                    taskEditingComponent.setupValues();
                    taskDetailsPanel.clear();
                    taskDetailsPanel.add(taskEditingComponent.getPanel());
                    taskDetailsPanel.setVisible(true);
                }
            }
        });

        table.addColumn(nameColumn, "Name");
        table.setWidth("100%");

        table.setRowStyles(new RowStyles<Dtos.TaskDto>() {
            @Override
            public String getStyleNames(Dtos.TaskDto row, int rowIndex) {
                return rowIndex % 2 == 0 ? style.evenRow() : style.oddRow();
            }
        });

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


        initWidget(uiBinder.createAndBindUi(this));

        searchBox.getElement().setPropertyString("placeholder", "search...");
        searchBox.addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                updateFilter();
            }
        });


        contentPanel.add(searchBox);
        contentPanel.add(table);

        optionsButton.addClickHandler(new ConfigureClickHandler());
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
        List<Dtos.TaskDto> res = new ArrayList<>();
        for (Dtos.TaskDto task : realList) {
            res.add(task);
        }
        return res;
    }

    @Override
    public int getTaskIndex(Dtos.TaskDto dto) {
        return getTaskIndexFrom(dataProvider.getList(), dto);
    }

    private int getTaskIndexFrom(List<Dtos.TaskDto> list, Dtos.TaskDto dto) {
        int i = 0;
        for (Dtos.TaskDto task : list) {
            if (task.getId().equals(dto.getId())) {
                return i;
            }
            i++;
        }
        return -1;
    }

    private void removeFrom(List<Dtos.TaskDto> list, Dtos.TaskDto taskDto) {
        int taskIndex = getTaskIndexFrom(list, taskDto);
        if (taskIndex == -1) {
            return;
        }

        list.remove(taskIndex);
    }

    @Override
    public void removeTask(Dtos.TaskDto taskDto, boolean partOfMove) {
        removeFrom(realList, taskDto);
        removeFrom(dataProvider.getList(), taskDto);
    }

    @Override
    public boolean containsTask(Dtos.TaskDto taskDto) {
        return false;
    }

    @Override
    public HasVisibility add(Dtos.TaskDto taskDto, BoardsFilter filter, DragController dragController) {

        dataProvider.getList().add(taskDto);
        realList.add(taskDto);

        addLabelColumn(taskDto);

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

    private void addLabelColumn(Dtos.TaskDto taskDto) {
        // it actually works but not needed now
//        if (taskDto.getTaskTags() == null) {
//            return;
//        }
//
//        for (Dtos.TaskTag tag : taskDto.getTaskTags()) {
//            String tagName = tag.getName();
//            if (tagColumns.containsKey(tagName)) {
//                continue;
//            }
//
//            Column<Dtos.TaskDto, String> column = new TagColumn(new TextCell(), tagName);
//            tagColumns.put(tagName, column);
//            table.addColumn(column, tagName);
//        }
    }

    @Override
    public void setWipLimitGuard(WipLimitGuard wipLimitGuard) {

    }

    @Override
    public void setWipCorrect(boolean wipCorrect) {

    }

    @Override
    public void setDropController(DropController dropController) {
        this.dropController = dropController;
    }

    @Override
    public DropController getDropController() {
        return dropController;
    }

    class ConfigureClickHandler implements ClickHandler {

        @Override
        public void onClick(ClickEvent event) {
            final WorkflowitemConfigEditor configEditor = new WorkflowitemConfigEditor();
            PanelContainingDialog configEditorPopup = new PanelContainingDialog("Configure Workflowitem", configEditor, null, false, 400, -1);
            configEditorPopup.addListener(new PanelContainingDialog.PanelContainingDialolgListener() {
                @Override
                public void okClicked(PanelContainingDialog dialog) {
                    contentPanel.setWidth(configEditor.getWidth() + "px");
                }

                @Override
                public void cancelClicked(PanelContainingDialog dialog) {

                }
            });
            configEditorPopup.setupToMinSize();
            configEditorPopup.center();
        }
    }
}

class TagColumn extends Column<Dtos.TaskDto, String> {

    private String tagName;

    public TagColumn(Cell<String> cell, String tagName) {
        super(cell);
        this.tagName = tagName;
    }

    @Override
    public String getValue(Dtos.TaskDto object) {
        if (object.getTaskTags() == null) {
            return "";
        }

        for (Dtos.TaskTag tag : object.getTaskTags()) {
            if (tagName.equals(tag.getName())) {
                if (tag.getPictureUrl() != null) {
                    return tag.getPictureUrl();
                }

                return tag.getDescription() != null ? tag.getDescription() : "";
            }
        }

        return "";
    }
}


