package me.hifei.questmaster.api.quest;

import me.hifei.questmaster.api.state.Stateful;
import me.hifei.questmaster.api.team.QuestTeam;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface Quest extends Stateful{
    @NotNull Timer getTimer();
    @NotNull QuestType getType();
    @NotNull QuestTeam getTeam();
    @SuppressWarnings("unused")
    @NotNull Difficult getDifficult();
    @SuppressWarnings("unused")
    double getDifficultValue();
    @NotNull Reward getReward();
    double getProgress();
    @SuppressWarnings("unused")
    boolean isCompleted();
    @SuppressWarnings("unused")
    void complete();
    void timeUp();
    @NotNull String getName();
    int getTotalCount();
    int getCurrentCount();
    @NotNull ConfigurationSection getItem(boolean addClickText);
    @NotNull ConfigurationSection getItem();
    void openPanel(@NotNull Player player);
    String getProcessBar();
    double getCoinPunish();
    double getPointPunish();
}
