package com.googlecode.kanbanik.dto;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class ProjectDto_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.lang.String getName(com.googlecode.kanbanik.dto.ProjectDto instance) /*-{
    return instance.@com.googlecode.kanbanik.dto.ProjectDto::name;
  }-*/;
  
  private static native void setName(com.googlecode.kanbanik.dto.ProjectDto instance, java.lang.String value) 
  /*-{
    instance.@com.googlecode.kanbanik.dto.ProjectDto::name = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, com.googlecode.kanbanik.dto.ProjectDto instance) throws SerializationException {
    setName(instance, streamReader.readString());
    
  }
  
  public static com.googlecode.kanbanik.dto.ProjectDto instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new com.googlecode.kanbanik.dto.ProjectDto();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, com.googlecode.kanbanik.dto.ProjectDto instance) throws SerializationException {
    streamWriter.writeString(getName(instance));
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return com.googlecode.kanbanik.dto.ProjectDto_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    com.googlecode.kanbanik.dto.ProjectDto_FieldSerializer.deserialize(reader, (com.googlecode.kanbanik.dto.ProjectDto)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    com.googlecode.kanbanik.dto.ProjectDto_FieldSerializer.serialize(writer, (com.googlecode.kanbanik.dto.ProjectDto)object);
  }
  
}
