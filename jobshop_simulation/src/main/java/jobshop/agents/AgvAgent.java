package jobshop.agents;

import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalContextCondition;
import jadex.bdiv3.annotation.GoalParameter;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bdiv3.runtime.IPlan;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IMessageFeature;
import jadex.bridge.fipa.FipaMessage;

import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentFeature;
import jadex.micro.annotation.AgentMessageArrived;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jobshop.agents.algortihms.PotentialField;
import jobshop.agents.algortihms.helper.Vector;
import jobshop.agents.messaging.CustomMessages.Performative;
import jobshop.environment.IAutomatedGuidedVehicle;
import jobshop.environment.ILocation;
import jobshop.environment.IMachine;
import jobshop.environment.IProduct;
import jobshop.environment.SensorActuatorAgv;
import jobshop.environment.impl.Environment;
import jobshop.environment.impl.Location;
import jobshop.environment.impl.Product;

@Agent(type = "bdi")
public class AgvAgent {

  private static final int NAVIGATE_ITER_DEFAULT = 1;
  private static final int NAVIGATE_ITER_RANDOM = 20;
  private static final int NAVIGATE_ITER_DELIVER = 10;

  private static final int MESSAGE_DELAY = 100;

  private SensorActuatorAgv actsense = new SensorActuatorAgv();

  @Belief
  private IAutomatedGuidedVehicle self = actsense.getSelf();

  @Agent
  protected IInternalAccess agent;

  @AgentFeature
  protected IBDIAgentFeature bdiFeature;

  @AgentBody
  private void reactiveBehavior() {
    bdiFeature.dispatchTopLevelGoal(new SearchProduct());
    bdiFeature.dispatchTopLevelGoal(new DeliverProduct());
  }

  //------------------------- Search Product ---------------------------

  @Goal(recur = true)
  private class SearchProduct {
    @GoalContextCondition(beliefs = {"self"})
    private boolean context() {
      return self.getProduct() == null;
    }
  }

  @Plan(trigger = @Trigger(goals = SearchProduct.class))
  private void searchProduct(IPlan plan) {
    if (!actsense.getResetFlag()) {
      IMachine machine = null;
      IProduct product = selectRandomProduct();

      //sum up all potential forces
      LinkedHashMap<IMachine, Double> machineForces = getMachineForces();
      double forceSum = machineForces.values().stream().reduce(0.0, Double::sum);
      if (product != null) {
        forceSum += exponentialDecay(product.getLocation(), calcProductPotential(product));
      }

      //select random machine/product based on force-strength
      double current = 0;
      double rand = actsense.getRandom().nextDouble();
      for (Map.Entry<IMachine, Double> entry: machineForces.entrySet()) {
        current += entry.getValue() / forceSum;
        if (rand < current) {
          machine = entry.getKey();
          break;
        }
      }

      if (machine != null) {
        plan.dispatchSubgoal(new PickUpMachineProduct(machine)).get();
      } else if (product != null) {
        plan.dispatchSubgoal(new PickUpRandomProduct(product)).get();
      } else {
        plan.dispatchSubgoal(new MoveRandom()).get();
      }
    }
  }


  //----------------------- Get product from machine -------------------------

  @Goal
  static class PickUpMachineProduct {
    @GoalParameter
    private IMachine machine;

    public PickUpMachineProduct(IMachine machine) {
      this.machine = machine;
    }
  }

  @Plan(trigger = @Trigger(goals = PickUpMachineProduct.class))
  private void pickUpMachineProduct(PickUpMachineProduct goal) {
    while (actsense.getMachine(goal.machine.getAgentIdentifier()).getOutputPotentialForce() > 0.0
            && !actsense.getResetFlag()) {
      navigateLoop(goal.machine.getOutputLocation(), NAVIGATE_ITER_DEFAULT);
      if (actsense.checkRange(goal.machine.getOutputLocation())) {
        sendPickupProposal(goal.machine);
        break;
      }
    }
  }


  //-------------------- Get random product from start-zone ------------------------

  @Goal
  static class PickUpRandomProduct {
    @GoalParameter
    private IProduct product;

    public PickUpRandomProduct(IProduct product) {
      this.product = product;
    }
  }

