package jobshop.environment;

import jadex.bridge.IComponentIdentifier;
import java.util.List;
import jobshop.environment.impl.ProductManager;

public interface IDispatcher {

  IComponentIdentifier getAgentIdentifier();

  IArea getArea();

  List<ProductManager> getProductManagers();

  IDispatcher deepCopy();
}
