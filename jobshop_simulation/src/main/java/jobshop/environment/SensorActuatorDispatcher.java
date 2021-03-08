package jobshop.environment;

import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.component.impl.ExecutionComponentFeature;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import jobshop.environment.impl.Dispatcher;
import jobshop.environment.impl.Environment;
import jobshop.environment.impl.Product;
import jobshop.environment.impl.ProductManager;


public class SensorActuatorDispatcher {
  //-------- attributes --------

  /** The agent. */
  private IInternalAccess agent;

  /** The Machine. */
  private Dispatcher self;
  private Environment env = Environment.getInstance();


  //-------- constructors ---------

  /**
   * Create agent and get random object.
   */
  public SensorActuatorDispatcher() {
    this.agent = ExecutionComponentFeature.LOCAL.get();
    if (agent == null) {
      throw new IllegalStateException("Must be called on agent thread.");
    }
    self = Environment.getInstance().createDispatcher(agent);
  }

  public void setup() {
    waitStepWithCheck(false);
  }

  public Random getRandom() {
    return env.getRandom(agent.getId());
  }

  public boolean getResetFlag() {
    return env.getResetFlag();
  }

  //-------- sensor methods --------

  public IDispatcher getSelf() {
    if (!agent.getFeature(IExecutionFeature.class).isComponentThread()) {
      throw new IllegalStateException("Error: Must be called on agent thread.");
    }
    return self;
  }

  //-------- actuator methods --------

  public void reset() {
    env.clearProducts();
    Product.resetCounter();
    createProducts();
    trySpawn(true);
  }

  public boolean checkSpawn() {
    return env.checkBatchCount() && env.getCounter()
        % (env.getSettings().getBatchFrequency() * env.getStepTimeScaler()) == 0;
  }

  public void trySpawn(boolean reset) {
    if (checkSpawn()) {
      spawnProducts(reset);
    } else {
      waitStepWithCheck(reset);
    }
  }

  /**
   * Spawn newly arrived products.
   * @param reset Spawn differently on reset.
   */
  public void spawnProducts(boolean reset) {
    env.increaseBatchCounter();
    List<Product> products = new ArrayList<>();
    for (Product product: env.getProducts()) {
      if (product.getArrivalTime() == env.getTimeStep()) {
        products.add(product);
      }
    }
    syncUpdateWithCheck(products, reset);
  }

  /**
   * Create a batch of products in the start area.
   */
  public void createProducts() {
    List<Product> products = new ArrayList<>();
    for (int i = 0; i < env.getSettings().getBatchCount(); i++) {
      int step = i * env.getSettings().getBatchFrequency();
      if (env.getSettings().isBatchRandomized()) {
        products.addAll(createRandomProducts(step));
      } else {
        products.addAll(createProductsDefault(step));
      }
    }
    env.initProducts(products);
  }

  /**
   * Spawn random product composition.
   */
  private ArrayList<Product> createRandomProducts(int arrival) {
    ArrayList<Integer> ratios = randomDistribution();
    ArrayList<Product> products = new ArrayList<>();
    int currentType = 0;
    for (Integer pos: ratios) {
      if (pos == 1) {
        currentType += 1;
      } else {
        products.add(getTemplate(self.getProductManagers().get(currentType), arrival));
      }
    }
    return products;
  }

  /**
   * Create a random product distribution.
   * @return A list with the product composition.
   */
  private ArrayList<Integer> randomDistribution() {
    ArrayList<Integer> distribution = new ArrayList<>();
    //each one in array is a split point between product types
    for (int i = 0; i < self.getProductManagers().size() - 1; i++) {
      distribution.add(1);
    }
    //each zero in array stands for a product
    for (int i = 0; i < Environment.getInstance().getSettings().getBatchRandomizedSize(); i++) {
      distribution.add(0);
    }
    //shuffle array
    Collections.shuffle(distribution, getRandom());
    return distribution;
  }

  /**
   * Create the given product type and wait for some time afterwards.
   */
  private ArrayList<Product> createProductsDefault(int arrival) {
    //spawn products ...
    ArrayList<Product> products = new ArrayList<>();
    for (ProductManager manager: self.getProductManagers()) {
      for (int i = 0; i < manager.getQuantity(); i++) {
        products.add(getTemplate(manager, arrival));
      }
    }
    return products;
  }

  /**
   * Spawn some kind of product.
   * @param manager Contains the product description.
   */
  private Product getTemplate(ProductManager manager, int arrival) {
    IProduct template = manager.getProduct();
    return new Product(template.getId(),
            self.getArea().getRandomLocation(getRandom()), template.getDuration(),
            arrival, template.getSize(), template.getWorkflow());
  }

  //-------- sync methods --------

  private void syncUpdateWithCheck(List<Product> products, boolean reset) {
    if (!getResetFlag() || reset) {
      env.syncAgents();
      env.addSpawnedProducts(products);
      env.setEventBatch(true);
      env.syncAgents();
    }
  }

  private void waitStepWithCheck(boolean reset) {
    if (!getResetFlag() || reset) {
      env.syncAgents();
      env.syncAgents();
    }
  }


}
