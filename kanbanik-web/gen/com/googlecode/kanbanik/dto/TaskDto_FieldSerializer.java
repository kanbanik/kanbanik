package com.googlecode.kanbanik.dto;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class TaskDto_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native int getClassOfService(com.googlecode.kanbanik.dto.TaskDto instance) /*-{
    return instance.@com.googlecode.kanbanik.dto.TaskDto::classOfService;
  }-*/;
  
  private static native void setClassOfService(com.googlecode.kanbanik.dto.TaskDto instance, int value) 
  /*-{
    instance.@com.googlecode.kanbanik.dto.TaskDto::classOfService = value;
  }-*/;
  
  private static native java.lang.String getDescription(com.googlecode.kanbanik.dto.TaskDto instance) /*-{
    return instance.@com.googlecode.kanbanik.dto.TaskDto::description;
  }-*/;
  
  private static native void setDescription(com.googlecode.kanbanik.dto.TaskDto instance, java.lang.String value) 
  /*-{
    instance.@com.googlecode.kanbanik.dto.TaskDto::description = value;
  }-*/;
  
  private static native java.lang.String getId(com.googlecode.kanbanik.dto.TaskDto instance) /*-{
    return instance.@com.googlecode.kanbanik.dto.TaskDto::id;
  }-*/;
  
  private static native void setId(com.googlecode.kanbanik.dto.TaskDto instance, java.lang.String value) 
  /*-{
    instance.@com.googlecode.kanbanik.dto.TaskDto::id = value;
  }-*/;
  
  private static native java.lang.String getName(com.googlecode.kanbanik.dto.TaskDto instance) /*-{
    return instance.@com.googlecode.kanbanik.dto.TaskDto::name;
  }-*/;
  
  private static native void setName(com.googlecode.kanbanik.dto.TaskDto instance, java.lang.String value) 
  /*-{
    instance.@com.googlecode.kanbanik.dto.TaskDto::name = value;
  }-*/;
  
  private static native com.googlecode.kanbanik.dto.WorkflowitemDto getWorkflowitem(com.googlecode.kanbanik.dto.TaskDto instance) /*-{
    return instance.@com.googlecode.kanbanik.dto.TaskDto::workflowitem;
  }-*/;
  
  private static native void setWorkflowitem(com.googlecode.kanbanik.dto.TaskDto instance, com.googlecode.kanbanik.dto.WorkflowitemDto value) 
  /*-{
    instance.@com.googlecode.kanbanik.dto.TaskDto::workflowitem = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, com.googlecode.kanbanik.dto.TaskDto instance) throws SerializationException {
    setClassOfService(instance, streamReader.readInt());
    setDescription(instance, streamReader.readString());
    setId(instance, streamReader.readString());
    setName(instance, streamReader.readString());
    setWorkflowitem(instance, (com.googlecode.kanbanik.dto.WorkflowitemDto) streamReader.readObject());
    
  }
  
  public static com.googlecode.kanbanik.dto.TaskDto instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new com.googlecode.kanbanik.dto.TaskDto();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, com.googlecode.kanbanik.dto.TaskDto instance) throws SerializationException {
    streamWriter.writeInt(getClassOfService(instance));
    streamWriter.writeString(getDescription(instance));
    streamWriter.writeString(getId(instance));
    streamWriter.writeString(getName(instance));
    streamWriter.writeObject(getWorkflowitem(instance));
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return com.googlecode.kanbanik.dto.TaskDto_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    com.googlecode.kanbanik.dto.TaskDto_FieldSerializer.deserialize(reader, (com.googlecode.kanbanik.dto.TaskDto)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    com.googlecode.kanbanik.dto.TaskDto_FieldSerializer.serialize(writer, (com.googlecode.kanbanik.dto.TaskDto)object);
  }
  
}
