package me.hifei.questmaster.api.quest;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface QuestType extends ConfigurationSerializable {
    @NotNull Quest quest();

    void sendQuestObject(Quest q);

    boolean isCompleted();

    double progress();

    @NotNull Reward baseReward();

    int time();

    @NotNull Difficult difficult();

    double difficultValue();

    @NotNull List<QuestInterface> interfaces();

    @NotNull String name();

    int totalCount();

    int currentCount();

    @NotNull ConfigurationSection item();

    void openPanel(@NotNull Player player);
}
