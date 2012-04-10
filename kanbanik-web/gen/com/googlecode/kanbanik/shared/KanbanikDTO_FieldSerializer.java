package com.googlecode.kanbanik.shared;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class KanbanikDTO_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.util.List getBoards(com.googlecode.kanbanik.shared.KanbanikDTO instance) /*-{
    return instance.@com.googlecode.kanbanik.shared.KanbanikDTO::boards;
  }-*/;
  
  private static native void setBoards(com.googlecode.kanbanik.shared.KanbanikDTO instance, java.util.List value) 
  /*-{
    instance.@com.googlecode.kanbanik.shared.KanbanikDTO::boards = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, com.googlecode.kanbanik.shared.KanbanikDTO instance) throws SerializationException {
    setBoards(instance, (java.util.List) streamReader.readObject());
    
  }
  
  public static com.googlecode.kanbanik.shared.KanbanikDTO instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new com.googlecode.kanbanik.shared.KanbanikDTO();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, com.googlecode.kanbanik.shared.KanbanikDTO instance) throws SerializationException {
    streamWriter.writeObject(getBoards(instance));
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return com.googlecode.kanbanik.shared.KanbanikDTO_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    com.googlecode.kanbanik.shared.KanbanikDTO_FieldSerializer.deserialize(reader, (com.googlecode.kanbanik.shared.KanbanikDTO)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    com.googlecode.kanbanik.shared.KanbanikDTO_FieldSerializer.serialize(writer, (com.googlecode.kanbanik.shared.KanbanikDTO)object);
  }
  
}
