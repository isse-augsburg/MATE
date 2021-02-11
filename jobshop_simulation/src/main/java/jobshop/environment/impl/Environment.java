package jobshop.environment.impl;

import com.moandjiezana.toml.Toml;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.types.cms.CMSStatusEvent;
import jadex.bridge.service.types.cms.CMSStatusEvent.CMSTerminatedEvent;
import jadex.bridge.service.types.cms.SComponentManagementService;
import jadex.commons.future.IIntermediateResultListener;

import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeSet;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import jobshop.environment.IDispatcher;
import jobshop.environment.ILocationObject;
import jobshop.environment.RandomGenerator;
import jobshop.toml.Settings;


public class Environment {
  //-------- class attributes --------

  private static final double VISION_RANGE = 0.1;
  // How many env-steps for approximate 1 second?
  private static final int STEP_TIME = 100;
  private static final int STEP_TIME_SCALER = 10;

  // Path to .toml layout setting files inside resource folder
  private static final String SETTINGS_PATH = "settings/";

  private static Environment instance;

  private Map<IComponentIdentifier, AutomatedGuidedVehicle> agvs = new LinkedHashMap<>();
  private Map<IComponentIdentifier, Machine> machines = new LinkedHashMap<>();
  // One list containing all products (+future products) and one that shows spawned products.
  private Map<String, Product> spawnedProducts = new LinkedHashMap<>();
  private Map<String, Product> products = new LinkedHashMap<>();

  // If active, machines need to wait before entering barrier
  private Map<IComponentIdentifier, ArrayList<String>> inputRequests = new LinkedHashMap<>();
  private Map<IComponentIdentifier, ArrayList<String>> outputRequests = new LinkedHashMap<>();
  private Map<String, ArrayList<String>> productRequests = new LinkedHashMap<>();

  // Manage random generators by hand for better reproducibility.
  private Map<String, Random> randomMap = new LinkedHashMap<>();

  private boolean resetFlag;
  private boolean actionFlag;
  private boolean eventBatch ;
  private boolean eventFailure;
  private int batchCounter;

  // Count environment steps
  private int counter;
  private CyclicBarrier barrier;

  private Dispatcher dispatcher;
  private MachineManager machineManager;
  private Settings settings;

  /* Can be used for reward construction. */
  private int finishedGlobalProductsCount;
  private int makespan;


  //-------- constructors --------

  private Environment() {}

  public static synchronized Environment getInstance() {
    if (instance == null) {
      instance = new Environment();
    }
    return instance;
  }


  //------------------- methods -----------------------------

  //---------------- reset/setup the environment

  /**
   * Reset the environment: Reset counters, flags, ...
   */
  public synchronized void resetEnvironment() {
    finishedGlobalProductsCount = 0;
    makespan = getSettings().getTimeout();
    eventBatch = false;
    eventFailure = false;
    actionFlag = false;
    batchCounter = 0;
    counter = 0;
  }

  /**
   * After initializing Gym. Set some status variables.
   * @param config The environment to use.
   */
  public synchronized void setupEnvironment(String config) {
    setConfiguration(config);
    machineManager = new MachineManager(getSettings().getMachines());
    // AGVs + Machines + 1xDispatcher + 1xServer
    int agentCount = getAgvCount() + getMachineCount() + 2;
    barrier = new CyclicBarrier(agentCount);
    counter = -1;
    batchCounter = settings.getBatchCount();
    resetFlag = false;
    actionFlag = false;
    eventBatch = false;
    eventFailure = false;
  }

  /**
   * Load a toml configuration file.
   * @param config the toml file.
   */
  private synchronized void setConfiguration(String config) {
    InputStream inputStream = this.getClass()
        .getClassLoader()
        .getResourceAsStream(SETTINGS_PATH + config);
    assert inputStream != null;
    Settings settings = new Toml().read(inputStream).to(Settings.class);
    setSettings(settings);
  }

