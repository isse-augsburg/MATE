package jobshop.agents.messaging;

public class CustomMessages {

  public abstract static class Performative {
    public static final String DELIVERY_PROPOSAL = "delivery-propose";
    public static final String DELIVERY_ACCEPT = "delivery-accept";
    public static final String DELIVERY_REJECT = "delivery-reject";
    public static final String PICKUP_PROPOSAL = "pickup-propose";
    public static final String PICKUP_ACCEPT = "pickup-accept";
    public static final String PICKUP_REJECT = "pickup-reject";
    public static final String ACTION_CHANGE = "change";
    public static final String ACTION_MAINTENANCE = "maintenance";
    public static final String RESET = "reset";
  }
}
