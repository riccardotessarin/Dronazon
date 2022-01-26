package com.smartcitydrone.chargeservice;

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
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.25.0)",
    comments = "Source: ChargeService.proto")
public final class ChargeServiceGrpc {

  private ChargeServiceGrpc() {}

  public static final String SERVICE_NAME = "com.smartcitydrone.chargeservice.ChargeService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.smartcitydrone.chargeservice.ChargeServiceOuterClass.ChargeRequest,
      com.smartcitydrone.chargeservice.ChargeServiceOuterClass.ChargeResponse> getChargeMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "charge",
      requestType = com.smartcitydrone.chargeservice.ChargeServiceOuterClass.ChargeRequest.class,
      responseType = com.smartcitydrone.chargeservice.ChargeServiceOuterClass.ChargeResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.smartcitydrone.chargeservice.ChargeServiceOuterClass.ChargeRequest,
      com.smartcitydrone.chargeservice.ChargeServiceOuterClass.ChargeResponse> getChargeMethod() {
    io.grpc.MethodDescriptor<com.smartcitydrone.chargeservice.ChargeServiceOuterClass.ChargeRequest, com.smartcitydrone.chargeservice.ChargeServiceOuterClass.ChargeResponse> getChargeMethod;
    if ((getChargeMethod = ChargeServiceGrpc.getChargeMethod) == null) {
      synchronized (ChargeServiceGrpc.class) {
        if ((getChargeMethod = ChargeServiceGrpc.getChargeMethod) == null) {
          ChargeServiceGrpc.getChargeMethod = getChargeMethod =
              io.grpc.MethodDescriptor.<com.smartcitydrone.chargeservice.ChargeServiceOuterClass.ChargeRequest, com.smartcitydrone.chargeservice.ChargeServiceOuterClass.ChargeResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "charge"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.smartcitydrone.chargeservice.ChargeServiceOuterClass.ChargeRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.smartcitydrone.chargeservice.ChargeServiceOuterClass.ChargeResponse.getDefaultInstance()))
              .setSchemaDescriptor(new ChargeServiceMethodDescriptorSupplier("charge"))
              .build();
        }
      }
    }
    return getChargeMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static ChargeServiceStub newStub(io.grpc.Channel channel) {
    return new ChargeServiceStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static ChargeServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new ChargeServiceBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static ChargeServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new ChargeServiceFutureStub(channel);
  }

  /**
   */
  public static abstract class ChargeServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void charge(com.smartcitydrone.chargeservice.ChargeServiceOuterClass.ChargeRequest request,
        io.grpc.stub.StreamObserver<com.smartcitydrone.chargeservice.ChargeServiceOuterClass.ChargeResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getChargeMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getChargeMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.smartcitydrone.chargeservice.ChargeServiceOuterClass.ChargeRequest,
                com.smartcitydrone.chargeservice.ChargeServiceOuterClass.ChargeResponse>(
                  this, METHODID_CHARGE)))
          .build();
    }
  }

  /**
   */
  public static final class ChargeServiceStub extends io.grpc.stub.AbstractStub<ChargeServiceStub> {
    private ChargeServiceStub(io.grpc.Channel channel) {
      super(channel);
    }

    private ChargeServiceStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ChargeServiceStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new ChargeServiceStub(channel, callOptions);
    }

    /**
     */
    public void charge(com.smartcitydrone.chargeservice.ChargeServiceOuterClass.ChargeRequest request,
        io.grpc.stub.StreamObserver<com.smartcitydrone.chargeservice.ChargeServiceOuterClass.ChargeResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getChargeMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class ChargeServiceBlockingStub extends io.grpc.stub.AbstractStub<ChargeServiceBlockingStub> {
    private ChargeServiceBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private ChargeServiceBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ChargeServiceBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new ChargeServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public com.smartcitydrone.chargeservice.ChargeServiceOuterClass.ChargeResponse charge(com.smartcitydrone.chargeservice.ChargeServiceOuterClass.ChargeRequest request) {
      return blockingUnaryCall(
          getChannel(), getChargeMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class ChargeServiceFutureStub extends io.grpc.stub.AbstractStub<ChargeServiceFutureStub> {
    private ChargeServiceFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private ChargeServiceFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ChargeServiceFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new ChargeServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.smartcitydrone.chargeservice.ChargeServiceOuterClass.ChargeResponse> charge(
        com.smartcitydrone.chargeservice.ChargeServiceOuterClass.ChargeRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getChargeMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_CHARGE = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final ChargeServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(ChargeServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_CHARGE:
          serviceImpl.charge((com.smartcitydrone.chargeservice.ChargeServiceOuterClass.ChargeRequest) request,
              (io.grpc.stub.StreamObserver<com.smartcitydrone.chargeservice.ChargeServiceOuterClass.ChargeResponse>) responseObserver);
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

  private static abstract class ChargeServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    ChargeServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.smartcitydrone.chargeservice.ChargeServiceOuterClass.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("ChargeService");
    }
  }

  private static final class ChargeServiceFileDescriptorSupplier
      extends ChargeServiceBaseDescriptorSupplier {
    ChargeServiceFileDescriptorSupplier() {}
  }

  private static final class ChargeServiceMethodDescriptorSupplier
      extends ChargeServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    ChargeServiceMethodDescriptorSupplier(String methodName) {
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
      synchronized (ChargeServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new ChargeServiceFileDescriptorSupplier())
              .addMethod(getChargeMethod())
              .build();
        }
      }
    }
    return result;
  }
}
