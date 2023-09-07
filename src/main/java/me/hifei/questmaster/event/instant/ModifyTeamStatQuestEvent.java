package me.hifei.questmaster.event.instant;

import me.hifei.questmaster.api.CoreManager;
import me.hifei.questmaster.api.event.InstantQuestEvent;
import me.hifei.questmaster.running.gsoncfg.event.SingleEventConfig;
import me.hifei.questmaster.running.gsoncfg.rolling.DoubleBoundConfig;

@SuppressWarnings("unused")
public class ModifyTeamStatQuestEvent extends InstantQuestEvent {
    public double mul;

    public ModifyTeamStatQuestEvent(SingleEventConfig config) {
        super(config);
    }

    @Override
    protected void preprocess() {
        mul = loadSettings(Settings.class).bound.next();
    }

    public String getName() {
        return super.getName().formatted(mul * 100, "%");
    }

    private enum Target {
        COIN,
        POINT
    }

    private static class Settings {
        public Target target;
        public DoubleBoundConfig bound;
    }

    @Override
    public void doChange() {
        Target target = loadSettings(Settings.class).target;
        if (target == Target.COIN) {
            CoreManager.game.runEachTeam((team) -> team.setCoin(team.coin() * mul));
        } else {
            CoreManager.game.runEachTeam((team) -> team.setPoint(team.point() * mul));
        }
    }
}