  @Plan(trigger = @Trigger(goals = PickUpRandomProduct.class))
  private void pickUpRandomProduct(PickUpRandomProduct goal) {
    while (!actsense.getProduct(goal.product.getId()).isLocked()
            && !actsense.getResetFlag()) {
      navigateLoop(goal.product.getLocation(), NAVIGATE_ITER_DEFAULT);
      actsense.pickupProductFromGround((Product) goal.product);
    }
  }


  //----------------- Move to random location ------------------------------

  @Goal
  private class MoveRandom {}

  @Plan(trigger = @Trigger(goals = MoveRandom.class))
  private void moveRandom(MoveRandom goal) {
    if (!actsense.getResetFlag()) {
      navigateLoop(new Location(actsense.getRandom().nextDouble(),
                      actsense.getRandom().nextDouble()), NAVIGATE_ITER_RANDOM);
    }
  }


  //------------------------- Deliver Product ---------------------------

  @Goal(recur = true)
  private class DeliverProduct {
    @GoalContextCondition(beliefs = {"self"})
    private boolean context() {
      return self.getProduct() != null;
    }
  }

  @Plan(trigger = @Trigger(goals = DeliverProduct.class))
  private void locateReceiver(IPlan plan) {
    if (!actsense.getResetFlag()) {
      String step = self.getProduct().getCurrentStepId();
      if (step.equals(self.getProduct().getFinTag())) {
        ILocation randomLoc = actsense.getEndZone().getRandomLocation(actsense.getRandom());
        plan.dispatchSubgoal(new DeliverProductToZone(randomLoc)).get();
      } else {
        IMachine machine = selectRandomDeliverMachine(step);
        if (machine != null) {
          plan.dispatchSubgoal(new DeliverProductToMachine(machine)).get();
        } else {
          ILocation randomLoc = actsense.getStartZone().getRandomLocation(actsense.getRandom());
          plan.dispatchSubgoal(new DeliverProductToZone(randomLoc)).get();
        }
      }
    }
  }


  //--------------- Deliver product to machine ----------------------------

  @Goal
  static class DeliverProductToMachine {
    @GoalParameter
    IMachine machine;

    public  DeliverProductToMachine(IMachine machine) {
      this.machine = machine;
    }
  }

  @Plan(trigger = @Trigger(goals = DeliverProductToMachine.class))
  private void deliverProductToMachine(DeliverProductToMachine goal) {
    while (actsense.getMachine(goal.machine.getAgentIdentifier()).getInputPotentialForce() > 0.0
            && !actsense.getResetFlag()) {
      navigateLoop(goal.machine.getInputLocation(), NAVIGATE_ITER_DEFAULT);
      if (actsense.checkRange(goal.machine.getInputLocation())) {
        sendDeliveryProposal(goal.machine, self.getProduct());
      }
    }
  }


  //--------------- Deliver product to a zone ----------------------------

  @Goal
  static class DeliverProductToZone {
    @GoalParameter
    ILocation location;

    public DeliverProductToZone(ILocation location) {
      this.location = location;
    }
  }

  @Plan(trigger = @Trigger(goals = DeliverProductToZone.class))
  private void deliverProductToZone(DeliverProductToZone goal) {
    if (!actsense.getResetFlag()) {
      navigateLoop(goal.location, NAVIGATE_ITER_DELIVER);
      if (actsense.checkRange(goal.location)) {
        if (self.getProduct().getCurrentStepId().equals(self.getProduct().getFinTag())) {
          actsense.finishProduct();
        } else {
          actsense.dropUnlockedProduct();
        }
      }
    }
  }


  //-------------- Messaging between agents (AGV and Machine) ------------------
  /** ATTENTION: When sending whole objects Jadex will eventually reset some fields
   * (maybe because I switched from clone to copy constructor, not sure yet). */

  @AgentMessageArrived
  void messageArrived(FipaMessage message) {
    switch (message.getPerformative()) {
      case Performative.DELIVERY_ACCEPT:
        acceptedDeliveryProposal(message);
        break;
      case Performative.DELIVERY_REJECT:
        rejectedDeliveryProposal(message);
        break;
      case Performative.PICKUP_ACCEPT:
        acceptedPickupProposal(message);
        break;
      case Performative.PICKUP_REJECT:
        rejectedPickupProposal(message);
        break;
      case Performative.RESET:
        reset();
        break;
      default:
        break;
    }
  }