  public synchronized void initRequestMaps(IComponentIdentifier id) {
    inputRequests.put(id, new ArrayList<>());
    outputRequests.put(id, new ArrayList<>());
  }

  public synchronized int getAgvCount() {
    return getSettings().getAgvGroupSize();
  }

  public synchronized int getMachineCount() {
    return machineManager.getMachines().size();
  }


  //------------------- random generator handling -----------------

  public synchronized Random getRandom(IComponentIdentifier id) {
    return this.randomMap.get(id.toString());
  }

  public synchronized void generateRandomMap() {
    for (String id: getAgentIds()) {
      this.randomMap.put(id, RandomGenerator.getRandom());
    }
  }

  private synchronized TreeSet<String> getAgentIds() {
    TreeSet<String> ids = new TreeSet<>();
    ids.add(this.dispatcher.getAgentIdentifier().toString());
    for (IComponentIdentifier agvKey: this.agvs.keySet()) {
      ids.add(agvKey.toString());
    }
    for (IComponentIdentifier machineKey: this.machines.keySet()) {
      ids.add(machineKey.toString());
    }
    return ids;
  }

  //------------------- functions regarding agent synchronization ---------------------

  public void syncAgents() {
    try {
      barrier.await();
    } catch (InterruptedException | BrokenBarrierException e) {
      e.printStackTrace();
    }
  }

  //------------------ Counter related -------------------------------------

  public synchronized void increaseCounter() {
    counter++;
  }

  public synchronized void increaseBatchCounter() {
    batchCounter++;
  }

  public synchronized int getStepTime() {
    return STEP_TIME;
  }

  public synchronized int getStepTimeScaler() {
    return STEP_TIME_SCALER;
  }

  public synchronized int getTimeStep() {
    return counter / STEP_TIME_SCALER;
  }

  public synchronized int getCounter() {
    return counter;
  }

  public synchronized boolean checkBatchCount() {
    return settings.getBatchCount() > batchCounter;
  }

  public synchronized boolean checkTimeout() {
    return (settings.getTimeout() * STEP_TIME_SCALER) <= counter;
  }

  public synchronized void increaseFinishedGlobalProductsCount() {
    finishedGlobalProductsCount++;
  }

  public synchronized int getFinishedGlobalProductsCount() {
    return finishedGlobalProductsCount;
  }

  //----------------- Status-flags related -------------------------------

  public synchronized void resetEventFlags() {
    eventBatch = false;
    eventFailure = false;
  }

  public synchronized boolean getActionFlag() {
    return actionFlag;
  }

  public synchronized void setActionFlag(boolean status) {
    actionFlag = status;
  }

  public synchronized boolean getResetFlag() {
    return resetFlag;
  }

  public synchronized void setResetFlag(boolean status) {
    resetFlag = status;
  }

  public synchronized void setEventBatch(boolean status) {
    eventBatch = status;
  }

  public synchronized void setEventFailure(boolean status) {
    eventFailure = status;
  }

  //---------------------- Map management -------------------------------

  public synchronized void putInputRequest(IComponentIdentifier machineId,
                                           IComponentIdentifier agvId) {
    inputRequests.get(machineId).add(agvId.toString());
  }

  public synchronized boolean checkInputRequest(IComponentIdentifier machineId,
                                                IComponentIdentifier agvId) {
    Collections.sort(inputRequests.get(machineId));
    return inputRequests.get(machineId).get(0).equals(agvId.toString());
  }

  public synchronized void clearInputRequests(IComponentIdentifier machineId) {
    inputRequests.get(machineId).clear();
  }

  public synchronized boolean checkPendingInputRequests(IComponentIdentifier machineId) {
    return inputRequests.get(machineId).size() > 0;
  }

  public synchronized void putOutputRequest(IComponentIdentifier machineId,
                                            IComponentIdentifier agvId) {
    outputRequests.get(machineId).add(agvId.toString());
  }

