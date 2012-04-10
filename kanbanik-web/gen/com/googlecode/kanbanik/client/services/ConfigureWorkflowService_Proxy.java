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

public class ConfigureWorkflowService_Proxy extends RemoteServiceProxy implements com.googlecode.kanbanik.client.services.ConfigureWorkflowServiceAsync {
  private static final String REMOTE_SERVICE_INTERFACE_NAME = "com.googlecode.kanbanik.client.services.ConfigureWorkflowService";
  private static final String SERIALIZATION_POLICY ="8A88B3755517BAACD8E83ADA1509F3D5";
  private static final com.googlecode.kanbanik.client.services.ConfigureWorkflowService_TypeSerializer SERIALIZER = new com.googlecode.kanbanik.client.services.ConfigureWorkflowService_TypeSerializer();
  
  public ConfigureWorkflowService_Proxy() {
    super(GWT.getModuleBaseURL(),
      "configureWorkflow", 
      SERIALIZATION_POLICY, 
      SERIALIZER);
  }
  
  public void addProjects(com.googlecode.kanbanik.shared.BoardDTO board, java.util.List projects, com.google.gwt.user.client.rpc.AsyncCallback callback) {
    com.google.gwt.user.client.rpc.impl.RemoteServiceProxy.ServiceHelper helper = new com.google.gwt.user.client.rpc.impl.RemoteServiceProxy.ServiceHelper("ConfigureWorkflowService_Proxy", "addProjects");
    try {
      SerializationStreamWriter streamWriter = helper.start(REMOTE_SERVICE_INTERFACE_NAME, 2);
      streamWriter.writeString("com.googlecode.kanbanik.shared.BoardDTO/330503676");
      streamWriter.writeString("java.util.List");
      streamWriter.writeObject(board);
      streamWriter.writeObject(projects);
      helper.finish(callback, ResponseReader.VOID);
    } catch (SerializationException ex) {
      callback.onFailure(ex);
    }
  }
  
  public void allBoards(com.google.gwt.user.client.rpc.AsyncCallback callback) {
    com.google.gwt.user.client.rpc.impl.RemoteServiceProxy.ServiceHelper helper = new com.google.gwt.user.client.rpc.impl.RemoteServiceProxy.ServiceHelper("ConfigureWorkflowService_Proxy", "allBoards");
    try {
      SerializationStreamWriter streamWriter = helper.start(REMOTE_SERVICE_INTERFACE_NAME, 0);
      helper.finish(callback, ResponseReader.OBJECT);
    } catch (SerializationException ex) {
      callback.onFailure(ex);
    }
  }
  
  public void allProjects(com.google.gwt.user.client.rpc.AsyncCallback callback) {
    com.google.gwt.user.client.rpc.impl.RemoteServiceProxy.ServiceHelper helper = new com.google.gwt.user.client.rpc.impl.RemoteServiceProxy.ServiceHelper("ConfigureWorkflowService_Proxy", "allProjects");
    try {
      SerializationStreamWriter streamWriter = helper.start(REMOTE_SERVICE_INTERFACE_NAME, 0);
      helper.finish(callback, ResponseReader.OBJECT);
    } catch (SerializationException ex) {
      callback.onFailure(ex);
    }
  }
  
  public void createNewBoard(com.googlecode.kanbanik.shared.BoardDTO dto, com.google.gwt.user.client.rpc.AsyncCallback kanbanikAsynchCallback) {
    com.google.gwt.user.client.rpc.impl.RemoteServiceProxy.ServiceHelper helper = new com.google.gwt.user.client.rpc.impl.RemoteServiceProxy.ServiceHelper("ConfigureWorkflowService_Proxy", "createNewBoard");
    try {
      SerializationStreamWriter streamWriter = helper.start(REMOTE_SERVICE_INTERFACE_NAME, 1);
      streamWriter.writeString("com.googlecode.kanbanik.shared.BoardDTO/330503676");
      streamWriter.writeObject(dto);
      helper.finish(kanbanikAsynchCallback, ResponseReader.OBJECT);
    } catch (SerializationException ex) {
      kanbanikAsynchCallback.onFailure(ex);
    }
  }
  
