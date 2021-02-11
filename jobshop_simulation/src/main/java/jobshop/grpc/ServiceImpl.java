package jobshop.grpc;

import io.grpc.stub.StreamObserver;

import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IMessageFeature;
import jadex.bridge.fipa.FipaMessage;

import java.util.ArrayList;

import jobshop.agents.JadexManager;
import jobshop.agents.messaging.CustomMessages;
import jobshop.env.BufferMsg;
import jobshop.env.CapabilityMsg;
import jobshop.env.Empty;
import jobshop.env.EnvironmentGrpc.EnvironmentImplBase;
import jobshop.env.Info;
import jobshop.env.MachineMsg;
import jobshop.env.MasAction;
import jobshop.env.MasActionResponse;
import jobshop.env.MasState;
import jobshop.env.PositionMsg;
import jobshop.env.ProductMsg;
import jobshop.env.ProductSetupMsg;
import jobshop.env.RewardMsg;
import jobshop.env.Seed;
import jobshop.env.SettingsMsg;
import jobshop.env.SetupMsg;
import jobshop.environment.RandomGenerator;
import jobshop.environment.impl.AutomatedGuidedVehicle;
import jobshop.environment.impl.Capability;
import jobshop.environment.impl.Environment;
import jobshop.environment.impl.Machine;
import jobshop.environment.impl.Product;
import jobshop.environment.impl.ProductManager;

public class ServiceImpl {

  static class EnvironmentImpl extends EnvironmentImplBase {

    private IInternalAccess agent;
    private Environment env = Environment.getInstance();
    private JadexManager jad = JadexManager.getInstance();

    public EnvironmentImpl(IInternalAccess agent) {
      this.agent = agent;
    }


    @Override
    public void applyAction(MasAction request, StreamObserver<MasActionResponse> responseObserver) {
      env.setActionFlag(true);
      env.syncAgents();
      sendAction(request);
      env.increaseCounter();
      env.resetEventFlags();
      env.syncAgents();
      env.setActionFlag(false);
      //wait until all products are processed or a timeout occurred.
      while (!env.checkReschedule() && !env.checkTimeout()) {
        syncPassSecond();
      }
      responseObserver.onNext(buildMasActionResponse());
      responseObserver.onCompleted();
    }

    @Override
    public void reset(Empty request, StreamObserver<MasState> responseObserver) {
      startReset();
      env.resetEnvironment();
      sendMessageAll(CustomMessages.Performative.RESET);
      stopReset();
      responseObserver.onNext(buildMasState());
      responseObserver.onCompleted();
    }

    @Override
    public void render(Empty request, StreamObserver<Empty> responseObserver) {
      GymRender.getInstance().render();
      responseObserver.onNext(Empty.newBuilder().build());
      responseObserver.onCompleted();
    }

    @Override
    public void setup(SettingsMsg request, StreamObserver<SetupMsg> responseObserver) {
      env.setupEnvironment(request.getConfig());
      jad.startEnvironmentAgents(request.getClockSpeed());
      env.syncAgents();
      env.generateRandomMap();
      env.syncAgents();
      responseObserver.onNext(SetupMsg.newBuilder()
          .addAllMachines(buildMachines())
          .setProducts(buildProductSetupMsg())
          .build());
      responseObserver.onCompleted();
    }

    @Override
    public void setSeed(Seed request, StreamObserver<Empty> responseObserver) {
      RandomGenerator.setSeed(request.getSeed());
      responseObserver.onNext(Empty.newBuilder().build());
      responseObserver.onCompleted();
    }

    //--------------------- sync methods -----------------------------------------------

    private void syncPassSecond() {
      env.syncAgents();
      agent.waitForDelay(env.getStepTime()).get();
      env.increaseCounter();
      env.calcMakespan();
      env.syncAgents();
    }

    private void startReset() {
      env.syncAgents();
      env.setResetFlag(true);
      env.generateRandomMap();
      env.syncAgents();
    }

    private void stopReset() {
      env.syncAgents();
      env.setResetFlag(false);
      env.syncAgents();
    }

    //--------------------- helper methods ---------------------------------------------

    //--------------------- messaging --------------------------------------------------

    private void sendMessageAll(String type) {
      FipaMessage message = new FipaMessage(type, null);
      //message machines
      for (Machine machine: Environment.getInstance().getMachines()) {
        message.addReceiver(machine.getAgentIdentifier());
      }
      //message AGVs
      for (AutomatedGuidedVehicle agv: Environment.getInstance().getAgvs()) {
        message.addReceiver(agv.getAgentIdentifier());
      }
      //message dispatcher
      message.addReceiver(Environment.getInstance().getDispatcher().getAgentIdentifier());
      agent.getFeature(IMessageFeature.class).sendMessage(message);
    }

    /**
     * Send a message to each machine, containing its next action.
     * @param request contains the action.
     */
    private void sendAction(MasAction request) {
      Machine[] machines = Environment.getInstance().getMachines();
      for (int i = 0; i < request.getActionList().size(); i++) {
        FipaMessage message = new FipaMessage(
            getMsgType(request.getAction(i).getOperation()),
            request.getAction(i).getCapability());
        message.addReceiver(machines[i].getAgentIdentifier());
        agent.getFeature(IMessageFeature.class).sendMessage(message).get();
      }
    }

