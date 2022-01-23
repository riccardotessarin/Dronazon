package com.smartcitydrone.droneservice;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

/**
 * <pre>
 * Defining a Service, a Service can have multiple RPC operations
 * </pre>
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.25.0)",
    comments = "Source: DroneService.proto")
public final class DroneServiceGrpc {

  private DroneServiceGrpc() {}

  public static final String SERVICE_NAME = "com.smartcitydrone.droneservice.DroneService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.smartcitydrone.droneservice.DroneServiceOuterClass.JoinRequest,
      com.smartcitydrone.droneservice.DroneServiceOuterClass.JoinResponse> getJoinNetworkMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "joinNetwork",
      requestType = com.smartcitydrone.droneservice.DroneServiceOuterClass.JoinRequest.class,
      responseType = com.smartcitydrone.droneservice.DroneServiceOuterClass.JoinResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.smartcitydrone.droneservice.DroneServiceOuterClass.JoinRequest,
      com.smartcitydrone.droneservice.DroneServiceOuterClass.JoinResponse> getJoinNetworkMethod() {
    io.grpc.MethodDescriptor<com.smartcitydrone.droneservice.DroneServiceOuterClass.JoinRequest, com.smartcitydrone.droneservice.DroneServiceOuterClass.JoinResponse> getJoinNetworkMethod;
    if ((getJoinNetworkMethod = DroneServiceGrpc.getJoinNetworkMethod) == null) {
      synchronized (DroneServiceGrpc.class) {
        if ((getJoinNetworkMethod = DroneServiceGrpc.getJoinNetworkMethod) == null) {
          DroneServiceGrpc.getJoinNetworkMethod = getJoinNetworkMethod =
              io.grpc.MethodDescriptor.<com.smartcitydrone.droneservice.DroneServiceOuterClass.JoinRequest, com.smartcitydrone.droneservice.DroneServiceOuterClass.JoinResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "joinNetwork"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.smartcitydrone.droneservice.DroneServiceOuterClass.JoinRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.smartcitydrone.droneservice.DroneServiceOuterClass.JoinResponse.getDefaultInstance()))
              .setSchemaDescriptor(new DroneServiceMethodDescriptorSupplier("joinNetwork"))
              .build();
        }
      }
    }
    return getJoinNetworkMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.smartcitydrone.droneservice.DroneServiceOuterClass.OrderRequest,
      com.smartcitydrone.droneservice.DroneServiceOuterClass.OrderResponse> getDispatchOrderMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "dispatchOrder",
      requestType = com.smartcitydrone.droneservice.DroneServiceOuterClass.OrderRequest.class,
      responseType = com.smartcitydrone.droneservice.DroneServiceOuterClass.OrderResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.smartcitydrone.droneservice.DroneServiceOuterClass.OrderRequest,
      com.smartcitydrone.droneservice.DroneServiceOuterClass.OrderResponse> getDispatchOrderMethod() {
    io.grpc.MethodDescriptor<com.smartcitydrone.droneservice.DroneServiceOuterClass.OrderRequest, com.smartcitydrone.droneservice.DroneServiceOuterClass.OrderResponse> getDispatchOrderMethod;
    if ((getDispatchOrderMethod = DroneServiceGrpc.getDispatchOrderMethod) == null) {
      synchronized (DroneServiceGrpc.class) {
        if ((getDispatchOrderMethod = DroneServiceGrpc.getDispatchOrderMethod) == null) {
          DroneServiceGrpc.getDispatchOrderMethod = getDispatchOrderMethod =
              io.grpc.MethodDescriptor.<com.smartcitydrone.droneservice.DroneServiceOuterClass.OrderRequest, com.smartcitydrone.droneservice.DroneServiceOuterClass.OrderResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "dispatchOrder"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.smartcitydrone.droneservice.DroneServiceOuterClass.OrderRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.smartcitydrone.droneservice.DroneServiceOuterClass.OrderResponse.getDefaultInstance()))
              .setSchemaDescriptor(new DroneServiceMethodDescriptorSupplier("dispatchOrder"))
              .build();
        }
      }
    }
    return getDispatchOrderMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.smartcitydrone.droneservice.DroneServiceOuterClass.StatRequest,
      com.smartcitydrone.droneservice.DroneServiceOuterClass.StatResponse> getSendDroneStatMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "sendDroneStat",
      requestType = com.smartcitydrone.droneservice.DroneServiceOuterClass.StatRequest.class,
      responseType = com.smartcitydrone.droneservice.DroneServiceOuterClass.StatResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.smartcitydrone.droneservice.DroneServiceOuterClass.StatRequest,
      com.smartcitydrone.droneservice.DroneServiceOuterClass.StatResponse> getSendDroneStatMethod() {
    io.grpc.MethodDescriptor<com.smartcitydrone.droneservice.DroneServiceOuterClass.StatRequest, com.smartcitydrone.droneservice.DroneServiceOuterClass.StatResponse> getSendDroneStatMethod;
    if ((getSendDroneStatMethod = DroneServiceGrpc.getSendDroneStatMethod) == null) {
      synchronized (DroneServiceGrpc.class) {
        if ((getSendDroneStatMethod = DroneServiceGrpc.getSendDroneStatMethod) == null) {
          DroneServiceGrpc.getSendDroneStatMethod = getSendDroneStatMethod =
              io.grpc.MethodDescriptor.<com.smartcitydrone.droneservice.DroneServiceOuterClass.StatRequest, com.smartcitydrone.droneservice.DroneServiceOuterClass.StatResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "sendDroneStat"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.smartcitydrone.droneservice.DroneServiceOuterClass.StatRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.smartcitydrone.droneservice.DroneServiceOuterClass.StatResponse.getDefaultInstance()))
              .setSchemaDescriptor(new DroneServiceMethodDescriptorSupplier("sendDroneStat"))
              .build();
        }
      }
    }
    return getSendDroneStatMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.smartcitydrone.droneservice.DroneServiceOuterClass.ElectionRequest,
      com.smartcitydrone.droneservice.DroneServiceOuterClass.ElectionResponse> getElectionMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "election",
      requestType = com.smartcitydrone.droneservice.DroneServiceOuterClass.ElectionRequest.class,
      responseType = com.smartcitydrone.droneservice.DroneServiceOuterClass.ElectionResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.smartcitydrone.droneservice.DroneServiceOuterClass.ElectionRequest,
      com.smartcitydrone.droneservice.DroneServiceOuterClass.ElectionResponse> getElectionMethod() {
    io.grpc.MethodDescriptor<com.smartcitydrone.droneservice.DroneServiceOuterClass.ElectionRequest, com.smartcitydrone.droneservice.DroneServiceOuterClass.ElectionResponse> getElectionMethod;
    if ((getElectionMethod = DroneServiceGrpc.getElectionMethod) == null) {
      synchronized (DroneServiceGrpc.class) {
        if ((getElectionMethod = DroneServiceGrpc.getElectionMethod) == null) {
          DroneServiceGrpc.getElectionMethod = getElectionMethod =
              io.grpc.MethodDescriptor.<com.smartcitydrone.droneservice.DroneServiceOuterClass.ElectionRequest, com.smartcitydrone.droneservice.DroneServiceOuterClass.ElectionResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "election"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.smartcitydrone.droneservice.DroneServiceOuterClass.ElectionRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.smartcitydrone.droneservice.DroneServiceOuterClass.ElectionResponse.getDefaultInstance()))
              .setSchemaDescriptor(new DroneServiceMethodDescriptorSupplier("election"))
              .build();
        }
      }
    }
    return getElectionMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.smartcitydrone.droneservice.DroneServiceOuterClass.ElectedRequest,
      com.smartcitydrone.droneservice.DroneServiceOuterClass.ElectedResponse> getElectedMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "elected",
      requestType = com.smartcitydrone.droneservice.DroneServiceOuterClass.ElectedRequest.class,
      responseType = com.smartcitydrone.droneservice.DroneServiceOuterClass.ElectedResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.smartcitydrone.droneservice.DroneServiceOuterClass.ElectedRequest,
      com.smartcitydrone.droneservice.DroneServiceOuterClass.ElectedResponse> getElectedMethod() {
    io.grpc.MethodDescriptor<com.smartcitydrone.droneservice.DroneServiceOuterClass.ElectedRequest, com.smartcitydrone.droneservice.DroneServiceOuterClass.ElectedResponse> getElectedMethod;
    if ((getElectedMethod = DroneServiceGrpc.getElectedMethod) == null) {
      synchronized (DroneServiceGrpc.class) {
        if ((getElectedMethod = DroneServiceGrpc.getElectedMethod) == null) {
          DroneServiceGrpc.getElectedMethod = getElectedMethod =
              io.grpc.MethodDescriptor.<com.smartcitydrone.droneservice.DroneServiceOuterClass.ElectedRequest, com.smartcitydrone.droneservice.DroneServiceOuterClass.ElectedResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "elected"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.smartcitydrone.droneservice.DroneServiceOuterClass.ElectedRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.smartcitydrone.droneservice.DroneServiceOuterClass.ElectedResponse.getDefaultInstance()))
              .setSchemaDescriptor(new DroneServiceMethodDescriptorSupplier("elected"))
              .build();
        }
      }
    }
    return getElectedMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.smartcitydrone.droneservice.DroneServiceOuterClass.PendingStatRequest,
      com.smartcitydrone.droneservice.DroneServiceOuterClass.PendingStatResponse> getSendPendingDroneStatMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "sendPendingDroneStat",
      requestType = com.smartcitydrone.droneservice.DroneServiceOuterClass.PendingStatRequest.class,
      responseType = com.smartcitydrone.droneservice.DroneServiceOuterClass.PendingStatResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.smartcitydrone.droneservice.DroneServiceOuterClass.PendingStatRequest,
      com.smartcitydrone.droneservice.DroneServiceOuterClass.PendingStatResponse> getSendPendingDroneStatMethod() {
    io.grpc.MethodDescriptor<com.smartcitydrone.droneservice.DroneServiceOuterClass.PendingStatRequest, com.smartcitydrone.droneservice.DroneServiceOuterClass.PendingStatResponse> getSendPendingDroneStatMethod;
    if ((getSendPendingDroneStatMethod = DroneServiceGrpc.getSendPendingDroneStatMethod) == null) {
      synchronized (DroneServiceGrpc.class) {
        if ((getSendPendingDroneStatMethod = DroneServiceGrpc.getSendPendingDroneStatMethod) == null) {
          DroneServiceGrpc.getSendPendingDroneStatMethod = getSendPendingDroneStatMethod =
              io.grpc.MethodDescriptor.<com.smartcitydrone.droneservice.DroneServiceOuterClass.PendingStatRequest, com.smartcitydrone.droneservice.DroneServiceOuterClass.PendingStatResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "sendPendingDroneStat"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.smartcitydrone.droneservice.DroneServiceOuterClass.PendingStatRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.smartcitydrone.droneservice.DroneServiceOuterClass.PendingStatResponse.getDefaultInstance()))
              .setSchemaDescriptor(new DroneServiceMethodDescriptorSupplier("sendPendingDroneStat"))
              .build();
        }
      }
    }
    return getSendPendingDroneStatMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.smartcitydrone.droneservice.DroneServiceOuterClass.CheckMessage,
      com.smartcitydrone.droneservice.DroneServiceOuterClass.CheckMessage> getCheckMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "check",
      requestType = com.smartcitydrone.droneservice.DroneServiceOuterClass.CheckMessage.class,
      responseType = com.smartcitydrone.droneservice.DroneServiceOuterClass.CheckMessage.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.smartcitydrone.droneservice.DroneServiceOuterClass.CheckMessage,
      com.smartcitydrone.droneservice.DroneServiceOuterClass.CheckMessage> getCheckMethod() {
    io.grpc.MethodDescriptor<com.smartcitydrone.droneservice.DroneServiceOuterClass.CheckMessage, com.smartcitydrone.droneservice.DroneServiceOuterClass.CheckMessage> getCheckMethod;
    if ((getCheckMethod = DroneServiceGrpc.getCheckMethod) == null) {
      synchronized (DroneServiceGrpc.class) {
        if ((getCheckMethod = DroneServiceGrpc.getCheckMethod) == null) {
          DroneServiceGrpc.getCheckMethod = getCheckMethod =
              io.grpc.MethodDescriptor.<com.smartcitydrone.droneservice.DroneServiceOuterClass.CheckMessage, com.smartcitydrone.droneservice.DroneServiceOuterClass.CheckMessage>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "check"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.smartcitydrone.droneservice.DroneServiceOuterClass.CheckMessage.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.smartcitydrone.droneservice.DroneServiceOuterClass.CheckMessage.getDefaultInstance()))
              .setSchemaDescriptor(new DroneServiceMethodDescriptorSupplier("check"))
              .build();
        }
      }
    }
    return getCheckMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.smartcitydrone.droneservice.DroneServiceOuterClass.CheckMessage,
      com.smartcitydrone.droneservice.DroneServiceOuterClass.LookForMasterResponse> getLookForMasterMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "lookForMaster",
      requestType = com.smartcitydrone.droneservice.DroneServiceOuterClass.CheckMessage.class,
      responseType = com.smartcitydrone.droneservice.DroneServiceOuterClass.LookForMasterResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.smartcitydrone.droneservice.DroneServiceOuterClass.CheckMessage,
      com.smartcitydrone.droneservice.DroneServiceOuterClass.LookForMasterResponse> getLookForMasterMethod() {
    io.grpc.MethodDescriptor<com.smartcitydrone.droneservice.DroneServiceOuterClass.CheckMessage, com.smartcitydrone.droneservice.DroneServiceOuterClass.LookForMasterResponse> getLookForMasterMethod;
    if ((getLookForMasterMethod = DroneServiceGrpc.getLookForMasterMethod) == null) {
      synchronized (DroneServiceGrpc.class) {
        if ((getLookForMasterMethod = DroneServiceGrpc.getLookForMasterMethod) == null) {
          DroneServiceGrpc.getLookForMasterMethod = getLookForMasterMethod =
              io.grpc.MethodDescriptor.<com.smartcitydrone.droneservice.DroneServiceOuterClass.CheckMessage, com.smartcitydrone.droneservice.DroneServiceOuterClass.LookForMasterResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "lookForMaster"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.smartcitydrone.droneservice.DroneServiceOuterClass.CheckMessage.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.smartcitydrone.droneservice.DroneServiceOuterClass.LookForMasterResponse.getDefaultInstance()))
              .setSchemaDescriptor(new DroneServiceMethodDescriptorSupplier("lookForMaster"))
              .build();
        }
      }
    }
    return getLookForMasterMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static DroneServiceStub newStub(io.grpc.Channel channel) {
    return new DroneServiceStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static DroneServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new DroneServiceBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static DroneServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new DroneServiceFutureStub(channel);
  }

  /**
   * <pre>
   * Defining a Service, a Service can have multiple RPC operations
   * </pre>
   */
  public static abstract class DroneServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void joinNetwork(com.smartcitydrone.droneservice.DroneServiceOuterClass.JoinRequest request,
        io.grpc.stub.StreamObserver<com.smartcitydrone.droneservice.DroneServiceOuterClass.JoinResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getJoinNetworkMethod(), responseObserver);
    }

    /**
     */
    public void dispatchOrder(com.smartcitydrone.droneservice.DroneServiceOuterClass.OrderRequest request,
        io.grpc.stub.StreamObserver<com.smartcitydrone.droneservice.DroneServiceOuterClass.OrderResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getDispatchOrderMethod(), responseObserver);
    }

    /**
     */
    public void sendDroneStat(com.smartcitydrone.droneservice.DroneServiceOuterClass.StatRequest request,
        io.grpc.stub.StreamObserver<com.smartcitydrone.droneservice.DroneServiceOuterClass.StatResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getSendDroneStatMethod(), responseObserver);
    }

    /**
     */
    public void election(com.smartcitydrone.droneservice.DroneServiceOuterClass.ElectionRequest request,
        io.grpc.stub.StreamObserver<com.smartcitydrone.droneservice.DroneServiceOuterClass.ElectionResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getElectionMethod(), responseObserver);
    }

    /**
     */
    public void elected(com.smartcitydrone.droneservice.DroneServiceOuterClass.ElectedRequest request,
        io.grpc.stub.StreamObserver<com.smartcitydrone.droneservice.DroneServiceOuterClass.ElectedResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getElectedMethod(), responseObserver);
    }

    /**
     */
    public void sendPendingDroneStat(com.smartcitydrone.droneservice.DroneServiceOuterClass.PendingStatRequest request,
        io.grpc.stub.StreamObserver<com.smartcitydrone.droneservice.DroneServiceOuterClass.PendingStatResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getSendPendingDroneStatMethod(), responseObserver);
    }

    /**
     */
    public void check(com.smartcitydrone.droneservice.DroneServiceOuterClass.CheckMessage request,
        io.grpc.stub.StreamObserver<com.smartcitydrone.droneservice.DroneServiceOuterClass.CheckMessage> responseObserver) {
      asyncUnimplementedUnaryCall(getCheckMethod(), responseObserver);
    }

    /**
     */
    public void lookForMaster(com.smartcitydrone.droneservice.DroneServiceOuterClass.CheckMessage request,
        io.grpc.stub.StreamObserver<com.smartcitydrone.droneservice.DroneServiceOuterClass.LookForMasterResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getLookForMasterMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getJoinNetworkMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.smartcitydrone.droneservice.DroneServiceOuterClass.JoinRequest,
                com.smartcitydrone.droneservice.DroneServiceOuterClass.JoinResponse>(
                  this, METHODID_JOIN_NETWORK)))
          .addMethod(
            getDispatchOrderMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.smartcitydrone.droneservice.DroneServiceOuterClass.OrderRequest,
                com.smartcitydrone.droneservice.DroneServiceOuterClass.OrderResponse>(
                  this, METHODID_DISPATCH_ORDER)))
          .addMethod(
            getSendDroneStatMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.smartcitydrone.droneservice.DroneServiceOuterClass.StatRequest,
                com.smartcitydrone.droneservice.DroneServiceOuterClass.StatResponse>(
                  this, METHODID_SEND_DRONE_STAT)))
          .addMethod(
            getElectionMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.smartcitydrone.droneservice.DroneServiceOuterClass.ElectionRequest,
                com.smartcitydrone.droneservice.DroneServiceOuterClass.ElectionResponse>(
                  this, METHODID_ELECTION)))
          .addMethod(
            getElectedMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.smartcitydrone.droneservice.DroneServiceOuterClass.ElectedRequest,
                com.smartcitydrone.droneservice.DroneServiceOuterClass.ElectedResponse>(
                  this, METHODID_ELECTED)))
          .addMethod(
            getSendPendingDroneStatMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.smartcitydrone.droneservice.DroneServiceOuterClass.PendingStatRequest,
                com.smartcitydrone.droneservice.DroneServiceOuterClass.PendingStatResponse>(
                  this, METHODID_SEND_PENDING_DRONE_STAT)))
          .addMethod(
            getCheckMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.smartcitydrone.droneservice.DroneServiceOuterClass.CheckMessage,
                com.smartcitydrone.droneservice.DroneServiceOuterClass.CheckMessage>(
                  this, METHODID_CHECK)))
          .addMethod(
            getLookForMasterMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.smartcitydrone.droneservice.DroneServiceOuterClass.CheckMessage,
                com.smartcitydrone.droneservice.DroneServiceOuterClass.LookForMasterResponse>(
                  this, METHODID_LOOK_FOR_MASTER)))
          .build();
    }
  }

  /**
   * <pre>
   * Defining a Service, a Service can have multiple RPC operations
   * </pre>
   */
  public static final class DroneServiceStub extends io.grpc.stub.AbstractStub<DroneServiceStub> {
    private DroneServiceStub(io.grpc.Channel channel) {
      super(channel);
    }

    private DroneServiceStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected DroneServiceStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new DroneServiceStub(channel, callOptions);
    }

    /**
     */
    public void joinNetwork(com.smartcitydrone.droneservice.DroneServiceOuterClass.JoinRequest request,
        io.grpc.stub.StreamObserver<com.smartcitydrone.droneservice.DroneServiceOuterClass.JoinResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getJoinNetworkMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void dispatchOrder(com.smartcitydrone.droneservice.DroneServiceOuterClass.OrderRequest request,
        io.grpc.stub.StreamObserver<com.smartcitydrone.droneservice.DroneServiceOuterClass.OrderResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getDispatchOrderMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void sendDroneStat(com.smartcitydrone.droneservice.DroneServiceOuterClass.StatRequest request,
        io.grpc.stub.StreamObserver<com.smartcitydrone.droneservice.DroneServiceOuterClass.StatResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getSendDroneStatMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void election(com.smartcitydrone.droneservice.DroneServiceOuterClass.ElectionRequest request,
        io.grpc.stub.StreamObserver<com.smartcitydrone.droneservice.DroneServiceOuterClass.ElectionResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getElectionMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void elected(com.smartcitydrone.droneservice.DroneServiceOuterClass.ElectedRequest request,
        io.grpc.stub.StreamObserver<com.smartcitydrone.droneservice.DroneServiceOuterClass.ElectedResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getElectedMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void sendPendingDroneStat(com.smartcitydrone.droneservice.DroneServiceOuterClass.PendingStatRequest request,
        io.grpc.stub.StreamObserver<com.smartcitydrone.droneservice.DroneServiceOuterClass.PendingStatResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getSendPendingDroneStatMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void check(com.smartcitydrone.droneservice.DroneServiceOuterClass.CheckMessage request,
        io.grpc.stub.StreamObserver<com.smartcitydrone.droneservice.DroneServiceOuterClass.CheckMessage> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getCheckMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void lookForMaster(com.smartcitydrone.droneservice.DroneServiceOuterClass.CheckMessage request,
        io.grpc.stub.StreamObserver<com.smartcitydrone.droneservice.DroneServiceOuterClass.LookForMasterResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getLookForMasterMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * <pre>
   * Defining a Service, a Service can have multiple RPC operations
   * </pre>
   */
  public static final class DroneServiceBlockingStub extends io.grpc.stub.AbstractStub<DroneServiceBlockingStub> {
    private DroneServiceBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private DroneServiceBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected DroneServiceBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new DroneServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public com.smartcitydrone.droneservice.DroneServiceOuterClass.JoinResponse joinNetwork(com.smartcitydrone.droneservice.DroneServiceOuterClass.JoinRequest request) {
      return blockingUnaryCall(
          getChannel(), getJoinNetworkMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.smartcitydrone.droneservice.DroneServiceOuterClass.OrderResponse dispatchOrder(com.smartcitydrone.droneservice.DroneServiceOuterClass.OrderRequest request) {
      return blockingUnaryCall(
          getChannel(), getDispatchOrderMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.smartcitydrone.droneservice.DroneServiceOuterClass.StatResponse sendDroneStat(com.smartcitydrone.droneservice.DroneServiceOuterClass.StatRequest request) {
      return blockingUnaryCall(
          getChannel(), getSendDroneStatMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.smartcitydrone.droneservice.DroneServiceOuterClass.ElectionResponse election(com.smartcitydrone.droneservice.DroneServiceOuterClass.ElectionRequest request) {
      return blockingUnaryCall(
          getChannel(), getElectionMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.smartcitydrone.droneservice.DroneServiceOuterClass.ElectedResponse elected(com.smartcitydrone.droneservice.DroneServiceOuterClass.ElectedRequest request) {
      return blockingUnaryCall(
          getChannel(), getElectedMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.smartcitydrone.droneservice.DroneServiceOuterClass.PendingStatResponse sendPendingDroneStat(com.smartcitydrone.droneservice.DroneServiceOuterClass.PendingStatRequest request) {
      return blockingUnaryCall(
          getChannel(), getSendPendingDroneStatMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.smartcitydrone.droneservice.DroneServiceOuterClass.CheckMessage check(com.smartcitydrone.droneservice.DroneServiceOuterClass.CheckMessage request) {
      return blockingUnaryCall(
          getChannel(), getCheckMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.smartcitydrone.droneservice.DroneServiceOuterClass.LookForMasterResponse lookForMaster(com.smartcitydrone.droneservice.DroneServiceOuterClass.CheckMessage request) {
      return blockingUnaryCall(
          getChannel(), getLookForMasterMethod(), getCallOptions(), request);
    }
  }

  /**
   * <pre>
   * Defining a Service, a Service can have multiple RPC operations
   * </pre>
   */
  public static final class DroneServiceFutureStub extends io.grpc.stub.AbstractStub<DroneServiceFutureStub> {
    private DroneServiceFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private DroneServiceFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected DroneServiceFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new DroneServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.smartcitydrone.droneservice.DroneServiceOuterClass.JoinResponse> joinNetwork(
        com.smartcitydrone.droneservice.DroneServiceOuterClass.JoinRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getJoinNetworkMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.smartcitydrone.droneservice.DroneServiceOuterClass.OrderResponse> dispatchOrder(
        com.smartcitydrone.droneservice.DroneServiceOuterClass.OrderRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getDispatchOrderMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.smartcitydrone.droneservice.DroneServiceOuterClass.StatResponse> sendDroneStat(
        com.smartcitydrone.droneservice.DroneServiceOuterClass.StatRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getSendDroneStatMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.smartcitydrone.droneservice.DroneServiceOuterClass.ElectionResponse> election(
        com.smartcitydrone.droneservice.DroneServiceOuterClass.ElectionRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getElectionMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.smartcitydrone.droneservice.DroneServiceOuterClass.ElectedResponse> elected(
        com.smartcitydrone.droneservice.DroneServiceOuterClass.ElectedRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getElectedMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.smartcitydrone.droneservice.DroneServiceOuterClass.PendingStatResponse> sendPendingDroneStat(
        com.smartcitydrone.droneservice.DroneServiceOuterClass.PendingStatRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getSendPendingDroneStatMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.smartcitydrone.droneservice.DroneServiceOuterClass.CheckMessage> check(
        com.smartcitydrone.droneservice.DroneServiceOuterClass.CheckMessage request) {
      return futureUnaryCall(
          getChannel().newCall(getCheckMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.smartcitydrone.droneservice.DroneServiceOuterClass.LookForMasterResponse> lookForMaster(
        com.smartcitydrone.droneservice.DroneServiceOuterClass.CheckMessage request) {
      return futureUnaryCall(
          getChannel().newCall(getLookForMasterMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_JOIN_NETWORK = 0;
  private static final int METHODID_DISPATCH_ORDER = 1;
  private static final int METHODID_SEND_DRONE_STAT = 2;
  private static final int METHODID_ELECTION = 3;
  private static final int METHODID_ELECTED = 4;
  private static final int METHODID_SEND_PENDING_DRONE_STAT = 5;
  private static final int METHODID_CHECK = 6;
  private static final int METHODID_LOOK_FOR_MASTER = 7;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final DroneServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(DroneServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_JOIN_NETWORK:
          serviceImpl.joinNetwork((com.smartcitydrone.droneservice.DroneServiceOuterClass.JoinRequest) request,
              (io.grpc.stub.StreamObserver<com.smartcitydrone.droneservice.DroneServiceOuterClass.JoinResponse>) responseObserver);
          break;
        case METHODID_DISPATCH_ORDER:
          serviceImpl.dispatchOrder((com.smartcitydrone.droneservice.DroneServiceOuterClass.OrderRequest) request,
              (io.grpc.stub.StreamObserver<com.smartcitydrone.droneservice.DroneServiceOuterClass.OrderResponse>) responseObserver);
          break;
        case METHODID_SEND_DRONE_STAT:
          serviceImpl.sendDroneStat((com.smartcitydrone.droneservice.DroneServiceOuterClass.StatRequest) request,
              (io.grpc.stub.StreamObserver<com.smartcitydrone.droneservice.DroneServiceOuterClass.StatResponse>) responseObserver);
          break;
        case METHODID_ELECTION:
          serviceImpl.election((com.smartcitydrone.droneservice.DroneServiceOuterClass.ElectionRequest) request,
              (io.grpc.stub.StreamObserver<com.smartcitydrone.droneservice.DroneServiceOuterClass.ElectionResponse>) responseObserver);
          break;
        case METHODID_ELECTED:
          serviceImpl.elected((com.smartcitydrone.droneservice.DroneServiceOuterClass.ElectedRequest) request,
              (io.grpc.stub.StreamObserver<com.smartcitydrone.droneservice.DroneServiceOuterClass.ElectedResponse>) responseObserver);
          break;
        case METHODID_SEND_PENDING_DRONE_STAT:
          serviceImpl.sendPendingDroneStat((com.smartcitydrone.droneservice.DroneServiceOuterClass.PendingStatRequest) request,
              (io.grpc.stub.StreamObserver<com.smartcitydrone.droneservice.DroneServiceOuterClass.PendingStatResponse>) responseObserver);
          break;
        case METHODID_CHECK:
          serviceImpl.check((com.smartcitydrone.droneservice.DroneServiceOuterClass.CheckMessage) request,
              (io.grpc.stub.StreamObserver<com.smartcitydrone.droneservice.DroneServiceOuterClass.CheckMessage>) responseObserver);
          break;
        case METHODID_LOOK_FOR_MASTER:
          serviceImpl.lookForMaster((com.smartcitydrone.droneservice.DroneServiceOuterClass.CheckMessage) request,
              (io.grpc.stub.StreamObserver<com.smartcitydrone.droneservice.DroneServiceOuterClass.LookForMasterResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class DroneServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    DroneServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.smartcitydrone.droneservice.DroneServiceOuterClass.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("DroneService");
    }
  }

  private static final class DroneServiceFileDescriptorSupplier
      extends DroneServiceBaseDescriptorSupplier {
    DroneServiceFileDescriptorSupplier() {}
  }

  private static final class DroneServiceMethodDescriptorSupplier
      extends DroneServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    DroneServiceMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (DroneServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new DroneServiceFileDescriptorSupplier())
              .addMethod(getJoinNetworkMethod())
              .addMethod(getDispatchOrderMethod())
              .addMethod(getSendDroneStatMethod())
              .addMethod(getElectionMethod())
              .addMethod(getElectedMethod())
              .addMethod(getSendPendingDroneStatMethod())
              .addMethod(getCheckMethod())
              .addMethod(getLookForMasterMethod())
              .build();
        }
      }
    }
    return result;
  }
}
