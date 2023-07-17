package me.hifei.questmaster.api.quest;

import me.hifei.questmaster.api.state.Stateful;
import me.hifei.questmaster.api.team.QuestTeam;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface Quest extends Stateful, ConfigurationSerializable {
    @NotNull Timer getTimer();
    @NotNull QuestType getType();
    @NotNull QuestTeam getTeam();
    @NotNull Difficult getDifficult();
    double getDifficultValue();
    @NotNull Reward getReward();
    double getProgress();
    boolean isCompleted();
    @NotNull List<QuestInterface> interfaces();
    void complete();
    void timeUp();
    @NotNull String getName();
    int getTotalCount();
    int getCurrentCount();
    @NotNull ConfigurationSection getItem(boolean addClickText);
    @NotNull ConfigurationSection getItem();
    void openPanel(@NotNull Player player);
    String getProcessBar();
}
