package com.googlecode.kanbanik.client.modules.editworkflow.projects;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class ProjectsToBoardAdding_MyUiBinderImpl_GenBundle_default_InlineClientBundleGenerator implements com.googlecode.kanbanik.client.modules.editworkflow.projects.ProjectsToBoardAdding_MyUiBinderImpl_GenBundle {
  private static ProjectsToBoardAdding_MyUiBinderImpl_GenBundle_default_InlineClientBundleGenerator _instance0 = new ProjectsToBoardAdding_MyUiBinderImpl_GenBundle_default_InlineClientBundleGenerator();
  private void styleInitializer() {
    style = new com.googlecode.kanbanik.client.modules.editworkflow.projects.ProjectsToBoardAdding_MyUiBinderImpl_GenCss_style() {
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
        return com.google.gwt.i18n.client.LocaleInfo.getCurrentLocale().isRTL() ? ((".GK40RFKDFJ{border-bottom-style:" + ("solid")  + ";border-bottom-width:" + ("1px")  + ";}.GK40RFKDGJ{height:" + ("100%")  + ";}.GK40RFKDHJ{height:" + ("100%")  + ";width:" + ("100px")  + ";min-height:" + ("100px")  + ";min-width:" + ("100px")  + ";vertical-align:" + ("top")  + ";background-color:" + ("#f2f4f9")  + ";}.GK40RFKDIJ{border-left-style:" + ("solid")  + ";border-left-width:") + (("1px")  + ";height:" + ("100%")  + ";}")) : ((".GK40RFKDFJ{border-bottom-style:" + ("solid")  + ";border-bottom-width:" + ("1px")  + ";}.GK40RFKDGJ{height:" + ("100%")  + ";}.GK40RFKDHJ{height:" + ("100%")  + ";width:" + ("100px")  + ";min-height:" + ("100px")  + ";min-width:" + ("100px")  + ";vertical-align:" + ("top")  + ";background-color:" + ("#f2f4f9")  + ";}.GK40RFKDIJ{border-right-style:" + ("solid")  + ";border-right-width:") + (("1px")  + ";height:" + ("100%")  + ";}"));
      }
      public java.lang.String headerLabel(){
        return "GK40RFKDFJ";
      }
      public java.lang.String workfloweditProjects(){
        return "GK40RFKDGJ";
      }
      public java.lang.String workfloweditProjectsPart(){
        return "GK40RFKDHJ";
      }
      public java.lang.String workfloweditProjectsPartLeft(){
        return "GK40RFKDIJ";
      }
    }
    ;
  }
  private static class styleInitializer {
    static {
      _instance0.styleInitializer();
    }
    static com.googlecode.kanbanik.client.modules.editworkflow.projects.ProjectsToBoardAdding_MyUiBinderImpl_GenCss_style get() {
      return style;
    }
  }
  public com.googlecode.kanbanik.client.modules.editworkflow.projects.ProjectsToBoardAdding_MyUiBinderImpl_GenCss_style style() {
    return styleInitializer.get();
  }
  private static java.util.HashMap<java.lang.String, com.google.gwt.resources.client.ResourcePrototype> resourceMap;
  private static com.googlecode.kanbanik.client.modules.editworkflow.projects.ProjectsToBoardAdding_MyUiBinderImpl_GenCss_style style;
  
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
      case 'style': return this.@com.googlecode.kanbanik.client.modules.editworkflow.projects.ProjectsToBoardAdding_MyUiBinderImpl_GenBundle::style()();
    }
    return null;
  }-*/;
}
