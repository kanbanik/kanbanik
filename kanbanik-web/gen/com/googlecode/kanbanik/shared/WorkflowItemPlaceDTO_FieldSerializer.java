package com.googlecode.kanbanik.shared;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class WorkflowItemPlaceDTO_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native int getId(com.googlecode.kanbanik.shared.WorkflowItemPlaceDTO instance) /*-{
    return instance.@com.googlecode.kanbanik.shared.WorkflowItemPlaceDTO::id;
  }-*/;
  
  private static native void setId(com.googlecode.kanbanik.shared.WorkflowItemPlaceDTO instance, int value) 
  /*-{
    instance.@com.googlecode.kanbanik.shared.WorkflowItemPlaceDTO::id = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, com.googlecode.kanbanik.shared.WorkflowItemPlaceDTO instance) throws SerializationException {
    setId(instance, streamReader.readInt());
    
  }
  
  public static com.googlecode.kanbanik.shared.WorkflowItemPlaceDTO instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new com.googlecode.kanbanik.shared.WorkflowItemPlaceDTO();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, com.googlecode.kanbanik.shared.WorkflowItemPlaceDTO instance) throws SerializationException {
    streamWriter.writeInt(getId(instance));
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return com.googlecode.kanbanik.shared.WorkflowItemPlaceDTO_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    com.googlecode.kanbanik.shared.WorkflowItemPlaceDTO_FieldSerializer.deserialize(reader, (com.googlecode.kanbanik.shared.WorkflowItemPlaceDTO)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    com.googlecode.kanbanik.shared.WorkflowItemPlaceDTO_FieldSerializer.serialize(writer, (com.googlecode.kanbanik.shared.WorkflowItemPlaceDTO)object);
  }
  
}