  public void createNewProject(com.googlecode.kanbanik.shared.ProjectDTO project, com.google.gwt.user.client.rpc.AsyncCallback callback) {
    com.google.gwt.user.client.rpc.impl.RemoteServiceProxy.ServiceHelper helper = new com.google.gwt.user.client.rpc.impl.RemoteServiceProxy.ServiceHelper("ConfigureWorkflowService_Proxy", "createNewProject");
    try {
      SerializationStreamWriter streamWriter = helper.start(REMOTE_SERVICE_INTERFACE_NAME, 1);
      streamWriter.writeString("com.googlecode.kanbanik.shared.ProjectDTO/2501098673");
      streamWriter.writeObject(project);
      helper.finish(callback, ResponseReader.OBJECT);
    } catch (SerializationException ex) {
      callback.onFailure(ex);
    }
  }
  
  public void deleteBoard(com.googlecode.kanbanik.shared.BoardDTO board, com.google.gwt.user.client.rpc.AsyncCallback callback) {
    com.google.gwt.user.client.rpc.impl.RemoteServiceProxy.ServiceHelper helper = new com.google.gwt.user.client.rpc.impl.RemoteServiceProxy.ServiceHelper("ConfigureWorkflowService_Proxy", "deleteBoard");
    try {
      SerializationStreamWriter streamWriter = helper.start(REMOTE_SERVICE_INTERFACE_NAME, 1);
      streamWriter.writeString("com.googlecode.kanbanik.shared.BoardDTO/330503676");
      streamWriter.writeObject(board);
      helper.finish(callback, ResponseReader.OBJECT);
    } catch (SerializationException ex) {
      callback.onFailure(ex);
    }
  }
  
  public void deleteProject(com.googlecode.kanbanik.shared.ProjectDTO projectDto, com.google.gwt.user.client.rpc.AsyncCallback kanbanikAsyncCallback) {
    com.google.gwt.user.client.rpc.impl.RemoteServiceProxy.ServiceHelper helper = new com.google.gwt.user.client.rpc.impl.RemoteServiceProxy.ServiceHelper("ConfigureWorkflowService_Proxy", "deleteProject");
    try {
      SerializationStreamWriter streamWriter = helper.start(REMOTE_SERVICE_INTERFACE_NAME, 1);
      streamWriter.writeString("com.googlecode.kanbanik.shared.ProjectDTO/2501098673");
      streamWriter.writeObject(projectDto);
      helper.finish(kanbanikAsyncCallback, ResponseReader.OBJECT);
    } catch (SerializationException ex) {
      kanbanikAsyncCallback.onFailure(ex);
    }
  }
  
  public void deleteWorkflowItem(com.googlecode.kanbanik.shared.WorkflowDTO workflow, com.googlecode.kanbanik.shared.WorkflowItemDTO workfloitem, com.google.gwt.user.client.rpc.AsyncCallback callback) {
    com.google.gwt.user.client.rpc.impl.RemoteServiceProxy.ServiceHelper helper = new com.google.gwt.user.client.rpc.impl.RemoteServiceProxy.ServiceHelper("ConfigureWorkflowService_Proxy", "deleteWorkflowItem");
    try {
      SerializationStreamWriter streamWriter = helper.start(REMOTE_SERVICE_INTERFACE_NAME, 2);
      streamWriter.writeString("com.googlecode.kanbanik.shared.WorkflowDTO/2985037766");
      streamWriter.writeString("com.googlecode.kanbanik.shared.WorkflowItemDTO/3022508069");
      streamWriter.writeObject(workflow);
      streamWriter.writeObject(workfloitem);
      helper.finish(callback, ResponseReader.OBJECT);
    } catch (SerializationException ex) {
      callback.onFailure(ex);
    }
  }
  
