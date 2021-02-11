package jobshop;

import java.io.IOException;

import jobshop.agents.JadexManager;
import jobshop.environment.impl.Environment;
import jobshop.grpc.Server;

public class GymMain {

  public static void main(String[] args) {
    if (args.length > 0) {
      Server.setPort(Integer.parseInt(args[0]));
    }
    JadexManager.getInstance().startPlatform();
  }
}
