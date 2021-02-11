package jobshop.grpc;

import javafx.application.Application;

public class GymRender {

  private static GymRender instance;
  private static boolean rendering = false;

  private GymRender() {}

  public static synchronized GymRender getInstance() {
    if (instance == null) {
      instance = new GymRender();
    }
    return instance;
  }

  public void render() {
    if (!rendering) {
      //bad idea?
      new Thread(() -> Application.launch(GymVisualization.class)).start();
      rendering = true;
    }
  }

}
