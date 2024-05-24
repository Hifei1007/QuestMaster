package me.hifei.questmaster.api.quest;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface QuestType{
    @SuppressWarnings("unused")
    @NotNull Quest quest();

    void sendQuestObject(Quest q);

    boolean isCompleted();

    double progress();

    @NotNull Reward baseReward();

    int time();

    @NotNull Difficult difficult();

    double difficultValue();

    @NotNull String name();

    int totalCount();

    int currentCount();

    @NotNull ConfigurationSection item();

    void openPanel(@NotNull Player player);
}