    private String getMsgType(String operation) {
      if ("MAINTENANCE".equals(operation)) {
        return CustomMessages.Performative.ACTION_MAINTENANCE;
      }
      return CustomMessages.Performative.ACTION_CHANGE;
    }


    //-------------------- state builders ----------------------------------------

    /**
     * Build the setupMsg returned on Gym creation.
     * @return ProductSetupMsg.
     */
    private ProductSetupMsg buildProductSetupMsg() {
      return ProductSetupMsg.newBuilder()
          .addAllProducts(buildProductTemplates())
          .setBatchSize(getBatchSize())
          .setBatchCount(env.getSettings().getBatchCount())
          .build();
    }

    /**
     * Build a message containing the product templates.
     * @return The product templates.
     */
    private ArrayList<ProductMsg> buildProductTemplates() {
      ArrayList<ProductMsg> products = new ArrayList<>();
      for (ProductManager pm: env.getSettings().getProductManagers()) {
        products.add(ProductMsg.newBuilder()
            .setId(pm.getProduct().getId())
            .setSize(pm.getProduct().getSize())
            .setDuration(pm.getProduct().getDuration())
            .addAllWorkflow(pm.getProduct().getWorkflow())
            .build());
      }
      return products;
    }

    /**
     * Return the batch size, depending on randomized or not.
     * @return The batch size.
     */
    private int getBatchSize() {
      if (env.getSettings().isBatchRandomized()) {
        return env.getSettings().getBatchRandomizedSize();
      }
      int batchSize = 0;
      for (ProductManager pm: env.getSettings().getProductManagers()) {
        batchSize += pm.getQuantity();
      }
      return batchSize;
    }

    /**
     * Build the GRPC MasState message.
     * @return The MasState to send.
     */
    private MasState buildMasState() {
      return MasState.newBuilder()
          .addAllMachines(buildMachines())
          .addAllProducts(buildProducts())
          .build();
    }

    private ArrayList<ProductMsg> buildProducts() {
      ArrayList<ProductMsg> products = new ArrayList<>();
      for (Product product: env.getProducts()) {
        products.add(ProductMsg.newBuilder()
            .setId(product.getId())
            .setSize(product.getSize())
            .setStep(product.getCurrentStepNumber())
            .setArrival(product.getArrivalTime())
            .setDuration(product.getDuration())
            .addAllWorkflow(product.getWorkflow())
            .build());
      }
      return products;
    }

    /**
     * Msg contains information about all machines in the environment.
     * @return Return the repeated MachineMsg.
     */
    private ArrayList<MachineMsg> buildMachines() {
      ArrayList<MachineMsg> machines = new ArrayList<>();
      for (Machine machine: env.getMachines()) {
        PositionMsg pos = PositionMsg.newBuilder()
            .setPosX(machine.getLocation().getPosX())
            .setPosY(machine.getLocation().getPosY())
            .build();
        BufferMsg inputBuffer = BufferMsg.newBuilder()
            .setSize(machine.getInputBuffer().getSize())
            .setUtilization(machine.getInputBuffer().getOccupiedSpace())
            .build();
        BufferMsg outputBuffer = BufferMsg.newBuilder()
            .setSize(machine.getOutputBuffer().getSize())
            .setUtilization(machine.getOutputBuffer().getOccupiedSpace())
            .build();
        machines.add(MachineMsg.newBuilder()
            .setPosition(pos)
            .setInputBuffer(inputBuffer)
            .setOutputBuffer(outputBuffer)
            .setCapability(machine.getServiceManager().getCapability().getId())
            .addAllCapabilities(buildCapabilities(machine))
            .build());
      }
      return machines;
    }

    private ArrayList<CapabilityMsg> buildCapabilities(Machine machine) {
      ArrayList<CapabilityMsg> capabilities = new ArrayList<>();
      for (Capability capability: machine.getServiceManager().getCapabilities()) {
        capabilities.add(CapabilityMsg.newBuilder()
            .setId(capability.getId())
            .setStatus(capability.getStatus())
            .setSetupTime(capability.getSetupTime())
            .setProcessingTime(capability.getProcessingTime())
            .setMaintenanceTime(capability.getMaintenanceTime())
            .setRepairTime(capability.getRepairTime())
            .setFailureRate(capability.getFailureRate())
            .build());
      }
      return capabilities;
    }

    private RewardMsg buildReward(Machine machine) {
      return RewardMsg.newBuilder()
          .setGlobalProcessed(env.getFinishedGlobalProductsCount())
          .setLocalProcessed(machine.getFinishCount())
          .setMakespan(env.calcMakespan())
          .setFlowTime(env.calcFlowTime())
          .setTardiness(env.calcTardiness())
          .setLateness(env.calcLateness())
          .setUtilization(machine.getUtilization())
          .build();
    }

    private Info buildInfo() {
      return Info.newBuilder()
          .setCurrentStep(env.getTimeStep())
          .setTimeout(env.getSettings().getTimeout())
          .build();
    }

    private MasActionResponse buildMasActionResponse() {
      ArrayList<RewardMsg> rewards = new ArrayList<>();
      for (Machine machine: env.getMachines()) {
        rewards.add(buildReward(machine));
      }
      return MasActionResponse.newBuilder()
          .setState(buildMasState())
          .addAllReward(rewards)
          .setDone(env.checkTimeout())
          .setInfo(buildInfo())
          .build();
    }
  }
}
