package jobshop.environment;


/**
 *  A location on the virtual map.
 */
public interface ILocation {
  /**
   *  Get the x of this Location.
   *  The x-coordinate.
   * @return x
   */
  double getPosX();

  /**
   *  Get the y of this Location.
   *  The y-coordinate.
   * @return y
   */
  double getPosY();

  /**
   *  Caculate the distance to another location.
   *  @return The distance.
   */
  double getDistance(ILocation other);

  /**
   *  Check, if the other location is in range.
   *  E.g. when the chargin station is near to the cleaner it can recharge, etc.
   *  @return True, if the given locations is near to this location.
   */
  boolean isNear(ILocation other);

  /**
   * Checks if two objects are in range for delivery (e.g. box to machine).
   * @param other the other location.
   * @return true if in range.
   */
  boolean isInDeliveryRange(ILocation other);
}
