package com.googlecode.kanbanik.shared;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class QueuedItemDTO_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.util.List getPlaces(com.googlecode.kanbanik.shared.QueuedItemDTO instance) /*-{
    return instance.@com.googlecode.kanbanik.shared.QueuedItemDTO::places;
  }-*/;
  
  private static native void setPlaces(com.googlecode.kanbanik.shared.QueuedItemDTO instance, java.util.List value) 
  /*-{
    instance.@com.googlecode.kanbanik.shared.QueuedItemDTO::places = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, com.googlecode.kanbanik.shared.QueuedItemDTO instance) throws SerializationException {
    setPlaces(instance, (java.util.List) streamReader.readObject());
    
    com.googlecode.kanbanik.shared.WorkflowItemDTO_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static com.googlecode.kanbanik.shared.QueuedItemDTO instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new com.googlecode.kanbanik.shared.QueuedItemDTO();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, com.googlecode.kanbanik.shared.QueuedItemDTO instance) throws SerializationException {
    streamWriter.writeObject(getPlaces(instance));
    
    com.googlecode.kanbanik.shared.WorkflowItemDTO_FieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return com.googlecode.kanbanik.shared.QueuedItemDTO_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    com.googlecode.kanbanik.shared.QueuedItemDTO_FieldSerializer.deserialize(reader, (com.googlecode.kanbanik.shared.QueuedItemDTO)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    com.googlecode.kanbanik.shared.QueuedItemDTO_FieldSerializer.serialize(writer, (com.googlecode.kanbanik.shared.QueuedItemDTO)object);
  }
  
}
