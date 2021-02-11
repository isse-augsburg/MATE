import gym


class DefaultActionWrapper(gym.ActionWrapper):
    """
    Remove the repair operation from the action tuple.
    """
    def __init__(self, env):
        super().__init__(env)
        self._settings = env._settings
        self.action_space = self._configure_action_space()

    def action(self, action):
        return [(a, 0) for a in action]

    def _configure_action_space(self):
        action_space = []
        for machine in self._settings.machines:
            action_space.append(gym.spaces.Discrete(len(machine.capabilities)))
        return action_space
