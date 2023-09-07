package me.hifei.questmaster.event.instant;

import me.hifei.questmaster.api.CoreManager;
import me.hifei.questmaster.api.event.InstantQuestEvent;
import me.hifei.questmaster.running.gsoncfg.event.SingleEventConfig;

@SuppressWarnings("unused")
public class ClearLocationQuestEvent extends InstantQuestEvent {
    public ClearLocationQuestEvent(SingleEventConfig config) {
        super(config);
    }

    @Override
    public void doChange() {
        CoreManager.game.runEachTeam((team) -> team.locations().clear());
    }
}
