package me.hifei.questmaster.api;

import me.hifei.questmaster.api.event.NormalQuestEvent;
import me.hifei.questmaster.api.event.QuestEvent;
import me.hifei.questmaster.api.state.Stateful;
import me.hifei.questmaster.api.team.QuestTeam;
import org.bukkit.entity.Player;

import java.util.List;

public interface QuestGame extends Stateful  {
    List<NormalQuestEvent> getEvents();
    void appendEvent(QuestEvent event);
    int getGoal();
    List<Player> getPlayers();
    void checkScore(QuestTeam team);
}
