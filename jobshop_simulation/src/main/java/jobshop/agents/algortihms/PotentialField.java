package jobshop.agents.algortihms;

import java.util.Random;
import jobshop.agents.algortihms.helper.Vector;
import jobshop.environment.ILocation;

public class PotentialField {

  private static final double EPS = 1e-6;

  /**
   * Calculate the force gradients for the attraction force.
   * @param agent The location of the agent.
   * @param target The taget location.
   * @return The direction vector.
   */
  public static Vector attractionForce(ILocation agent, ILocation target, double scaler) {
    double selfX = agent.getPosX();
    double selfY = agent.getPosY();
    double goalX = target.getPosX();
    double goalY = target.getPosY();
    double distance = Math.sqrt(((selfX - goalX) * (selfX - goalX))
        + ((selfY - goalY) * (selfY - goalY))) + EPS;
    return new Vector(scaler * (goalX - selfX) / distance,scaler * (goalY - selfY) / distance);
  }

  /**
   * Calculate the force gradients for the obstacles.
   * @param agent The location of the agent.
   * @param obstacle The location of the obstacle.
   * @param scaler Scale the force gradients.
   * @param minDistance The distance required to calculate the force gradients.
   * @return the direction vector.
   */
  public static Vector repulsionForce(ILocation agent, ILocation obstacle,
      double scaler, double minDistance) {
    double selfX = agent.getPosX();
    double selfY = agent.getPosY();
    double obsX = obstacle.getPosX();
    double obsY = obstacle.getPosY();
    double distance = Math.sqrt(((selfX - obsX) * (selfX - obsX))
        + ((selfY - obsY) * (selfY - obsY))) + EPS;

    if (distance > minDistance) {
      return new Vector(0,0);
    }
    return new Vector(-(scaler / (distance * distance)) * (obsX - selfX) / distance,
        -(scaler / (distance * distance)) * (obsY - selfY) / distance);
  }

  public static Vector randomForce(double scaler, Random random) {
    double ex = 2.0 * random.nextDouble() - 1.0;
    double ey = 2.0 * random.nextDouble() - 1.0;
    return new Vector(scaler * ex,scaler * ey);
  }
}
