package me.hifei.questmaster.manager;

import me.hifei.questmaster.api.QuestGame;
import me.hifei.questmaster.api.QuestManager;
import me.hifei.questmaster.api.quest.Quest;
import me.hifei.questmaster.api.quest.QuestInterface;
import me.hifei.questmaster.api.quest.QuestType;
import me.hifei.questmaster.api.team.QuestTeam;
import me.hifei.questmaster.api.team.QuestTeamScoreboard;
import me.hifei.questmaster.quest.questcollectitem.QuestTypeCollectItem;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;

public class CQuestManager implements QuestManager {
    private final @NotNull List<QuestTeam> teams;

    public CQuestManager () {
        teams = new LinkedList<>();
    }

    @Override
    public @NotNull Quest createQuest(@NotNull QuestType qt, @NotNull QuestTeam team, QuestInterface... interfaces) {
        return new CQuest(qt, team, interfaces);
    }

    @Override
    public @NotNull Quest createQuest(@NotNull QuestType qt, @NotNull QuestTeam team) {
        return new CQuest(qt, team);
    }

    @Override
    public @NotNull QuestTeam createTeam(@NotNull String name, @NotNull ChatColor color) {
        QuestTeam team = new CQuestTeam(name, color);
        teams.add(team);
        return team;
    }

    @Override
    public void setTeam(@NotNull Player player, @NotNull QuestTeam team) {
        for (QuestTeam t : teams) {
            if (t != team) t.removePlayer(player);
        }
        team.addPlayer(player);
    }

    @Override
    public void clearTeam(@NotNull Player player) {
        for (QuestTeam t : teams) {
            t.removePlayer(player);
        }
    }

    @Override
    public boolean hasTeam(@NotNull Player player) {
        return getTeam(player) != null;
    }

    @Override
    public @Nullable QuestTeam getTeam(@NotNull Player player) {
        for (QuestTeam t : teams) {
            if (t.members().contains(player)) return t;
        }
        return null;
    }

    @Override
    public @Nullable QuestTeam getTeam(@NotNull String string) {
        for (QuestTeam team : teams) {
            if (team.name().equals(string)) return team;
        }
        return null;
    }

    @Override
    public @NotNull List<QuestTeam> getTeams() {
        return teams;
    }

    @Override
    public @NotNull QuestGame createGame(@NotNull List<QuestTeam> teams, int goal) {
        return new CQuestGame(teams, goal);
    }

    @Override
    public @NotNull QuestType createType() {
        return QuestTypeCollectItem.create();
    }

    @Override
    public @NotNull QuestTeamScoreboard createScoreboard(QuestGame game, QuestTeam team) {
        return new CQuestTeamScoreboard(game, team);
    }
}
