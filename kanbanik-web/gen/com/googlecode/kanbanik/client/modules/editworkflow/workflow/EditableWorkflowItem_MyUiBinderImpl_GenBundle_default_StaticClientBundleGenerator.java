package com.googlecode.kanbanik.client.modules.editworkflow.workflow;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class EditableWorkflowItem_MyUiBinderImpl_GenBundle_default_StaticClientBundleGenerator implements com.googlecode.kanbanik.client.modules.editworkflow.workflow.EditableWorkflowItem_MyUiBinderImpl_GenBundle {
  private static EditableWorkflowItem_MyUiBinderImpl_GenBundle_default_StaticClientBundleGenerator _instance0 = new EditableWorkflowItem_MyUiBinderImpl_GenBundle_default_StaticClientBundleGenerator();
  private void styleInitializer() {
    style = new com.googlecode.kanbanik.client.modules.editworkflow.workflow.EditableWorkflowItem_MyUiBinderImpl_GenCss_style() {
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
        return com.google.gwt.i18n.client.LocaleInfo.getCurrentLocale().isRTL() ? ((".GK40RFKDOJ{padding-bottom:" + ("7px")  + ";margin-bottom:" + ("4px")  + ";margin-top:" + ("2px")  + ";margin-left:" + ("2px")  + ";width:" + ("220px")  + ";text-align:" + ("left")  + ";}.GK40RFKDNJ{background-color:" + ("#d0e4f6")  + ";border-bottom-style:" + ("solid")  + ";border-bottom-width:" + ("1px")  + ";width:" + ("220px")  + ";text-align:") + (("center")  + ";}")) : ((".GK40RFKDOJ{padding-bottom:" + ("7px")  + ";margin-bottom:" + ("4px")  + ";margin-top:" + ("2px")  + ";margin-right:" + ("2px")  + ";width:" + ("220px")  + ";text-align:" + ("right")  + ";}.GK40RFKDNJ{background-color:" + ("#d0e4f6")  + ";border-bottom-style:" + ("solid")  + ";border-bottom-width:" + ("1px")  + ";width:" + ("220px")  + ";text-align:") + (("center")  + ";}"));
      }
      public java.lang.String workflowItemHeader(){
        return "GK40RFKDNJ";
      }
      public java.lang.String workflowItemToolbar(){
        return "GK40RFKDOJ";
      }
    }
    ;
  }
  private static class styleInitializer {
    static {
      _instance0.styleInitializer();
    }
    static com.googlecode.kanbanik.client.modules.editworkflow.workflow.EditableWorkflowItem_MyUiBinderImpl_GenCss_style get() {
      return style;
    }
  }
  public com.googlecode.kanbanik.client.modules.editworkflow.workflow.EditableWorkflowItem_MyUiBinderImpl_GenCss_style style() {
    return styleInitializer.get();
  }
  private static java.util.HashMap<java.lang.String, com.google.gwt.resources.client.ResourcePrototype> resourceMap;
  private static com.googlecode.kanbanik.client.modules.editworkflow.workflow.EditableWorkflowItem_MyUiBinderImpl_GenCss_style style;
  
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
      case 'style': return this.@com.googlecode.kanbanik.client.modules.editworkflow.workflow.EditableWorkflowItem_MyUiBinderImpl_GenBundle::style()();
    }
    return null;
  }-*/;
}
