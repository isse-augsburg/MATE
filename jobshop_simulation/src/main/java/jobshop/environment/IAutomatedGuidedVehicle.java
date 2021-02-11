package jobshop.environment;

import jadex.bridge.IComponentIdentifier;

public interface IAutomatedGuidedVehicle extends ILocationObject {

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
   *  Get the carried product of this AGV.
   * @return The carried product, if any.
   */
  IProduct getProduct();

  /**
   * Place the current product in the environment.
   */
  IProduct removeProduct();

  /**
   *  Get the vision-range of this Cleaner.
   * @return The distance that this cleaner is able to see.
   */
  double getVisionRange();

  /**
   *  Get the identifier (address) of the AGV.
   *  @return The identifier that can be used to send a message to the AGV.
   */
  IComponentIdentifier getAgentIdentifier();
}
