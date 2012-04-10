package com.googlecode.kanbanik.client.services;

import com.google.gwt.user.client.rpc.impl.RemoteServiceProxy;
import com.google.gwt.user.client.rpc.impl.ClientSerializationStreamWriter;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.impl.RequestCallbackAdapter.ResponseReader;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.RpcToken;
import com.google.gwt.user.client.rpc.RpcTokenException;
import com.google.gwt.core.client.impl.Impl;
import com.google.gwt.user.client.rpc.impl.RpcStatsContext;

public class KanbanikService_Proxy extends RemoteServiceProxy implements com.googlecode.kanbanik.client.services.KanbanikServiceAsync {
  private static final String REMOTE_SERVICE_INTERFACE_NAME = "com.googlecode.kanbanik.client.services.KanbanikService";
  private static final String SERIALIZATION_POLICY ="2F9EDF351491026C5CC2B2EEDF6BE1D4";
  private static final com.googlecode.kanbanik.client.services.KanbanikService_TypeSerializer SERIALIZER = new com.googlecode.kanbanik.client.services.KanbanikService_TypeSerializer();
  
  public KanbanikService_Proxy() {
    super(GWT.getModuleBaseURL(),
      "kanbanik", 
      SERIALIZATION_POLICY, 
      SERIALIZER);
  }
  
  public void deleteTask(com.googlecode.kanbanik.shared.TaskDTO task, com.google.gwt.user.client.rpc.AsyncCallback callback) {
    com.google.gwt.user.client.rpc.impl.RemoteServiceProxy.ServiceHelper helper = new com.google.gwt.user.client.rpc.impl.RemoteServiceProxy.ServiceHelper("KanbanikService_Proxy", "deleteTask");
    try {
      SerializationStreamWriter streamWriter = helper.start(REMOTE_SERVICE_INTERFACE_NAME, 1);
      streamWriter.writeString("com.googlecode.kanbanik.shared.TaskDTO/2904293825");
      streamWriter.writeObject(task);
      helper.finish(callback, ResponseReader.VOID);
    } catch (SerializationException ex) {
      callback.onFailure(ex);
    }
  }
  
  public void loadKanbanikData(com.google.gwt.user.client.rpc.AsyncCallback callback) {
    com.google.gwt.user.client.rpc.impl.RemoteServiceProxy.ServiceHelper helper = new com.google.gwt.user.client.rpc.impl.RemoteServiceProxy.ServiceHelper("KanbanikService_Proxy", "loadKanbanikData");
    try {
      SerializationStreamWriter streamWriter = helper.start(REMOTE_SERVICE_INTERFACE_NAME, 0);
      helper.finish(callback, ResponseReader.OBJECT);
    } catch (SerializationException ex) {
      callback.onFailure(ex);
    }
  }
  
  public void saveTask(com.googlecode.kanbanik.shared.TaskDTO task, com.google.gwt.user.client.rpc.AsyncCallback callback) {
    com.google.gwt.user.client.rpc.impl.RemoteServiceProxy.ServiceHelper helper = new com.google.gwt.user.client.rpc.impl.RemoteServiceProxy.ServiceHelper("KanbanikService_Proxy", "saveTask");
    try {
      SerializationStreamWriter streamWriter = helper.start(REMOTE_SERVICE_INTERFACE_NAME, 1);
      streamWriter.writeString("com.googlecode.kanbanik.shared.TaskDTO/2904293825");
      streamWriter.writeObject(task);
      helper.finish(callback, ResponseReader.OBJECT);
    } catch (SerializationException ex) {
      callback.onFailure(ex);
    }
  }
  @Override
  public SerializationStreamWriter createStreamWriter() {
    ClientSerializationStreamWriter toReturn =
      (ClientSerializationStreamWriter) super.createStreamWriter();
    if (getRpcToken() != null) {
      toReturn.addFlags(ClientSerializationStreamWriter.FLAG_RPC_TOKEN_INCLUDED);
    }
    return toReturn;
  }
  @Override
  protected void checkRpcTokenType(RpcToken token) {
    if (!(token instanceof com.google.gwt.user.client.rpc.XsrfToken)) {
      throw new RpcTokenException("Invalid RpcToken type: expected 'com.google.gwt.user.client.rpc.XsrfToken' but got '" + token.getClass() + "'");
    }
  }
}
