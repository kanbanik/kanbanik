package com.googlecode.kanbanik.shared;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class ProjectDTO_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native int getId(com.googlecode.kanbanik.shared.ProjectDTO instance) /*-{
    return instance.@com.googlecode.kanbanik.shared.ProjectDTO::id;
  }-*/;
  
  private static native void setId(com.googlecode.kanbanik.shared.ProjectDTO instance, int value) 
  /*-{
    instance.@com.googlecode.kanbanik.shared.ProjectDTO::id = value;
  }-*/;
  
  private static native java.lang.String getName(com.googlecode.kanbanik.shared.ProjectDTO instance) /*-{
    return instance.@com.googlecode.kanbanik.shared.ProjectDTO::name;
  }-*/;
  
  private static native void setName(com.googlecode.kanbanik.shared.ProjectDTO instance, java.lang.String value) 
  /*-{
    instance.@com.googlecode.kanbanik.shared.ProjectDTO::name = value;
  }-*/;
  
  private static native java.util.List getTasks(com.googlecode.kanbanik.shared.ProjectDTO instance) /*-{
    return instance.@com.googlecode.kanbanik.shared.ProjectDTO::tasks;
  }-*/;
  
  private static native void setTasks(com.googlecode.kanbanik.shared.ProjectDTO instance, java.util.List value) 
  /*-{
    instance.@com.googlecode.kanbanik.shared.ProjectDTO::tasks = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, com.googlecode.kanbanik.shared.ProjectDTO instance) throws SerializationException {
    setId(instance, streamReader.readInt());
    setName(instance, streamReader.readString());
    setTasks(instance, (java.util.List) streamReader.readObject());
    
  }
  
  public static com.googlecode.kanbanik.shared.ProjectDTO instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new com.googlecode.kanbanik.shared.ProjectDTO();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, com.googlecode.kanbanik.shared.ProjectDTO instance) throws SerializationException {
    streamWriter.writeInt(getId(instance));
    streamWriter.writeString(getName(instance));
    streamWriter.writeObject(getTasks(instance));
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return com.googlecode.kanbanik.shared.ProjectDTO_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    com.googlecode.kanbanik.shared.ProjectDTO_FieldSerializer.deserialize(reader, (com.googlecode.kanbanik.shared.ProjectDTO)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    com.googlecode.kanbanik.shared.ProjectDTO_FieldSerializer.serialize(writer, (com.googlecode.kanbanik.shared.ProjectDTO)object);
  }
  
}
