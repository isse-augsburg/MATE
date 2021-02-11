import collections

import gym
import grpc

from environment.grpc import jobshop_pb2
from environment.grpc import jobshop_pb2_grpc


class MATE(gym.Env):
    instance = False
    """
    Implements a Jobshop environment. Uses GRPC to communicate with a Java simulation.
    """
    def __init__(self, config, clockspeed=100, port=50051):
        """
        :param settings: A dict with the following keys:
            - channel: The GRPC channel.
            - config: The .toml file used for the simulation.
            - clock_speed: How fast the simulation will run.
        """
        if MATE.instance:
            raise Exception('MATE only allows for one environment!')
        MATE.instance = True
        self._channel = grpc.insecure_channel('localhost:' + str(port))
        self._stub = jobshop_pb2_grpc.EnvironmentStub(self._channel)
        # clip if clockspeed to high, but user can test with different values
        clockspeed = min(150, clockspeed)
        self._clock_speed = clockspeed
        # Setup simulation
        self._settings = self._setup_env(config, clockspeed)
        # Setup env-spaces
        self._capabilities = self._get_all_services()
        self._operations = ['CHANGE', 'MAINTENANCE']
        self.action_space = self._configure_action_space()
        self.observation_space = self._configure_observation_space()

    def step(self, action):
        mas_state = self._send_action_msg(action)
        return self._get_observation(mas_state.state), \
               self._get_reward(mas_state.reward), \
               mas_state.done, \
               self._get_info(mas_state.info)

    def reset(self):
        mas_state = self._stub.Reset(jobshop_pb2.Empty())
        return self._get_observation(mas_state)

    def render(self, mode='human'):
        self._stub.Render(jobshop_pb2.Empty())

    def close(self):
        self._channel.close()

    def seed(self, seed=None):
        self._stub.SetSeed(jobshop_pb2.Seed(seed=seed))

    # ---------------- Helper methods: -------------------------

    def _setup_env(self, config, clock_speed):
        msg = jobshop_pb2.SettingsMsg(config=config, clockSpeed=clock_speed)
        return self._stub.Setup(msg)

    def _get_state(self):
        return self._stub.GetState(jobshop_pb2.Empty())

    def _get_all_services(self):
        capabilities = []
        for machine in self._settings.machines:
            capabilities.append([cap.id for cap in machine.capabilities])
        return capabilities

    def _configure_action_space(self):
        action_space = []
        for machine in self._settings.machines:
            action_space.append(gym.spaces.Tuple(
                spaces=(gym.spaces.Discrete(len(machine.capabilities)),
                        gym.spaces.Discrete(len(self._operations)))))
        return action_space

    def _send_action_msg(self, mas_action):
        msgs = []
        for (cap_i, ope_i), cap in zip(mas_action, self._capabilities):
            msgs.append(jobshop_pb2.Action(capability=cap[cap_i],
                                           operation=self._operations[ope_i]))
        mas_msg = jobshop_pb2.MasAction(action=msgs)
        return self._stub.ApplyAction(mas_msg)

    def _configure_observation_space(self):
        return {'machines': self._get_machines(self._settings.machines),
                'products': self._get_product_templates(self._settings.products.products),
                'batchSize': self._settings.products.batchSize,
                'batchCount': self._settings.products.batchCount}

    def _get_observation(self, mas_state):
        return {'machines': self._get_machines(mas_state.machines),
                'products': self._get_products(mas_state.products)}

    @classmethod
    def _get_machines(cls, machines):
        machines_ret = []
        for machine in machines:
            machines_ret.append({
                'position': machine.position,
                'inputBuffer': {
                    'size': machine.inputBuffer.size,
                    'utilization': machine.inputBuffer.utilization,
                },
                'outputBuffer': {
                    'size': machine.outputBuffer.size,
                    'utilization': machine.outputBuffer.utilization,
                },
                'capability': machine.capability,
                'capabilities': [{
                    'id': cap.id,
                    'status': cap.status,
                    'setupTime': cap.setupTime,
                    'processingTime': cap.processingTime,
                    'maintenanceTime': cap.maintenanceTime,
                    'repairTime': cap.repairTime,
                    'failureRate': cap.failureRate,
                } for cap in machine.capabilities]
            })
        return machines_ret

    @classmethod
    def _get_products(cls, products):
        products_ret = []
        for product in products:
            products_ret.append({
                'id': product.id,
                'size': product.size,
                'step': product.step,
                'arrival': product.arrival,
                'duration': product.duration,
                'workflow': [cap for cap in product.workflow]
            })
        return products_ret

    @classmethod
    def _get_product_templates(cls, products):
        products_ret = []
        for product in products:
            products_ret.append({
                'id': product.id,
                'size': product.size,
                'duration': product.duration,
                'workflow': [cap for cap in product.workflow]
            })
        return products_ret

    @classmethod
    def _get_reward(cls, reward_msg):
        mas_reward = []
        for reward in reward_msg:
            mas_reward.append({
                'global_processed': reward.globalProcessed,
                'local_processed': reward.localProcessed,
                'makespan': reward.makespan,
                'flowTime': reward.flowTime,
                'tardiness': reward.tardiness,
                'lateness': reward.lateness,
                'utilization': reward.utilization
            })
        return mas_reward

    @classmethod
    def _get_info(cls, info_msg):
        return {'currentStep': info_msg.currentStep,
                'timeout': info_msg.timeout}
