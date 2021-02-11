package jobshop.environment.impl;

import java.util.Random;
import jobshop.environment.IArea;
import jobshop.environment.ILocation;

public class Area implements IArea {

  private double posX;
  private double posY;
  private double width;
  private double height;

  public Area(double posX, double posY, double width, double height) {
    this.posX = posX;
    this.posY = posY;
    this.width = width;
    this.height = height;
  }

  @Override
  public double getPosX() {
    return posX;
  }

  @Override
  public double getPosY() {
    return posY;
  }

  @Override
  public double getWidth() {
    return width;
  }

  @Override
  public double getHeight() {
    return height;
  }

  @Override
  public Location getCenter() {
    double posX = this.posX + this.width / 2.0;
    double posY = this.posY + this.height / 2.0;
    return new Location(posX, posY);
  }

  @Override
  public Location getRandomLocation(Random random) {
    return new Location(getRandomX(random), getRandomY(random));
  }

  private double getRandomX(Random random) {
    return this.getPosX() + this.getWidth() * random.nextDouble();
  }

  private double getRandomY(Random random) {
    return this.getPosY() + this.getHeight() * random.nextDouble();
  }
}
