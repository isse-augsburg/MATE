package jobshop.toml;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import jobshop.environment.impl.Area;
import jobshop.environment.impl.Machine;
import jobshop.environment.impl.ProductManager;


public class Settings {
  private int timeout;
  private int agvGroupSize;

  private int batchRandomizedSize;
  private int batchCount;
  private int batchFrequency;
  private boolean batchRandomized;

  private int schedulingFrequency;
  private LinkedHashMap<String, Boolean> schedulingStrategy;

  private Area agvStartArea;
  private Area productStartArea;
  private Area productEndArea;

  private LinkedList<Machine> machines;
  private ArrayList<ProductManager> productManagers;

  public int getTimeout() {
    return timeout;
  }

  public int getAgvGroupSize() {
    return agvGroupSize;
  }

  public int getBatchRandomizedSize() {
    return batchRandomizedSize;
  }

  public int getBatchCount() {
    return batchCount;
  }

  public int getBatchFrequency() {
    return batchFrequency;
  }

  public boolean isBatchRandomized() {
    return batchRandomized;
  }

  public int getSchedulingFrequency() {
    return schedulingFrequency;
  }

  public LinkedHashMap<String, Boolean> getSchedulingStrategy() {
    return schedulingStrategy;
  }

  public Area getAgvStartArea() {
    return agvStartArea;
  }

  public Area getProductStartArea() {
    return productStartArea;
  }

  public Area getProductEndArea() {
    return productEndArea;
  }

  public LinkedList<Machine> getMachines() {
    return machines;
  }

  public ArrayList<ProductManager> getProductManagers() {
    return productManagers;
  }
}