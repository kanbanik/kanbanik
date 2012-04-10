package com.googlecode.kanbanik.shared;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class ClassOfServiceDTO_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  public static void deserialize(SerializationStreamReader streamReader, com.googlecode.kanbanik.shared.ClassOfServiceDTO instance) throws SerializationException {
    // Enum deserialization is handled via the instantiate method
  }
  
  public static com.googlecode.kanbanik.shared.ClassOfServiceDTO instantiate(SerializationStreamReader streamReader) throws SerializationException {
    int ordinal = streamReader.readInt();
    com.googlecode.kanbanik.shared.ClassOfServiceDTO[] values = com.googlecode.kanbanik.shared.ClassOfServiceDTO.values();
    assert (ordinal >= 0 && ordinal < values.length);
    return values[ordinal];
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, com.googlecode.kanbanik.shared.ClassOfServiceDTO instance) throws SerializationException {
    assert (instance != null);
    streamWriter.writeInt(instance.ordinal());
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return com.googlecode.kanbanik.shared.ClassOfServiceDTO_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    com.googlecode.kanbanik.shared.ClassOfServiceDTO_FieldSerializer.deserialize(reader, (com.googlecode.kanbanik.shared.ClassOfServiceDTO)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    com.googlecode.kanbanik.shared.ClassOfServiceDTO_FieldSerializer.serialize(writer, (com.googlecode.kanbanik.shared.ClassOfServiceDTO)object);
  }
  
}
