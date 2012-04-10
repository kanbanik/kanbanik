package com.googlecode.kanbanik.shared;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class BoardDTO_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native int getId(com.googlecode.kanbanik.shared.BoardDTO instance) /*-{
    return instance.@com.googlecode.kanbanik.shared.BoardDTO::id;
  }-*/;
  
  private static native void setId(com.googlecode.kanbanik.shared.BoardDTO instance, int value) 
  /*-{
    instance.@com.googlecode.kanbanik.shared.BoardDTO::id = value;
  }-*/;
  
  private static native java.lang.String getName(com.googlecode.kanbanik.shared.BoardDTO instance) /*-{
    return instance.@com.googlecode.kanbanik.shared.BoardDTO::name;
  }-*/;
  
  private static native void setName(com.googlecode.kanbanik.shared.BoardDTO instance, java.lang.String value) 
  /*-{
    instance.@com.googlecode.kanbanik.shared.BoardDTO::name = value;
  }-*/;
  
  private static native java.util.List getProjects(com.googlecode.kanbanik.shared.BoardDTO instance) /*-{
    return instance.@com.googlecode.kanbanik.shared.BoardDTO::projects;
  }-*/;
  
  private static native void setProjects(com.googlecode.kanbanik.shared.BoardDTO instance, java.util.List value) 
  /*-{
    instance.@com.googlecode.kanbanik.shared.BoardDTO::projects = value;
  }-*/;
  
  private static native com.googlecode.kanbanik.shared.WorkflowDTO getWorkflow(com.googlecode.kanbanik.shared.BoardDTO instance) /*-{
    return instance.@com.googlecode.kanbanik.shared.BoardDTO::workflow;
  }-*/;
  
  private static native void setWorkflow(com.googlecode.kanbanik.shared.BoardDTO instance, com.googlecode.kanbanik.shared.WorkflowDTO value) 
  /*-{
    instance.@com.googlecode.kanbanik.shared.BoardDTO::workflow = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, com.googlecode.kanbanik.shared.BoardDTO instance) throws SerializationException {
    setId(instance, streamReader.readInt());
    setName(instance, streamReader.readString());
    setProjects(instance, (java.util.List) streamReader.readObject());
    setWorkflow(instance, (com.googlecode.kanbanik.shared.WorkflowDTO) streamReader.readObject());
    
  }
  
  public static com.googlecode.kanbanik.shared.BoardDTO instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new com.googlecode.kanbanik.shared.BoardDTO();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, com.googlecode.kanbanik.shared.BoardDTO instance) throws SerializationException {
    streamWriter.writeInt(getId(instance));
    streamWriter.writeString(getName(instance));
    streamWriter.writeObject(getProjects(instance));
    streamWriter.writeObject(getWorkflow(instance));
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return com.googlecode.kanbanik.shared.BoardDTO_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    com.googlecode.kanbanik.shared.BoardDTO_FieldSerializer.deserialize(reader, (com.googlecode.kanbanik.shared.BoardDTO)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    com.googlecode.kanbanik.shared.BoardDTO_FieldSerializer.serialize(writer, (com.googlecode.kanbanik.shared.BoardDTO)object);
  }
  
}
