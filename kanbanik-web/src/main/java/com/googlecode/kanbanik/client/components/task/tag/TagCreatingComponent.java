package com.googlecode.kanbanik.client.components.task.tag;

import com.googlecode.kanbanik.client.api.DtoFactory;
import com.googlecode.kanbanik.client.components.PanelContainingDialog;

public class TagCreatingComponent extends BaseTagEditingComponent {

    @Override
    protected void edit() {
        super.edit();

        name.setText("");
        description.setText("");
        pictureUrl.setText("");
        onClickUrl.setText("");
        setColorHex("ffffff");
    }

    @Override
    public void okClicked(PanelContainingDialog dialog) {
        getParentWidget().addNewItem(doFlush(DtoFactory.taskTag()));
        super.okClicked(dialog);
    }
}
