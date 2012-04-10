package com.googlecode.kanbanik.dto;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class ListDto_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.util.List getList(com.googlecode.kanbanik.dto.ListDto instance) /*-{
    return instance.@com.googlecode.kanbanik.dto.ListDto::list;
  }-*/;
  
  private static native void setList(com.googlecode.kanbanik.dto.ListDto instance, java.util.List value) 
  /*-{
    instance.@com.googlecode.kanbanik.dto.ListDto::list = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, com.googlecode.kanbanik.dto.ListDto instance) throws SerializationException {
    setList(instance, (java.util.List) streamReader.readObject());
    
  }
  
  public static com.googlecode.kanbanik.dto.ListDto instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new com.googlecode.kanbanik.dto.ListDto();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, com.googlecode.kanbanik.dto.ListDto instance) throws SerializationException {
    streamWriter.writeObject(getList(instance));
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return com.googlecode.kanbanik.dto.ListDto_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    com.googlecode.kanbanik.dto.ListDto_FieldSerializer.deserialize(reader, (com.googlecode.kanbanik.dto.ListDto)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    com.googlecode.kanbanik.dto.ListDto_FieldSerializer.serialize(writer, (com.googlecode.kanbanik.dto.ListDto)object);
  }
  
}
