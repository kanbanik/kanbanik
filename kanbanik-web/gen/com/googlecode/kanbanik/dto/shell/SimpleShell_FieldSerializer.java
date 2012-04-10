package com.googlecode.kanbanik.dto.shell;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class SimpleShell_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native com.googlecode.kanbanik.dto.KanbanikDto getPayload(com.googlecode.kanbanik.dto.shell.SimpleShell instance) /*-{
    return instance.@com.googlecode.kanbanik.dto.shell.SimpleShell::payload;
  }-*/;
  
  private static native void setPayload(com.googlecode.kanbanik.dto.shell.SimpleShell instance, com.googlecode.kanbanik.dto.KanbanikDto value) 
  /*-{
    instance.@com.googlecode.kanbanik.dto.shell.SimpleShell::payload = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, com.googlecode.kanbanik.dto.shell.SimpleShell instance) throws SerializationException {
    setPayload(instance, (com.googlecode.kanbanik.dto.KanbanikDto) streamReader.readObject());
    
  }
  
  public static com.googlecode.kanbanik.dto.shell.SimpleShell instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new com.googlecode.kanbanik.dto.shell.SimpleShell();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, com.googlecode.kanbanik.dto.shell.SimpleShell instance) throws SerializationException {
    streamWriter.writeObject(getPayload(instance));
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return com.googlecode.kanbanik.dto.shell.SimpleShell_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    com.googlecode.kanbanik.dto.shell.SimpleShell_FieldSerializer.deserialize(reader, (com.googlecode.kanbanik.dto.shell.SimpleShell)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    com.googlecode.kanbanik.dto.shell.SimpleShell_FieldSerializer.serialize(writer, (com.googlecode.kanbanik.dto.shell.SimpleShell)object);
  }
  
}
