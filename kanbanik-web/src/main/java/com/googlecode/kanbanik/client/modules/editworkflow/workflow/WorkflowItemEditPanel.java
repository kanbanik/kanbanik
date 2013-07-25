package com.googlecode.kanbanik.client.modules.editworkflow.workflow;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;
import com.googlecode.kanbanik.dto.ItemType;

public class WorkflowItemEditPanel extends FlowPanel {

	private TextBox nameBox;
	
	private TextBox wipLimitBox;
	
	private CheckBox wipLimitEnabled;
	
	private TextBox verticalSizingSizeBox;
	
	private CheckBox verticalSizingEnabled;
	
	private RadioButton horizontal = new RadioButton("itemTypeGroup", "Horizontal");

	private RadioButton vertical = new RadioButton("itemTypeGroup", "Vertical");
	
	public void setWipLimit(int wipLimit) {
		wipLimitBox = new TextBox();
		Panel panel = createNameWaluePair("WIP Limit: ", Integer.toString(wipLimit), wipLimitBox);
		
		wipLimitEnabled = new CheckBox();
		wipLimitEnabled.setValue(true);
		
		if (wipLimit == -1) {
			disableWipLimit();
			wipLimitEnabled.setValue(false);
		} 
		
		wipLimitEnabled.addClickHandler(new WipLimitEnabledClickHandler());
		panel.add(wipLimitEnabled);
		setWidth("223px");
		
	}
	
	public void setVerticalSizingEnabled(boolean enabled, String reason) {
		verticalSizingEnabled.setEnabled(enabled);
		if (!enabled) {
			verticalSizingEnabled.setTitle(reason);
			verticalSizingEnabled.setValue(false);
			verticalSizingSizeBox.setText("");
		}
		
		verticalSizingSizeBox.setEnabled(enabled);
	}
	
	public void setVerticalSizing(boolean enabled, int size) {
		verticalSizingSizeBox = new TextBox();
		Panel panel = createNameWaluePair("Fixed Verical Size (in tasks): ", Integer.toString(size), verticalSizingSizeBox);
		
		verticalSizingEnabled = new CheckBox();
		verticalSizingEnabled.setValue(enabled);
		verticalSizingSizeBox.setEnabled(enabled);
		if (!enabled) {
			verticalSizingSizeBox.setText("");
		}
		verticalSizingEnabled.addClickHandler(new VerticalSizingEnabledClickHandler());
		panel.add(verticalSizingEnabled);
		setWidth("223px");
	}

	private void disableWipLimit() {
		wipLimitBox.setText("");
		wipLimitBox.setEnabled(false);
	}
	
	public void setType(ItemType itemType) {
		Panel panel = new FlowPanel();
		panel.add(horizontal);
		panel.add(vertical);
		
		Panel nameValue = new FlowPanel();
		nameValue.add(new Label("Orientation:"));
		nameValue.add(panel);
		add(nameValue);
		
		if (itemType == ItemType.HORIZONTAL) {
			horizontal.setValue(true);
		} else {
			vertical.setValue(true);
		}
	}
	
	class WipLimitEnabledClickHandler implements ClickHandler {

		public void onClick(ClickEvent event) {
			if (!wipLimitEnabled.getValue()) {
				wipLimitBox.setEnabled(false);
			} else {
				wipLimitBox.setEnabled(true);
			}
		}
		
	}
	
	class VerticalSizingEnabledClickHandler implements ClickHandler {

		public void onClick(ClickEvent event) {
			if (!verticalSizingEnabled.getValue()) {
				verticalSizingSizeBox.setEnabled(false);
			} else {
				verticalSizingSizeBox.setEnabled(true);
			}
		}
		
	}
	
	public FocusWidget getDefaultFocusWidget() {
		return nameBox;
	}
	
	public void setName(String name) {
		nameBox = new TextBox();
		createNameWaluePair("Name: ", name, nameBox);	
	}

	private Panel createNameWaluePair(String label, String value, TextBox textBox) {
		Panel panel = new FlowPanel();
		panel.add(new Label(label));
		textBox.setText(value);
		panel.add(textBox);
		add(panel);
		return panel;
	}
	
	public String getName() {
		return nameBox.getText();
	}
	
	public int getWipLimit() {
		if (!wipLimitEnabled.getValue()) {
			return -1;
		}
		try {
			return Integer.parseInt(wipLimitBox.getText());
		} catch (NumberFormatException e) {
			return -1;
		}
	}
	
	public int getVerticalSize() {
		if (!verticalSizingEnabled.getValue()) {
			return -1;
		}
		try {
			return Integer.parseInt(verticalSizingSizeBox.getText());
		} catch (NumberFormatException e) {
			return -1;
		}
	}

	public ItemType getItemType() {
		if (horizontal.getValue()) {
			return ItemType.HORIZONTAL;
		}
		
		return ItemType.VERTICAL;
	}
	
}