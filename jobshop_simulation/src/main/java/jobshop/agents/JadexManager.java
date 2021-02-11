package jobshop.agents;

import jadex.base.IPlatformConfiguration;
import jadex.base.PlatformConfigurationHandler;
import jadex.base.Starter;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.clock.IClock;
import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.threadpool.IThreadPoolService;
import jadex.commons.future.IFuture;
import jobshop.environment.impl.Environment;

public class JadexManager {

  private static JadexManager instance;

  private static final String AGV_AGENT = "jobshop/agents/AgvAgent.class";
  private static final String MACHINE_AGENT = "jobshop/agents/MachineAgent.class";
  private static final String DISPATCHER_AGENT = "jobshop/agents/DispatcherAgent.class";
  private static final String SERVER_AGENT = "jobshop/agents/ServerAgent.class";

  private IFuture<IExternalAccess> fut;
  private IClockService cs;

  private JadexManager() {}

  public static synchronized JadexManager getInstance() {
    if (instance == null) {
      instance = new JadexManager();
    }
    return instance;
  }

  /**
   * Start a Jadex platform with machine and agv agents.
   */
  public void startPlatform() {
    IPlatformConfiguration conf = PlatformConfigurationHandler.getMinimal();
    conf.setValue("kernel_bdi", true);
    conf.addComponent(SERVER_AGENT);
    fut = Starter.createPlatform(conf);
    fut.get();

    // Apply the chosen clock speed.
    cs = fut.get().searchService(new ServiceQuery<>(IClockService.class)).get();
    cs.setClock(IClock.TYPE_CONTINUOUS, fut.get().searchService(
        new ServiceQuery<>(IThreadPoolService.class)).get());
  }

  /**
   * Kill the Jadex platform, will also kill server.
   */
  public void stopPlatform() {
    if (fut != null) {
      fut.get().killComponent();
      fut = null;
    }
  }

  /**
   * Start the remaining Jadex agents.
   */
  public void startEnvironmentAgents(double clockSpeed) {
    applyClockSpeed(clockSpeed);
    fut.get().addComponent(DISPATCHER_AGENT);
    for (int i = 0; i < Environment.getInstance().getSettings().getMachines().size(); i++) {
      fut.get().addComponent(MACHINE_AGENT);
    }
    for (int i = 0; i < Environment.getInstance().getSettings().getAgvGroupSize(); i++) {
      fut.get().addComponent(AGV_AGENT);
    }
  }

  /**
   * Change the clockspeed of the environment.
   * @param clockSpeed The clockspeed.
   */
  public void applyClockSpeed(double clockSpeed) {
    if (cs != null && fut != null) {
      cs.setDilation(clockSpeed);
    }
  }
}
