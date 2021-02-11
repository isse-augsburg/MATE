package jobshop.environment;

import jadex.bridge.IComponentIdentifier;

public interface IMachine extends ILocationObject {
  /**
   *  Get the id (or name) of this object.
   *  @return The id.
   */
  String getId();

  /**
   *  Get the identifier (address) of the machine.
   *  @return The identifier that can be used to send a message to the machine.
   */
  IComponentIdentifier getAgentIdentifier();


  //------------------ potential field ----------------------

  /**
   * A value between 0.0 and 1.0 that stands for the attractive force send out by this machine.
   * @return A force value.
   */
  double getInputPotentialForce();

  double getOutputPotentialForce();


  //------------------ location getters ---------------------

  ILocation getLocation();

  ILocation getInputLocation();

  ILocation getOutputLocation();


  //---------------- manage buffers ---------------------

  IMachineBuffer getInputBuffer();

  IMachineBuffer getOutputBuffer();


  //---------------- manage current product --------------

  IProduct getCurrentProduct();


  //--------------- manage services ---------------------

  IServiceManager getServiceManager();


  //-------------- The next methods can be used for reward construction. -------------

  void increaseFinishCount();

  int getFinishCount();

  void increaseRejectCount();

  int getRejectCount();

  void increaseChangeCount();

  int getChangeCount();

  void increaseWorkload();

  int getUtilization();

  void resetCounters();
}
