package com.googlecode.kanbanik.client.components.task.tag;

import com.googlecode.kanbanik.client.api.DtoFactory;
import com.googlecode.kanbanik.client.components.PanelContainingDialog;

public class TagCreatingComponent extends BaseTagEditingComponent {

    @Override
    protected void edit() {
        name.setText("");
        description.setText("");
        pictureUrl.setText("");
        onClickUrl.setText("");
        setColorHex("003d89");
    }

    @Override
    public void okClicked(PanelContainingDialog dialog) {
        getParentWidget().addNewItem(doFlush(DtoFactory.taskTag()));
        super.okClicked(dialog);
    }
}
