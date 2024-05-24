package me.hifei.questmaster.api.team;

import me.hifei.questmaster.api.quest.Quest;
import me.hifei.questmaster.shop.Upgrade;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public interface QuestTeam {
    @NotNull List<Quest> getQuests();
    @NotNull Map<String, Upgrade> getUpgrades();

    @NotNull String name();

    @NotNull List<Player> members();

    @NotNull List<Location> locations();

    @SuppressWarnings("unused")
    @NotNull ChatColor color();

    void addPlayer(@NotNull Player player);

    void removePlayer(@NotNull Player player);

    void makeNewQuest();

    void clear();

    void init();

    void teamBroadcast(@NotNull String message);

    double score();

    double point();

    double coin();

    void setScore(double score);

    void setPoint(double point);

    void setCoin(double coin);
}
