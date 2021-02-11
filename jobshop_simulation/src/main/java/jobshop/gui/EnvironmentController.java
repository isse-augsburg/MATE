package jobshop.gui;

import javafx.animation.AnimationTimer;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.util.Callback;
import jobshop.environment.ILocationObject;
import jobshop.environment.impl.Area;
import jobshop.environment.impl.AutomatedGuidedVehicle;
import jobshop.environment.impl.Environment;
import jobshop.environment.impl.Location;
import jobshop.environment.impl.LocationObject;
import jobshop.environment.impl.Machine;
import jobshop.environment.impl.Product;
import jobshop.gui.components.LocationObjectListCell;
import jobshop.gui.components.ProductListCell;


public class EnvironmentController {

  //------ Class Attributes --------

  private Environment env = Environment.getInstance();

  private static final String AGV_IMG_PATH = "/images/agv.png";
  private static final String MACHINE_IMG_PATH = "/images/machine.png";
  private static final String PRODUCT_IMG_PATH = "/images/product.png";
  private static final String AREA_START_IMG_PATH = "/images/area_start.png";
  private static final String AREA_END_IMG_PATH = "/images/area_end.png";

  private Image agvImg;
  private Image machineImg;
  private Image productImg;

  // For icon scaling on resize.
  private double canvasWidth;
  private double canvasHeight;

  // Marker related
  enum Marker {
    NONE, AGV, MACHINE, PRODUCT
  }

  private Marker markerMode = Marker.NONE;

  // JavaFX menu elements
  @FXML private Pane canvasPane;
  @FXML private Canvas staticMap;
  @FXML private Canvas dynamicMap;
  @FXML private ListView<ILocationObject> agvListView;
  @FXML private ListView<ILocationObject> machineListView;
  @FXML private ListView<ILocationObject> productListView;
  @FXML private CheckBox infoCheckbox;

  // Default sizes
  private static final int AGV_SIZE_DEFAULT = 40;
  private static final int MACHINE_SIZE_DEFAULT = 100;
  private static final int PRODUCT_SIZE_DEFAULT = 25;

  private static final int DEFAULT_WIDTH = 990;
  private static final int DEFAULT_HEIGHT = 800;


  //-------- Constructor ------------

  /**
   * Get Jadex environment and load images.
   */
  public EnvironmentController() {
    this.agvImg = new Image(AGV_IMG_PATH, AGV_SIZE_DEFAULT, AGV_SIZE_DEFAULT, true, true);
    this.machineImg = new Image(MACHINE_IMG_PATH, MACHINE_SIZE_DEFAULT,
            MACHINE_SIZE_DEFAULT, true, true);
    this.productImg = new Image(PRODUCT_IMG_PATH, PRODUCT_SIZE_DEFAULT,
            PRODUCT_SIZE_DEFAULT, true, true);
  }

  /**
   * Will get called right after constructor.
   * Setup GUI elemnts here.
   */
  @FXML public void initialize() {
    setupCanvas();
    setupAllListListeners();
    setupTimer();
  }

  //------ List Related ----------------

  private void setupAllListListeners() {
    productListView.setOnMouseClicked(evt -> markerMode = Marker.PRODUCT);
    agvListView.setOnMouseClicked(evt -> markerMode = Marker.AGV);
    machineListView.setOnMouseClicked(evt -> markerMode = Marker.MACHINE);
  }

  private void updateAllList() {
    updateList(env.getSpawnedProducts(), productListView, view -> new ProductListCell(),
        Marker.PRODUCT);
    updateList(env.getAgvs(), agvListView, view -> new LocationObjectListCell(),
        Marker.AGV);
    updateList(env.getMachines(), machineListView, view -> new LocationObjectListCell(),
        Marker.MACHINE);
  }

  private void updateList(LocationObject[] objects, ListView<ILocationObject> view,
      Callback<ListView<ILocationObject>, ListCell<ILocationObject>> callback, Marker marker) {
    view.setItems(FXCollections.observableArrayList(objects));
    view.setCellFactory(callback);
  }

  private void clearAllListViews() {
    markerMode = Marker.NONE;
    agvListView.getItems().clear();
    machineListView.getItems().clear();
    productListView.getItems().clear();
  }


  //------ Canvas drawing related -------

  /**
   * Setup Canvas (Activate event listeners, draw images).
   */
  private void setupCanvas() {
    canvasPane.widthProperty().addListener(evt -> redrawCanvas());
    canvasPane.heightProperty().addListener(evt -> redrawCanvas());
  }

  /**
   * Repaint image to visualize agent movement.
   */
  private void setupTimer() {
    new AnimationTimer() {
      @Override
      public void handle(long now) {
        clearCanvas();
        drawArea(env.getSettings().getProductStartArea(), AREA_START_IMG_PATH);
        drawArea(env.getSettings().getProductEndArea(), AREA_END_IMG_PATH);
        drawMachines();
        drawAgvs();
        drawProducts();
        drawMarker();
        updateAllList();

        if (infoCheckbox.isSelected()) {
          drawText();
        }
      }
    }.start();
  }

  /**
   * Redraw the both canvas on startup or canvas resize.
   */
  private void redrawCanvas() {
    double width = canvasPane.getWidth();
    double height = canvasPane.getHeight();

    staticMap.setWidth(width);
    staticMap.setHeight(height);
    dynamicMap.setWidth(width);
    dynamicMap.setHeight(height);

    scaleImages();
    drawBackground();
    canvasWidth = canvasPane.getWidth();
    canvasHeight = canvasPane.getHeight();
  }

