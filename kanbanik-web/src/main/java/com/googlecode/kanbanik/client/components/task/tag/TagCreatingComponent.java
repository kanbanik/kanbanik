package com.googlecode.kanbanik.client.components.task.tag;

import com.googlecode.kanbanik.client.api.DtoFactory;
import com.googlecode.kanbanik.client.components.PanelContainingDialog;

public class TagCreatingComponent extends BaseTagEditingComponent {

    @Override
    protected void edit() {
        super.edit();

        name.setText("");
        description.setText("");
        setPictureUrl("");
        onClickUrl.setText("");
        setColor(TagConstants.predefinedColors.get(TagConstants.TRANSPARENT_INDEX));
    }

    @Override
    public void okClicked(PanelContainingDialog dialog) {
        if (!validate()) {
            return;
        }

        getParentWidget().addNewItem(doFlush(DtoFactory.taskTag()));
        super.okClicked(dialog);
    }
}
