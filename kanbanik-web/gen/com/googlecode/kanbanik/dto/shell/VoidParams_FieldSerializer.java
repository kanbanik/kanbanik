package com.googlecode.kanbanik.dto.shell;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class VoidParams_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  public static void deserialize(SerializationStreamReader streamReader, com.googlecode.kanbanik.dto.shell.VoidParams instance) throws SerializationException {
    
  }
  
  public static com.googlecode.kanbanik.dto.shell.VoidParams instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new com.googlecode.kanbanik.dto.shell.VoidParams();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, com.googlecode.kanbanik.dto.shell.VoidParams instance) throws SerializationException {
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return com.googlecode.kanbanik.dto.shell.VoidParams_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    com.googlecode.kanbanik.dto.shell.VoidParams_FieldSerializer.deserialize(reader, (com.googlecode.kanbanik.dto.shell.VoidParams)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    com.googlecode.kanbanik.dto.shell.VoidParams_FieldSerializer.serialize(writer, (com.googlecode.kanbanik.dto.shell.VoidParams)object);
  }
  
}
