package me.hifei.questmaster.event.instant;

import me.hifei.questmaster.api.CoreManager;
import me.hifei.questmaster.api.event.InstantQuestEvent;
import me.hifei.questmaster.running.gsoncfg.event.SingleEventConfig;

@SuppressWarnings("unused")
public class HungerClearQuestEvent extends InstantQuestEvent {
    public HungerClearQuestEvent(SingleEventConfig config) {
        super(config);
    }

    @Override
    public void doChange() {
        CoreManager.manager.runEachPlayer((player) -> player.setFoodLevel(0));
    }
}
