package com.googlecode.kanbanik.dto;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class BoardsDto_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.util.List getBoards(com.googlecode.kanbanik.dto.BoardsDto instance) /*-{
    return instance.@com.googlecode.kanbanik.dto.BoardsDto::boards;
  }-*/;
  
  private static native void setBoards(com.googlecode.kanbanik.dto.BoardsDto instance, java.util.List value) 
  /*-{
    instance.@com.googlecode.kanbanik.dto.BoardsDto::boards = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, com.googlecode.kanbanik.dto.BoardsDto instance) throws SerializationException {
    setBoards(instance, (java.util.List) streamReader.readObject());
    
  }
  
  public static com.googlecode.kanbanik.dto.BoardsDto instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new com.googlecode.kanbanik.dto.BoardsDto();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, com.googlecode.kanbanik.dto.BoardsDto instance) throws SerializationException {
    streamWriter.writeObject(getBoards(instance));
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return com.googlecode.kanbanik.dto.BoardsDto_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    com.googlecode.kanbanik.dto.BoardsDto_FieldSerializer.deserialize(reader, (com.googlecode.kanbanik.dto.BoardsDto)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    com.googlecode.kanbanik.dto.BoardsDto_FieldSerializer.serialize(writer, (com.googlecode.kanbanik.dto.BoardsDto)object);
  }
  
}
