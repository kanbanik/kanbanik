package com.googlecode.kanbanik.shared;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class RegularItemDTO_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native com.googlecode.kanbanik.shared.WorkflowItemPlaceDTO getPlace(com.googlecode.kanbanik.shared.RegularItemDTO instance) /*-{
    return instance.@com.googlecode.kanbanik.shared.RegularItemDTO::place;
  }-*/;
  
  private static native void setPlace(com.googlecode.kanbanik.shared.RegularItemDTO instance, com.googlecode.kanbanik.shared.WorkflowItemPlaceDTO value) 
  /*-{
    instance.@com.googlecode.kanbanik.shared.RegularItemDTO::place = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, com.googlecode.kanbanik.shared.RegularItemDTO instance) throws SerializationException {
    setPlace(instance, (com.googlecode.kanbanik.shared.WorkflowItemPlaceDTO) streamReader.readObject());
    
    com.googlecode.kanbanik.shared.WorkflowItemDTO_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static com.googlecode.kanbanik.shared.RegularItemDTO instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new com.googlecode.kanbanik.shared.RegularItemDTO();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, com.googlecode.kanbanik.shared.RegularItemDTO instance) throws SerializationException {
    streamWriter.writeObject(getPlace(instance));
    
    com.googlecode.kanbanik.shared.WorkflowItemDTO_FieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return com.googlecode.kanbanik.shared.RegularItemDTO_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    com.googlecode.kanbanik.shared.RegularItemDTO_FieldSerializer.deserialize(reader, (com.googlecode.kanbanik.shared.RegularItemDTO)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    com.googlecode.kanbanik.shared.RegularItemDTO_FieldSerializer.serialize(writer, (com.googlecode.kanbanik.shared.RegularItemDTO)object);
  }
  
}
