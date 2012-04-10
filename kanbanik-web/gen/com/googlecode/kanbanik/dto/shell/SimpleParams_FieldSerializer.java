package com.googlecode.kanbanik.dto.shell;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class SimpleParams_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native com.googlecode.kanbanik.dto.KanbanikDto getPayload(com.googlecode.kanbanik.dto.shell.SimpleParams instance) /*-{
    return instance.@com.googlecode.kanbanik.dto.shell.SimpleParams::payload;
  }-*/;
  
  private static native void setPayload(com.googlecode.kanbanik.dto.shell.SimpleParams instance, com.googlecode.kanbanik.dto.KanbanikDto value) 
  /*-{
    instance.@com.googlecode.kanbanik.dto.shell.SimpleParams::payload = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, com.googlecode.kanbanik.dto.shell.SimpleParams instance) throws SerializationException {
    setPayload(instance, (com.googlecode.kanbanik.dto.KanbanikDto) streamReader.readObject());
    
  }
  
  public static com.googlecode.kanbanik.dto.shell.SimpleParams instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new com.googlecode.kanbanik.dto.shell.SimpleParams();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, com.googlecode.kanbanik.dto.shell.SimpleParams instance) throws SerializationException {
    streamWriter.writeObject(getPayload(instance));
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return com.googlecode.kanbanik.dto.shell.SimpleParams_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    com.googlecode.kanbanik.dto.shell.SimpleParams_FieldSerializer.deserialize(reader, (com.googlecode.kanbanik.dto.shell.SimpleParams)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    com.googlecode.kanbanik.dto.shell.SimpleParams_FieldSerializer.serialize(writer, (com.googlecode.kanbanik.dto.shell.SimpleParams)object);
  }
  
}
