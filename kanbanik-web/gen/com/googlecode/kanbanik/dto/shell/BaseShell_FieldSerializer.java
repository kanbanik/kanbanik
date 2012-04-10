package com.googlecode.kanbanik.dto.shell;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class BaseShell_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  public static void deserialize(SerializationStreamReader streamReader, com.googlecode.kanbanik.dto.shell.BaseShell instance) throws SerializationException {
    
  }
  
  public static com.googlecode.kanbanik.dto.shell.BaseShell instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new com.googlecode.kanbanik.dto.shell.BaseShell();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, com.googlecode.kanbanik.dto.shell.BaseShell instance) throws SerializationException {
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return com.googlecode.kanbanik.dto.shell.BaseShell_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    com.googlecode.kanbanik.dto.shell.BaseShell_FieldSerializer.deserialize(reader, (com.googlecode.kanbanik.dto.shell.BaseShell)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    com.googlecode.kanbanik.dto.shell.BaseShell_FieldSerializer.serialize(writer, (com.googlecode.kanbanik.dto.shell.BaseShell)object);
  }
  
}
