package jobshop.environment;

import java.util.ArrayList;
import java.util.Random;

public class RandomGenerator {
  private static Random random = new Random(42);

  public static synchronized Random getRandom() {
    return new Random(random.nextInt());
  }

  public static void setSeed(int newSeed) {
    random.setSeed(newSeed);
  }
}
