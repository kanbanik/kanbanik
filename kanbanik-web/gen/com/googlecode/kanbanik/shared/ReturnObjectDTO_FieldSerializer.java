package com.googlecode.kanbanik.shared;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class ReturnObjectDTO_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native boolean getIsOK(com.googlecode.kanbanik.shared.ReturnObjectDTO instance) /*-{
    return instance.@com.googlecode.kanbanik.shared.ReturnObjectDTO::isOK;
  }-*/;
  
  private static native void setIsOK(com.googlecode.kanbanik.shared.ReturnObjectDTO instance, boolean value) 
  /*-{
    instance.@com.googlecode.kanbanik.shared.ReturnObjectDTO::isOK = value;
  }-*/;
  
  private static native java.lang.String getMessage(com.googlecode.kanbanik.shared.ReturnObjectDTO instance) /*-{
    return instance.@com.googlecode.kanbanik.shared.ReturnObjectDTO::message;
  }-*/;
  
  private static native void setMessage(com.googlecode.kanbanik.shared.ReturnObjectDTO instance, java.lang.String value) 
  /*-{
    instance.@com.googlecode.kanbanik.shared.ReturnObjectDTO::message = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, com.googlecode.kanbanik.shared.ReturnObjectDTO instance) throws SerializationException {
    setIsOK(instance, streamReader.readBoolean());
    setMessage(instance, streamReader.readString());
    
  }
  
  public static com.googlecode.kanbanik.shared.ReturnObjectDTO instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new com.googlecode.kanbanik.shared.ReturnObjectDTO();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, com.googlecode.kanbanik.shared.ReturnObjectDTO instance) throws SerializationException {
    streamWriter.writeBoolean(getIsOK(instance));
    streamWriter.writeString(getMessage(instance));
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return com.googlecode.kanbanik.shared.ReturnObjectDTO_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    com.googlecode.kanbanik.shared.ReturnObjectDTO_FieldSerializer.deserialize(reader, (com.googlecode.kanbanik.shared.ReturnObjectDTO)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    com.googlecode.kanbanik.shared.ReturnObjectDTO_FieldSerializer.serialize(writer, (com.googlecode.kanbanik.shared.ReturnObjectDTO)object);
  }
  
}
