package com.googlecode.kanbanik.shared;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class TaskDTO_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native com.googlecode.kanbanik.shared.ClassOfServiceDTO getClassOfService(com.googlecode.kanbanik.shared.TaskDTO instance) /*-{
    return instance.@com.googlecode.kanbanik.shared.TaskDTO::classOfService;
  }-*/;
  
  private static native void setClassOfService(com.googlecode.kanbanik.shared.TaskDTO instance, com.googlecode.kanbanik.shared.ClassOfServiceDTO value) 
  /*-{
    instance.@com.googlecode.kanbanik.shared.TaskDTO::classOfService = value;
  }-*/;
  
  private static native java.lang.String getDescription(com.googlecode.kanbanik.shared.TaskDTO instance) /*-{
    return instance.@com.googlecode.kanbanik.shared.TaskDTO::description;
  }-*/;
  
  private static native void setDescription(com.googlecode.kanbanik.shared.TaskDTO instance, java.lang.String value) 
  /*-{
    instance.@com.googlecode.kanbanik.shared.TaskDTO::description = value;
  }-*/;
  
  private static native int getId(com.googlecode.kanbanik.shared.TaskDTO instance) /*-{
    return instance.@com.googlecode.kanbanik.shared.TaskDTO::id;
  }-*/;
  
  private static native void setId(com.googlecode.kanbanik.shared.TaskDTO instance, int value) 
  /*-{
    instance.@com.googlecode.kanbanik.shared.TaskDTO::id = value;
  }-*/;
  
  private static native java.lang.String getName(com.googlecode.kanbanik.shared.TaskDTO instance) /*-{
    return instance.@com.googlecode.kanbanik.shared.TaskDTO::name;
  }-*/;
  
  private static native void setName(com.googlecode.kanbanik.shared.TaskDTO instance, java.lang.String value) 
  /*-{
    instance.@com.googlecode.kanbanik.shared.TaskDTO::name = value;
  }-*/;
  
  private static native com.googlecode.kanbanik.shared.WorkflowItemPlaceDTO getPlace(com.googlecode.kanbanik.shared.TaskDTO instance) /*-{
    return instance.@com.googlecode.kanbanik.shared.TaskDTO::place;
  }-*/;
  
  private static native void setPlace(com.googlecode.kanbanik.shared.TaskDTO instance, com.googlecode.kanbanik.shared.WorkflowItemPlaceDTO value) 
  /*-{
    instance.@com.googlecode.kanbanik.shared.TaskDTO::place = value;
  }-*/;
  
  private static native com.googlecode.kanbanik.shared.ProjectDTO getProject(com.googlecode.kanbanik.shared.TaskDTO instance) /*-{
    return instance.@com.googlecode.kanbanik.shared.TaskDTO::project;
  }-*/;
  
  private static native void setProject(com.googlecode.kanbanik.shared.TaskDTO instance, com.googlecode.kanbanik.shared.ProjectDTO value) 
  /*-{
    instance.@com.googlecode.kanbanik.shared.TaskDTO::project = value;
  }-*/;
  
  private static native java.lang.String getTicketId(com.googlecode.kanbanik.shared.TaskDTO instance) /*-{
    return instance.@com.googlecode.kanbanik.shared.TaskDTO::ticketId;
  }-*/;
  
  private static native void setTicketId(com.googlecode.kanbanik.shared.TaskDTO instance, java.lang.String value) 
  /*-{
    instance.@com.googlecode.kanbanik.shared.TaskDTO::ticketId = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, com.googlecode.kanbanik.shared.TaskDTO instance) throws SerializationException {
    setClassOfService(instance, (com.googlecode.kanbanik.shared.ClassOfServiceDTO) streamReader.readObject());
    setDescription(instance, streamReader.readString());
    setId(instance, streamReader.readInt());
    setName(instance, streamReader.readString());
    setPlace(instance, (com.googlecode.kanbanik.shared.WorkflowItemPlaceDTO) streamReader.readObject());
    setProject(instance, (com.googlecode.kanbanik.shared.ProjectDTO) streamReader.readObject());
    setTicketId(instance, streamReader.readString());
    
  }
  
  public static com.googlecode.kanbanik.shared.TaskDTO instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new com.googlecode.kanbanik.shared.TaskDTO();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, com.googlecode.kanbanik.shared.TaskDTO instance) throws SerializationException {
    streamWriter.writeObject(getClassOfService(instance));
    streamWriter.writeString(getDescription(instance));
    streamWriter.writeInt(getId(instance));
    streamWriter.writeString(getName(instance));
    streamWriter.writeObject(getPlace(instance));
    streamWriter.writeObject(getProject(instance));
    streamWriter.writeString(getTicketId(instance));
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return com.googlecode.kanbanik.shared.TaskDTO_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    com.googlecode.kanbanik.shared.TaskDTO_FieldSerializer.deserialize(reader, (com.googlecode.kanbanik.shared.TaskDTO)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    com.googlecode.kanbanik.shared.TaskDTO_FieldSerializer.serialize(writer, (com.googlecode.kanbanik.shared.TaskDTO)object);
  }
  
}
