package com.googlecode.kanbanik.client.components.common;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PushButton;
import com.googlecode.kanbanik.client.components.PanelContainingDialog;
import net.auroris.ColorPicker.client.ColorPicker;

public class ColorPickerComponent extends PushButton {

    private PanelContainingDialog colorPickerDialog;

    final ColorPicker colorPicker = new ColorPicker();

    public interface ButtonTemplate extends SafeHtmlTemplates {
        @Template("<div style=\"width: 20px; height: 15px; background-color:#{0}\"/>")
        SafeHtml buttonColour(String colour);
    }

    private static final ButtonTemplate template = GWT
            .create(ButtonTemplate.class);

    /**
     * In hex without the #
     */
    public String getColor() {
        return colorPicker.getHexColor();
    }

    /**
     * In hex without the #
     */
    public void setColor(String hexColor) {
        getUpFace().setHTML(template.buttonColour(hexColor));
        try {
            colorPicker.setHex(hexColor);
        } catch (Exception e) {
            // well, so than leave the default color
        }
    }


    public void init() {
        Panel colorPickerPanel = new FlowPanel();
        colorPickerPanel.add(colorPicker);
        colorPickerDialog = new PanelContainingDialog("Select Colour",
                colorPickerPanel);

        addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                colorPickerDialog.center();

            }
        });

        colorPickerDialog.addListener(new PanelContainingDialog.PanelContainingDialolgListener() {

            @Override
            public void okClicked(PanelContainingDialog dialog) {
                setColor(colorPicker.getHexColor());
                colorPickerDialog.close();
            }

            @Override
            public void cancelClicked(PanelContainingDialog dialog) {
                // do nothing
            }
        });
    }
}
