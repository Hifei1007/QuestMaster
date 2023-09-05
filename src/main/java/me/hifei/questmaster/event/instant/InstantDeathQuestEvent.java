package me.hifei.questmaster.event.instant;

import me.hifei.questmaster.api.CoreManager;
import me.hifei.questmaster.api.event.InstantQuestEvent;
import me.hifei.questmaster.running.gsoncfg.event.SingleEventConfig;

public class InstantDeathQuestEvent extends InstantQuestEvent {
    public InstantDeathQuestEvent(SingleEventConfig config) {
        super(config);
    }

    @Override
    public void doChange() {
        CoreManager.game.runEachPlayer((player) -> player.damage(10000, player));
    }
}
