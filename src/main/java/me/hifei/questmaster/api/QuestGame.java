package me.hifei.questmaster.api;

import me.hifei.questmaster.api.state.Stateful;
import me.hifei.questmaster.api.team.QuestTeam;
import me.hifei.questmaster.api.team.QuestTeamScoreboard;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public interface QuestGame extends Stateful, ConfigurationSerializable {
    List<QuestTeam> getTeams();
    void runEachTeam(Consumer<QuestTeam> consumer);
    @NotNull Map<QuestTeam, QuestTeamScoreboard> getScoreboardMapping();
    int getGoal();
    List<Player> getPlayers();
    void runEachPlayer(Consumer<Player> consumer);
    void checkScore(QuestTeam team);
}
