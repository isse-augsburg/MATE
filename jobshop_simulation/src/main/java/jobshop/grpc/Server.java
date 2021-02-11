package jobshop.grpc;

import io.grpc.ServerBuilder;
import jadex.bridge.IInternalAccess;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;


public class Server {

  private static int port = 50051;

  private static final Logger logger = Logger.getLogger(Server.class.getName());
  private io.grpc.Server server;
  private IInternalAccess agent;

  public Server(IInternalAccess agent) {
    this.agent = agent;
  }

  public static void setPort(int newPort) {
    port = newPort;
  }

  /**
   * Start the GRPC server.
   * @throws IOException catch that one.
   */
  public void start() throws IOException {
    /* The port on which the server should run */
    server = ServerBuilder.forPort(port)
        .addService(new ServiceImpl.EnvironmentImpl(agent))
        .build()
        .start();
    logger.info("Server started, listening on " + port);
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      // Use stderr here since the logger may have been reset by its JVM shutdown hook.
      System.err.println("*** shutting down gRPC server since JVM is shutting down");
      try {
        Server.this.stop();
      } catch (InterruptedException e) {
        e.printStackTrace(System.err);
      }
      System.err.println("*** server shut down");
    }));
  }

  /**
   * Stop the running server.
   * @throws InterruptedException catch me if you can.
   */
  public void stop() throws InterruptedException {
    if (server != null) {
      server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
    }
  }

  /**
   * Await termination on the main thread since the grpc library uses daemon threads.
   */
  public void blockUntilShutdown() throws InterruptedException {
    if (server != null) {
      server.awaitTermination();
    }
  }
}