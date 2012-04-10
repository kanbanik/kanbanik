package com.googlecode.kanbanik.client.modules.editworkflow.boards;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class BoardsBox_MyUiBinderImpl_GenBundle_default_InlineClientBundleGenerator implements com.googlecode.kanbanik.client.modules.editworkflow.boards.BoardsBox_MyUiBinderImpl_GenBundle {
  private static BoardsBox_MyUiBinderImpl_GenBundle_default_InlineClientBundleGenerator _instance0 = new BoardsBox_MyUiBinderImpl_GenBundle_default_InlineClientBundleGenerator();
  private void styleInitializer() {
    style = new com.googlecode.kanbanik.client.modules.editworkflow.boards.BoardsBox_MyUiBinderImpl_GenCss_style() {
      private boolean injected;
      public boolean ensureInjected() {
        if (!injected) {
          injected = true;
          com.google.gwt.dom.client.StyleInjector.inject(getText());
          return true;
        }
        return false;
      }
      public String getName() {
        return "style";
      }
      public String getText() {
        return com.google.gwt.i18n.client.LocaleInfo.getCurrentLocale().isRTL() ? ((".GK40RFKDBJ{border-bottom-style:" + ("solid")  + ";border-bottom-width:" + ("1px")  + ";padding-bottom:" + ("7px")  + ";margin-bottom:" + ("4px")  + ";margin-top:" + ("2px")  + ";margin-left:" + ("2px")  + ";width:" + ("100%")  + ";text-align:" + ("left")  + ";}.GK40RFKDCJ{font-weight:" + ("bold")  + ";}.GK40RFKDEJ{font-weight:" + ("bold")  + ";text-align:") + (("center")  + ";padding-top:" + ("10px")  + ";}.GK40RFKDPI{text-align:" + ("center")  + ";}.GK40RFKDDJ{text-align:" + ("center")  + ";width:" + ("100%")  + ";}.GK40RFKDAJ{width:" + ("220px")  + ";padding-left:" + ("5px")  + ";}")) : ((".GK40RFKDBJ{border-bottom-style:" + ("solid")  + ";border-bottom-width:" + ("1px")  + ";padding-bottom:" + ("7px")  + ";margin-bottom:" + ("4px")  + ";margin-top:" + ("2px")  + ";margin-right:" + ("2px")  + ";width:" + ("100%")  + ";text-align:" + ("right")  + ";}.GK40RFKDCJ{font-weight:" + ("bold")  + ";}.GK40RFKDEJ{font-weight:" + ("bold")  + ";text-align:") + (("center")  + ";padding-top:" + ("10px")  + ";}.GK40RFKDPI{text-align:" + ("center")  + ";}.GK40RFKDDJ{text-align:" + ("center")  + ";width:" + ("100%")  + ";}.GK40RFKDAJ{width:" + ("220px")  + ";padding-right:" + ("5px")  + ";}"));
      }
      public java.lang.String boardHeadler(){
        return "GK40RFKDPI";
      }
      public java.lang.String boardsBoxStyle(){
        return "GK40RFKDAJ";
      }
      public java.lang.String buttonToolbar(){
        return "GK40RFKDBJ";
      }
      public java.lang.String labelStyle(){
        return "GK40RFKDCJ";
      }
      public java.lang.String listBoxStyle(){
        return "GK40RFKDDJ";
      }
      public java.lang.String projectsOnBoard(){
        return "GK40RFKDEJ";
      }
    }
    ;
  }
  private static class styleInitializer {
    static {
      _instance0.styleInitializer();
    }
    static com.googlecode.kanbanik.client.modules.editworkflow.boards.BoardsBox_MyUiBinderImpl_GenCss_style get() {
      return style;
    }
  }
  public com.googlecode.kanbanik.client.modules.editworkflow.boards.BoardsBox_MyUiBinderImpl_GenCss_style style() {
    return styleInitializer.get();
  }
  private static java.util.HashMap<java.lang.String, com.google.gwt.resources.client.ResourcePrototype> resourceMap;
  private static com.googlecode.kanbanik.client.modules.editworkflow.boards.BoardsBox_MyUiBinderImpl_GenCss_style style;
  
  public ResourcePrototype[] getResources() {
    return new ResourcePrototype[] {
      style(), 
    };
  }
  public ResourcePrototype getResource(String name) {
    if (GWT.isScript()) {
      return getResourceNative(name);
    } else {
      if (resourceMap == null) {
        resourceMap = new java.util.HashMap<java.lang.String, com.google.gwt.resources.client.ResourcePrototype>();
        resourceMap.put("style", style());
      }
      return resourceMap.get(name);
    }
  }
  private native ResourcePrototype getResourceNative(String name) /*-{
    switch (name) {
      case 'style': return this.@com.googlecode.kanbanik.client.modules.editworkflow.boards.BoardsBox_MyUiBinderImpl_GenBundle::style()();
    }
    return null;
  }-*/;
}
