package jobshop.environment.impl;

import jadex.bridge.IComponentIdentifier;
import jobshop.environment.ILocationObject;
import jobshop.environment.IMachine;


public class Machine extends LocationObject implements IMachine {
  //-------- attributes ----------

  /** The offset of the buffers compared to the machine center (for visualization). */
  private static final double BUFFER_OFFSET = 0.04;

  /** The agent that is controlling the Machine. */
  private IComponentIdentifier cid;

  private ServiceManager serviceManager;

  private Product currentProduct;
  private MachineBuffer inputBuffer;
  private MachineBuffer outputBuffer;

  /* Next three attributes can be used for reward construction. */
  private int finishCount = 0;
  private int rejectCount = 0;
  private int changeCount = 0;
  private int utilization = 0;

  //-------- constructors --------

  public Machine() {
    // Empty constructor required for JavaBeans (do not remove).
  }

  public Machine(IComponentIdentifier cid, Location location, ServiceManager serviceManager,
      MachineBuffer inputBuffer, MachineBuffer outputBuffer) {
    super(cid.getName(), location);
    this.cid = cid;
    this.serviceManager = serviceManager;
    this.inputBuffer = inputBuffer;
    this.outputBuffer = outputBuffer;
  }

  /**
   * Copy constructor.
   * @param other The object to copy.
   */
  public Machine(Machine other) {
    super(other);
    this.cid = other.cid;
    this.finishCount = other.finishCount;
    this.rejectCount = other.rejectCount;
    this.changeCount = other.changeCount;
    this.utilization = other.utilization;
    if (other.serviceManager != null) {
      this.serviceManager = new ServiceManager(other.serviceManager);
    }
    if (other.currentProduct != null) {
      this.currentProduct = new Product(other.currentProduct);
    }
    if (other.inputBuffer != null) {
      this.inputBuffer = new MachineBuffer(other.inputBuffer);
    }
    if (other.outputBuffer != null) {
      this.outputBuffer = new MachineBuffer(other.outputBuffer);
    }
  }


  //-------- methods --------

  @Override
  public IComponentIdentifier getAgentIdentifier() {
    return cid;
  }

  public void setAgentIdentifier(IComponentIdentifier cid) {
    this.cid = cid;
  }

  @Override
  public double getInputPotentialForce() {
    return 1.0 - (this.inputBuffer.getOccupiedSpace() / this.inputBuffer.getSize());
  }

  @Override
  public double getOutputPotentialForce() {
    return this.outputBuffer.getOccupiedSpace() / this.outputBuffer.getSize();
  }

  @Override
  public Location getInputLocation() {
    double newX = this.getLocation().getPosX() - BUFFER_OFFSET;
    return new Location(newX, this.getLocation().getPosY());
  }

  @Override
  public Location getOutputLocation() {
    double newX = this.getLocation().getPosX() + BUFFER_OFFSET;
    return new Location(newX, this.getLocation().getPosY());
  }

  @Override
  public MachineBuffer getInputBuffer() {
    return this.inputBuffer;
  }

  public void setInputBuffer(MachineBuffer buffer) {
    MachineBuffer old = this.inputBuffer;
    this.inputBuffer = buffer;
    pcs.firePropertyChange("inputBuffer", old, buffer);
  }

  @Override
  public MachineBuffer getOutputBuffer() {
    return this.outputBuffer;
  }

  public void setOutputBuffer(MachineBuffer buffer) {
    MachineBuffer old = this.outputBuffer;
    this.outputBuffer = buffer;
    pcs.firePropertyChange("outputBuffer", old, buffer);
  }

  @Override
  public Product getCurrentProduct() {
    return this.currentProduct;
  }

  public void setCurrentProduct(Product product) {
    Product oldProduct = this.currentProduct;
    this.currentProduct = product;
    pcs.firePropertyChange("currentProduct", oldProduct, product);
  }

  @Override
  public ServiceManager getServiceManager() {
    return this.serviceManager;
  }

  @Override
  public void increaseFinishCount() {
    this.finishCount++;
  }

  @Override
  public int getFinishCount() {
    return this.finishCount;
  }

  @Override
  public void increaseRejectCount() {
    this.rejectCount++;
  }

  @Override
  public int getRejectCount() {
    return this.rejectCount;
  }

  @Override
  public void increaseChangeCount() {
    this.changeCount++;
  }

  @Override
  public int getChangeCount() {
    return this.changeCount;
  }

  @Override
  public void increaseWorkload() {
    this.utilization++;
  }

  @Override
  public int getUtilization() {
    return this.utilization;
  }

  @Override
  public void resetCounters() {
    this.finishCount = 0;
    this.rejectCount = 0;
    this.changeCount = 0;
    this.utilization = 0;
  }

  public void setServiceManager(ServiceManager manager) {
    ServiceManager old = this.serviceManager;
    this.serviceManager = manager;
    pcs.firePropertyChange("serviceManager", old, manager);
  }

  @Override
  public void update(ILocationObject machine) {
    if (this.getId().equals(machine.getId())) {
      super.update(machine);
      setCurrentProduct(((Machine) machine).getCurrentProduct());
      setAgentIdentifier(((Machine) machine).getAgentIdentifier());
      setServiceManager(((Machine) machine).getServiceManager());
      setInputBuffer(((Machine) machine).getInputBuffer());
      setOutputBuffer(((Machine) machine).getOutputBuffer());
      this.finishCount = ((Machine) machine).finishCount;
      this.rejectCount = ((Machine) machine).rejectCount;
      this.changeCount = ((Machine) machine).changeCount;
      this.utilization = ((Machine) machine).utilization;
    }
  }

  @Override
  public ILocationObject deepCopy() {
    return new Machine(this);
  }

  @Override
  public String toString() {
    return String.format("Input-Force: %.2f", this.getInputPotentialForce()) + " – "
            + String.format("Output-Force: %.2f\n", this.getOutputPotentialForce())
            +  this.inputBuffer.getOccupiedSpace() + "/" + this.inputBuffer.getSize()
            + " – " + this.serviceManager.getCapabilityState() + " – "
            + this.outputBuffer.getOccupiedSpace() + "/" + this.outputBuffer.getSize();
  }
}
