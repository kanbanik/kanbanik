package com.googlecode.kanbanik.client.components.task.tag;

import com.googlecode.kanbanik.client.components.PanelContainingDialog;

public class TagEditingComponent extends BaseTagEditingComponent {

    @Override
    protected void edit() {
        super.edit();

        name.setText(getDto().getName());
        description.setText(getDto().getDescription());
        setPictureUrl(getDto().getPictureUrl());
        onClickUrl.setText(getDto().getOnClickUrl());
        setColor(getDto().getColour());
    }

    @Override
    public void okClicked(PanelContainingDialog dialog) {
        if (!validate()) {
            return;
        }

        getParentWidget().editItem(doFlush(getDto()));
        super.okClicked(dialog);
    }
}
