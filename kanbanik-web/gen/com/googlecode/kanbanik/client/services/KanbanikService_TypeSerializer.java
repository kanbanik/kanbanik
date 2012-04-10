package com.googlecode.kanbanik.client.services;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.user.client.rpc.impl.TypeHandler;
import java.util.HashMap;
import java.util.Map;
import com.google.gwt.core.client.GwtScriptOnly;

public class KanbanikService_TypeSerializer extends com.google.gwt.user.client.rpc.impl.SerializerBase {
  private static final MethodMap methodMapNative;
  private static final JsArrayString signatureMapNative;
  
  static {
    methodMapNative = loadMethodsNative();
    signatureMapNative = loadSignaturesNative();
  }
  
  @SuppressWarnings("deprecation")
  @GwtScriptOnly
  private static native MethodMap loadMethodsNative() /*-{
    var result = {};
    result["com.google.gwt.user.client.rpc.IncompatibleRemoteServiceException/3936916533"] = [
        @com.google.gwt.user.client.rpc.IncompatibleRemoteServiceException_FieldSerializer::instantiate(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
        @com.google.gwt.user.client.rpc.IncompatibleRemoteServiceException_FieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;Lcom/google/gwt/user/client/rpc/IncompatibleRemoteServiceException;),
        @com.google.gwt.user.client.rpc.IncompatibleRemoteServiceException_FieldSerializer::serialize(Lcom/google/gwt/user/client/rpc/SerializationStreamWriter;Lcom/google/gwt/user/client/rpc/IncompatibleRemoteServiceException;)
      ];
    
    result["com.google.gwt.user.client.rpc.RpcTokenException/2345075298"] = [
        @com.google.gwt.user.client.rpc.RpcTokenException_FieldSerializer::instantiate(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
        @com.google.gwt.user.client.rpc.RpcTokenException_FieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;Lcom/google/gwt/user/client/rpc/RpcTokenException;),
      ];
    
    result["com.google.gwt.user.client.rpc.XsrfToken/4254043109"] = [
        ,
        ,
        @com.google.gwt.user.client.rpc.XsrfToken_FieldSerializer::serialize(Lcom/google/gwt/user/client/rpc/SerializationStreamWriter;Lcom/google/gwt/user/client/rpc/XsrfToken;)
      ];
    
    result["com.googlecode.kanbanik.shared.BoardDTO/330503676"] = [
        @com.googlecode.kanbanik.shared.BoardDTO_FieldSerializer::instantiate(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
        @com.googlecode.kanbanik.shared.BoardDTO_FieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;Lcom/googlecode/kanbanik/shared/BoardDTO;),
      ];
    
    result["[Lcom.googlecode.kanbanik.shared.BoardDTO;/1437454045"] = [
        @com.googlecode.kanbanik.shared.BoardDTO_Array_Rank_1_FieldSerializer::instantiate(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
        @com.googlecode.kanbanik.shared.BoardDTO_Array_Rank_1_FieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;[Lcom/googlecode/kanbanik/shared/BoardDTO;),
      ];
    
    result["com.googlecode.kanbanik.shared.ClassOfServiceDTO/4063773277"] = [
        @com.googlecode.kanbanik.shared.ClassOfServiceDTO_FieldSerializer::instantiate(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
        @com.googlecode.kanbanik.shared.ClassOfServiceDTO_FieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;Lcom/googlecode/kanbanik/shared/ClassOfServiceDTO;),
        @com.googlecode.kanbanik.shared.ClassOfServiceDTO_FieldSerializer::serialize(Lcom/google/gwt/user/client/rpc/SerializationStreamWriter;Lcom/googlecode/kanbanik/shared/ClassOfServiceDTO;)
      ];
    
    result["com.googlecode.kanbanik.shared.KanbanikDTO/1030448337"] = [
        @com.googlecode.kanbanik.shared.KanbanikDTO_FieldSerializer::instantiate(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
        @com.googlecode.kanbanik.shared.KanbanikDTO_FieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;Lcom/googlecode/kanbanik/shared/KanbanikDTO;),
      ];
    
    result["com.googlecode.kanbanik.shared.ProjectDTO/2501098673"] = [
        @com.googlecode.kanbanik.shared.ProjectDTO_FieldSerializer::instantiate(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
        @com.googlecode.kanbanik.shared.ProjectDTO_FieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;Lcom/googlecode/kanbanik/shared/ProjectDTO;),
        @com.googlecode.kanbanik.shared.ProjectDTO_FieldSerializer::serialize(Lcom/google/gwt/user/client/rpc/SerializationStreamWriter;Lcom/googlecode/kanbanik/shared/ProjectDTO;)
      ];
    
    result["[Lcom.googlecode.kanbanik.shared.ProjectDTO;/1689281042"] = [
        @com.googlecode.kanbanik.shared.ProjectDTO_Array_Rank_1_FieldSerializer::instantiate(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
        @com.googlecode.kanbanik.shared.ProjectDTO_Array_Rank_1_FieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;[Lcom/googlecode/kanbanik/shared/ProjectDTO;),
      ];
    
    result["com.googlecode.kanbanik.shared.QueuedItemDTO/3452340714"] = [
        @com.googlecode.kanbanik.shared.QueuedItemDTO_FieldSerializer::instantiate(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
        @com.googlecode.kanbanik.shared.QueuedItemDTO_FieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;Lcom/googlecode/kanbanik/shared/QueuedItemDTO;),
      ];
    
    result["[Lcom.googlecode.kanbanik.shared.QueuedItemDTO;/57536689"] = [
        @com.googlecode.kanbanik.shared.QueuedItemDTO_Array_Rank_1_FieldSerializer::instantiate(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
        @com.googlecode.kanbanik.shared.QueuedItemDTO_Array_Rank_1_FieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;[Lcom/googlecode/kanbanik/shared/QueuedItemDTO;),
      ];
    
    result["com.googlecode.kanbanik.shared.RegularItemDTO/4138191146"] = [
        @com.googlecode.kanbanik.shared.RegularItemDTO_FieldSerializer::instantiate(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
        @com.googlecode.kanbanik.shared.RegularItemDTO_FieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;Lcom/googlecode/kanbanik/shared/RegularItemDTO;),
      ];
    
    result["[Lcom.googlecode.kanbanik.shared.RegularItemDTO;/1335457400"] = [
        @com.googlecode.kanbanik.shared.RegularItemDTO_Array_Rank_1_FieldSerializer::instantiate(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
        @com.googlecode.kanbanik.shared.RegularItemDTO_Array_Rank_1_FieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;[Lcom/googlecode/kanbanik/shared/RegularItemDTO;),
      ];
    
    result["com.googlecode.kanbanik.shared.TaskDTO/2904293825"] = [
        @com.googlecode.kanbanik.shared.TaskDTO_FieldSerializer::instantiate(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
        @com.googlecode.kanbanik.shared.TaskDTO_FieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;Lcom/googlecode/kanbanik/shared/TaskDTO;),
        @com.googlecode.kanbanik.shared.TaskDTO_FieldSerializer::serialize(Lcom/google/gwt/user/client/rpc/SerializationStreamWriter;Lcom/googlecode/kanbanik/shared/TaskDTO;)
      ];
    
    result["[Lcom.googlecode.kanbanik.shared.TaskDTO;/2944002217"] = [
        @com.googlecode.kanbanik.shared.TaskDTO_Array_Rank_1_FieldSerializer::instantiate(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
        @com.googlecode.kanbanik.shared.TaskDTO_Array_Rank_1_FieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;[Lcom/googlecode/kanbanik/shared/TaskDTO;),
        @com.googlecode.kanbanik.shared.TaskDTO_Array_Rank_1_FieldSerializer::serialize(Lcom/google/gwt/user/client/rpc/SerializationStreamWriter;[Lcom/googlecode/kanbanik/shared/TaskDTO;)
      ];
    
    result["com.googlecode.kanbanik.shared.WorkflowDTO/2985037766"] = [
        @com.googlecode.kanbanik.shared.WorkflowDTO_FieldSerializer::instantiate(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
        @com.googlecode.kanbanik.shared.WorkflowDTO_FieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;Lcom/googlecode/kanbanik/shared/WorkflowDTO;),
      ];
    
    result["[Lcom.googlecode.kanbanik.shared.WorkflowItemDTO;/1207493282"] = [
        @com.googlecode.kanbanik.shared.WorkflowItemDTO_Array_Rank_1_FieldSerializer::instantiate(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
        @com.googlecode.kanbanik.shared.WorkflowItemDTO_Array_Rank_1_FieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;[Lcom/googlecode/kanbanik/shared/WorkflowItemDTO;),
      ];
    
    result["com.googlecode.kanbanik.shared.WorkflowItemPlaceDTO/89820863"] = [
        @com.googlecode.kanbanik.shared.WorkflowItemPlaceDTO_FieldSerializer::instantiate(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
        @com.googlecode.kanbanik.shared.WorkflowItemPlaceDTO_FieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;Lcom/googlecode/kanbanik/shared/WorkflowItemPlaceDTO;),
        @com.googlecode.kanbanik.shared.WorkflowItemPlaceDTO_FieldSerializer::serialize(Lcom/google/gwt/user/client/rpc/SerializationStreamWriter;Lcom/googlecode/kanbanik/shared/WorkflowItemPlaceDTO;)
      ];
    
    result["[Lcom.googlecode.kanbanik.shared.WorkflowItemPlaceDTO;/2703254060"] = [
        @com.googlecode.kanbanik.shared.WorkflowItemPlaceDTO_Array_Rank_1_FieldSerializer::instantiate(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
        @com.googlecode.kanbanik.shared.WorkflowItemPlaceDTO_Array_Rank_1_FieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;[Lcom/googlecode/kanbanik/shared/WorkflowItemPlaceDTO;),
      ];
    
    result["java.lang.String/2004016611"] = [
        @com.google.gwt.user.client.rpc.core.java.lang.String_CustomFieldSerializer::instantiate(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
        @com.google.gwt.user.client.rpc.core.java.lang.String_CustomFieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;Ljava/lang/String;),
        @com.google.gwt.user.client.rpc.core.java.lang.String_CustomFieldSerializer::serialize(Lcom/google/gwt/user/client/rpc/SerializationStreamWriter;Ljava/lang/String;)
      ];
    
    result["java.util.ArrayList/4159755760"] = [
        @com.google.gwt.user.client.rpc.core.java.util.ArrayList_FieldSerializer::instantiate(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
        @com.google.gwt.user.client.rpc.core.java.util.ArrayList_CustomFieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;Ljava/util/ArrayList;),
        @com.google.gwt.user.client.rpc.core.java.util.ArrayList_CustomFieldSerializer::serialize(Lcom/google/gwt/user/client/rpc/SerializationStreamWriter;Ljava/util/ArrayList;)
      ];
    
    result["java.util.Arrays$ArrayList/2507071751"] = [
        @com.google.gwt.user.client.rpc.core.java.util.Arrays.ArrayList_CustomFieldSerializer::instantiate(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
        @com.google.gwt.user.client.rpc.core.java.util.Arrays.ArrayList_CustomFieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;Ljava/util/List;),
        @com.google.gwt.user.client.rpc.core.java.util.Arrays.ArrayList_CustomFieldSerializer::serialize(Lcom/google/gwt/user/client/rpc/SerializationStreamWriter;Ljava/util/List;)
      ];
    
    result["java.util.Collections$EmptyList/4157118744"] = [
        @com.google.gwt.user.client.rpc.core.java.util.Collections.EmptyList_CustomFieldSerializer::instantiate(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
        @com.google.gwt.user.client.rpc.core.java.util.Collections.EmptyList_CustomFieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;Ljava/util/List;),
        @com.google.gwt.user.client.rpc.core.java.util.Collections.EmptyList_CustomFieldSerializer::serialize(Lcom/google/gwt/user/client/rpc/SerializationStreamWriter;Ljava/util/List;)
      ];
    
    result["java.util.Collections$SingletonList/1586180994"] = [
        @com.google.gwt.user.client.rpc.core.java.util.Collections.SingletonList_CustomFieldSerializer::instantiate(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
        @com.google.gwt.user.client.rpc.core.java.util.Collections.SingletonList_CustomFieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;Ljava/util/List;),
        @com.google.gwt.user.client.rpc.core.java.util.Collections.SingletonList_CustomFieldSerializer::serialize(Lcom/google/gwt/user/client/rpc/SerializationStreamWriter;Ljava/util/List;)
      ];
    
    result["java.util.LinkedList/3953877921"] = [
        @com.google.gwt.user.client.rpc.core.java.util.LinkedList_FieldSerializer::instantiate(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
        @com.google.gwt.user.client.rpc.core.java.util.LinkedList_CustomFieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;Ljava/util/LinkedList;),
        @com.google.gwt.user.client.rpc.core.java.util.LinkedList_CustomFieldSerializer::serialize(Lcom/google/gwt/user/client/rpc/SerializationStreamWriter;Ljava/util/LinkedList;)
      ];
    
    result["java.util.Stack/1346942793"] = [
        @com.google.gwt.user.client.rpc.core.java.util.Stack_FieldSerializer::instantiate(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
        @com.google.gwt.user.client.rpc.core.java.util.Stack_FieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;Ljava/util/Stack;),
        @com.google.gwt.user.client.rpc.core.java.util.Stack_FieldSerializer::serialize(Lcom/google/gwt/user/client/rpc/SerializationStreamWriter;Ljava/util/Stack;)
      ];
    
    result["java.util.Vector/3057315478"] = [
        @com.google.gwt.user.client.rpc.core.java.util.Vector_FieldSerializer::instantiate(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
        @com.google.gwt.user.client.rpc.core.java.util.Vector_CustomFieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;Ljava/util/Vector;),
        @com.google.gwt.user.client.rpc.core.java.util.Vector_CustomFieldSerializer::serialize(Lcom/google/gwt/user/client/rpc/SerializationStreamWriter;Ljava/util/Vector;)
      ];
    
    return result;
  }-*/;
  
