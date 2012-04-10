package com.googlecode.kanbanik.client.modules.editworkflow.projects;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiBinderUtil;
import com.google.gwt.user.client.ui.Widget;

public class ProjectsToBoardAdding_MyUiBinderImpl implements UiBinder<com.google.gwt.user.client.ui.Widget, com.googlecode.kanbanik.client.modules.editworkflow.projects.ProjectsToBoardAdding>, com.googlecode.kanbanik.client.modules.editworkflow.projects.ProjectsToBoardAdding.MyUiBinder {

  interface Template extends SafeHtmlTemplates {
    @Template("<div style='border-style: solid; border-width: 1px; margin:5px 5px 5px 5px; align: right;'> <span id='{0}'></span> </div>")
    SafeHtml html1(String arg0);
     
  }

  Template template = GWT.create(Template.class);

  public com.google.gwt.user.client.ui.Widget createAndBindUi(final com.googlecode.kanbanik.client.modules.editworkflow.projects.ProjectsToBoardAdding owner) {

    com.googlecode.kanbanik.client.modules.editworkflow.projects.ProjectsToBoardAdding_MyUiBinderImpl_GenBundle clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay = (com.googlecode.kanbanik.client.modules.editworkflow.projects.ProjectsToBoardAdding_MyUiBinderImpl_GenBundle) GWT.create(com.googlecode.kanbanik.client.modules.editworkflow.projects.ProjectsToBoardAdding_MyUiBinderImpl_GenBundle.class);
    com.googlecode.kanbanik.client.modules.editworkflow.projects.ProjectsToBoardAdding_MyUiBinderImpl_GenCss_style style = clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay.style();
    java.lang.String domId0 = com.google.gwt.dom.client.Document.get().createUniqueId();
    com.google.gwt.user.client.ui.Label f_Label4 = (com.google.gwt.user.client.ui.Label) GWT.create(com.google.gwt.user.client.ui.Label.class);
    com.google.gwt.user.client.ui.FlowPanel toBeAdded = (com.google.gwt.user.client.ui.FlowPanel) GWT.create(com.google.gwt.user.client.ui.FlowPanel.class);
    com.google.gwt.user.client.ui.FlowPanel f_FlowPanel3 = (com.google.gwt.user.client.ui.FlowPanel) GWT.create(com.google.gwt.user.client.ui.FlowPanel.class);
    com.google.gwt.user.client.ui.Label f_Label6 = (com.google.gwt.user.client.ui.Label) GWT.create(com.google.gwt.user.client.ui.Label.class);
    com.google.gwt.user.client.ui.FlowPanel projectsOfBoard = (com.google.gwt.user.client.ui.FlowPanel) GWT.create(com.google.gwt.user.client.ui.FlowPanel.class);
    com.google.gwt.user.client.ui.FlowPanel f_FlowPanel5 = (com.google.gwt.user.client.ui.FlowPanel) GWT.create(com.google.gwt.user.client.ui.FlowPanel.class);
    com.google.gwt.user.client.ui.HorizontalPanel f_HorizontalPanel2 = (com.google.gwt.user.client.ui.HorizontalPanel) GWT.create(com.google.gwt.user.client.ui.HorizontalPanel.class);
    com.google.gwt.user.client.ui.AbsolutePanel panelWithDraggablePanels = (com.google.gwt.user.client.ui.AbsolutePanel) GWT.create(com.google.gwt.user.client.ui.AbsolutePanel.class);
    com.google.gwt.user.client.ui.HTMLPanel f_HTMLPanel1 = new com.google.gwt.user.client.ui.HTMLPanel(template.html1(domId0).asString());

    f_Label4.setStyleName("" + style.headerLabel() + "");
    f_Label4.setText("All Projects");
    f_FlowPanel3.add(f_Label4);
    toBeAdded.setStyleName("" + style.workfloweditProjectsPart() + "");
    f_FlowPanel3.add(toBeAdded);
    f_FlowPanel3.setStyleName("" + style.workfloweditProjectsPartLeft() + "");
    f_HorizontalPanel2.add(f_FlowPanel3);
    f_Label6.setStyleName("" + style.headerLabel() + "");
    f_Label6.setText("On Board");
    f_FlowPanel5.add(f_Label6);
    projectsOfBoard.setStyleName("" + style.workfloweditProjectsPart() + "");
    f_FlowPanel5.add(projectsOfBoard);
    f_FlowPanel5.setStyleName("" + style.workfloweditProjectsPart() + "");
    f_HorizontalPanel2.add(f_FlowPanel5);
    f_HorizontalPanel2.setStyleName("" + style.workfloweditProjects() + "");
    panelWithDraggablePanels.add(f_HorizontalPanel2);

    UiBinderUtil.TempAttachment attachRecord0 = UiBinderUtil.attachToDom(f_HTMLPanel1.getElement());
    com.google.gwt.user.client.Element domId0Element = com.google.gwt.dom.client.Document.get().getElementById(domId0).cast();
    attachRecord0.detach();
    f_HTMLPanel1.addAndReplaceElement(panelWithDraggablePanels, domId0Element);


    owner.panelWithDraggablePanels = panelWithDraggablePanels;
    owner.projectsOfBoard = projectsOfBoard;
    owner.toBeAdded = toBeAdded;
    clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay.style().ensureInjected();

    return f_HTMLPanel1;
  }
}
