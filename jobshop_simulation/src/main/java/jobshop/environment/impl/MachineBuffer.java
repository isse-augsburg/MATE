package jobshop.environment.impl;

import java.util.LinkedList;
import java.util.Queue;
import java.util.stream.Collectors;
import jobshop.environment.IMachineBuffer;
import jobshop.environment.IProduct;

public class MachineBuffer implements IMachineBuffer {

  private double size;
  private Queue<Product> products = new LinkedList<>();


  //-------------------- constructor ------------------------

  public MachineBuffer() {}

  public MachineBuffer(int bufferSize) {
    this.size = bufferSize;
  }

  //copy constructor
  public MachineBuffer(MachineBuffer other) {
    this.size = other.size;
    this.products = new LinkedList<>();
    for (Product product: other.getProducts()) {
      this.products.add(new Product(product));
    }
  }


  //--------------------- methods ----------------------------

  @Override
  public double getSize() {
    return size;
  }

  @Override
  public double getFreeSpace() {
    double space = this.size;
    for (IProduct product: this.products) {
      space -= product.getSize();
    }
    return space;
  }

  @Override
  public double getOccupiedSpace() {
    double space = 0;
    for (IProduct product: this.products) {
      space += product.getSize();
    }
    return space;
  }

  @Override
  public boolean tryPutProduct(IProduct product) {
    if (getFreeSpace() >= product.getSize()) {
      this.products.add((Product) product);
      return true;
    }
    return false;
  }

  /**
   * Return products in a FIFO order.
   * @return Next product to process.
   */
  @Override
  public Product tryRemoveProduct() {
    return products.poll();
  }

  @Override
  public Queue<Product> getProducts() {
    return this.products;
  }

  @Override
  public void clear() {
    products.clear();
  }
}
