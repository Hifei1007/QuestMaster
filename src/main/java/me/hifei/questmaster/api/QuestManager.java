package me.hifei.questmaster.api;

import me.hifei.questmaster.CoreManager;
import me.hifei.questmaster.api.quest.Quest;
import me.hifei.questmaster.api.quest.QuestInterface;
import me.hifei.questmaster.api.quest.QuestType;
import me.hifei.questmaster.api.team.QuestTeam;
import me.hifei.questmaster.api.team.QuestTeamScoreboard;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface QuestManager {
    static @NotNull QuestManager instance() {
        return CoreManager.manager;
    }

    @NotNull Quest createQuest(@NotNull QuestType qt, @NotNull QuestTeam team);

    @NotNull Quest createQuest(@NotNull QuestType qt, @NotNull QuestTeam team, @NotNull QuestInterface... interfaces);

    @NotNull QuestTeam createTeam(@NotNull String name, @NotNull ChatColor color);

    void setTeam(@NotNull Player player, @NotNull QuestTeam team);

    void clearTeam(@NotNull Player player);

    boolean hasTeam(@NotNull Player player);

    @Nullable QuestTeam getTeam(@NotNull Player player);

    @Nullable QuestTeam getTeam(@NotNull String string);

    @NotNull List<QuestTeam> getTeams();

    @NotNull QuestGame createGame(@NotNull List<QuestTeam> teams, int goal);

    @NotNull QuestType createType();
    @NotNull QuestTeamScoreboard createScoreboard(QuestGame game, QuestTeam team);
}
