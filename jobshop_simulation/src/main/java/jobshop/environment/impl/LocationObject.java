package jobshop.environment.impl;

import jadex.commons.SimplePropertyChangeSupport;
import jadex.commons.beans.PropertyChangeListener;
import jobshop.environment.ILocation;
import jobshop.environment.ILocationObject;

/**
 *  Base class for all map objects.
 */
public abstract class LocationObject implements ILocationObject {
  //-------- attributes ----------

  protected String id;
  protected Location location;

  /** The property change support. */
  protected SimplePropertyChangeSupport pcs;

  //-------- constructors --------

  public LocationObject() {
    pcs = new SimplePropertyChangeSupport(this);
  }

  public LocationObject(String id, Location location) {
    this();
    this.id = id;
    this.location = location;
  }

  /**
   * Copy constructor.
   * @param other The object to copy.
   */
  public LocationObject(LocationObject other) {
    this();
    this.id = other.id;
    this.pcs = other.pcs;
    if (other.location != null) {
      this.location = new Location(other.location);
    }
  }


  //-------- accessor methods --------

  @Override
  public String getId() {
    return this.id;
  }

  public void setId(String id) {
    String oldid = this.id;
    this.id = id;
    pcs.firePropertyChange("id", oldid, id);
  }

  @Override
  public Location getLocation() {
    return this.location;
  }

  public void setLocation(Location location) {
    Location oldloc = this.location;
    this.location = location;
    pcs.firePropertyChange("location", oldloc, location);
  }

  @Override
  public void update(ILocationObject obj) {
    assert this.getId().equals(obj.getId());
    setLocation((Location) obj.getLocation());
  }


  //-------- custom code --------

  @Override
  public boolean equals(Object obj) {
    return obj instanceof LocationObject
        && ((LocationObject)obj).id.equals(id)
        && obj.getClass().equals(this.getClass());
  }

  @Override
  public int hashCode() {
    return 31 + id.hashCode();
  }


  //-------- property methods --------

  /**
   * Add a PropertyChangeListener to the listener list.
   * The listener is registered for all properties.
   *
   * @param listener The PropertyChangeListener to be added.
   */
  public void addPropertyChangeListener(PropertyChangeListener listener) {
    pcs.addPropertyChangeListener(listener);
  }

  /**
   * Remove a PropertyChangeListener from the listener list.
   * This removes a PropertyChangeListener that was registered
   * for all properties.
   *
   * @param listener The PropertyChangeListener to be removed.
   */
  public void removePropertyChangeListener(PropertyChangeListener listener) {
    pcs.removePropertyChangeListener(listener);
  }

  /**
   *  Get the property change handler for firing events.
   */
  protected SimplePropertyChangeSupport getPropertyChangeHandler() {
    return pcs;
  }
}
