package jobshop.agents;

import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bridge.fipa.FipaMessage;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentFeature;

import jadex.micro.annotation.AgentMessageArrived;
import jobshop.agents.messaging.CustomMessages;
import jobshop.environment.SensorActuatorDispatcher;
import jobshop.environment.impl.Environment;


@Agent(type = "bdi")
public class DispatcherAgent {
  private SensorActuatorDispatcher actsense = new SensorActuatorDispatcher();
  private Environment env = Environment.getInstance();

  @AgentFeature
  protected IBDIAgentFeature bdiFeature;

  @AgentBody
  private void behavior() {
    bdiFeature.dispatchTopLevelGoal(new CreateProducts());
  }


  //--------------------- Create products -----------------------------

  @Goal(recur = true)
  private class CreateProducts {}

  @Plan(trigger = @Trigger(goals = CreateProducts.class))
  private void createProductsPlan(CreateProducts goal) {
    if (!actsense.getResetFlag()) {
      actsense.trySpawn(false);
    }
  }

  //----------------- Communication between agents -------------------

  @AgentMessageArrived
  void messageArrived(FipaMessage message) {
    if (CustomMessages.Performative.RESET.equals(message.getPerformative())) {
      reset();
    }
  }

  private void reset() {
    actsense.reset();
  }

}
