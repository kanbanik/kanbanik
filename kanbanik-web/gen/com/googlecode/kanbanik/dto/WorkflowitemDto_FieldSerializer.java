package com.googlecode.kanbanik.dto;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class WorkflowitemDto_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native com.googlecode.kanbanik.dto.WorkflowitemDto getChild(com.googlecode.kanbanik.dto.WorkflowitemDto instance) /*-{
    return instance.@com.googlecode.kanbanik.dto.WorkflowitemDto::child;
  }-*/;
  
  private static native void setChild(com.googlecode.kanbanik.dto.WorkflowitemDto instance, com.googlecode.kanbanik.dto.WorkflowitemDto value) 
  /*-{
    instance.@com.googlecode.kanbanik.dto.WorkflowitemDto::child = value;
  }-*/;
  
  private static native java.lang.String getId(com.googlecode.kanbanik.dto.WorkflowitemDto instance) /*-{
    return instance.@com.googlecode.kanbanik.dto.WorkflowitemDto::id;
  }-*/;
  
  private static native void setId(com.googlecode.kanbanik.dto.WorkflowitemDto instance, java.lang.String value) 
  /*-{
    instance.@com.googlecode.kanbanik.dto.WorkflowitemDto::id = value;
  }-*/;
  
  private static native java.lang.String getName(com.googlecode.kanbanik.dto.WorkflowitemDto instance) /*-{
    return instance.@com.googlecode.kanbanik.dto.WorkflowitemDto::name;
  }-*/;
  
  private static native void setName(com.googlecode.kanbanik.dto.WorkflowitemDto instance, java.lang.String value) 
  /*-{
    instance.@com.googlecode.kanbanik.dto.WorkflowitemDto::name = value;
  }-*/;
  
  private static native com.googlecode.kanbanik.dto.WorkflowitemDto getNextItem(com.googlecode.kanbanik.dto.WorkflowitemDto instance) /*-{
    return instance.@com.googlecode.kanbanik.dto.WorkflowitemDto::nextItem;
  }-*/;
  
  private static native void setNextItem(com.googlecode.kanbanik.dto.WorkflowitemDto instance, com.googlecode.kanbanik.dto.WorkflowitemDto value) 
  /*-{
    instance.@com.googlecode.kanbanik.dto.WorkflowitemDto::nextItem = value;
  }-*/;
  
  private static native int getWipLimit(com.googlecode.kanbanik.dto.WorkflowitemDto instance) /*-{
    return instance.@com.googlecode.kanbanik.dto.WorkflowitemDto::wipLimit;
  }-*/;
  
  private static native void setWipLimit(com.googlecode.kanbanik.dto.WorkflowitemDto instance, int value) 
  /*-{
    instance.@com.googlecode.kanbanik.dto.WorkflowitemDto::wipLimit = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, com.googlecode.kanbanik.dto.WorkflowitemDto instance) throws SerializationException {
    setChild(instance, (com.googlecode.kanbanik.dto.WorkflowitemDto) streamReader.readObject());
    setId(instance, streamReader.readString());
    setName(instance, streamReader.readString());
    setNextItem(instance, (com.googlecode.kanbanik.dto.WorkflowitemDto) streamReader.readObject());
    setWipLimit(instance, streamReader.readInt());
    
  }
  
  public static com.googlecode.kanbanik.dto.WorkflowitemDto instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new com.googlecode.kanbanik.dto.WorkflowitemDto();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, com.googlecode.kanbanik.dto.WorkflowitemDto instance) throws SerializationException {
    streamWriter.writeObject(getChild(instance));
    streamWriter.writeString(getId(instance));
    streamWriter.writeString(getName(instance));
    streamWriter.writeObject(getNextItem(instance));
    streamWriter.writeInt(getWipLimit(instance));
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return com.googlecode.kanbanik.dto.WorkflowitemDto_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    com.googlecode.kanbanik.dto.WorkflowitemDto_FieldSerializer.deserialize(reader, (com.googlecode.kanbanik.dto.WorkflowitemDto)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    com.googlecode.kanbanik.dto.WorkflowitemDto_FieldSerializer.serialize(writer, (com.googlecode.kanbanik.dto.WorkflowitemDto)object);
  }
  
}
