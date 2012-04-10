package com.googlecode.kanbanik.shared;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class WorkflowItemDTO_FieldSerializer {
  private static native int getId(com.googlecode.kanbanik.shared.WorkflowItemDTO instance) /*-{
    return instance.@com.googlecode.kanbanik.shared.WorkflowItemDTO::id;
  }-*/;
  
  private static native void setId(com.googlecode.kanbanik.shared.WorkflowItemDTO instance, int value) 
  /*-{
    instance.@com.googlecode.kanbanik.shared.WorkflowItemDTO::id = value;
  }-*/;
  
  private static native java.lang.String getName(com.googlecode.kanbanik.shared.WorkflowItemDTO instance) /*-{
    return instance.@com.googlecode.kanbanik.shared.WorkflowItemDTO::name;
  }-*/;
  
  private static native void setName(com.googlecode.kanbanik.shared.WorkflowItemDTO instance, java.lang.String value) 
  /*-{
    instance.@com.googlecode.kanbanik.shared.WorkflowItemDTO::name = value;
  }-*/;
  
  private static native int getNextId(com.googlecode.kanbanik.shared.WorkflowItemDTO instance) /*-{
    return instance.@com.googlecode.kanbanik.shared.WorkflowItemDTO::nextId;
  }-*/;
  
  private static native void setNextId(com.googlecode.kanbanik.shared.WorkflowItemDTO instance, int value) 
  /*-{
    instance.@com.googlecode.kanbanik.shared.WorkflowItemDTO::nextId = value;
  }-*/;
  
  private static native int getWipLimit(com.googlecode.kanbanik.shared.WorkflowItemDTO instance) /*-{
    return instance.@com.googlecode.kanbanik.shared.WorkflowItemDTO::wipLimit;
  }-*/;
  
  private static native void setWipLimit(com.googlecode.kanbanik.shared.WorkflowItemDTO instance, int value) 
  /*-{
    instance.@com.googlecode.kanbanik.shared.WorkflowItemDTO::wipLimit = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, com.googlecode.kanbanik.shared.WorkflowItemDTO instance) throws SerializationException {
    setId(instance, streamReader.readInt());
    setName(instance, streamReader.readString());
    setNextId(instance, streamReader.readInt());
    setWipLimit(instance, streamReader.readInt());
    
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, com.googlecode.kanbanik.shared.WorkflowItemDTO instance) throws SerializationException {
    streamWriter.writeInt(getId(instance));
    streamWriter.writeString(getName(instance));
    streamWriter.writeInt(getNextId(instance));
    streamWriter.writeInt(getWipLimit(instance));
    
  }
  
}
