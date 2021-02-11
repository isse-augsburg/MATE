import gym


class MakespanRewardWrapper(gym.RewardWrapper):
    """
    Return the time needed to complete all jobs.
    """
    def __init__(self, env):
        super().__init__(env)
        self._settings = env._settings

    def reward(self, reward):
        return [-r['makespan'] for r in reward]