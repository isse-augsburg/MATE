import collections

import gym
import numpy as np


class DefaultObservationWrapper(gym.ObservationWrapper):
    """
    The default values used by the neural networks.
    Includes communication between agents (the available services)
    and product composition.
    """
    def __init__(self, env):
        super().__init__(env)
        self._settings = env._settings
        self._all_operations = set()
        self._capa_list = []
        self.observation_space = self._configure_observation_space()

    def _configure_observation_space(self):
        all_capas = 0
        single_capas = []
        for obs in self.env.observation_space['machines']:
            all_capas += len(obs['capabilities'])
            single_capas.append(len(obs['capabilities']))
            self._capa_list.append(sorted([cap['id'] for cap in obs['capabilities']]))
        self._all_operations = self._get_product_composition(self.env.observation_space['products'])
        observation_space = []
        for obs, capas in zip(self.env.observation_space['machines'], single_capas):
            observation_space.append(
                gym.spaces.Dict({
                    'available_capabilities': gym.spaces.MultiDiscrete([2] * all_capas),
                    'current_capability': gym.spaces.MultiDiscrete([2] * capas),
                    'product_composition': gym.spaces.Box(low=0.0, high=1.0,
                                                          shape=(len(self._all_operations),),
                                                          dtype=np.float32),
                }))
        return observation_space

    def observation(self, observation):
        obs_r = []
        avail_capas = self._calc_avail_capas(observation)
        product_comp = self._calc_product_compo(observation)
        for current in self._get_current_capas(observation['machines']):
            obs_r.append(collections.OrderedDict({
                'available_services': avail_capas,
                'current_service': current,
                'product_composition': product_comp,
            }))
        return obs_r

    def _get_product_composition(self, products):
        needed_capas = set()
        for product in products:
            for capa in product['workflow']:
                needed_capas.add(capa)
        return sorted(needed_capas)

    def _get_current_capas(self, machines):
        current_capas = []
        for capas, machine in zip(self._capa_list, machines):
            capa_states = []
            for c in capas:
                state = 1 if c == machine['capability'] else 0
                capa_states.append(state)
            current_capas.append(capa_states)
        return current_capas

    def _calc_avail_capas(self, obs):
        avail_capas = []
        for machine in obs['machines']:
            for capa in machine['capabilities']:
                avail_capas.append(1 if capa['status'] else 0)
        return avail_capas

    def _calc_product_compo(self, obs):
        counter = {s: 0 for s in self._all_operations}
        workflows = [w['workflow'] for w in obs['products']]
        if len(workflows) > 0:
            counter.update(collections.Counter(np.concatenate(workflows)))
        sorted_values = [i[1] for i in sorted(counter.items())]
        if sum(sorted_values) == 0:
            return np.array([i for i in sorted_values], dtype=np.float32)
        return np.array([i / sum(sorted_values) for i in sorted_values], dtype=np.float32)
