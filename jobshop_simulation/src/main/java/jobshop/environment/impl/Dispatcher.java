package jobshop.environment.impl;

import jadex.bridge.IComponentIdentifier;
import java.util.ArrayList;
import java.util.List;
import jobshop.environment.IDispatcher;

public class Dispatcher implements IDispatcher {

  /** The agent that is controlling the Machine. */
  private IComponentIdentifier cid;

  private Area area;
  private ArrayList<ProductManager> productManagers;

  public Dispatcher(IComponentIdentifier cid, Area area,
      ArrayList<ProductManager> productManagers) {
    this.cid = cid;
    this.area = area;
    this.productManagers = productManagers;
  }

  /**
   * Copy constructor.
   * @param other The object to copy.
   */
  public Dispatcher(Dispatcher other) {
    this.cid = other.cid;
    this.area = other.area;
    this.productManagers = new ArrayList<>();
    this.productManagers.addAll(other.productManagers);
  }

  @Override
  public IComponentIdentifier getAgentIdentifier() {
    return this.cid;
  }

  @Override
  public Area getArea() {
    return this.area;
  }

  @Override
  public List<ProductManager> getProductManagers() {
    return this.productManagers;
  }

  @Override
  public IDispatcher deepCopy() {
    return new Dispatcher(this);
  }
}
