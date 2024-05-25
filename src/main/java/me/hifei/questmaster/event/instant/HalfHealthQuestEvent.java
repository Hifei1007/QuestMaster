package me.hifei.questmaster.event.instant;

import me.hifei.questmaster.api.CoreManager;
import me.hifei.questmaster.api.event.InstantQuestEvent;
import me.hifei.questmaster.running.gsoncfg.event.SingleEventConfig;

@SuppressWarnings("unused")
public class HalfHealthQuestEvent extends InstantQuestEvent {
    public HalfHealthQuestEvent(SingleEventConfig config) {
        super(config);
    }

    @Override
    public void doChange() {
        CoreManager.manager.runEachPlayer((player) -> {
            if (player.getHealth() > 0.2) player.setHealth(0.1);
        });
    }
}
