package com.googlecode.kanbanik.client.modules.editworkflow.projects;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class ProjectWidget_MyUiBinderImpl_GenBundle_default_InlineClientBundleGenerator implements com.googlecode.kanbanik.client.modules.editworkflow.projects.ProjectWidget_MyUiBinderImpl_GenBundle {
  private static ProjectWidget_MyUiBinderImpl_GenBundle_default_InlineClientBundleGenerator _instance0 = new ProjectWidget_MyUiBinderImpl_GenBundle_default_InlineClientBundleGenerator();
  private void styleInitializer() {
    style = new com.googlecode.kanbanik.client.modules.editworkflow.projects.ProjectWidget_MyUiBinderImpl_GenCss_style() {
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
        return com.google.gwt.i18n.client.LocaleInfo.getCurrentLocale().isRTL() ? ((".GK40RFKDMJ{padding-bottom:" + ("7px")  + ";margin-bottom:" + ("4px")  + ";margin-top:" + ("2px")  + ";margin-left:" + ("2px")  + ";width:" + ("100%")  + ";text-align:" + ("left")  + ";}.GK40RFKDKJ{background-color:" + ("#d0e4f6")  + ";border-bottom-style:" + ("solid")  + ";border-bottom-width:" + ("1px")  + ";width:" + ("100%")  + ";text-align:") + (("center")  + ";}.GK40RFKDLJ{font-weight:" + ("bold")  + ";}")) : ((".GK40RFKDMJ{padding-bottom:" + ("7px")  + ";margin-bottom:" + ("4px")  + ";margin-top:" + ("2px")  + ";margin-right:" + ("2px")  + ";width:" + ("100%")  + ";text-align:" + ("right")  + ";}.GK40RFKDKJ{background-color:" + ("#d0e4f6")  + ";border-bottom-style:" + ("solid")  + ";border-bottom-width:" + ("1px")  + ";width:" + ("100%")  + ";text-align:") + (("center")  + ";}.GK40RFKDLJ{font-weight:" + ("bold")  + ";}"));
      }
      public java.lang.String projectHeader(){
        return "GK40RFKDKJ";
      }
      public java.lang.String projectName(){
        return "GK40RFKDLJ";
      }
      public java.lang.String projectToolbar(){
        return "GK40RFKDMJ";
      }
    }
    ;
  }
  private static class styleInitializer {
    static {
      _instance0.styleInitializer();
    }
    static com.googlecode.kanbanik.client.modules.editworkflow.projects.ProjectWidget_MyUiBinderImpl_GenCss_style get() {
      return style;
    }
  }
  public com.googlecode.kanbanik.client.modules.editworkflow.projects.ProjectWidget_MyUiBinderImpl_GenCss_style style() {
    return styleInitializer.get();
  }
  private static java.util.HashMap<java.lang.String, com.google.gwt.resources.client.ResourcePrototype> resourceMap;
  private static com.googlecode.kanbanik.client.modules.editworkflow.projects.ProjectWidget_MyUiBinderImpl_GenCss_style style;
  
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
      case 'style': return this.@com.googlecode.kanbanik.client.modules.editworkflow.projects.ProjectWidget_MyUiBinderImpl_GenBundle::style()();
    }
    return null;
  }-*/;
}
