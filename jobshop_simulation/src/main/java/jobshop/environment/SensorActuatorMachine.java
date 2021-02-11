package jobshop.environment;

import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.component.impl.ExecutionComponentFeature;

import java.util.Random;

import jobshop.environment.impl.Environment;
import jobshop.environment.impl.Machine;
import jobshop.environment.impl.Product;
import jobshop.environment.impl.ServiceManager;



public class SensorActuatorMachine {
  //-------- attributes --------

  private Machine self;
  private ServiceManager template;
  private IInternalAccess agent;
  private Environment env = Environment.getInstance();

  private boolean messageFlag;
  private boolean actionFlag;

  //-------- constructors ---------

  /**
   * Constructor: init machine-agent and random.
   */
  public SensorActuatorMachine() {
    this.agent = ExecutionComponentFeature.LOCAL.get();
    if (agent == null) {
      throw new IllegalStateException("Must be called on agent thread.");
    }
    self = Environment.getInstance().createMachine(agent);
    template = new ServiceManager(self.getServiceManager());
    waitStepWithCheck();
  }


  //-------- sensor methods --------

  /**
   * Return the machine object controlled by the agent.
   * @return the machine object.
   */
  public IMachine getSelf() {
    if (!agent.getFeature(IExecutionFeature.class).isComponentThread()) {
      throw new IllegalStateException("Error: Must be called on agent thread.");
    }
    return self;
  }

  public Random getRandom() {
    return env.getRandom(agent.getId());
  }

  public boolean getMessageFlag() {
    return messageFlag;
  }

  public boolean getResetFlag() {
    return env.getResetFlag();
  }

  public void setActionFlag(boolean status) {
    actionFlag = status;
  }


  //-------- actuator methods --------

  /**
   * Reset machine-settings. Will be called when Gym is reset.
   */
  public void reset() {
    actionFlag = true;
    messageFlag = false;
    self.getInputBuffer().clear();
    self.getOutputBuffer().clear();
    self.setCurrentProduct(null);
    self.resetCounters();
    self.getServiceManager().init(getRandom(), template);
    syncUpdate();
  }

  /**
   * Get a product from the input buffer and start to process it.
   * @return True if product could be fetched.
   */
  public boolean tryGetInputProduct() {
    Product product = self.getInputBuffer().tryRemoveProduct();
    if (product != null) {
      self.setCurrentProduct(product);
      syncUpdateWithCheck(false);
      return true;
    }
    waitStepWithCheck();
    return false;
  }

  /**
   * Accept product if enough space left on input buffer.
   * @param productId The product id.
   * @return True if successful.
   */
  public boolean tryAcceptProduct(String productId) {
    // No sync, will be called between msg.
    Product product = env.getSpawnedProduct(productId);
    if (self.getInputBuffer().tryPutProduct(product)) {
      env.updateMachine(self);
      return true;
    }
    return false;
  }

  /**
   * Free one product from the output buffer for further processing.
   * @return The product freed.
   */
  public IProduct tryHandoutProduct() {
    // No sync, because called between barriers anyway
    Product product = self.getOutputBuffer().tryRemoveProduct();
    env.updateMachine(self);
    return product;
  }

  public void rejectProduct() {
    // Will be called between msg, no sync.
    self.increaseRejectCount();
    env.updateMachine(self);
  }

  /**
   * Change the current capability.
   * @param capability The capability to change to.
   */
  public void changeMachineCapability(String capability) {
    if (self.getServiceManager().checkChangeAllowed(capability)) {
      self.getServiceManager().setChanging(true);
      syncUpdateWithCheck(false);
      // Wait as long as capability changes
      int setupTime = self.getServiceManager().getCapability(capability).getSetupTime();
      for (int i = 0; i < setupTime * env.getStepTimeScaler(); i++) {
        waitStepWithCheck();
      }
      self.increaseChangeCount();
      self.getServiceManager().changeCapability(capability);
      self.getServiceManager().setChanging(false);
      syncUpdateWithCheck(false);
    }
  }

  /**
   * Maintenance the specified capability. Can take a different amount of time
   * if capability is already broken.
   * @param capability The capability for maintenance.
   */
  public void maintenanceMachineCapability(String capability) {
    if (self.getServiceManager().checkMaintenanceAllowed(capability)) {
      self.getServiceManager().setMaintenance(true);
      syncUpdateWithCheck(false);
      int mainTime = self.getServiceManager().getCapability(capability).getMaintenanceTime();
      if (!self.getServiceManager().getCapability(capability).getStatus()) {
        mainTime = self.getServiceManager().getCapability(capability).getRepairTime();
      }
      for (int i = 0; i < mainTime * env.getStepTimeScaler(); i++) {
        waitStepWithCheck();
      }
      self.getServiceManager().repairCapability(capability);
      self.getServiceManager().setMaintenance(false);
      syncUpdateWithCheck(false);
    }
  }

  /**
   * Get product from input-buffer and start processing.
   */
  public void processProduct() {
    Product product = self.getCurrentProduct();
    ServiceManager sm = self.getServiceManager();
    boolean failure = false;

    if (product != null) {
      //update the product's location
      product.setLocation(self.getLocation());
      env.updateProduct(product);

      //process product if the current service equals the needed service and is available,
      //otherwise, just forward the product to the output-buffer without further action.
      if (sm.checkProcessingAllowed(product.getCurrentStepId())) {
        int processingSteps = sm.getCapability().getProcessingTime() * env.getStepTimeScaler();
        for (int i = 1; i <= processingSteps; i++) {
          if (i % env.getStepTimeScaler() == 0) {
            self.increaseWorkload();
            syncUpdateWithCheck(false);
          } else {
            waitStepWithCheck();
          }
        }
        failure = sm.checkFailure(getRandom());
      }

      //block until enough space on output-buffer
      while (!self.getOutputBuffer().tryPutProduct(product) && !getResetFlag()) {
        waitStepWithCheck();
      }
      // update product, no sync – does not change pf
      product.finishCurrentStep();
      product.setLocation(self.getOutputLocation());
      env.updateProduct(product);
      // move product to output-buffer + break a service (optional)
      self.setCurrentProduct(null);
      self.increaseFinishCount();
      syncUpdateWithCheck(failure);
    }
  }

  //-------- sync methods --------

  private void syncUpdate() {
    env.syncAgents();
    checkWait();
    env.updateMachine(self);
    env.syncAgents();
  }

  private void syncUpdateWithCheck(boolean failure) {
    if (!getResetFlag()) {
      env.syncAgents();
      checkWait();
      env.updateMachine(self);
      env.setEventFailure(failure);
      env.syncAgents();
    }
  }

  private void waitStepWithCheck() {
    if (!getResetFlag()) {
      env.syncAgents();
      checkWait();
      env.syncAgents();
    }
  }

  private void checkWait() {
    messageFlag = checkMessage();
    while (checkMessage()) {
      agent.waitForDelay(100).get();
    }
    messageFlag = false;
    actionFlag = true;
  }

  private boolean checkMessage() {
    return env.checkPendingInputRequests(agent.getId())
        || env.checkPendingOutputRequests(agent.getId())
        || (actionFlag && env.getActionFlag());
  }

}
