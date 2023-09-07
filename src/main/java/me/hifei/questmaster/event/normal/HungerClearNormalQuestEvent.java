package me.hifei.questmaster.event.normal;

import me.hifei.questmaster.api.CoreManager;
import me.hifei.questmaster.api.event.NormalQuestEvent;
import me.hifei.questmaster.running.gsoncfg.event.SingleEventConfig;

@SuppressWarnings("unused")
public class HungerClearNormalQuestEvent extends NormalQuestEvent {
    public HungerClearNormalQuestEvent(SingleEventConfig config) {
        super(config);
    }

    protected void tick() {
        super.tick();
        CoreManager.game.runEachPlayer((player) -> player.setFoodLevel(0));
    }
}
