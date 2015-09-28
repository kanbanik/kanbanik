package com.googlecode.kanbanik.client.components.security;

import com.google.gwt.core.client.GWT;
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
import com.googlecode.kanbanik.client.components.common.filters.CommonFilterCheckBox;
import com.googlecode.kanbanik.client.components.common.filters.PanelWithCheckboxes;
import com.googlecode.kanbanik.client.managers.UsersManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PermissionsEditingComponent extends Composite {

    @UiField
    FlowPanel mainPanel;

    @UiField
    VerticalPanel contentPanel;

    private static final List<? extends GlobalPermissionEditingComponent> permissionEditors = Arrays.asList(
            new ManipulateBoardPEC(),
            new ManipulateUserPEC()
    );

    interface MyUiBinder extends UiBinder<Widget, PermissionsEditingComponent> {
    }

    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

    public PermissionsEditingComponent() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    public void init(List<Dtos.PermissionDto> permissions) {
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
                    args.add(((IdProvider) checkBox).provideId());
                }

            }

            permissionsDto.setArgs(args);

            return super.flush();
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
            return 0;
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

    static class ManipulateUserPEC extends ListPermissionEditingComponent<Dtos.UserDto> {

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

        @Override
        protected Integer getKey() {
            return 7;
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

}
