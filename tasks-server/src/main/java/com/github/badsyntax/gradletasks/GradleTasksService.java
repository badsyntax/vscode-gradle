package com.github.badsyntax.gradletasks;

import java.io.File;
import com.google.rpc.Code;
import com.google.rpc.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.grpc.StatusRuntimeException;
import io.grpc.protobuf.StatusProto;
import io.grpc.stub.StreamObserver;

public class GradleTasksService extends GradleTasksGrpc.GradleTasksImplBase {
  private static final Logger logger = LoggerFactory.getLogger(GradleTasksService.class.getName());
  private static final String SOURCE_DIR_ERROR = "Source directory does not exist: %s";

  @Override
  public void getProject(final GetProjectRequest req,
      final StreamObserver<GetProjectReply> responseObserver) {
    try {
      File sourceDir = new File(req.getSourceDir().trim());
      if (!sourceDir.exists()) {
        throw new GradleTasksException(String.format(SOURCE_DIR_ERROR, req.getSourceDir()));
      }
      GradleTasksUtil.getProject(sourceDir, responseObserver);
      responseObserver.onCompleted();
    } catch (GradleTasksException e) {
      logger.error(e.getMessage());
      StatusRuntimeException exception = StatusProto.toStatusRuntimeException(Status.newBuilder()
          .setCode(Code.INTERNAL.getNumber()).setMessage(e.getMessage()).build());
      responseObserver.onError(exception);
    }
  }

  @Override
  public void runTask(final RunTaskRequest req,
      final StreamObserver<RunTaskReply> responseObserver) {
    try {
      File sourceDir = new File(req.getSourceDir().trim());
      if (!sourceDir.exists()) {
        throw new GradleTasksException(String.format(SOURCE_DIR_ERROR, req.getSourceDir()));
      }
      GradleTasksUtil.runTask(sourceDir, req.getTask(), req.getArgsList(), responseObserver);
      responseObserver.onCompleted();
    } catch (GradleTasksException e) {
      logger.error(e.getMessage());
      StatusRuntimeException exception = StatusProto.toStatusRuntimeException(Status.newBuilder()
          .setCode(Code.INTERNAL.getNumber()).setMessage(e.getMessage()).build());
      responseObserver.onError(exception);
    }
  }

  @Override
  public void cancelGetProjects(final CancelGetProjectsRequest req,
      final StreamObserver<CancelGetProjectsReply> responseObserver) {
    GradleTasksUtil.cancelGetProjects(responseObserver);
    responseObserver.onCompleted();
  }

  @Override
  public void cancelRunTask(final CancelRunTaskRequest req,
      final StreamObserver<CancelRunTaskReply> responseObserver) {
    try {
      File sourceDir = new File(req.getSourceDir().trim());
      if (!sourceDir.exists()) {
        throw new GradleTasksException(String.format(SOURCE_DIR_ERROR, req.getSourceDir()));
      }
      GradleTasksUtil.cancelRunTask(sourceDir, req.getTask(), responseObserver);
      responseObserver.onCompleted();
    } catch (GradleTasksException e) {
      logger.error(e.getMessage());
      StatusRuntimeException exception = StatusProto.toStatusRuntimeException(Status.newBuilder()
          .setCode(Code.INTERNAL.getNumber()).setMessage(e.getMessage()).build());
      responseObserver.onError(exception);
    }
  }

  @Override
  public void cancelRunTasks(CancelRunTasksRequest req,
      StreamObserver<CancelRunTasksReply> responseObserver) {
    GradleTasksUtil.cancelRunTasks(responseObserver);
    responseObserver.onCompleted();
  }
}