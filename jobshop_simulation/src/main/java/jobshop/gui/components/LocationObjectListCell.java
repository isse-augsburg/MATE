package jobshop.gui.components;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;

import jobshop.environment.ILocationObject;


public class LocationObjectListCell extends ListCell<ILocationObject> {

  @FXML private Label label;
  @FXML private HBox hbox;

  private FXMLLoader loader;

  /**
   * Load the fxml file for the list-cell.
   */
  public LocationObjectListCell() {
    try {
      loader = new FXMLLoader(getClass().getResource("LocationObjectListCell.fxml"));
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
      label.setText(locationObject.getId());
      setText(null);
      setGraphic(hbox);
    }
  }
}
