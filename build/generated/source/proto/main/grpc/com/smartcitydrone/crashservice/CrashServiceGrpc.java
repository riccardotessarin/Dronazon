package com.smartcitydrone.crashservice;

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
    comments = "Source: CrashService.proto")
public final class CrashServiceGrpc {

  private CrashServiceGrpc() {}

  public static final String SERVICE_NAME = "com.smartcitydrone.crashservice.CrashService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.smartcitydrone.crashservice.CrashServiceOuterClass.CheckMessage,
      com.smartcitydrone.crashservice.CrashServiceOuterClass.CheckMessage> getCheckChargeMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "checkCharge",
      requestType = com.smartcitydrone.crashservice.CrashServiceOuterClass.CheckMessage.class,
      responseType = com.smartcitydrone.crashservice.CrashServiceOuterClass.CheckMessage.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.smartcitydrone.crashservice.CrashServiceOuterClass.CheckMessage,
      com.smartcitydrone.crashservice.CrashServiceOuterClass.CheckMessage> getCheckChargeMethod() {
    io.grpc.MethodDescriptor<com.smartcitydrone.crashservice.CrashServiceOuterClass.CheckMessage, com.smartcitydrone.crashservice.CrashServiceOuterClass.CheckMessage> getCheckChargeMethod;
    if ((getCheckChargeMethod = CrashServiceGrpc.getCheckChargeMethod) == null) {
      synchronized (CrashServiceGrpc.class) {
        if ((getCheckChargeMethod = CrashServiceGrpc.getCheckChargeMethod) == null) {
          CrashServiceGrpc.getCheckChargeMethod = getCheckChargeMethod =
              io.grpc.MethodDescriptor.<com.smartcitydrone.crashservice.CrashServiceOuterClass.CheckMessage, com.smartcitydrone.crashservice.CrashServiceOuterClass.CheckMessage>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "checkCharge"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.smartcitydrone.crashservice.CrashServiceOuterClass.CheckMessage.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.smartcitydrone.crashservice.CrashServiceOuterClass.CheckMessage.getDefaultInstance()))
              .setSchemaDescriptor(new CrashServiceMethodDescriptorSupplier("checkCharge"))
              .build();
        }
      }
    }
    return getCheckChargeMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.smartcitydrone.crashservice.CrashServiceOuterClass.ResetElection,
      com.smartcitydrone.crashservice.CrashServiceOuterClass.ResetElection> getRestartElectionMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "restartElection",
      requestType = com.smartcitydrone.crashservice.CrashServiceOuterClass.ResetElection.class,
      responseType = com.smartcitydrone.crashservice.CrashServiceOuterClass.ResetElection.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.smartcitydrone.crashservice.CrashServiceOuterClass.ResetElection,
      com.smartcitydrone.crashservice.CrashServiceOuterClass.ResetElection> getRestartElectionMethod() {
    io.grpc.MethodDescriptor<com.smartcitydrone.crashservice.CrashServiceOuterClass.ResetElection, com.smartcitydrone.crashservice.CrashServiceOuterClass.ResetElection> getRestartElectionMethod;
    if ((getRestartElectionMethod = CrashServiceGrpc.getRestartElectionMethod) == null) {
      synchronized (CrashServiceGrpc.class) {
        if ((getRestartElectionMethod = CrashServiceGrpc.getRestartElectionMethod) == null) {
          CrashServiceGrpc.getRestartElectionMethod = getRestartElectionMethod =
              io.grpc.MethodDescriptor.<com.smartcitydrone.crashservice.CrashServiceOuterClass.ResetElection, com.smartcitydrone.crashservice.CrashServiceOuterClass.ResetElection>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "restartElection"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.smartcitydrone.crashservice.CrashServiceOuterClass.ResetElection.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.smartcitydrone.crashservice.CrashServiceOuterClass.ResetElection.getDefaultInstance()))
              .setSchemaDescriptor(new CrashServiceMethodDescriptorSupplier("restartElection"))
              .build();
        }
      }
    }
    return getRestartElectionMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static CrashServiceStub newStub(io.grpc.Channel channel) {
    return new CrashServiceStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static CrashServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new CrashServiceBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static CrashServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new CrashServiceFutureStub(channel);
  }

  /**
   */
  public static abstract class CrashServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void checkCharge(com.smartcitydrone.crashservice.CrashServiceOuterClass.CheckMessage request,
        io.grpc.stub.StreamObserver<com.smartcitydrone.crashservice.CrashServiceOuterClass.CheckMessage> responseObserver) {
      asyncUnimplementedUnaryCall(getCheckChargeMethod(), responseObserver);
    }

    /**
     */
    public void restartElection(com.smartcitydrone.crashservice.CrashServiceOuterClass.ResetElection request,
        io.grpc.stub.StreamObserver<com.smartcitydrone.crashservice.CrashServiceOuterClass.ResetElection> responseObserver) {
      asyncUnimplementedUnaryCall(getRestartElectionMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getCheckChargeMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.smartcitydrone.crashservice.CrashServiceOuterClass.CheckMessage,
                com.smartcitydrone.crashservice.CrashServiceOuterClass.CheckMessage>(
                  this, METHODID_CHECK_CHARGE)))
          .addMethod(
            getRestartElectionMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.smartcitydrone.crashservice.CrashServiceOuterClass.ResetElection,
                com.smartcitydrone.crashservice.CrashServiceOuterClass.ResetElection>(
                  this, METHODID_RESTART_ELECTION)))
          .build();
    }
  }

  /**
   */
  public static final class CrashServiceStub extends io.grpc.stub.AbstractStub<CrashServiceStub> {
    private CrashServiceStub(io.grpc.Channel channel) {
      super(channel);
    }

    private CrashServiceStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected CrashServiceStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new CrashServiceStub(channel, callOptions);
    }

    /**
     */
    public void checkCharge(com.smartcitydrone.crashservice.CrashServiceOuterClass.CheckMessage request,
        io.grpc.stub.StreamObserver<com.smartcitydrone.crashservice.CrashServiceOuterClass.CheckMessage> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getCheckChargeMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void restartElection(com.smartcitydrone.crashservice.CrashServiceOuterClass.ResetElection request,
        io.grpc.stub.StreamObserver<com.smartcitydrone.crashservice.CrashServiceOuterClass.ResetElection> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getRestartElectionMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class CrashServiceBlockingStub extends io.grpc.stub.AbstractStub<CrashServiceBlockingStub> {
    private CrashServiceBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private CrashServiceBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected CrashServiceBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new CrashServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public com.smartcitydrone.crashservice.CrashServiceOuterClass.CheckMessage checkCharge(com.smartcitydrone.crashservice.CrashServiceOuterClass.CheckMessage request) {
      return blockingUnaryCall(
          getChannel(), getCheckChargeMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.smartcitydrone.crashservice.CrashServiceOuterClass.ResetElection restartElection(com.smartcitydrone.crashservice.CrashServiceOuterClass.ResetElection request) {
      return blockingUnaryCall(
          getChannel(), getRestartElectionMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class CrashServiceFutureStub extends io.grpc.stub.AbstractStub<CrashServiceFutureStub> {
    private CrashServiceFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private CrashServiceFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected CrashServiceFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new CrashServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.smartcitydrone.crashservice.CrashServiceOuterClass.CheckMessage> checkCharge(
        com.smartcitydrone.crashservice.CrashServiceOuterClass.CheckMessage request) {
      return futureUnaryCall(
          getChannel().newCall(getCheckChargeMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.smartcitydrone.crashservice.CrashServiceOuterClass.ResetElection> restartElection(
        com.smartcitydrone.crashservice.CrashServiceOuterClass.ResetElection request) {
      return futureUnaryCall(
          getChannel().newCall(getRestartElectionMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_CHECK_CHARGE = 0;
  private static final int METHODID_RESTART_ELECTION = 1;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final CrashServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(CrashServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_CHECK_CHARGE:
          serviceImpl.checkCharge((com.smartcitydrone.crashservice.CrashServiceOuterClass.CheckMessage) request,
              (io.grpc.stub.StreamObserver<com.smartcitydrone.crashservice.CrashServiceOuterClass.CheckMessage>) responseObserver);
          break;
        case METHODID_RESTART_ELECTION:
          serviceImpl.restartElection((com.smartcitydrone.crashservice.CrashServiceOuterClass.ResetElection) request,
              (io.grpc.stub.StreamObserver<com.smartcitydrone.crashservice.CrashServiceOuterClass.ResetElection>) responseObserver);
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

  private static abstract class CrashServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    CrashServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.smartcitydrone.crashservice.CrashServiceOuterClass.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("CrashService");
    }
  }

  private static final class CrashServiceFileDescriptorSupplier
      extends CrashServiceBaseDescriptorSupplier {
    CrashServiceFileDescriptorSupplier() {}
  }

  private static final class CrashServiceMethodDescriptorSupplier
      extends CrashServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    CrashServiceMethodDescriptorSupplier(String methodName) {
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
      synchronized (CrashServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new CrashServiceFileDescriptorSupplier())
              .addMethod(getCheckChargeMethod())
              .addMethod(getRestartElectionMethod())
              .build();
        }
      }
    }
    return result;
  }
}
