package jobshop.environment;

import java.util.List;
import java.util.Random;
import jobshop.environment.impl.Capability;
import jobshop.environment.impl.ServiceManager;


public interface IServiceManager {
  /**
   *  Get the current service of the machine.
   * @return The type.
   */
  Capability getCapability();

  Capability getCapability(String capability);

  String getCapabilityState();

  List<Capability> getCapabilities();

  /**
   * Changes the current service of the machine.
   * @param capability The service to change to.
   */
  void changeCapability(String capability);


  /**
   * Check if the machine supports specified service.
   * @param capability The service to check.
   * @return True if supported.
   */
  boolean isCapabilitySupported(String capability);

  /**
   * Break the specified service.
   * @param capability The service to break.
   */
  void breakCapability(String capability);

  /**
   * Break the currently equipped service with some prob.
   * @param random The random object.
   */
  boolean checkFailure(Random random);

  /**
   * Repair the specified service.
   * @param capability The service to break.
   */
  void repairCapability(String capability);

  /**
   * Initialize the currently used service randomly.
   */
  void init(Random random, ServiceManager template);

  /**
   * Signal that the machine changes its service.
   * @param status True if currently changing.
   */
  void setChanging(boolean status);

  void setMaintenance(boolean status);

  boolean checkChangeAllowed(String capability);

  boolean checkMaintenanceAllowed(String capability);

  boolean checkProcessingAllowed(String capability);
}
