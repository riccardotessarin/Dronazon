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

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getJoinNetworkMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.smartcitydrone.droneservice.DroneServiceOuterClass.JoinRequest,
                com.smartcitydrone.droneservice.DroneServiceOuterClass.JoinResponse>(
                  this, METHODID_JOIN_NETWORK)))
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
  }

  private static final int METHODID_JOIN_NETWORK = 0;

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
              .build();
        }
      }
    }
    return result;
  }
}
