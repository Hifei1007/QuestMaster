package me.hifei.questmaster.manager;

import me.hifei.questmaster.api.QuestGame;
import me.hifei.questmaster.api.QuestManager;
import me.hifei.questmaster.api.event.QuestEvent;
import me.hifei.questmaster.api.quest.Quest;
import me.hifei.questmaster.api.quest.QuestInterface;
import me.hifei.questmaster.api.quest.QuestType;
import me.hifei.questmaster.api.team.QuestTeam;
import me.hifei.questmaster.api.team.QuestTeamScoreboard;
import me.hifei.questmaster.running.gsoncfg.event.SingleEventConfig;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class CQuestManager implements QuestManager {
    private record WeightedQuestType(Class<? extends QuestType> questType, int weight) {
    }

    private final @NotNull List<QuestTeam> teams;
    private final @NotNull List<WeightedQuestType> questTypeList;
    private final @NotNull List<SingleEventConfig> eventConfigs;

    public CQuestManager () {
        teams = new LinkedList<>();
        questTypeList = new ArrayList<>();
        eventConfigs = new ArrayList<>();
    }

    @Override
    public @NotNull Quest createQuest(@NotNull QuestType qt, @NotNull QuestTeam team, QuestInterface... interfaces) {
        return new CQuest(qt, team, interfaces);
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
    public @NotNull List<QuestTeam> getTeams() {
        return teams;
    }

    @Override
    public @NotNull QuestGame createGame(@NotNull List<QuestTeam> teams, int goal) {
        return new CQuestGame(teams, goal);
    }

    private @NotNull QuestType createTypeByClass(Class<? extends QuestType> qtClass) {
        try {
            Method method = qtClass.getMethod("create");
            return (QuestType) method.invoke(null);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public @NotNull QuestType createType() {
        int totalWeight = 0;
        for (WeightedQuestType qt : questTypeList) {
            totalWeight += qt.weight;
        }
        int r = new Random().nextInt(totalWeight);
        for (WeightedQuestType qt : questTypeList) {
            if (r < qt.weight) return createTypeByClass(qt.questType);
            r -= qt.weight;
        }
        throw new RuntimeException("Can't create QuestType object");
    }

    @Override
    public @NotNull QuestEvent createEvent() {
        int totalWeight = 0;
        for (SingleEventConfig cfg : eventConfigs) {
            totalWeight += cfg.weight;
        }
        int r = new Random().nextInt(totalWeight);
        for (SingleEventConfig cfg : eventConfigs) {
            if (r < cfg.weight) return cfg.build();
            r -= cfg.weight;
        }
        throw new RuntimeException("Can't create QuestType object");
    }

    @Override
    public void registerEvent(SingleEventConfig config) {
        eventConfigs.add(config);
    }

    @Override
    public void registerType(Class<? extends QuestType> questTypeClass, int weight) {
        questTypeList.add(new WeightedQuestType(questTypeClass, weight));
    }

    @Override
    public @NotNull QuestTeamScoreboard createScoreboard(QuestGame game, QuestTeam team) {
        return new CQuestTeamScoreboard(game, team);
    }
}
