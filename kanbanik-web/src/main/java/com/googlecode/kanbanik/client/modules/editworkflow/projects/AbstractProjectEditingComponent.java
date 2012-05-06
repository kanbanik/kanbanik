package com.googlecode.kanbanik.client.modules.editworkflow.projects;


import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.googlecode.kanbanik.client.components.PanelContainingDialog;
import com.googlecode.kanbanik.client.components.PanelContainingDialog.PanelContainingDialolgListener;
import com.googlecode.kanbanik.dto.ProjectDto;

public abstract class AbstractProjectEditingComponent implements PanelContainingDialolgListener {
	
	private Panel panel = new HorizontalPanel();

	private Label projectNameLabel = new Label("Project Name: ");
	
	private TextBox projectNameText = new TextBox();
	
	private PanelContainingDialog dialog;
	
	private HasClickHandlers clickHandlers;

	public AbstractProjectEditingComponent(HasClickHandlers clickHandlers) {
		this.clickHandlers = clickHandlers;
		panel.add(projectNameLabel);
		panel.add(projectNameText);
		dialog = new PanelContainingDialog("Add Project", panel);
		dialog.addListener(this);
		this.clickHandlers.addClickHandler(new ShowDialogHandler());
	}
	
	class ShowDialogHandler implements ClickHandler {

		public void onClick(ClickEvent event) {
			projectNameText.setText(getProjectName());
			dialog.center();
			projectNameText.setFocus(true);
		}

	}

	protected abstract String getProjectName();
	
	protected abstract void onOkClicked(ProjectDto project);
	
	public void okClicked(PanelContainingDialog dialog) {
		ProjectDto project = createProject();
		project.setName(projectNameText.getText());
		onOkClicked(project);
	}

	public void cancelClicked(PanelContainingDialog dialog) {
		
	}
	
	protected abstract ProjectDto createProject();
}
