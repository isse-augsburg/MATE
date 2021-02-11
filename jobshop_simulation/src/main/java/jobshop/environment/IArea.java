package jobshop.environment;

import java.util.Random;

public interface IArea {

  double getPosX();

  double getPosY();

  double getWidth();

  double getHeight();

  /**
   * Get the center of the area.
   * @return The location.
   */
  ILocation getCenter();

  /**
   * Get a random location from the area.
   * @return The location object.
   */
  ILocation getRandomLocation(Random random);
}
