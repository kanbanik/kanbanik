package com.googlecode.kanbanik.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class KanbanikResources_default_StaticClientBundleGenerator implements com.googlecode.kanbanik.client.KanbanikResources {
  private static KanbanikResources_default_StaticClientBundleGenerator _instance0 = new KanbanikResources_default_StaticClientBundleGenerator();
  private void addButtonImageInitializer() {
    addButtonImage = new com.google.gwt.resources.client.impl.ImageResourcePrototype(
      "addButtonImage",
      com.google.gwt.safehtml.shared.UriUtils.fromTrustedString(bundledImage_None),
      32, 0, 16, 16, false, false
    );
  }
  private static class addButtonImageInitializer {
    static {
      _instance0.addButtonImageInitializer();
    }
    static com.google.gwt.resources.client.ImageResource get() {
      return addButtonImage;
    }
  }
  public com.google.gwt.resources.client.ImageResource addButtonImage() {
    return addButtonImageInitializer.get();
  }
  private void deleteButtonImageInitializer() {
    deleteButtonImage = new com.google.gwt.resources.client.impl.ImageResourcePrototype(
      "deleteButtonImage",
      com.google.gwt.safehtml.shared.UriUtils.fromTrustedString(bundledImage_None),
      16, 0, 16, 16, false, false
    );
  }
  private static class deleteButtonImageInitializer {
    static {
      _instance0.deleteButtonImageInitializer();
    }
    static com.google.gwt.resources.client.ImageResource get() {
      return deleteButtonImage;
    }
  }
  public com.google.gwt.resources.client.ImageResource deleteButtonImage() {
    return deleteButtonImageInitializer.get();
  }
  private void editButtonImageInitializer() {
    editButtonImage = new com.google.gwt.resources.client.impl.ImageResourcePrototype(
      "editButtonImage",
      com.google.gwt.safehtml.shared.UriUtils.fromTrustedString(bundledImage_None),
      0, 0, 16, 16, false, false
    );
  }
  private static class editButtonImageInitializer {
    static {
      _instance0.editButtonImageInitializer();
    }
    static com.google.gwt.resources.client.ImageResource get() {
      return editButtonImage;
    }
  }
  public com.google.gwt.resources.client.ImageResource editButtonImage() {
    return editButtonImageInitializer.get();
  }
  private static java.util.HashMap<java.lang.String, com.google.gwt.resources.client.ResourcePrototype> resourceMap;
  private static final java.lang.String bundledImage_None = GWT.getModuleBaseURL() + "2B98559B47DC2B65547A56F2DFF51C6C.cache.png";
  private static com.google.gwt.resources.client.ImageResource addButtonImage;
  private static com.google.gwt.resources.client.ImageResource deleteButtonImage;
  private static com.google.gwt.resources.client.ImageResource editButtonImage;
  
  public ResourcePrototype[] getResources() {
    return new ResourcePrototype[] {
      addButtonImage(), 
      deleteButtonImage(), 
      editButtonImage(), 
    };
  }
  public ResourcePrototype getResource(String name) {
    if (GWT.isScript()) {
      return getResourceNative(name);
    } else {
      if (resourceMap == null) {
        resourceMap = new java.util.HashMap<java.lang.String, com.google.gwt.resources.client.ResourcePrototype>();
        resourceMap.put("addButtonImage", addButtonImage());
        resourceMap.put("deleteButtonImage", deleteButtonImage());
        resourceMap.put("editButtonImage", editButtonImage());
      }
      return resourceMap.get(name);
    }
  }
  private native ResourcePrototype getResourceNative(String name) /*-{
    switch (name) {
      case 'addButtonImage': return this.@com.googlecode.kanbanik.client.KanbanikResources::addButtonImage()();
      case 'deleteButtonImage': return this.@com.googlecode.kanbanik.client.KanbanikResources::deleteButtonImage()();
      case 'editButtonImage': return this.@com.googlecode.kanbanik.client.KanbanikResources::editButtonImage()();
    }
    return null;
  }-*/;
}
