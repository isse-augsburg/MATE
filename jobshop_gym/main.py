from environment.mate import MATE
from environment.observation_wrappers import DefaultObservationWrapper
from environment.reward_wrappers import MakespanRewardWrapper
from environment.action_wrappers import DefaultActionWrapper




def demo(iter):
    # carefull: Jadex seems to have problems when clockspeed is too high,
    # therfore it will be clipped at around 150—the user can experiment with different values.
    env = MATE(config='triple.toml', clockspeed=100, port=50051)
    # optional: use wrappers to preprocess the state/action/reward space
    #env = DefaultObservationWrapper(env)
    #env = MakespanRewardWrapper(env)
    #env = DefaultActionWrapper(env)
    env.seed(42)
    env.render()

    # Seed the action-space, because of sampling
    for i,e in enumerate(env.action_space):
        e.seed(i+1)

    for _ in range(iter):
        mas_state = env.reset()
        while True:
            # sample one action for each agent
            mas_action = []
            for space in env.action_space:
                mas_action.append(space.sample())
            # apply the multi-agent action to the environment
            mas_state, mas_reward, done, info = env.step(mas_action)
            if done:
                break


if __name__ == '__main__':
    demo(5)