  public synchronized boolean checkOutputRequest(IComponentIdentifier machineId,
                                                 IComponentIdentifier agvId) {
    Collections.sort(outputRequests.get(machineId));
    return outputRequests.get(machineId).get(0).equals(agvId.toString());
  }

  public synchronized void clearOutputRequests(IComponentIdentifier machineId) {
    outputRequests.get(machineId).clear();
  }

  public synchronized boolean checkPendingOutputRequests(IComponentIdentifier machineId) {
    return outputRequests.get(machineId).size() > 0;
  }

  public synchronized void putProductRequest(String productId, IComponentIdentifier agvId) {
    productRequests.get(productId).add(agvId.toString());
  }

  public synchronized boolean checkProductRequest(String productId, IComponentIdentifier agvId) {
    Collections.sort(productRequests.get(productId));
    return productRequests.get(productId).get(0).equals(agvId.toString());
  }

  public synchronized void clearProductRequests(String productId) {
    productRequests.get(productId).clear();
  }




  //----------------- rescheduling related -------------------------

  /**
   * Check if all products have been processed.
   * @return True if yes, otherwise false.
   */
  public synchronized boolean checkProductsDone() {
    if (getSettings().getBatchCount() <= batchCounter) {
      for (Product product: spawnedProducts.values()) {
        if (!product.isDone()) {
          return false;
        }
      }
      return true;
    }
    return false;
  }

  /**
   * Reschedule can be triggered on:
   * 1) periodically
   * 2) event-based
   * 3) hybrid
   * @return True if reschedule.
   */
  public synchronized boolean checkReschedule() {
    boolean reschedule = false;
    if (settings.getSchedulingStrategy().get("batch")) {
      reschedule = eventBatch;
    }
    if (settings.getSchedulingStrategy().get("failure")) {
      reschedule |= eventFailure;
    }
    if (settings.getSchedulingStrategy().get("periodic")) {
      reschedule |= checkRescheduleFrequency();
    }
    return reschedule;
  }

  private synchronized boolean checkRescheduleFrequency() {
    return counter % (settings.getSchedulingFrequency()
            * STEP_TIME_SCALER) == 0;
  }

  //--------------- Reward calculation ------------------------------

  /**
   * Calculate the makespan if all products are done.
   * @return The makespan.
   */
  public synchronized int calcMakespan() {
    if (checkProductsDone()) {
      makespan = Math.min(getTimeStep(), makespan);
    }
    return makespan;
  }

  /**
   * Calculate the lateness of all spawned products
   * once the episode has finished.
   * @return The lateness value.
   */
  public synchronized double calcLateness() {
    if (checkTimeout()) {
      int sum = 0;
      int counter = 0;
      for (Product p: getSpawnedProducts()) {
        if (p.isDone() && p.getDuration() != -1) {
          sum += p.getCompletionTime() - (p.getArrivalTime() + p.getDuration());
          counter++;
        } else if (p.getDuration() != -1) {
          sum += p.getCompletionTime() - (p.getArrivalTime() + p.getDuration());
          counter++;
        }
      }
      if (counter > 0) {
        return (double) sum / counter;
      }
    }
    return settings.getTimeout();
  }

  /**
   * Calculate the tardiness of all spawned products
   * once the episode has finished.
   * @return The tardiness value.
   */
  public synchronized double calcTardiness() {
    if (checkTimeout()) {
      int sum = 0;
      int counter = 0;
      for (Product p: getSpawnedProducts()) {
        if (p.isDone() && p.getDuration() != -1) {
          sum += Math.max(p.getCompletionTime() - (p.getArrivalTime() + p.getDuration()), 0);
          counter++;
        } else if (p.getDuration() != -1) {
          sum += Math.max(getSettings().getTimeout() - (p.getArrivalTime() + p.getDuration()), 0);
          counter++;
        }
      }
      if (counter > 0) {
        return (double) sum / counter;
      }
    }
    return getSettings().getTimeout();
  }

