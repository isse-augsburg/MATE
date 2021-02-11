package jobshop.environment.impl;


public class ProductManager {
  private int quantity;
  Product product;

  public ProductManager(int quantity, Product product) {
    this.quantity = quantity;
    this.product = product;
  }

  public int getQuantity() {
    return this.quantity;
  }

  public Product getProduct() {
    return this.product;
  }
}
