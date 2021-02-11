package jobshop.agents;

import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bridge.IInternalAccess;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentFeature;
import jadex.micro.annotation.AgentKilled;
import java.io.IOException;

import jobshop.grpc.Server;


@Agent(type = "bdi")
public class ServerAgent {

  private Server server;

  @Agent
  protected IInternalAccess agent;

  @AgentFeature
  protected IBDIAgentFeature bdiFeature;

  @AgentBody
  private void behavior() {
    try {
      server = new Server(agent);
      server.start();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @AgentKilled
  private void killed() {
    try {
      server.stop();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

}
