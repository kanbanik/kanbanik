package com.googlecode.kanbanik.client.modules.editworkflow.classofservice;

import net.auroris.ColorPicker.client.ColorPicker;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.kanbanik.client.components.Closable;
import com.googlecode.kanbanik.client.components.Component;
import com.googlecode.kanbanik.client.components.PanelContainingDialog;
import com.googlecode.kanbanik.client.components.PanelContainingDialog.PanelContainingDialolgListener;
import com.googlecode.kanbanik.client.components.common.KanbanikRichTextArea;
import com.googlecode.kanbanik.dto.ClassOfServiceDto;

public class ClassOfServiceCreatingComponent extends Composite implements
		PanelContainingDialolgListener, Closable, Component<ClassOfServiceDto>,
		ClickHandler {

	private PanelContainingDialog dialog;
	
	private PanelContainingDialog colorPickerDialog;

	@UiField
	TextBox nameBox;

	@UiField
	KanbanikRichTextArea richTextArea;

	@UiField
	PushButton colorButton;
	
	@UiField
	CheckBox makePublic;

	// default color is blue
	private String currentColour = "003d89";
	
	interface MyUiBinder extends
			UiBinder<Widget, ClassOfServiceCreatingComponent> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	public interface ButtonTemplate extends SafeHtmlTemplates {
		@Template("<div style=\"width: 20px; height: 15px; background-color:#{0}\"/>")
		SafeHtml buttonColour(String colour);
	}
	
	public interface Style extends CssResource {
		String colourPickerStyle();
    }
	
	private static final ButtonTemplate template = GWT.create(ButtonTemplate.class);

	public ClassOfServiceCreatingComponent() {
		initWidget(uiBinder.createAndBindUi(this));
		setColourButtonColour(currentColour);
	}

	private void setColourButtonColour(String colour) {
		colorButton.getUpFace().setHTML(template.buttonColour(colour));
		currentColour = colour;
	}

	@Override
	public void setup(HasClickHandlers clickHandler, String title) {
		dialog = new PanelContainingDialog(title, this, nameBox);
		dialog.addListener(this);
		clickHandler.addClickHandler(this);
		
		initColorPicker();
	}

	private void initColorPicker() {
		final ColorPicker colorPicker = new ColorPicker();
		Panel colorPickerPanel = new FlowPanel();
		colorPickerPanel.add(colorPicker);
		colorPickerDialog = new PanelContainingDialog("Select Colour", colorPickerPanel);
        
		colorButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				try {
					colorPicker.setHex(currentColour);
				} catch(Exception e) {
					// well, so than leave the default color
				}
				
				colorPickerDialog.center();
				
			}
		});
		
		colorPickerDialog.addListener(new PanelContainingDialolgListener() {
			
			@Override
			public void okClicked(PanelContainingDialog dialog) {
				setColourButtonColour(colorPicker.getHexColor());
				colorPickerDialog.close();
			}
			
			@Override
			public void cancelClicked(PanelContainingDialog dialog) {
				// do nothing
			}
		});
		
	}

	@Override
	public void setDto(ClassOfServiceDto dto) {

	}

	@Override
	public void close() {
		dialog.close();
	}

	@Override
	public void okClicked(PanelContainingDialog dialog) {

	}

	@Override
	public void cancelClicked(PanelContainingDialog dialog) {

	}

	@Override
	public void onClick(ClickEvent event) {
		dialog.center();
	}
	
}
