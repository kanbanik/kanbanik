package com.googlecode.kanbanik.client.modules.editworkflow.boards;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiBinderUtil;
import com.google.gwt.user.client.ui.Widget;

public class BoardsBox_MyUiBinderImpl implements UiBinder<com.google.gwt.user.client.ui.Widget, com.googlecode.kanbanik.client.modules.editworkflow.boards.BoardsBox>, com.googlecode.kanbanik.client.modules.editworkflow.boards.BoardsBox.MyUiBinder {

  interface Template extends SafeHtmlTemplates {
    @Template("<span style='text-align: center;'> <span id='{0}'></span> <span id='{1}'></span> </span>")
    SafeHtml html1(String arg0, String arg1);
     
    @Template("<div style='border-style: solid; border-width: 1px; margin:5px 5px 5px 5px; align: right; width: 212px;'> <span id='{0}'></span> <span id='{1}'></span> <span id='{2}'></span> <span id='{3}'></span> <span id='{4}'></span> </div>")
    SafeHtml html2(String arg0, String arg1, String arg2, String arg3, String arg4);
     
  }

  Template template = GWT.create(Template.class);

  public com.google.gwt.user.client.ui.Widget createAndBindUi(final com.googlecode.kanbanik.client.modules.editworkflow.boards.BoardsBox owner) {

    com.googlecode.kanbanik.client.modules.editworkflow.boards.BoardsBox_MyUiBinderImpl_GenBundle clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay = (com.googlecode.kanbanik.client.modules.editworkflow.boards.BoardsBox_MyUiBinderImpl_GenBundle) GWT.create(com.googlecode.kanbanik.client.modules.editworkflow.boards.BoardsBox_MyUiBinderImpl_GenBundle.class);
    com.googlecode.kanbanik.client.modules.editworkflow.boards.BoardsBox_MyUiBinderImpl_GenCss_style style = clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay.style();
    java.lang.String domId0 = com.google.gwt.dom.client.Document.get().createUniqueId();
    java.lang.String domId1 = com.google.gwt.dom.client.Document.get().createUniqueId();
    com.google.gwt.user.client.ui.Label f_Label3 = (com.google.gwt.user.client.ui.Label) GWT.create(com.google.gwt.user.client.ui.Label.class);
    java.lang.String domId2 = com.google.gwt.dom.client.Document.get().createUniqueId();
    com.google.gwt.user.client.ui.ListBox boardsList = owner.boardsList;
    com.google.gwt.user.client.ui.HTMLPanel f_HTMLPanel2 = new com.google.gwt.user.client.ui.HTMLPanel(template.html1(domId1, domId2).asString());
    java.lang.String domId3 = com.google.gwt.dom.client.Document.get().createUniqueId();
    com.google.gwt.user.client.ui.PushButton addBoardButton = (com.google.gwt.user.client.ui.PushButton) GWT.create(com.google.gwt.user.client.ui.PushButton.class);
    com.google.gwt.user.client.ui.PushButton editButton = (com.google.gwt.user.client.ui.PushButton) GWT.create(com.google.gwt.user.client.ui.PushButton.class);
    com.google.gwt.user.client.ui.PushButton deleteButton = (com.google.gwt.user.client.ui.PushButton) GWT.create(com.google.gwt.user.client.ui.PushButton.class);
    com.google.gwt.user.client.ui.HorizontalPanel f_HorizontalPanel4 = (com.google.gwt.user.client.ui.HorizontalPanel) GWT.create(com.google.gwt.user.client.ui.HorizontalPanel.class);
    java.lang.String domId4 = com.google.gwt.dom.client.Document.get().createUniqueId();
    com.google.gwt.user.client.ui.Label f_Label5 = (com.google.gwt.user.client.ui.Label) GWT.create(com.google.gwt.user.client.ui.Label.class);
    java.lang.String domId5 = com.google.gwt.dom.client.Document.get().createUniqueId();
    com.google.gwt.user.client.ui.PushButton addProjectButton = (com.google.gwt.user.client.ui.PushButton) GWT.create(com.google.gwt.user.client.ui.PushButton.class);
    java.lang.String domId6 = com.google.gwt.dom.client.Document.get().createUniqueId();
    com.google.gwt.user.client.ui.SimplePanel projectsToBoardAddingContainer = (com.google.gwt.user.client.ui.SimplePanel) GWT.create(com.google.gwt.user.client.ui.SimplePanel.class);
    com.google.gwt.user.client.ui.HTMLPanel f_HTMLPanel1 = new com.google.gwt.user.client.ui.HTMLPanel(template.html2(domId0, domId3, domId4, domId5, domId6).asString());

    f_Label3.setStyleName("" + style.labelStyle() + "");
    f_Label3.setText("Board");
    boardsList.setStyleName("" + style.listBoxStyle() + "");
    addBoardButton.setStyleName("imageButtonStyle");
    addBoardButton.setText("Add");
    addBoardButton.setTitle("New Board");
    f_HorizontalPanel4.add(addBoardButton);
    editButton.setStyleName("imageButtonStyle");
    editButton.setText("Edit");
    editButton.setTitle("Edit Board");
    f_HorizontalPanel4.add(editButton);
    deleteButton.setStyleName("imageButtonStyle");
    deleteButton.setText("Delete");
    deleteButton.setTitle("Delete Borad");
    f_HorizontalPanel4.add(deleteButton);
    f_HorizontalPanel4.setStyleName("" + style.buttonToolbar() + "");
    f_Label5.setStyleName("" + style.projectsOnBoard() + "");
    f_Label5.setText("Projects on Board");
    addProjectButton.setStyleName("imageButtonStyle");
    addProjectButton.setTitle("New Project");
    f_HTMLPanel1.setStyleName("" + style.boardsBoxStyle() + "");

    UiBinderUtil.TempAttachment attachRecord0 = UiBinderUtil.attachToDom(f_HTMLPanel1.getElement());
    UiBinderUtil.TempAttachment attachRecord1 = UiBinderUtil.attachToDom(f_HTMLPanel2.getElement());
    com.google.gwt.user.client.Element domId1Element = com.google.gwt.dom.client.Document.get().getElementById(domId1).cast();
    com.google.gwt.user.client.Element domId2Element = com.google.gwt.dom.client.Document.get().getElementById(domId2).cast();
    attachRecord1.detach();
    f_HTMLPanel2.addAndReplaceElement(f_Label3, domId1Element);
    f_HTMLPanel2.addAndReplaceElement(boardsList, domId2Element);
    com.google.gwt.user.client.Element domId0Element = com.google.gwt.dom.client.Document.get().getElementById(domId0).cast();
    com.google.gwt.user.client.Element domId3Element = com.google.gwt.dom.client.Document.get().getElementById(domId3).cast();
    com.google.gwt.user.client.Element domId4Element = com.google.gwt.dom.client.Document.get().getElementById(domId4).cast();
    com.google.gwt.user.client.Element domId5Element = com.google.gwt.dom.client.Document.get().getElementById(domId5).cast();
    com.google.gwt.user.client.Element domId6Element = com.google.gwt.dom.client.Document.get().getElementById(domId6).cast();
    attachRecord0.detach();
    f_HTMLPanel1.addAndReplaceElement(f_HTMLPanel2, domId0Element);
    f_HTMLPanel1.addAndReplaceElement(f_HorizontalPanel4, domId3Element);
    f_HTMLPanel1.addAndReplaceElement(f_Label5, domId4Element);
    f_HTMLPanel1.addAndReplaceElement(addProjectButton, domId5Element);
    f_HTMLPanel1.addAndReplaceElement(projectsToBoardAddingContainer, domId6Element);


    owner.addBoardButton = addBoardButton;
    owner.addProjectButton = addProjectButton;
    owner.deleteButton = deleteButton;
    owner.editButton = editButton;
    owner.projectsToBoardAddingContainer = projectsToBoardAddingContainer;
    clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay.style().ensureInjected();

    return f_HTMLPanel1;
  }
}