  private void sendDeliveryProposal(IMachine machine, IProduct product) {
    if (actsense.syncStartInputMessage(machine.getAgentIdentifier())) {
      FipaMessage message = new FipaMessage(Performative.DELIVERY_PROPOSAL, product.getId());
      message.addReceiver(machine.getAgentIdentifier());
      agent.getFeature(IMessageFeature.class).sendMessage(message).get();
    }
  }

  private void acceptedDeliveryProposal(FipaMessage message) {
    actsense.dropLockedProduct();
    actsense.syncEndInputMessage(message.getSender());
  }

  private void rejectedDeliveryProposal(FipaMessage message) {
    System.out.println("Delivery not possible, input-buffer full!");
    actsense.syncEndInputMessage(message.getSender());
    agent.waitForDelay(MESSAGE_DELAY).get();
  }

  private void sendPickupProposal(IMachine machine) {
    if (actsense.syncStartOutputMessage(machine.getAgentIdentifier())) {
      FipaMessage message = new FipaMessage(Performative.PICKUP_PROPOSAL, null);
      message.addReceiver(machine.getAgentIdentifier());
      agent.getFeature(IMessageFeature.class).sendMessage(message).get();
    }
  }

  private void acceptedPickupProposal(FipaMessage message) {
    String content = (String) message.getContent();
    actsense.pickupProductFromMachine(content);
    actsense.syncEndOutputMessage(message.getSender());
  }

  private void rejectedPickupProposal(FipaMessage message) {
    System.out.println("Pickup not possible, output-buffer empty!");
    actsense.syncEndOutputMessage(message.getSender());
    agent.waitForDelay(MESSAGE_DELAY).get();
  }

  private void reset() {
    actsense.reset();
  }


  //----------------- helper methods ---------------------------

  /**
   * Navigate in the direction of the goal for a specified number of times,
   * afterwards may check for new signals/goals.
   * @param location The goal location.
   * @param iterations The number of navigation steps.
   */
  private void navigateLoop(ILocation location, int iterations) {
    for (int i = 0; i < iterations; i++) {
      if (actsense.checkRange(location) || actsense.getResetFlag()) {
        break;
      }
      navigate(location);
    }
  }

  /**
   * Navigate to the next position, using the potential-fields algorithm.
   * @param goal The goal location.
   */
  private void navigate(ILocation goal) {
    List<Vector> vectors = new ArrayList<>();
    vectors.add(PotentialField.attractionForce(self.getLocation(), goal, 0.01));
    vectors.add(PotentialField.randomForce(0.002, actsense.getRandom()));
    vectors.addAll(getAgvPotential());
    vectors.addAll(getMachinePotential());
    Vector vector = vectorSummation(vectors, 0.01);
    ILocation locOld = self.getLocation();
    actsense.moveTo(new Location(locOld.getPosX() + vector.getEx(),
        locOld.getPosY() + vector.getEy()));
  }

  private List<Vector> getAgvPotential() {
    return actsense.getSensedAgvs().stream()
        .map(agv -> PotentialField.repulsionForce(self.getLocation(),
            agv.getLocation(), 0.00001, 0.05))
        .collect(Collectors.toList());
  }

  private List<Vector> getMachinePotential() {
    return actsense.getMachines().stream()
        .map(m -> PotentialField.repulsionForce(self.getLocation(),
            m.getLocation(), 0.00001, 0.15))
        .collect(Collectors.toList());
  }

  /**
   * Vector summation: Combine all pf-forces to get the final direction.
   * Normalize the vector afterwards. The magnitude will be scaled if necessary.
   * @param vectors All force vectors.
   * @param maxMagnitude Scale the unit-vector.
   * @return The final vector.
   */
  private Vector vectorSummation(List<Vector> vectors, double maxMagnitude) {
    double ex = vectors.stream().mapToDouble(Vector::getEx).sum();
    double ey = vectors.stream().mapToDouble(Vector::getEy).sum();
    double magnitude = Math.sqrt((ex * ex) + (ey * ey));
    //scale magnitude when too big
    if (magnitude > maxMagnitude) {
      ex = maxMagnitude * ex / magnitude;
      ey = maxMagnitude * ey / magnitude;
    }
    return new Vector(ex, ey);
  }

