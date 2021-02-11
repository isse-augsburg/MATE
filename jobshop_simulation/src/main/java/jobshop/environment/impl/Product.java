package jobshop.environment.impl;

import java.util.List;
import jobshop.environment.ILocationObject;
import jobshop.environment.IProduct;

public class Product extends LocationObject implements IProduct {

  private static int productcnt;
  private static final String FIN_TAG = "FINISHED";
  private static final double POTENTIAL_FORCE = 0.5;

  private int arrivalTime;
  private int completionTime;
  private int duration;
  private double size;
  private boolean locked;
  private int currentStep = 0;
  private boolean done = false;

  private List<String> workflow;

  /**
   *  Get an instance number.
   */
  private static synchronized int getNumber() {
    return ++productcnt;
  }

  public static synchronized void resetCounter() {
    productcnt = 0;
  }

  //-------- constructors --------

  public Product() {}

  public Product(String id, Location location, int duration,
                 int arrival, double size, List<String> workflow) {
    super(id + "_#" + getNumber(), location);
    this.arrivalTime = arrival;
    this.duration = duration;
    this.size = size;
    this.workflow = workflow;
  }

  /**
   * Copy constructor.
   * @param other The object to copy.
   */
  public Product(Product other) {
    super(other);
    this.arrivalTime = other.arrivalTime;
    this.completionTime = other.completionTime;
    this.duration = other.duration;
    this.size = other.size;
    this.locked = other.locked;
    this.currentStep = other.currentStep;
    this.done = other.done;
    this.workflow = other.workflow;
  }


  //-------- methods --------

  @Override
  public int getCompletionTime() {
    return completionTime;
  }

  public void setCompletionTime(int completionTime) {
    this.completionTime = completionTime;
  }

  @Override
  public int getArrivalTime() {
    return arrivalTime;
  }

  @Override
  public int getDuration() {
    return duration;
  }

  @Override
  public double getSize() {
    return this.size;
  }

  @Override
  public List<String> getWorkflow() {
    return this.workflow;
  }

  @Override
  public boolean isLocked() {
    return locked;
  }

  public void setLocked(boolean locked) {
    this.locked = locked;
  }

  @Override
  public String getCurrentStepId() {
    if (this.workflow.size() > this.currentStep) {
      return this.workflow.get(currentStep);
    }
    return FIN_TAG;
  }

  @Override
  public void finishCurrentStep() {
    this.currentStep++;
  }

  @Override
  public double getProgress() {
    return (double) this.currentStep / this.workflow.size();
  }

  @Override
  public String getFinTag() {
    return FIN_TAG;
  }

  @Override
  public boolean isDone() {
    return done;
  }

  public void setDone(boolean done) {
    this.done = done;
  }

  @Override
  public double getPotentialForce() {
    return POTENTIAL_FORCE;
  }

  public int getCurrentStepNumber() {
    return this.currentStep;
  }


  @Override
  public void update(ILocationObject product) {
    super.update(product);
    arrivalTime = ((Product) product).getArrivalTime();
    completionTime = ((Product) product).getCompletionTime();
    duration = ((Product) product).getDuration();
    size = ((Product) product).getSize();
    locked = ((Product) product).isLocked();
    workflow = ((Product) product).getWorkflow();
    currentStep = ((Product) product).getCurrentStepNumber();
  }

  @Override
  public ILocationObject deepCopy() {
    return new Product(this);
  }

  @Override
  public String toString() {
    return getId();
  }
}
