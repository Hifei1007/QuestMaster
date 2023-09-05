package me.hifei.questmaster.event.normal;

import me.hifei.questmaster.api.bukkitevent.TeamRewardedByQuestEvent;
import me.hifei.questmaster.api.event.NormalQuestEvent;
import me.hifei.questmaster.api.quest.Reward;
import me.hifei.questmaster.running.gsoncfg.event.SingleEventConfig;
import me.hifei.questmaster.running.gsoncfg.rolling.DoubleBoundConfig;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ModifyRewardQuestEvent extends NormalQuestEvent {
    public double mul;

    public ModifyRewardQuestEvent(SingleEventConfig config) {
        super(config);
    }

    private enum Target {
        SCORE,
        COIN,
        POINT
    }

    private static class Settings {
        public Target target;
        public DoubleBoundConfig bound;
    }

    public String getName() {
        return super.getName().formatted(mul * 100, "%");
    }

    @Override
    protected void preprocess() {
        mul = loadSettings(Settings.class).bound.next();
    }

    public Listener getListener() {
        return new Listener() {
            @EventHandler
            public void onRewarded(TeamRewardedByQuestEvent event) {
                Target target = loadSettings(Settings.class).target;
                Reward reward = event.getReward();
                if (target == Target.SCORE)
                    reward = new Reward(reward.score() * mul, reward.point(), reward.coin(), reward.time());
                if (target == Target.POINT)
                    reward = new Reward(reward.score(), reward.point() * mul, reward.coin(), reward.time());
                if (target == Target.COIN)
                    reward = new Reward(reward.score(), reward.point(), reward.coin() * mul, reward.time());
                event.setReward(reward);
            }
        };
    }
}