  private void drawArea(Area area, String imgPath) {
    GraphicsContext gc = dynamicMap.getGraphicsContext2D();
    double offsetW = productImg.getWidth();
    double offsetH = productImg.getHeight();
    double imgX = area.getPosX() * dynamicMap.getWidth() - offsetW;
    double imgY = area.getPosY() * dynamicMap.getHeight() - offsetH;
    double imgWidth = area.getWidth() * dynamicMap.getWidth() + 2 * offsetW;
    double imgHeight = area.getHeight() * dynamicMap.getHeight() + 2 * offsetH;
    Image img = new Image(imgPath, imgWidth, imgHeight, false, true);
    gc.drawImage(img, imgX, imgY);
  }

  private void clearCanvas() {
    GraphicsContext gcDynamic = dynamicMap.getGraphicsContext2D();
    gcDynamic.clearRect(0, 0, dynamicMap.getWidth(), dynamicMap.getHeight());
  }

  private void drawMarker() {
    switch (markerMode) {
      case AGV:
        AutomatedGuidedVehicle[] agvs = env.getAgvs();
        int index = agvListView.getSelectionModel().getSelectedIndex();
        if (index > -1 && agvs.length > index) {
          drawRectangle(agvs[index].getLocation(), agvImg);
        }
        break;
      case MACHINE:
        Machine[] machines = env.getMachines();
        index = machineListView.getSelectionModel().getSelectedIndex();
        if (index > -1 && machines.length > index) {
          drawRectangle(machines[index].getLocation(), machineImg);
        }
        break;
      case PRODUCT:
        Product[] products = env.getSpawnedProducts();
        index = productListView.getSelectionModel().getSelectedIndex();
        if (index > -1 && products.length > index) {
          drawRectangle(products[index].getLocation(), productImg);
        }
        break;
      default:
        break;
    }
  }

  private void drawRectangle(Location location, Image img) {
    GraphicsContext gcDynamic = dynamicMap.getGraphicsContext2D();
    gcDynamic.setStroke(Color.RED);
    gcDynamic.setLineWidth(4);
    double offset = 2.0;
    double posX = location.getPosX() * dynamicMap.getWidth() - offset - img.getWidth() / 2.0;
    double posY = location.getPosY() * dynamicMap.getHeight() - offset - img.getHeight() / 2.0;
    gcDynamic.strokeRoundRect(posX, posY, img.getWidth() + 2 * offset,
        img.getHeight() + 2 * offset,20,20);
  }

  private void drawText() {
    GraphicsContext gcDynamic = dynamicMap.getGraphicsContext2D();
    gcDynamic.setTextAlign(TextAlignment.CENTER);
    gcDynamic.setTextBaseline(VPos.CENTER);
    gcDynamic.setFont(new Font("Arial", 15));
    gcDynamic.setFill(Color.BLACK);
    for (Machine machine: env.getMachines()) {
      double posX = machine.getLocation().getPosX() * dynamicMap.getWidth();
      double offsetHeight = machineImg.getHeight() / 1.2;
      double posY = machine.getLocation().getPosY() * dynamicMap.getHeight() - offsetHeight;
      gcDynamic.fillText(machine.toString(), posX, posY);
    }
  }

  private void drawAgvs() {
    for (AutomatedGuidedVehicle agv: env.getAgvs()) {
      drawWithOffset(agv.getLocation(), dynamicMap, agvImg);
    }
  }

  private void drawProducts() {
    for (Product product: env.getSpawnedProducts()) {
      drawWithOffset(product.getLocation(), dynamicMap, productImg);
    }
  }

  private void drawMachines() {
    for (Machine machine: env.getSettings().getMachines()) {
      drawWithOffset(machine.getLocation(), dynamicMap, machineImg);
    }
  }

  private void drawWithOffset(Location location, Canvas canvas, Image img) {
    GraphicsContext gc = canvas.getGraphicsContext2D();
    double mapWidth = canvas.getWidth();
    double mapHeight = canvas.getHeight();
    double imgWidth = img.getWidth();
    double imgHeight = img.getHeight();
    double posX = location.getPosX() * mapWidth - imgWidth / 2.0;
    double posY = location.getPosY() * mapHeight - imgHeight / 2.0;
    gc.drawImage(img, posX, posY);
  }

  /**
   * Fill canvas with background images.
   */
  private void drawBackground() {
    GraphicsContext gc = staticMap.getGraphicsContext2D();
    gc.setFill(Color.LIGHTGRAY);
    gc.fillRect(0,0, staticMap.getWidth(), staticMap.getHeight());
  }

  /**
   * Scale all images on window resize (currently deactivated).
   */
  private void scaleImages() {
    if (canvasWidth > 0 && canvasHeight > 0) {
      double scaleW = canvasPane.getWidth() / DEFAULT_WIDTH;
      double scaleH = canvasPane.getHeight() / DEFAULT_HEIGHT;
      machineImg = updateImage(MACHINE_SIZE_DEFAULT, MACHINE_IMG_PATH, scaleW, scaleH);
      agvImg = updateImage(AGV_SIZE_DEFAULT, AGV_IMG_PATH, scaleW, scaleH);
      productImg = updateImage(PRODUCT_SIZE_DEFAULT, PRODUCT_IMG_PATH, scaleW, scaleH);
    }
  }

  private Image updateImage(int defaultSize, String imgPath, double scaleW, double scaleH) {
    double newWidth = defaultSize * scaleW;
    double newHeight = defaultSize * scaleH;
    return new Image(imgPath, newWidth, newHeight, true, true);
  }
}
