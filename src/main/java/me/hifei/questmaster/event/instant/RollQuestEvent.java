package me.hifei.questmaster.event.instant;

import me.hifei.questmaster.api.CoreManager;
import me.hifei.questmaster.api.event.InstantQuestEvent;
import me.hifei.questmaster.api.state.Stateful;
import me.hifei.questmaster.manager.CQuestGame;
import me.hifei.questmaster.running.gsoncfg.event.SingleEventConfig;

@SuppressWarnings("unused")
public class RollQuestEvent extends InstantQuestEvent {
    public RollQuestEvent(SingleEventConfig config) {
        super(config);
    }

    @Override
    public void doChange() {
        CoreManager.manager.runEachTeam((team) -> {
            team.getQuests().forEach(Stateful::drop);
            team.getQuests().clear();
            team.makeNewQuest();
            team.makeNewQuest();
            team.makeNewQuest();
        });
    }
}
