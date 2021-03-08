package jobshop.agents;

import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bdiv3.runtime.IPlan;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IMessageFeature;
import jadex.bridge.fipa.FipaMessage;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentFeature;
import jadex.micro.annotation.AgentMessageArrived;
import jobshop.agents.messaging.CustomMessages.Performative;
import jobshop.environment.SensorActuatorMachine;


@Agent(type = "bdi")
public class MachineAgent {

  private static final String PROCESS = "PROCESS";
  private static final String CHANGE = "CHANGE";
  private static final String MAINTENANCE = "MAINTENANCE";

  private SensorActuatorMachine actsense = new SensorActuatorMachine();

  private String capability;
  private String operation = "PROCESS";

  @Agent
  protected IInternalAccess agent;

  @AgentFeature
  protected IBDIAgentFeature bdiFeature;

  @AgentBody
  private void behavior() {
    actsense.setup();
    bdiFeature.dispatchTopLevelGoal(new MachinePlan());
  }

  //----------------- Get a product from the input-buffer to work on -----------------

  @Goal(recur = true)
  private class MachinePlan {}

  @Plan(trigger = @Trigger(goals = MachinePlan.class))
  private void machinePlan(IPlan plan) {
    if (!actsense.getResetFlag() && !actsense.getMessageFlag()) {
      switch (operation) {
        case PROCESS:
          process();
          break;
        case CHANGE:
          change();
          operation = PROCESS;
          break;
        case MAINTENANCE:
          change();
          maintenance();
          operation = PROCESS;
          break;
        default:
          operation = PROCESS;
          System.out.println("Invalid operation!");
      }
    }
  }

  private void process() {
    if (actsense.tryGetInputProduct()) {
      actsense.processProduct();
    }
  }

  private void change() {
    actsense.changeMachineCapability(capability);
  }

  private void maintenance() {
    actsense.maintenanceMachineCapability(capability);
  }

  //----------------- Communication between agents -------------------

  @AgentMessageArrived
  void messageArrived(FipaMessage message) {
    switch (message.getPerformative()) {
      case Performative.DELIVERY_PROPOSAL:
        checkDeliveryProposal(message);
        break;
      case Performative.PICKUP_PROPOSAL:
        checkPickupProposal(message);
        break;
      case Performative.ACTION_CHANGE:
        actionChange(message);
        break;
      case Performative.ACTION_MAINTENANCE:
        actionMaintenance(message);
        break;
      case Performative.RESET:
        reset();
        break;
      default:
        break;
    }
  }

  private void checkDeliveryProposal(FipaMessage message) {
    String content = (String) message.getContent();
    if (actsense.tryAcceptProduct(content)) {
      answerProposal(message.getSender(), Performative.DELIVERY_ACCEPT, null);
    } else {
      actsense.rejectProduct();
      answerProposal(message.getSender(), Performative.DELIVERY_REJECT, null);
    }
  }

  private void checkPickupProposal(FipaMessage message) {
    String content = actsense.tryHandoutProduct().getId();
    if (content != null) {
      answerProposal(message.getSender(), Performative.PICKUP_ACCEPT, content);
    } else {
      answerProposal(message.getSender(), Performative.PICKUP_REJECT, content);
    }
  }

  private void answerProposal(IComponentIdentifier receiver, String performative, Object content) {
    FipaMessage message = new FipaMessage(performative, content);
    message.addReceiver(receiver);
    agent.getFeature(IMessageFeature.class).sendMessage(message).get();
  }

  private void actionChange(FipaMessage message) {
    capability = (String) message.getContent();
    operation = CHANGE;
    actsense.setActionFlag(false);
  }

  private void actionMaintenance(FipaMessage message) {
    capability = (String) message.getContent();
    operation = MAINTENANCE;
    actsense.setActionFlag(false);
  }

  private void reset() {
    actsense.reset();
  }

}
