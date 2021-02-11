package jobshop.environment.impl;

import jadex.commons.SimplePropertyChangeSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import jobshop.environment.IServiceManager;

public class ServiceManager implements IServiceManager {

  private static final String BROKEN_TAG = "BROKEN";
  private static final String CHANGING_TAG = "CHANGING";
  private static final String MAINTENANCE_TAG = "MAINTENANCE";

  //keep the starting capability for reset purposes
  private String capability = "UNDEFINED";
  private boolean randomCapability;
  private double randomFailures;

  private List<Capability> capabilities = new ArrayList<>();

  private boolean changing = false;
  private boolean maintenance = false;

  /** The property change support. */
  protected SimplePropertyChangeSupport pcs;


  //------------------ constructors -----------------------------

  public ServiceManager() {
    pcs = new SimplePropertyChangeSupport(this);
  }

  /**
   * copy constructor.
   * @param other ServiceManager to copy.
   */
  public ServiceManager(ServiceManager other) {
    this.randomCapability = other.randomCapability;
    this.randomFailures = other.randomFailures;
    this.capability = other.capability;
    this.changing = other.changing;
    this.maintenance = other.maintenance;
    this.pcs = other.pcs;
    copyCapabilities(other.getCapabilities());
  }


  //------------------- methods -----------------------------------

  @Override
  public Capability getCapability() {
    return getCapability(capability);
  }

  @Override
  public Capability getCapability(String id) {
    for (Capability c: capabilities) {
      if (c.getId().equals(id)) {
        return c;
      }
    }
    return null;
  }

  @Override
  public String getCapabilityState() {
    if (changing) {
      return CHANGING_TAG;
    }
    if (maintenance) {
      return MAINTENANCE_TAG;
    }
    if (!getCapability().getStatus()) {
      return BROKEN_TAG;
    }
    return getCapability().getId();
  }

  @Override
  public List<Capability> getCapabilities() {
    return capabilities;
  }

  @Override
  public void changeCapability(String newCapability) {
    if (isCapabilitySupported(newCapability)) {
      capability = newCapability;
    }
  }

  @Override
  public boolean isCapabilitySupported(String capability) {
    return getCapability(capability) != null;
  }

  @Override
  public void breakCapability(String capability) {
    Capability cap = getCapability(capability);
    if (cap != null) {
      cap.setStatus(false);
    }
  }

  @Override
  public void repairCapability(String service) {
    Capability cap = getCapability(capability);
    if (cap != null) {
      cap.setStatus(true);
      cap.resetCurrentFailureRate();
    }
  }

  @Override
  public boolean checkFailure(Random random) {
    Capability cap = getCapability();
    if (cap != null) {
      cap.increaseCurrentFailureRate();
      if (random.nextDouble() < cap.getCurrentFailureRate()) {
        breakCapability(cap.getId());
        return true;
      }
    }
    return false;
  }

  @Override
  public void init(Random random, ServiceManager template) {
    reset(template);
    if (this.randomCapability) {
      randomCurrentService(random);
    }
    randomBrokenServices(random, randomFailures);
  }

  private void reset(ServiceManager template) {
    this.capability = template.getCapability().getId();
    copyCapabilities(template.getCapabilities());
  }

  private void copyCapabilities(List<Capability> capabilities) {
    this.capabilities = new ArrayList<>();
    for (Capability cap: capabilities) {
      this.capabilities.add(new Capability(cap));
    }
  }

  @Override
  public void setChanging(boolean status) {
    this.changing = status;
  }

  @Override
  public void setMaintenance(boolean status) {
    this.maintenance = status;
  }

  @Override
  public boolean checkChangeAllowed(String newCapability) {
    return isCapabilitySupported(newCapability) && !capability.equals(newCapability);
  }

  @Override
  public boolean checkMaintenanceAllowed(String capability) {
    return isCapabilitySupported(capability);
  }

  @Override
  public boolean checkProcessingAllowed(String newCapability) {
    return capability.equals(newCapability) && getCapability().getStatus();
  }

  private void randomCurrentService(Random random) {
    int nextCapability = random.nextInt(capabilities.size());
    capability = capabilities.get(nextCapability).getId();
  }

  /**
   * Iterate over all services and break each with some probability.
   * @param random The random object.
   */
  private void randomBrokenServices(Random random, double failRate) {
    for (Capability cap: capabilities) {
      if (random.nextDouble() < failRate) {
        breakCapability(cap.getId());
      } else {
        repairCapability(cap.getId());
      }
    }
  }
}
