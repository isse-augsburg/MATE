package jobshop.agents.algortihms.helper;

public class Vector {
  private double ex;
  private double ey;

  public Vector(double ex, double ey) {
    this.ex = ex;
    this.ey = ey;
  }

  public double getEx() {
    return ex;
  }

  public void setEx(double ex) {
    this.ex = ex;
  }

  public double getEy() {
    return ey;
  }

  public void setEy(double ey) {
    this.ey = ey;
  }

  @Override
  public String toString() {
    return this.ex + " " + this.ey;
  }
}
