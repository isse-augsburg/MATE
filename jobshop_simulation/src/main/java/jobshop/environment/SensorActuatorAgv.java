package jobshop.environment;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.impl.ExecutionComponentFeature;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import jobshop.environment.impl.Area;
import jobshop.environment.impl.AutomatedGuidedVehicle;
import jobshop.environment.impl.Environment;
import jobshop.environment.impl.Location;
import jobshop.environment.impl.Product;

public class SensorActuatorAgv {
  //-------- attributes --------

  /** The agent. */
  private IInternalAccess agent;

  private AutomatedGuidedVehicle self;
  private Environment env = Environment.getInstance();


  //-------- constructors --------

  /**
   *  Create a sensor for a new AGV.
   */
  public SensorActuatorAgv() {
    agent = ExecutionComponentFeature.LOCAL.get();
    if (agent == null) {
      throw new IllegalStateException("Must be called on agent thread.");
    }
    self = Environment.getInstance().createAgv(agent);
  }

  public void setup() {
    waitStepWithCheck();
  }

  public void reset() {
    self.removeProduct();
    self.setLocation(env.getSettings().getAgvStartArea().getCenter());
    syncUpdateAgv();
  }


  //-------- actuator methods --------

  public boolean checkRange(ILocation location) {
    return self.getLocation().isInDeliveryRange(location);
  }

  /**
   * Check if a product is in range, pick it up and lock it.
   * @param product The product to pick up.
   */
  public void pickupProductFromGround(Product product) {
    if (checkRange(product.getLocation())) {
      syncPickUp(product);
    }
  }

  public void pickupProductFromMachine(String productId) {
    self.setProduct(env.getSpawnedProduct(productId));
    env.updateAgv(self);
  }

  public void dropLockedProduct() {
    self.removeProduct();
    env.updateAgv(self);
  }

  public void dropUnlockedProduct() {
    Product product = self.removeProduct();
    env.updateAgv(self);
    product.setLocked(false);
    syncUpdateProductWithCheck(product);
  }

  /**
   * Finish a product by putting it in the end-zone, and setting some variables.
   */
  public void finishProduct() {
    Product product = self.removeProduct();
    product.setDone(true);
    product.setCompletionTime(env.getTimeStep());
    env.increaseFinishedGlobalProductsCount();
    env.updateAgv(self);
    env.updateProduct(product);
  }

  public void moveTo(ILocation target) {
    Location location = new Location(target.getPosX(), target.getPosY());
    syncUpdateAgvWithCheck(location);
  }

  private void updateLocationObjects(Location location) {
    self.setLocation(location);
    env.updateAgv(self);
    if (self.getProduct() != null) {
      self.getProduct().setLocation(location);
      env.updateProduct(self.getProduct());
    }
  }


  //-------- sync methods --------

  private void syncUpdateAgv() {
    env.syncAgents();
    env.updateAgv(self);
    env.syncAgents();
  }

  private void syncUpdateAgvWithCheck(Location location) {
    if (!getResetFlag()) {
      env.syncAgents();
      updateLocationObjects(location);
      env.updateAgv(self);
      env.syncAgents();
    }
  }

  private void syncUpdateProductWithCheck(Product product) {
    if (!getResetFlag()) {
      env.syncAgents();
      env.updateProduct(product);
      env.syncAgents();
    }
  }

  private void waitStepWithCheck() {
    if (!getResetFlag()) {
      env.syncAgents();
      env.syncAgents();
    }
  }

  /**
   * Try to pick up product
   * @param product The product to pick up.
   */
  private void syncPickUp(Product product) {
    if (!getResetFlag()) {
      env.putProductRequest(product.getId(), self.getAgentIdentifier());
      env.syncAgents();
      if (env.checkProductRequest(product.getId(), self.getAgentIdentifier())) {
        product.setLocked(true);
        env.updateProduct(product);
        self.setProduct(product);
        env.updateAgv(self);
        env.clearProductRequests(product.getId());
      }
      env.syncAgents();
    }
  }

  /**
   * Try to request input-buffer,
   * only one barrier if success other barrier after msg.
   * @param machineId The machine to request.
   * @return true if request allowed.
   */
  public boolean syncStartInputMessage(IComponentIdentifier machineId) {
    if (!getResetFlag()) {
      env.putInputRequest(machineId, self.getAgentIdentifier());
      env.syncAgents();
      if (env.checkInputRequest(machineId, self.getAgentIdentifier())) {
        return true;
      }
      env.syncAgents();
    }
    return false;
  }

  public void syncEndInputMessage(IComponentIdentifier machineId) {
    env.clearInputRequests(machineId);
    env.syncAgents();
  }

  /**
   * Try to request output-buffer product from machine.
   * Only one barrier on success because of msg send.
   * @param machineId The machine to request from.
   * @return True if sucess.
   */
  public boolean syncStartOutputMessage(IComponentIdentifier machineId) {
    if (!getResetFlag()) {
      env.putOutputRequest(machineId, self.getAgentIdentifier());
      env.syncAgents();
      if (env.checkOutputRequest(machineId, self.getAgentIdentifier())) {
        return true;
      }
      env.syncAgents();
    }
    return false;
  }

  public void syncEndOutputMessage(IComponentIdentifier machineId) {
    env.clearOutputRequests(machineId);
    env.syncAgents();
  }


  //-------- Getter ------------------

  public Random getRandom() {
    return env.getRandom(agent.getId());
  }

  public boolean getResetFlag() {
    return env.getResetFlag();
  }

  public IAutomatedGuidedVehicle getSelf() {
    return self;
  }

  public Area getEndZone() {
    return env.getSettings().getProductEndArea();
  }

  public Area getStartZone() {
    return env.getSettings().getProductStartArea();
  }

  public ArrayList<IMachine> getMachines() {
    return new ArrayList<>(Arrays.asList(env.getMachines()));
  }

  public IMachine getMachine(IComponentIdentifier id) {
    return env.getMachine(id);
  }

  public ArrayList<IProduct> getProducts() {
    return new ArrayList<>(Arrays.asList(env.getSpawnedProducts()));
  }

  public IProduct getProduct(String id) {
    return env.getSpawnedProduct(id);
  }

  /**
   * Get all AGVs (expect itself) that are in sensor range.
   * @return A list og AGVs.
   */
  public ArrayList<IAutomatedGuidedVehicle> getSensedAgvs() {
    ArrayList<IAutomatedGuidedVehicle> agvs = new ArrayList<>();
    for (IAutomatedGuidedVehicle agv: env.getAgvs()) {
      if (self.getLocation().getDistance(agv.getLocation()) < self.getVisionRange()
          && !self.equals(agv)) {
        agvs.add(agv);
      }
    }
    return agvs;
  }
}
