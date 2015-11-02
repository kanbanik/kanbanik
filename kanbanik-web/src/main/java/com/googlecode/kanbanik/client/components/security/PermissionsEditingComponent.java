package com.googlecode.kanbanik.client.components.security;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.kanbanik.client.api.DtoFactory;
import com.googlecode.kanbanik.client.api.Dtos;
import com.googlecode.kanbanik.client.api.ServerCallCallback;
import com.googlecode.kanbanik.client.api.ServerCaller;
import com.googlecode.kanbanik.client.components.common.filters.CommonFilterCheckBox;
import com.googlecode.kanbanik.client.components.common.filters.PanelWithCheckboxes;
import com.googlecode.kanbanik.client.managers.UsersManager;
import com.googlecode.kanbanik.client.security.CurrentUser;
import com.googlecode.kanbanik.dto.CommandNames;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PermissionsEditingComponent extends Composite {

    @UiField
    FlowPanel mainPanel;

    @UiField
    VerticalPanel contentPanel;

    @UiField
    CheckBox editPermissions;

    private static final List<? extends GlobalPermissionEditingComponent> permissionEditors = Arrays.asList(
            new ManipulateBoardPEC(),
            new ReadBoardPEC(),

            new EditUserPermissionsPEC(),
            new EditUserDataPEC(),
            new DeleteUserPEC(),
            new CreateUserPEC(),
            new ReadUserPEC(),

            new ReadProjectPEC(),

            new MoveTaskBoardPEC(),
            new MoveTaskProjectPEC(),
            new EditTaskBoardPEC(),
            new EditTaskProjectPEC(),
            new CreateTaskBoardPEC(),
            new CreateTaskProjectPEC(),
            new DeleteTaskBoardPEC(),
            new DeleteTaskProjectPEC()

    );

    interface MyUiBinder extends UiBinder<Widget, PermissionsEditingComponent> {
    }

    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

    public PermissionsEditingComponent() {
        initWidget(uiBinder.createAndBindUi(this));

        editPermissions.setText("Edit permissions");

        editPermissions.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                contentPanel.setVisible(event.getValue());
            }
        });

        // todo only when has permission to edit permissions
        contentPanel.setVisible(editPermissions.getValue());
    }


    private boolean canEditPermissions(Dtos.UserDto userDto) {
        for (Dtos.PermissionDto permission : CurrentUser.getInstance().getUser().getPermissions()) {
            if (permission.getPermissionType().intValue() == Dtos.PermissionTypes.EditUserPermissions.getValue()) {
                return permission.getArgs().contains(userDto.getUserName()) || permission.getArgs().contains("*");
            }
        }

        return false;
    }

    private static Dtos.BoardsWithProjectsDto boardsWithProjectsDto;

    private static Dtos.ProjectsDto projectsDto;

    public void init(final List<Dtos.PermissionDto> permissions, final Dtos.UserDto userDto) {

        boolean createNew = userDto == null;
        if (!createNew && !canEditPermissions(userDto)) {
            // if new, can edit it's permissions
            // if edit, than it has to have the permission to do it
            setVisible(false);
            return;
        }

        ServerCaller.sendRequest(
                DtoFactory.getAllBoardsWithProjectsDto(true, true),
                Dtos.BoardsWithProjectsDto.class,
                new ServerCallCallback<Dtos.BoardsWithProjectsDto>() {

                    @Override
                    public void onSuccess(final Dtos.BoardsWithProjectsDto response) {
                        boardsWithProjectsDto = response;

                        Dtos.SessionDto getAllProjectsReq = DtoFactory.sessionDto();
                        getAllProjectsReq.setCommandName(CommandNames.GET_ALL_PROJECTS.name);

                        ServerCaller.<Dtos.SessionDto, Dtos.ProjectsDto>sendRequest(
                                getAllProjectsReq,
                                Dtos.ProjectsDto.class,
                                new ServerCallCallback<Dtos.ProjectsDto>() {

                                    @Override
                                    public void success(Dtos.ProjectsDto result) {
                                        projectsDto = result;
                                        initDialogAfterDataLoaded(permissions);
                                    }
                                }
                        );


                    }
                }
        );
    }

    private void initDialogAfterDataLoaded(List<Dtos.PermissionDto> permissions) {
        contentPanel.clear();

        Map<Integer, Dtos.PermissionDto> permissionDtoMap = new HashMap<>();
        for (Dtos.PermissionDto permission : permissions) {
            permissionDtoMap.put(permission.getPermissionType(), permission);
        }

        for (GlobalPermissionEditingComponent editor : permissionEditors) {
            editor.init(permissionDtoMap);
            contentPanel.add(editor);
        }
    }

    public List<Dtos.PermissionDto> flush() {
        if (!editPermissions.getValue()) {
            return null;
        }

        List<Dtos.PermissionDto> res = new ArrayList<>();

        for (GlobalPermissionEditingComponent editor : permissionEditors) {
            Dtos.PermissionDto permission = editor.flush();
            if (permission != null) {
                res.add(permission);
            }
        }

        return res;
    }

    abstract static class GlobalPermissionEditingComponent extends HorizontalPanel {

        private CheckBox checkBox = new CheckBox();

        protected void init(Map<Integer, Dtos.PermissionDto> permissionDtoMap) {
            clear();

            Label label = new Label(getLabel());
            label.setTitle(getDescription());
            add(label);

            add(checkBox);
            checkBox.setValue(permissionDtoMap.containsKey(getKey()));
        }

        Dtos.PermissionDto flush() {
            if (checkBox.getValue()) {
                Dtos.PermissionDto res = DtoFactory.permissionDto();
                res.setPermissionType(getKey());
                return res;
            }

            return null;
        }

        public CheckBox getCheckBox() {
            return checkBox;
        }

        protected abstract Integer getKey();

        protected abstract String getDescription();

        protected abstract String getLabel();

    }

    static abstract class ListPermissionEditingComponent<T> extends GlobalPermissionEditingComponent {

        private PanelWithCheckboxes<T> permissions = new PanelWithCheckboxes<T>();

        private Map<Integer, Dtos.PermissionDto> permissionDtoMap;

        @Override
        protected void init(Map<Integer, Dtos.PermissionDto> permissionDtoMap) {
            this.permissionDtoMap = permissionDtoMap;

            super.init(permissionDtoMap);

            permissions.initialize();
            fillPermissionsList(permissions);
            add(permissions);

            updateContentEnabled();

            getCheckBox().addValueChangeHandler(new ValueChangeHandler<Boolean>() {
                @Override
                public void onValueChange(ValueChangeEvent<Boolean> event) {
                    updateContentEnabled();
                }
            });
        }

        private void updateContentEnabled() {
            permissions.setEnabled(getCheckBox().getValue());
        }

        @Override
        Dtos.PermissionDto flush() {
            Dtos.PermissionDto permissionsDto = super.flush();
            if (permissionsDto == null) {
                return null;
            }

            List<String> args = new ArrayList<>();
            for (CommonFilterCheckBox<T> checkBox : permissions.getContent()) {
                if (checkBox instanceof IdProvider) {
                    if (checkBox.getValue()) {
                        args.add(((IdProvider) checkBox).provideId());
                    }
                }

            }

            if (args.size() == 0) {
                return null;
            }

            permissionsDto.setArgs(args);

            return permissionsDto;
        }

        boolean hasPermission(int permission, String id) {
            if (permissionDtoMap == null) {
                return false;
            }

            if (!permissionDtoMap.containsKey(permission)) {
                return false;
            }

            if (permissionDtoMap.get(permission).getArgs() == null) {
                return false;
            }

            return permissionDtoMap.get(permission).getArgs().contains(id);
        }

        protected abstract void fillPermissionsList(PanelWithCheckboxes<T> panelWithCheckboxes);
    }

    interface IdProvider {
        String provideId();
    }

    static class UserFilterCheckBox extends CommonFilterCheckBox<Dtos.UserDto> implements IdProvider {

        private Dtos.UserDto entity;

        public UserFilterCheckBox(Dtos.UserDto entity) {
            super(entity);
            this.entity = entity;
        }

        @Override
        protected String provideText(Dtos.UserDto entity) {
            String res = entity.getUserName();
            if (entity.getRealName() != null && !entity.getRealName().equals("")) {
                res += " ("+entity.getRealName()+")";
            }
            return res;
        }

        @Override
        public String provideId() {
            if (entity == UsersManager.getInstance().getAllUsers()) {
                return "*";
            }

            return entity.getUserName();
        }
    }

    static class ManipulateBoardPEC extends GlobalPermissionEditingComponent {

        @Override
        protected Integer getKey() {
            return Dtos.PermissionTypes.ManipulateBoard.getValue();
        }

        @Override
        protected String getDescription() {
            return "Allows the user to edit the board's workflow.";
        }

        @Override
        protected String getLabel() {
            return "Manipulate Board";
        }

    }

    static class CreateUserPEC extends GlobalPermissionEditingComponent {

        @Override
        protected Integer getKey() {
            return Dtos.PermissionTypes.CreateUser.getValue();
        }

        @Override
        protected String getDescription() {
            return "Allows creating a new user";
        }

        @Override
        protected String getLabel() {
            return "Create User";
        }
    }

    static abstract class BaseUserPEC extends ListPermissionEditingComponent<Dtos.UserDto> {

        @Override
        protected void fillPermissionsList(PanelWithCheckboxes<Dtos.UserDto> panelWithCheckboxes) {
            List<Dtos.UserDto> users = UsersManager.getInstance().getUsers();

            users.add(0, UsersManager.getInstance().getAllUsers());

            for (Dtos.UserDto user : users) {
                UserFilterCheckBox checkBox = new UserFilterCheckBox(user);
                panelWithCheckboxes.add(checkBox);
                checkBox.setValue(hasPermission(getKey(), checkBox.provideId()));
            }
        }

    }

    static class ReadUserPEC extends BaseUserPEC {

        @Override
        protected Integer getKey() {
            return Dtos.PermissionTypes.ReadUser.getValue();
        }

        @Override
        protected String getDescription() {
            return "Allows to see the user";
        }

        @Override
        protected String getLabel() {
            return "Read User";
        }
    }

    static class EditUserPermissionsPEC extends BaseUserPEC {

        @Override
        protected Integer getKey() {
            return Dtos.PermissionTypes.EditUserPermissions.getValue();
        }

        @Override
        protected String getDescription() {
            return "Allows to edit the user permissions. Allows to set only the permission this user holds.";
        }

        @Override
        protected String getLabel() {
            return "Manipulate User Permissions";
        }

    }

    static class EditUserDataPEC extends BaseUserPEC {

        @Override
        protected Integer getKey() {
            return Dtos.PermissionTypes.EditUserData.getValue();
        }

        @Override
        protected String getDescription() {
            return "Allows to edit the user (everything except permissions).";
        }

        @Override
        protected String getLabel() {
            return "Manipulate User Data";
        }

    }

    static class DeleteUserPEC extends BaseUserPEC {

        @Override
        protected Integer getKey() {
            return Dtos.PermissionTypes.DeleteUser.getValue();
        }

        @Override
        protected String getDescription() {
            return "Allows the user to delete the specific user";
        }

        @Override
        protected String getLabel() {
            return "Delete User";
        }
    }

    static class BoardFilterCheckBox extends CommonFilterCheckBox<Dtos.BoardDto> implements IdProvider {

        private Dtos.BoardDto entity;

        public BoardFilterCheckBox(Dtos.BoardDto entity) {
            super(entity);
            this.entity = entity;
        }

        @Override
        protected String provideText(Dtos.BoardDto entity) {
            return entity.getName();
        }

        @Override
        public String provideId() {
            return entity.getId();
        }
    }

    static class ProjectFilterCheckBox extends CommonFilterCheckBox<Dtos.ProjectDto> implements IdProvider {

        private Dtos.ProjectDto entity;

        public ProjectFilterCheckBox(Dtos.ProjectDto entity) {
            super(entity);
            this.entity = entity;
        }

        @Override
        protected String provideText(Dtos.ProjectDto entity) {
            return entity.getName();
        }

        @Override
        public String provideId() {
            return entity.getId();
        }
    }

    static abstract class BaseProjectPEC extends ListPermissionEditingComponent<Dtos.ProjectDto> {

        @Override
        protected void fillPermissionsList(PanelWithCheckboxes<Dtos.ProjectDto> panelWithCheckboxes) {
            List<Dtos.ProjectDto> projects = new ArrayList<>();

            Dtos.ProjectDto allProjects = DtoFactory.projectDto();
            allProjects.setId("*");
            allProjects.setName("All Projects");

            projects.add(0, allProjects);

            for (Dtos.ProjectDto dto : projectsDto.getValues()) {
                projects.add(dto);
            }

            for (Dtos.ProjectDto project : projects) {
                ProjectFilterCheckBox checkBox = new ProjectFilterCheckBox(project);
                panelWithCheckboxes.add(checkBox);
                checkBox.setValue(hasPermission(getKey(), checkBox.provideId()));
            }
        }
    }

    static class ReadProjectPEC extends BaseProjectPEC {

        @Override
        protected Integer getKey() {
            return Dtos.PermissionTypes.ReadProject.getValue();
        }

        @Override
        protected String getDescription() {
            return "Allows to see this particular project";
        }

        @Override
        protected String getLabel() {
            return "Read Project";
        }
    }

    static abstract class BaseBoardPEC extends ListPermissionEditingComponent<Dtos.BoardDto> {

        @Override
        protected void fillPermissionsList(PanelWithCheckboxes<Dtos.BoardDto> panelWithCheckboxes) {
            List<Dtos.BoardDto> boards = new ArrayList<>();
            for (Dtos.BoardWithProjectsDto boardWithProjectsDto : boardsWithProjectsDto.getValues()) {
                boards.add(boardWithProjectsDto.getBoard());
            }

            Dtos.BoardDto allBoards = DtoFactory.boardDto();
            allBoards.setId("*");
            allBoards.setName("All Boards");

            boards.add(0, allBoards);

            for (Dtos.BoardDto board : boards) {
                BoardFilterCheckBox checkBox = new BoardFilterCheckBox(board);
                panelWithCheckboxes.add(checkBox);
                checkBox.setValue(hasPermission(getKey(), checkBox.provideId()));
            }
        }
    }

    static class ReadBoardPEC extends BaseBoardPEC {

        @Override
        protected Integer getKey() {
            return Dtos.PermissionTypes.ReadBoard.getValue();
        }

        @Override
        protected String getDescription() {
            return "Allows to read the board";
        }

        @Override
        protected String getLabel() {
            return "Read Board";
        }
    }

    static class MoveTaskBoardPEC extends BaseBoardPEC {

        @Override
        protected Integer getKey() {
            return Dtos.PermissionTypes.MoveTask_b.getValue();
        }

        @Override
        protected String getDescription() {
            return "Allows to move the task. Need to have it on both source and dest";
        }

        @Override
        protected String getLabel() {
            return "Move Task on Board";
        }
    }

    static class MoveTaskProjectPEC extends BaseProjectPEC {

        @Override
        protected Integer getKey() {
            return Dtos.PermissionTypes.MoveTask_p.getValue();
        }

        @Override
        protected String getDescription() {
            return "Allows to move the task. Need to have it on both source and dest";
        }

        @Override
        protected String getLabel() {
            return "Move Task on Project";
        }
    }

    static class EditTaskBoardPEC extends BaseBoardPEC {

        @Override
        protected Integer getKey() {
            return Dtos.PermissionTypes.EditTask_b.getValue();
        }

        @Override
        protected String getDescription() {
            return "Allows to edit the task.";
        }

        @Override
        protected String getLabel() {
            return "Edit Task on Board";
        }
    }

    static class EditTaskProjectPEC extends BaseProjectPEC {

        @Override
        protected Integer getKey() {
            return Dtos.PermissionTypes.EditTask_p.getValue();
        }

        @Override
        protected String getDescription() {
            return "Allows to edit the task.";
        }

        @Override
        protected String getLabel() {
            return "Edit Task on Project";
        }
    }

    static class CreateTaskBoardPEC extends BaseBoardPEC {

        @Override
        protected Integer getKey() {
            return Dtos.PermissionTypes.CreateTask_b.getValue();
        }

        @Override
        protected String getDescription() {
            return "Allows to create the task.";
        }

        @Override
        protected String getLabel() {
            return "Create Task on Board";
        }
    }

    static class CreateTaskProjectPEC extends BaseProjectPEC {

        @Override
        protected Integer getKey() {
            return Dtos.PermissionTypes.CreateTask_p.getValue();
        }

        @Override
        protected String getDescription() {
            return "Allows to create the task.";
        }

        @Override
        protected String getLabel() {
            return "Create Task on Project";
        }
    }

    static class DeleteTaskBoardPEC extends BaseBoardPEC {

        @Override
        protected Integer getKey() {
            return Dtos.PermissionTypes.DeleteTask_b.getValue();
        }

        @Override
        protected String getDescription() {
            return "Allows to delete the task.";
        }

        @Override
        protected String getLabel() {
            return "Delete Task on Board";
        }
    }

    static class DeleteTaskProjectPEC extends BaseProjectPEC {

        @Override
        protected Integer getKey() {
            return Dtos.PermissionTypes.DeleteTask_p.getValue();
        }

        @Override
        protected String getDescription() {
            return "Allows to delete the task.";
        }

        @Override
        protected String getLabel() {
            return "Delete Task on Project";
        }
    }
}
