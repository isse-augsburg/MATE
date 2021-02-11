package jobshop.environment;

import java.util.Queue;

public interface IMachineBuffer {
  /**
   * The size of the buffer.
   * @return The size of the buffer.
   */
  double getSize();

  /**
   * The free space left on the buffer.
   * @return The free space left on the buffer.
   */
  double getFreeSpace();

  /**
   * The occupied on the buffer.
   * @return The occupied space on the buffer.
   */
  double getOccupiedSpace();

  /**
   * Place a product on the buffer.
   * @param product The product to be placed.
   * @return True if product could be placed else False.
   */
  boolean tryPutProduct(IProduct product);

  /**
   * Determine which product to get next.
   * @return The next product to be processed.
   */
  IProduct tryRemoveProduct();

  /**
   * Get all products currently placed on the buffer.
   * @return All products.
   */
  Queue<? extends IProduct> getProducts();

  /**
   * Delete all products from the buffer.
   */
  void clear();
}