  private IProduct selectRandomProduct() {
    List<IProduct> filteredProducts = actsense.getProducts().stream()
        .filter(p -> !p.isLocked())
        .collect(Collectors.toList());

    if (filteredProducts.size() == 0) {
      return null;
    }
    // calc attraction value for each product
    ArrayList<Double> attractionValues = new ArrayList<>();
    for (IProduct p: filteredProducts) {
      attractionValues.add(calcProductPotential(p));
    }
    // get random entry
    int selection = 0;
    double current = 0;
    double rand = actsense.getRandom().nextDouble();
    double forceSum = attractionValues.stream().reduce(0.0, Double::sum);
    for (int i = 0; i < attractionValues.size(); i++) {
      current += attractionValues.get(i) / forceSum;
      if (rand < current) {
        selection = i;
        break;
      }
    }
    return filteredProducts.get(selection);
  }

  private double calcProductPotential(IProduct product) {
    if (product.getDuration() == -1) {
      return product.getPotentialForce();
    } else {
      int currentStep = Environment.getInstance().getTimeStep();
      double dueValue = Math.min(
              (double)(currentStep - product.getArrivalTime()) / product.getDuration(), 1.0);
      return dueValue + product.getPotentialForce();
    }
  }

  /*
  * When selecting a machine for delivery/pickup the following steps will be taken:
  * - filter out machines with potential force <= 0
  * - filter out machines which offer the wrong service type (only for delivery)
  * - select the machine with the biggest remaining value
  * **/

  private IMachine selectPickupMachine() {
    return actsense.getMachines().stream()
        .filter(m -> m.getOutputPotentialForce() > 0.0)
        .max(Comparator.comparing(
            m -> exponentialDecay(m.getLocation(), m.getOutputPotentialForce())))
        .orElse(null);
  }

  private LinkedHashMap<IMachine, Double> getMachineForces() {
    LinkedHashMap<IMachine, Double> forces = new LinkedHashMap<>();
    for (IMachine m: actsense.getMachines()) {
      if (m.getOutputPotentialForce() > 0.0) {
        double pf = exponentialDecay(m.getLocation(), m.getOutputPotentialForce());
        forces.put(m, pf);
      }
    }
    return forces;
  }

  private IMachine selectDeliverMachine(String serviceType) {
    return actsense.getMachines().stream()
        .filter(m -> m.getInputPotentialForce() > 0.0)
        .filter(m -> serviceType.equals(m.getServiceManager().getCapabilityState()))
        .max(Comparator.comparing(
            m -> exponentialDecay(m.getLocation(), m.getInputPotentialForce())))
        .orElse(null);
  }

  /**
   * Select a random machine with probs based on based on attraction field strength.
   * @param serviceType The service needed.
   * @return The machine to deliver to.
   */
  private IMachine selectRandomDeliverMachine(String serviceType) {
    LinkedHashMap<IMachine, Double> forces = new LinkedHashMap<>();
    for (IMachine m: actsense.getMachines()) {
      if (serviceType.equals(m.getServiceManager().getCapabilityState())
          && m.getInputPotentialForce() > 0.0) {
        forces.put(m, exponentialDecay(m.getLocation(), m.getInputPotentialForce()));
      }
    }

    double current = 0;
    IMachine machine = null;
    double rand = actsense.getRandom().nextDouble();
    double forceSum = forces.values().stream().reduce(0.0, Double::sum);
    for (Map.Entry<IMachine, Double> entry: forces.entrySet()) {
      machine = entry.getKey();
      current += entry.getValue() / forceSum;
      if (rand < current) {
        break;
      }
    }
    return machine;
  }

  /**
   * Scale the potential force via exponential decay.
   * The decay is dependent on the distance between AGV and object.
   * @param loc The location of the machine or product.
   * @param force The potential force.
   * @return The scaled force value.
   */
  private double exponentialDecay(ILocation loc, double force) {
    return Math.exp(-self.getLocation().getDistance(loc) * 2.0) * force;
  }
}