  /**
   * The longest time a product spend in the system.
   * @return The flowtime.
   */
  public synchronized int calcFlowTime() {
    if (checkTimeout()) {
      int max = 0;
      for (Product p: getSpawnedProducts()) {
        if (p.isDone()) {
          max = Math.max(p.getCompletionTime() - p.getArrivalTime(), max);
        } else {
          max = Math.max(getSettings().getTimeout() - p.getArrivalTime(), max);
        }
      }
      return max;
    }
    return getSettings().getTimeout();
  }


  //---------------Product related ---------------------------

  public synchronized void initProducts(List<Product> productList) {
    for (Product product: productList) {
      products.put(product.getId(), (Product) product.deepCopy());
      productRequests.put(product.getId(), new ArrayList<>());
    }
  }

  public synchronized void clearProducts() {
    spawnedProducts.clear();
    products.clear();
    productRequests.clear();
  }

  public synchronized void updateProduct(Product product) {
    if (spawnedProducts.containsKey(product.getId())) {
      spawnedProducts.put(product.getId(), (Product) product.deepCopy());
      products.put(product.getId(), (Product) product.deepCopy());
    }
  }

  public synchronized void addSpawnedProducts(List<Product> productList) {
    for (Product product: productList) {
      spawnedProducts.put(product.getId(), (Product) product.deepCopy());
    }
  }

  public synchronized Product[] getProducts() {
    return cloneList(products.values(), Product.class);
  }

  public synchronized Product[] getSpawnedProducts() {
    return cloneList(spawnedProducts.values(), Product.class);
  }

  public synchronized Product getSpawnedProduct(String id) {
    Product product = spawnedProducts.get(id);
    if (product != null) {
      return (Product) product.deepCopy();
    }
    return null;
  }


  //--------------- AGV related -------------------------------

  public synchronized AutomatedGuidedVehicle[] getAgvs() {
    return cloneList(agvs.values(), AutomatedGuidedVehicle.class);
  }

  public synchronized void updateAgv(AutomatedGuidedVehicle agv) {
    if (agvs.containsKey(agv.getAgentIdentifier())) {
      agvs.put(agv.getAgentIdentifier(), (AutomatedGuidedVehicle) agv.deepCopy());
    }
  }


  //-------------------- Machine related --------------------------------

  public synchronized Machine[] getMachines() {
    return cloneList(machines.values(), Machine.class);
  }

  public synchronized Machine getMachine(IComponentIdentifier id) {
    return (Machine) machines.get(id).deepCopy();
  }

  public synchronized void updateMachine(Machine machine) {
    if (machines.containsKey(machine.getAgentIdentifier())) {
      machines.put(machine.getAgentIdentifier(), (Machine) machine.deepCopy());
    }
  }


  //------------------- Settings ---------------------------------

  public synchronized Settings getSettings() {
    return settings;
  }

  public synchronized void setSettings(Settings settings) {
    this.settings = settings;
  }


  //--------------------- dispatcher stuff -----------------------------------

  public synchronized IDispatcher getDispatcher() {
    return dispatcher.deepCopy();
  }

  //-------- AGV, Machine and Dispatcher handling --------

  /**
   *  Get a AGV object for an agent.
   *  Creates a new AGV object if none exists.
   */
  public AutomatedGuidedVehicle createAgv(IInternalAccess agent) {
    IComponentIdentifier cid = agent.getId();
    AutomatedGuidedVehicle ret;
    boolean create;
    synchronized (this) {
      ret = agvs.get(cid);
      create = ret == null;
      if (create) {
        ret = new AutomatedGuidedVehicle(cid, settings.getAgvStartArea().getCenter(),
            null, VISION_RANGE);
        agvs.put(cid, ret);
      }
    }

    if (create) {
      // Remove on agent kill.
      SComponentManagementService.listenToComponent(cid, agent)
          .addResultListener(new IIntermediateResultListener<CMSStatusEvent>() {
            @Override
            public void intermediateResultAvailable(CMSStatusEvent cse) {
              if (cse instanceof CMSTerminatedEvent) {
                synchronized (Environment.this) {
                  agvs.remove(cid);
                }
              }
            }

            @Override
            public void finished() {}

            @Override
            public void exceptionOccurred(Exception exception) {}

            @Override
            public void resultAvailable(Collection<CMSStatusEvent> result) {}
          });
    } else {
      throw new IllegalStateException("AGV for agent " + cid + " already exists.");
    }

    return (AutomatedGuidedVehicle) ret.deepCopy();
  }

