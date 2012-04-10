package com.googlecode.kanbanik.client.modules.editworkflow.boards;

public class BoardsBox_MyUiBinderImpl_TemplateImpl implements com.googlecode.kanbanik.client.modules.editworkflow.boards.BoardsBox_MyUiBinderImpl.Template {
  
  public com.google.gwt.safehtml.shared.SafeHtml html2(java.lang.String arg0,java.lang.String arg1,java.lang.String arg2,java.lang.String arg3,java.lang.String arg4) {
    StringBuilder sb = new java.lang.StringBuilder();
    sb.append("<div style='border-style: solid; border-width: 1px; margin:5px 5px 5px 5px; align: right; width: 212px;'> <span id='");
    sb.append(com.google.gwt.safehtml.shared.SafeHtmlUtils.htmlEscape(arg0));
    sb.append("'></span> <span id='");
    sb.append(com.google.gwt.safehtml.shared.SafeHtmlUtils.htmlEscape(arg1));
    sb.append("'></span> <span id='");
    sb.append(com.google.gwt.safehtml.shared.SafeHtmlUtils.htmlEscape(arg2));
    sb.append("'></span> <span id='");
    sb.append(com.google.gwt.safehtml.shared.SafeHtmlUtils.htmlEscape(arg3));
    sb.append("'></span> <span id='");
    sb.append(com.google.gwt.safehtml.shared.SafeHtmlUtils.htmlEscape(arg4));
    sb.append("'></span> </div>");
return new com.google.gwt.safehtml.shared.OnlyToBeUsedInGeneratedCodeStringBlessedAsSafeHtml(sb.toString());
}

public com.google.gwt.safehtml.shared.SafeHtml html1(java.lang.String arg0,java.lang.String arg1) {
StringBuilder sb = new java.lang.StringBuilder();
sb.append("<span style='text-align: center;'> <span id='");
sb.append(com.google.gwt.safehtml.shared.SafeHtmlUtils.htmlEscape(arg0));
sb.append("'></span> <span id='");
sb.append(com.google.gwt.safehtml.shared.SafeHtmlUtils.htmlEscape(arg1));
sb.append("'></span> </span>");
return new com.google.gwt.safehtml.shared.OnlyToBeUsedInGeneratedCodeStringBlessedAsSafeHtml(sb.toString());
}
}
