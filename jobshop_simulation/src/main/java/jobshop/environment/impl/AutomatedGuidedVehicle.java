package jobshop.environment.impl;

import jadex.bridge.IComponentIdentifier;
import jobshop.environment.IAutomatedGuidedVehicle;
import jobshop.environment.ILocationObject;
import jobshop.environment.IProduct;

public class AutomatedGuidedVehicle extends LocationObject implements IAutomatedGuidedVehicle {
  //-------- attributes ----------

  /** The agent that is controlling the AGV. */
  private IComponentIdentifier cid;

  private Product product;
  private double visionrange;


  //-------- constructors --------

  public AutomatedGuidedVehicle() {
    // Empty constructor required for JavaBeans (do not remove).
  }

  public AutomatedGuidedVehicle(IComponentIdentifier cid, Location location,
      Product product, double visionrange) {
    super(cid.getName(), location);
    this.cid = cid;
    this.product = product;
    this.visionrange = visionrange;
  }

  /**
   * Copy constructor.
   * @param other The object to copy.
   */
  public AutomatedGuidedVehicle(AutomatedGuidedVehicle other) {
    super(other);
    this.cid = other.cid;
    this.visionrange = other.visionrange;
    if (other.product != null) {
      this.product = new Product(other.product);
    }
  }


  //-------- methods --------

  @Override
  public IComponentIdentifier getAgentIdentifier() {
    return this.cid;
  }

  public void setAgentIdentifier(IComponentIdentifier cid) {
    this.cid = cid;
  }

  @Override
  public Product getProduct() {
    return this.product;
  }

  @Override
  public Product removeProduct() {
    Product product = this.product;
    setProduct(null);
    return product;
  }

  public void setProduct(Product carriedproduct) {
    Product oldcp = this.product;
    this.product = carriedproduct;
    pcs.firePropertyChange("product", oldcp, carriedproduct);
  }

  @Override
  public double getVisionRange() {
    return this.visionrange;
  }

  public void setVisionRange(double visionrange) {
    double oldvr = this.visionrange;
    this.visionrange = visionrange;
    pcs.firePropertyChange("visionRange", oldvr, visionrange);
  }

  @Override
  public void update(ILocationObject agv) {
    if (this.getId().equals(agv.getId())) {
      super.update(agv);
      setVisionRange(((AutomatedGuidedVehicle) agv).getVisionRange());
      setProduct(((AutomatedGuidedVehicle) agv).getProduct());
    }
  }

  @Override
  public ILocationObject deepCopy() {
    return new AutomatedGuidedVehicle(this);
  }

  @Override
  public String toString() {
    return getId();
  }
}
