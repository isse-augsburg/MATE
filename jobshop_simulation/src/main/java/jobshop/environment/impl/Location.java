package jobshop.environment.impl;

import jobshop.environment.ILocation;

public class Location implements ILocation {

  public static final double DEFAULT_TOLERANCE = 0.00;
  public static final double DEFAULT_DELIVERY_TOLERANCE = 0.005;

  private double posX;
  private double posY;

  public Location() {}

  public Location(double posX, double posY) {
    this.posX = posX;
    this.posY = posY;
  }

  //copy constructor
  public Location(Location other) {
    this.posX = other.posX;
    this.posY = other.posY;
  }


  //-------------- Accessor methods -----------------

  @Override
  public double getPosX() {
    return this.posX;
  }

  public void setPosX(double posX) {
    this.posX = posX;
  }

  @Override
  public double getPosY() {
    return this.posY;
  }

  public void setPosY(double posY) {
    this.posY = posY;
  }

  @Override
  public double getDistance(ILocation other) {
    double powX = Math.pow((other.getPosX() - this.getPosX()), 2);
    double powY = Math.pow((other.getPosY() - this.getPosY()), 2);
    return Math.sqrt(powX + powY);
  }

  @Override
  public boolean isNear(ILocation other) {
    return getDistance(other) <= DEFAULT_TOLERANCE;
  }

  @Override
  public boolean isInDeliveryRange(ILocation other) {
    return getDistance(other) <= DEFAULT_DELIVERY_TOLERANCE;
  }

  @Override
  public String toString() {
    return "Location(" + "x = " + getPosX() + ", y = " + getPosY() + ")";
  }

  @Override
  public int hashCode() {
    return (int)(this.getPosX() * 21 + this.getPosY());
  }

  @Override
  public boolean equals(Object obj) {
    boolean ret = false;
    if (obj instanceof Location) {
      Location loc = (Location) obj;
      if (loc.getPosX() == this.getPosX() && loc.getPosY() == this.getPosY()) {
        ret = true;
      }
    }
    return ret;
  }
}
