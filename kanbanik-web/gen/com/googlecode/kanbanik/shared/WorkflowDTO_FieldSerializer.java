package com.googlecode.kanbanik.shared;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class WorkflowDTO_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native int getId(com.googlecode.kanbanik.shared.WorkflowDTO instance) /*-{
    return instance.@com.googlecode.kanbanik.shared.WorkflowDTO::id;
  }-*/;
  
  private static native void setId(com.googlecode.kanbanik.shared.WorkflowDTO instance, int value) 
  /*-{
    instance.@com.googlecode.kanbanik.shared.WorkflowDTO::id = value;
  }-*/;
  
  private static native java.util.List getWorkflowItems(com.googlecode.kanbanik.shared.WorkflowDTO instance) /*-{
    return instance.@com.googlecode.kanbanik.shared.WorkflowDTO::workflowItems;
  }-*/;
  
  private static native void setWorkflowItems(com.googlecode.kanbanik.shared.WorkflowDTO instance, java.util.List value) 
  /*-{
    instance.@com.googlecode.kanbanik.shared.WorkflowDTO::workflowItems = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, com.googlecode.kanbanik.shared.WorkflowDTO instance) throws SerializationException {
    setId(instance, streamReader.readInt());
    setWorkflowItems(instance, (java.util.List) streamReader.readObject());
    
  }
  
  public static com.googlecode.kanbanik.shared.WorkflowDTO instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new com.googlecode.kanbanik.shared.WorkflowDTO();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, com.googlecode.kanbanik.shared.WorkflowDTO instance) throws SerializationException {
    streamWriter.writeInt(getId(instance));
    streamWriter.writeObject(getWorkflowItems(instance));
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return com.googlecode.kanbanik.shared.WorkflowDTO_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    com.googlecode.kanbanik.shared.WorkflowDTO_FieldSerializer.deserialize(reader, (com.googlecode.kanbanik.shared.WorkflowDTO)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    com.googlecode.kanbanik.shared.WorkflowDTO_FieldSerializer.serialize(writer, (com.googlecode.kanbanik.shared.WorkflowDTO)object);
  }
  
}
