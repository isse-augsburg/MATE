package jobshop.environment;


/**
 *  Base interface for all environment opbjects.
 */
public interface ILocationObject {
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
   * Update an object.
   * @param obj Another Ilocation object.
   */
  void update(ILocationObject obj);

  /**
   * Deep copy a LocationObject.
   * @return The deep copy.
   */
  ILocationObject deepCopy();
}
