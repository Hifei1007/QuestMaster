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
    List<QuestTeam> getTeams();
    void runEachTeam(Consumer<QuestTeam> consumer);
    @NotNull Map<QuestTeam, QuestTeamScoreboard> getScoreboardMapping();
    int getGoal();
    List<Player> getPlayers();
    void runEachPlayer(Consumer<Player> consumer);
    void checkScore(QuestTeam team);
}