  public void editBoard(com.googlecode.kanbanik.shared.BoardDTO toStore, com.google.gwt.user.client.rpc.AsyncCallback callback) {
    com.google.gwt.user.client.rpc.impl.RemoteServiceProxy.ServiceHelper helper = new com.google.gwt.user.client.rpc.impl.RemoteServiceProxy.ServiceHelper("ConfigureWorkflowService_Proxy", "editBoard");
    try {
      SerializationStreamWriter streamWriter = helper.start(REMOTE_SERVICE_INTERFACE_NAME, 1);
      streamWriter.writeString("com.googlecode.kanbanik.shared.BoardDTO/330503676");
      streamWriter.writeObject(toStore);
      helper.finish(callback, ResponseReader.VOID);
    } catch (SerializationException ex) {
      callback.onFailure(ex);
    }
  }
  
  public void editProject(com.googlecode.kanbanik.shared.ProjectDTO projectDto, com.google.gwt.user.client.rpc.AsyncCallback callback) {
    com.google.gwt.user.client.rpc.impl.RemoteServiceProxy.ServiceHelper helper = new com.google.gwt.user.client.rpc.impl.RemoteServiceProxy.ServiceHelper("ConfigureWorkflowService_Proxy", "editProject");
    try {
      SerializationStreamWriter streamWriter = helper.start(REMOTE_SERVICE_INTERFACE_NAME, 1);
      streamWriter.writeString("com.googlecode.kanbanik.shared.ProjectDTO/2501098673");
      streamWriter.writeObject(projectDto);
      helper.finish(callback, ResponseReader.OBJECT);
    } catch (SerializationException ex) {
      callback.onFailure(ex);
    }
  }
  
  public void loadRealBoard(com.googlecode.kanbanik.shared.BoardDTO boardStub, com.google.gwt.user.client.rpc.AsyncCallback callback) {
    com.google.gwt.user.client.rpc.impl.RemoteServiceProxy.ServiceHelper helper = new com.google.gwt.user.client.rpc.impl.RemoteServiceProxy.ServiceHelper("ConfigureWorkflowService_Proxy", "loadRealBoard");
    try {
      SerializationStreamWriter streamWriter = helper.start(REMOTE_SERVICE_INTERFACE_NAME, 1);
      streamWriter.writeString("com.googlecode.kanbanik.shared.BoardDTO/330503676");
      streamWriter.writeObject(boardStub);
      helper.finish(callback, ResponseReader.OBJECT);
    } catch (SerializationException ex) {
      callback.onFailure(ex);
    }
  }
  
  public void removeProjects(com.googlecode.kanbanik.shared.BoardDTO board, java.util.List dtos, com.google.gwt.user.client.rpc.AsyncCallback callback) {
    com.google.gwt.user.client.rpc.impl.RemoteServiceProxy.ServiceHelper helper = new com.google.gwt.user.client.rpc.impl.RemoteServiceProxy.ServiceHelper("ConfigureWorkflowService_Proxy", "removeProjects");
    try {
      SerializationStreamWriter streamWriter = helper.start(REMOTE_SERVICE_INTERFACE_NAME, 2);
      streamWriter.writeString("com.googlecode.kanbanik.shared.BoardDTO/330503676");
      streamWriter.writeString("java.util.List");
      streamWriter.writeObject(board);
      streamWriter.writeObject(dtos);
      helper.finish(callback, ResponseReader.VOID);
    } catch (SerializationException ex) {
      callback.onFailure(ex);
    }
  }
  
  public void storeWorkflowItem(com.googlecode.kanbanik.shared.WorkflowDTO workflow, com.googlecode.kanbanik.shared.WorkflowItemDTO workfloitem, com.google.gwt.user.client.rpc.AsyncCallback callback) {
    com.google.gwt.user.client.rpc.impl.RemoteServiceProxy.ServiceHelper helper = new com.google.gwt.user.client.rpc.impl.RemoteServiceProxy.ServiceHelper("ConfigureWorkflowService_Proxy", "storeWorkflowItem");
    try {
      SerializationStreamWriter streamWriter = helper.start(REMOTE_SERVICE_INTERFACE_NAME, 2);
      streamWriter.writeString("com.googlecode.kanbanik.shared.WorkflowDTO/2985037766");
      streamWriter.writeString("com.googlecode.kanbanik.shared.WorkflowItemDTO/3022508069");
      streamWriter.writeObject(workflow);
      streamWriter.writeObject(workfloitem);
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
