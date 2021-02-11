package jobshop.gui.components;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;
import jobshop.environment.ILocationObject;
import jobshop.environment.IProduct;
import jobshop.environment.impl.Environment;


public class ProductListCell extends ListCell<ILocationObject> {

  @FXML private Label label;
  @FXML private ProgressIndicator progressIndicator;
  @FXML private HBox hbox;

  private FXMLLoader loader;

  /**
   * Load the fxml file for the list-cell.
   */
  public ProductListCell() {
    try {
      loader = new FXMLLoader(getClass().getResource("ProductListCell.fxml"));
      loader.setController(this);
      loader.load();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  protected void updateItem(ILocationObject locationObject, boolean empty) {
    super.updateItem(locationObject, empty);

    if (empty || locationObject == null) {
      setText(null);
      setGraphic(null);
    } else {
      IProduct product = Environment.getInstance().getSpawnedProduct(locationObject.getId());
      if (product != null) {
        progressIndicator.setProgress(product.getProgress());
      }

      label.setText(locationObject.getId());
      setText(null);
      setGraphic(hbox);
    }
  }
}
