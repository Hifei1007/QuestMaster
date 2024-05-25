package me.hifei.questmaster.api;

import me.hifei.questmaster.api.event.NormalQuestEvent;
import me.hifei.questmaster.api.event.QuestEvent;
import me.hifei.questmaster.api.state.Stateful;
import me.hifei.questmaster.api.team.QuestTeam;
import me.hifei.questmaster.api.team.QuestTeamScoreboard;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public interface QuestGame extends Stateful  {
    List<NormalQuestEvent> getEvents();
    void appendEvent(QuestEvent event);
    int getGoal();
    List<Player> getPlayers();
    void checkScore(QuestTeam team);
}