  @SuppressWarnings("deprecation")
  @GwtScriptOnly
  private static native JsArrayString loadSignaturesNative() /*-{
    var result = [];
    result[@com.google.gwt.core.client.impl.Impl::getHashCode(Ljava/lang/Object;)(@com.google.gwt.user.client.rpc.IncompatibleRemoteServiceException::class)] = "com.google.gwt.user.client.rpc.IncompatibleRemoteServiceException/3936916533";
    result[@com.google.gwt.core.client.impl.Impl::getHashCode(Ljava/lang/Object;)(@com.google.gwt.user.client.rpc.RpcTokenException::class)] = "com.google.gwt.user.client.rpc.RpcTokenException/2345075298";
    result[@com.google.gwt.core.client.impl.Impl::getHashCode(Ljava/lang/Object;)(@com.google.gwt.user.client.rpc.XsrfToken::class)] = "com.google.gwt.user.client.rpc.XsrfToken/4254043109";
    result[@com.google.gwt.core.client.impl.Impl::getHashCode(Ljava/lang/Object;)(@com.googlecode.kanbanik.shared.BoardDTO::class)] = "com.googlecode.kanbanik.shared.BoardDTO/330503676";
    result[@com.google.gwt.core.client.impl.Impl::getHashCode(Ljava/lang/Object;)(@com.googlecode.kanbanik.shared.BoardDTO[]::class)] = "[Lcom.googlecode.kanbanik.shared.BoardDTO;/1437454045";
    result[@com.google.gwt.core.client.impl.Impl::getHashCode(Ljava/lang/Object;)(@com.googlecode.kanbanik.shared.ClassOfServiceDTO::class)] = "com.googlecode.kanbanik.shared.ClassOfServiceDTO/4063773277";
    result[@com.google.gwt.core.client.impl.Impl::getHashCode(Ljava/lang/Object;)(@com.googlecode.kanbanik.shared.KanbanikDTO::class)] = "com.googlecode.kanbanik.shared.KanbanikDTO/1030448337";
    result[@com.google.gwt.core.client.impl.Impl::getHashCode(Ljava/lang/Object;)(@com.googlecode.kanbanik.shared.ProjectDTO::class)] = "com.googlecode.kanbanik.shared.ProjectDTO/2501098673";
    result[@com.google.gwt.core.client.impl.Impl::getHashCode(Ljava/lang/Object;)(@com.googlecode.kanbanik.shared.ProjectDTO[]::class)] = "[Lcom.googlecode.kanbanik.shared.ProjectDTO;/1689281042";
    result[@com.google.gwt.core.client.impl.Impl::getHashCode(Ljava/lang/Object;)(@com.googlecode.kanbanik.shared.QueuedItemDTO::class)] = "com.googlecode.kanbanik.shared.QueuedItemDTO/3452340714";
    result[@com.google.gwt.core.client.impl.Impl::getHashCode(Ljava/lang/Object;)(@com.googlecode.kanbanik.shared.QueuedItemDTO[]::class)] = "[Lcom.googlecode.kanbanik.shared.QueuedItemDTO;/57536689";
    result[@com.google.gwt.core.client.impl.Impl::getHashCode(Ljava/lang/Object;)(@com.googlecode.kanbanik.shared.RegularItemDTO::class)] = "com.googlecode.kanbanik.shared.RegularItemDTO/4138191146";
    result[@com.google.gwt.core.client.impl.Impl::getHashCode(Ljava/lang/Object;)(@com.googlecode.kanbanik.shared.RegularItemDTO[]::class)] = "[Lcom.googlecode.kanbanik.shared.RegularItemDTO;/1335457400";
    result[@com.google.gwt.core.client.impl.Impl::getHashCode(Ljava/lang/Object;)(@com.googlecode.kanbanik.shared.TaskDTO::class)] = "com.googlecode.kanbanik.shared.TaskDTO/2904293825";
    result[@com.google.gwt.core.client.impl.Impl::getHashCode(Ljava/lang/Object;)(@com.googlecode.kanbanik.shared.TaskDTO[]::class)] = "[Lcom.googlecode.kanbanik.shared.TaskDTO;/2944002217";
    result[@com.google.gwt.core.client.impl.Impl::getHashCode(Ljava/lang/Object;)(@com.googlecode.kanbanik.shared.WorkflowDTO::class)] = "com.googlecode.kanbanik.shared.WorkflowDTO/2985037766";
    result[@com.google.gwt.core.client.impl.Impl::getHashCode(Ljava/lang/Object;)(@com.googlecode.kanbanik.shared.WorkflowItemDTO[]::class)] = "[Lcom.googlecode.kanbanik.shared.WorkflowItemDTO;/1207493282";
    result[@com.google.gwt.core.client.impl.Impl::getHashCode(Ljava/lang/Object;)(@com.googlecode.kanbanik.shared.WorkflowItemPlaceDTO::class)] = "com.googlecode.kanbanik.shared.WorkflowItemPlaceDTO/89820863";
    result[@com.google.gwt.core.client.impl.Impl::getHashCode(Ljava/lang/Object;)(@com.googlecode.kanbanik.shared.WorkflowItemPlaceDTO[]::class)] = "[Lcom.googlecode.kanbanik.shared.WorkflowItemPlaceDTO;/2703254060";
    result[@com.google.gwt.core.client.impl.Impl::getHashCode(Ljava/lang/Object;)(@java.lang.String::class)] = "java.lang.String/2004016611";
    result[@com.google.gwt.core.client.impl.Impl::getHashCode(Ljava/lang/Object;)(@java.util.ArrayList::class)] = "java.util.ArrayList/4159755760";
    result[@com.google.gwt.core.client.impl.Impl::getHashCode(Ljava/lang/Object;)(@java.util.Arrays.ArrayList::class)] = "java.util.Arrays$ArrayList/2507071751";
    result[@com.google.gwt.core.client.impl.Impl::getHashCode(Ljava/lang/Object;)(@java.util.Collections.EmptyList::class)] = "java.util.Collections$EmptyList/4157118744";
    result[@com.google.gwt.core.client.impl.Impl::getHashCode(Ljava/lang/Object;)(@java.util.Collections.SingletonList::class)] = "java.util.Collections$SingletonList/1586180994";
    result[@com.google.gwt.core.client.impl.Impl::getHashCode(Ljava/lang/Object;)(@java.util.LinkedList::class)] = "java.util.LinkedList/3953877921";
    result[@com.google.gwt.core.client.impl.Impl::getHashCode(Ljava/lang/Object;)(@java.util.Stack::class)] = "java.util.Stack/1346942793";
    result[@com.google.gwt.core.client.impl.Impl::getHashCode(Ljava/lang/Object;)(@java.util.Vector::class)] = "java.util.Vector/3057315478";
    return result;
  }-*/;
  
  public KanbanikService_TypeSerializer() {
    super(null, methodMapNative, null, signatureMapNative);
  }
  
}
