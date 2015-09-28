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

    private static final List<? extends PermissionEditingComponent> permissionEditors = Arrays.asList(
            new ManipulateBoardEditor()

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

        for (PermissionEditingComponent editor : permissionEditors) {
            editor.init(permissionDtoMap);
            contentPanel.add(editor);
        }
    }

    public List<Dtos.PermissionDto> flush() {
        List<Dtos.PermissionDto> res = new ArrayList<>();

        for (PermissionEditingComponent editor : permissionEditors) {
            Dtos.PermissionDto permission = editor.flush();
            if (permission != null) {
                res.add(permission);
            }
        }

        return res;
    }

    abstract static class PermissionEditingComponent extends HorizontalPanel {

        private CheckBox checkBox = new CheckBox();

        void init(Map<Integer, Dtos.PermissionDto> permissionDtoMap) {
            clear();

            Label label = new Label(getLabel());
            label.setTitle(getDescription());
            add(label);

            add(checkBox);
            checkBox.setValue(permissionDtoMap.containsKey(getKey()));
        }

        Dtos.PermissionDto flush() {
            if (checkBox.getValue()) {
                Dtos.PermissionDto res = createPermission();
                res.setPermissionType(getKey());
                return res;
            }

            return null;
        }

        protected abstract Integer getKey();

        protected abstract String getDescription();

        protected abstract String getLabel();

        protected abstract Dtos.PermissionDto createPermission();

    }

    static class ManipulateBoardEditor extends PermissionEditingComponent {

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

        @Override
        protected Dtos.PermissionDto createPermission() {
            return DtoFactory.permissionDto();
        }
    }

}
