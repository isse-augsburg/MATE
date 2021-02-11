package jobshop.environment;

import java.util.List;

public interface IProduct extends ILocationObject {
  /**
   *  Get the id (or name) of this object.
   *  @return The id.
   */
  String getId();

  /**
   *  Get the location of this object.
   * @return The location of the object.
   */
  ILocation getLocation();

  /**
   * The timestep at which the product will be spawned.
   * @return The timestep.
   */
  int getArrivalTime();

  /**
   * The timestep at which the product will be finished.
   * @return The fin-date.
   */
  int getCompletionTime();

  /**
   * The number of steps the product should be finished in.
   * @return The due date.
   */
  int getDuration();

  /**
   * Get the size of the product.
   * @return The product size.
   */
  double getSize();

  /**
   * Get the workflow of the product.
   * @return A list of steps required.
   */
  List<String> getWorkflow();

  /**
   * Is the product currently used by machines or agvs.
   * @return True if yes, else false.
   */
  boolean isLocked();

  /**
   * Get the current step required for the workflow.
   * @return The service required next.
   */
  String getCurrentStepId();

  /**
   * Finish the current step and change it to the next step in the workflow.
   */
  void finishCurrentStep();

  /**
   * Indicates the progress of the workflow.
   * @return A value between 0.0 and 1.0.
   */
  double getProgress();

  /**
   * A tag indicating if the product has finished its workflow.
   * @return The FIN string.
   */
  String getFinTag();

  /**
   * Check if the product is done.
   * @return True if done.
   */
  boolean isDone();

  /**
   * The potential force send out by this product (will be a constant).
   * @return A value between 0.0 and 1.0.
   */
  double getPotentialForce();

}
