package jobshop.environment.impl;

public class Capability {

  private String id;
  private boolean status;
  private int setupTime;
  private int processingTime;
  private int maintenanceTime;
  private int repairTime;
  private double failureRate;
  private double currentFailureRate = 0;

  /**
   * Copy constructor of Capability.
   * @param other A copy of other.
   */
  public Capability(Capability other) {
    this.id = other.id;
    this.status = other.status;
    this.setupTime = other.setupTime;
    this.processingTime = other.processingTime;
    this.maintenanceTime = other.maintenanceTime;
    this.repairTime = other.repairTime;
    this.failureRate = other.failureRate;
    this.currentFailureRate = other.currentFailureRate;
  }

  public String getId() {
    return id;
  }

  public boolean getStatus() {
    return status;
  }

  public void setStatus(boolean status) {
    this.status = status;
  }

  public int getSetupTime() {
    return setupTime;
  }

  public int getProcessingTime() {
    return processingTime;
  }

  public int getMaintenanceTime() {
    return maintenanceTime;
  }

  public int getRepairTime() {
    return repairTime;
  }

  public double getFailureRate() {
    return failureRate;
  }

  public double getCurrentFailureRate() {
    return currentFailureRate;
  }

  public void increaseCurrentFailureRate() {
    currentFailureRate += failureRate;
  }

  public void resetCurrentFailureRate() {
    currentFailureRate = 0;
  }
}
