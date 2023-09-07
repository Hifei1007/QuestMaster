package me.hifei.questmaster.event.instant;

import me.hifei.questmaster.api.CoreManager;
import me.hifei.questmaster.api.event.InstantQuestEvent;
import me.hifei.questmaster.running.gsoncfg.event.SingleEventConfig;
import me.hifei.questmaster.running.gsoncfg.rolling.IntegerBoundConfig;

@SuppressWarnings("unused")
public class AppendEventQuestEvent extends InstantQuestEvent {
    public int questCount;

    public AppendEventQuestEvent(SingleEventConfig config) {
        super(config);
    }

    @Override
    protected void preprocess() {
        questCount = loadSettings(IntegerBoundConfig.class).next();
    }

    public String getName() {
        return super.getName().formatted(questCount);
    }

    @Override
    public void doChange() {
        for (int i = 1; i <= questCount; i++) {
            CoreManager.game.appendEvent(CoreManager.manager.createEvent());
        }
    }
}
