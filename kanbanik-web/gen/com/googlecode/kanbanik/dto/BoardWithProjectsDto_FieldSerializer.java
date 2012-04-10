package com.googlecode.kanbanik.dto;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class BoardWithProjectsDto_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native com.googlecode.kanbanik.dto.BoardDto getBoard(com.googlecode.kanbanik.dto.BoardWithProjectsDto instance) /*-{
    return instance.@com.googlecode.kanbanik.dto.BoardWithProjectsDto::board;
  }-*/;
  
  private static native void setBoard(com.googlecode.kanbanik.dto.BoardWithProjectsDto instance, com.googlecode.kanbanik.dto.BoardDto value) 
  /*-{
    instance.@com.googlecode.kanbanik.dto.BoardWithProjectsDto::board = value;
  }-*/;
  
  private static native java.util.List getProjectsOnBoard(com.googlecode.kanbanik.dto.BoardWithProjectsDto instance) /*-{
    return instance.@com.googlecode.kanbanik.dto.BoardWithProjectsDto::projectsOnBoard;
  }-*/;
  
  private static native void setProjectsOnBoard(com.googlecode.kanbanik.dto.BoardWithProjectsDto instance, java.util.List value) 
  /*-{
    instance.@com.googlecode.kanbanik.dto.BoardWithProjectsDto::projectsOnBoard = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, com.googlecode.kanbanik.dto.BoardWithProjectsDto instance) throws SerializationException {
    setBoard(instance, (com.googlecode.kanbanik.dto.BoardDto) streamReader.readObject());
    setProjectsOnBoard(instance, (java.util.List) streamReader.readObject());
    
  }
  
  public static com.googlecode.kanbanik.dto.BoardWithProjectsDto instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new com.googlecode.kanbanik.dto.BoardWithProjectsDto();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, com.googlecode.kanbanik.dto.BoardWithProjectsDto instance) throws SerializationException {
    streamWriter.writeObject(getBoard(instance));
    streamWriter.writeObject(getProjectsOnBoard(instance));
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return com.googlecode.kanbanik.dto.BoardWithProjectsDto_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    com.googlecode.kanbanik.dto.BoardWithProjectsDto_FieldSerializer.deserialize(reader, (com.googlecode.kanbanik.dto.BoardWithProjectsDto)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    com.googlecode.kanbanik.dto.BoardWithProjectsDto_FieldSerializer.serialize(writer, (com.googlecode.kanbanik.dto.BoardWithProjectsDto)object);
  }
  
}
