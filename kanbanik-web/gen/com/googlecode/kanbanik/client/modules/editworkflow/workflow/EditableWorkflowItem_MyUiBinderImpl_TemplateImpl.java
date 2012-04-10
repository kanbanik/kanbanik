package com.googlecode.kanbanik.client.modules.editworkflow.workflow;

public class EditableWorkflowItem_MyUiBinderImpl_TemplateImpl implements com.googlecode.kanbanik.client.modules.editworkflow.workflow.EditableWorkflowItem_MyUiBinderImpl.Template {
  
  public com.google.gwt.safehtml.shared.SafeHtml html1(java.lang.String arg0,java.lang.String arg1,java.lang.String arg2) {
    StringBuilder sb = new java.lang.StringBuilder();
    sb.append("<div id='");
    sb.append(com.google.gwt.safehtml.shared.SafeHtmlUtils.htmlEscape(arg0));
    sb.append("' style='border-style: solid; border-width: 1px; margin:5px 5px 5px 5px; align: right; width: 220px'> <span id='");
    sb.append(com.google.gwt.safehtml.shared.SafeHtmlUtils.htmlEscape(arg1));
    sb.append("'></span> <span id='");
    sb.append(com.google.gwt.safehtml.shared.SafeHtmlUtils.htmlEscape(arg2));
    sb.append("'></span> </div>");
return new com.google.gwt.safehtml.shared.OnlyToBeUsedInGeneratedCodeStringBlessedAsSafeHtml(sb.toString());
}
}
