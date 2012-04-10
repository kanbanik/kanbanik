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

public class ServerCommandInvoker_Proxy extends RemoteServiceProxy implements com.googlecode.kanbanik.client.services.ServerCommandInvokerAsync {
  private static final String REMOTE_SERVICE_INTERFACE_NAME = "com.googlecode.kanbanik.client.services.ServerCommandInvoker";
  private static final String SERIALIZATION_POLICY ="634142F471E3525AA17E33A90134411E";
  private static final com.googlecode.kanbanik.client.services.ServerCommandInvoker_TypeSerializer SERIALIZER = new com.googlecode.kanbanik.client.services.ServerCommandInvoker_TypeSerializer();
  
  public ServerCommandInvoker_Proxy() {
    super(GWT.getModuleBaseURL(),
      "commandInvoker", 
      SERIALIZATION_POLICY, 
      SERIALIZER);
  }
  
  public void invokeCommand(com.googlecode.kanbanik.shared.ServerCommand command, com.googlecode.kanbanik.dto.shell.Params params, com.google.gwt.user.client.rpc.AsyncCallback result) {
    com.google.gwt.user.client.rpc.impl.RemoteServiceProxy.ServiceHelper helper = new com.google.gwt.user.client.rpc.impl.RemoteServiceProxy.ServiceHelper("ServerCommandInvoker_Proxy", "invokeCommand");
    try {
      SerializationStreamWriter streamWriter = helper.start(REMOTE_SERVICE_INTERFACE_NAME, 2);
      streamWriter.writeString("com.googlecode.kanbanik.shared.ServerCommand/3528474192");
      streamWriter.writeString("com.googlecode.kanbanik.dto.shell.Params");
      streamWriter.writeObject(command);
      streamWriter.writeObject(params);
      helper.finish(result, ResponseReader.OBJECT);
    } catch (SerializationException ex) {
      result.onFailure(ex);
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