  /**
   *  Get a Machine object for an agent.
   *  Creates a new Machine object if none exists.
   *  Use one of the predefined machines.
   */
  public Machine createMachine(IInternalAccess agent) {
    IComponentIdentifier cid = agent.getId();
    Machine ret;
    boolean create;
    synchronized (this) {
      ret = machines.get(cid);
      create = ret == null;
      if (create) {
        Machine temp = machineManager.removeFreeMachine();
        ret = new Machine(cid, temp.getLocation(), temp.getServiceManager(),
            temp.getInputBuffer(), temp.getOutputBuffer());
        machines.put(cid, ret);
        initRequestMaps(cid);
      }
    }

    if (create) {
      // Remove on agent kill.
      SComponentManagementService.listenToComponent(cid, agent)
          .addResultListener(new IIntermediateResultListener<CMSStatusEvent>() {
            @Override
            public void intermediateResultAvailable(CMSStatusEvent cse) {
              if (cse instanceof CMSTerminatedEvent) {
                synchronized (Environment.this) {
                  machineManager.addFreeMachine(machines.get(cid));
                  machines.remove(cid);
                }
              }
            }

            @Override
            public void finished() {}

            @Override
            public void exceptionOccurred(Exception exception) {}

            @Override
            public void resultAvailable(Collection<CMSStatusEvent> result) {}
          });
    } else {
      throw new IllegalStateException("Machine for agent " + cid + " already exists.");
    }

    return (Machine) ret.deepCopy();
  }

  /**
   * Creates one dispatcher object. The dispacther creates products in intervals.
   * @param agent The agent id.
   * @return The dispatcher object.
   */
  public Dispatcher createDispatcher(IInternalAccess agent) {
    IComponentIdentifier cid = agent.getId();
    Dispatcher ret;
    boolean create;
    synchronized (this) {
      ret = dispatcher;
      create = ret == null;
      if (create) {
        ret = new Dispatcher(cid, settings.getProductStartArea(),
            settings.getProductManagers());
        dispatcher = ret;
      }
    }

    if (create) {
      // Remove on agent kill.
      SComponentManagementService.listenToComponent(cid, agent)
          .addResultListener(new IIntermediateResultListener<CMSStatusEvent>() {
            @Override
            public void intermediateResultAvailable(CMSStatusEvent cse) {
              if (cse instanceof CMSTerminatedEvent) {
                synchronized (Environment.this) {
                  dispatcher = null;
                }
              }
            }

            @Override
            public void finished() {}

            @Override
            public void exceptionOccurred(Exception exception) {}

            @Override
            public void resultAvailable(Collection<CMSStatusEvent> result) {}
          });
    } else {
      throw new IllegalStateException("Dispatcher already exists.");
    }

    return (Dispatcher) ret.deepCopy();
  }


  /**
   *  Deep clone a list of objects.
   */
  public static <T extends ILocationObject> T[] cloneList(Collection<T> list, Class<T> type) {
    List<ILocationObject> ret = new ArrayList<>();
    for (ILocationObject o: list) {
      ret.add(o.deepCopy());
    }
    @SuppressWarnings("unchecked")
    T[] aret = ret.toArray((T[]) Array.newInstance(type, list.size()));
    return aret;
  }
}
