package jobshop.environment.impl;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class MachineManager {

  private Queue<Machine> freeMachines = new LinkedList<>();
  private Queue<Machine> occupiedMachines = new LinkedList<>();


  public MachineManager(LinkedList<Machine> machines) {
    for (Machine machine: machines) {
      this.freeMachines.add(new Machine(machine));
    }
  }


  /**
   * Combine free and occupied locations.
   * @return All locations.
   */
  public List<Machine> getMachines() {
    List<Machine> ret = new LinkedList<>();
    ret.addAll(this.freeMachines);
    ret.addAll(this.occupiedMachines);
    return ret;
  }

  /**
   * Free one machine and add it to the list again.
   * @param machine The machine to free.
   */
  public void addFreeMachine(Machine machine) {
    this.occupiedMachines.remove(machine);
    this.freeMachines.add(machine);
  }

  /**
   * Remove one free location and add it to the occupied queue.
   * @return A free location.
   */
  public Machine removeFreeMachine() {
    Machine machine = this.freeMachines.poll();
    if (machine != null) {
      this.occupiedMachines.add(machine);
    }
    return machine;
  }
}
