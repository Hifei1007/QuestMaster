package me.hifei.questmaster.manager;

import me.hifei.questmaster.CoreManager;
import me.hifei.questmaster.api.quest.Quest;
import me.hifei.questmaster.api.team.QuestTeam;
import me.hifei.questmaster.running.config.Message;
import me.hifei.questmaster.shop.DamageUpgrade;
import me.hifei.questmaster.shop.DefenseUpgrade;
import me.hifei.questmaster.shop.Upgrade;
import me.hifei.questmaster.shop.teamchest.TeamChestUpgrade;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class CQuestTeam implements QuestTeam {
    private double score;
    private double point;
    private double coin;
    private final @NotNull List<Quest> quests;
    private final @NotNull Map<String, Upgrade> upgrades;
    private final @NotNull String name;
    private final @NotNull List<Player> members;
    private final @NotNull ChatColor color;
    private final @NotNull List<Location> locations;

    public int hashCode() {
        return name.hashCode();
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof QuestTeam)) return false;
        return ((QuestTeam) obj).name().equals(this.name);
    }

    CQuestTeam(@NotNull String name, @NotNull ChatColor color) {
        quests = new LinkedList<>();
        members = new LinkedList<>();
        locations = new LinkedList<>();
        upgrades = new HashMap<>();
        coin = 0;
        point = 0;
        score = 0;
        this.name = name;
        this.color = color;
    }

    public CQuestTeam(@NotNull Map<String, Object> serializer) {
        quests = new LinkedList<>();
        members = new LinkedList<>();
        locations = new LinkedList<>();
        upgrades = new HashMap<>();
        coin = (double) serializer.get("coin");
        point = (double) serializer.get("point");
        score = (double) serializer.get("score");
        name = (String) serializer.get("name");
        color = Objects.requireNonNull(ChatColor.getByChar((String) serializer.get("color")));
        for (Object obj : (List<?>) serializer.get("quests")) {
            quests.add((Quest) obj);
        }
        for (Object member : (List<?>) serializer.get("members")) {
            String name = (String) member;
            members.add(Bukkit.getPlayerExact(name));
        }
        CoreManager.manager.getTeams().add(this);
    }

    private void initUpgrade() {
        upgrades.clear();
        upgrades.put("defense_magic", new DefenseUpgrade(DefenseUpgrade.DamageType.MAGIC, this));
        upgrades.put("defense_fire", new DefenseUpgrade(DefenseUpgrade.DamageType.FIRE, this));
        upgrades.put("defense_projectile", new DefenseUpgrade(DefenseUpgrade.DamageType.PROJECTILE, this));
        upgrades.put("defense_explosion", new DefenseUpgrade(DefenseUpgrade.DamageType.EXPLOSION, this));
        upgrades.put("defense_fall", new DefenseUpgrade(DefenseUpgrade.DamageType.FALL, this));
        upgrades.put("defense_weapon", new DefenseUpgrade(DefenseUpgrade.DamageType.WEAPON, this));
        upgrades.put("teamchest_1", new TeamChestUpgrade(1));
        upgrades.put("teamchest_2", new TeamChestUpgrade(2));
        upgrades.put("teamchest_3", new TeamChestUpgrade(3));
        upgrades.put("damage_magic", new DamageUpgrade(DamageUpgrade.DamageType.MAGIC, this));
        upgrades.put("damage_projectile", new DamageUpgrade(DamageUpgrade.DamageType.PROJECTILE, this));
        upgrades.put("damage_explosion", new DamageUpgrade(DamageUpgrade.DamageType.EXPLOSION, this));
        upgrades.put("damage_weapon", new DamageUpgrade(DamageUpgrade.DamageType.WEAPON, this));
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> serializer = new HashMap<>();
        serializer.put("coin", coin);
        serializer.put("point", point);
        serializer.put("score", score);
        serializer.put("name", name);
        serializer.put("color", String.valueOf(color.getChar()));
        serializer.put("quests", quests);
        List<String> nameList = new ArrayList<>();
        for (Player player : members) nameList.add(player.getName());
        serializer.put("members", nameList);
        return serializer;
    }

    @Override
    public @NotNull List<Quest> getQuests() {
        return quests;
    }

    @Override
    public @NotNull Map<String, Upgrade> getUpgrades() {
        return upgrades;
    }

    @Override
    public @NotNull String name() {
        return name;
    }

    @Override
    public @NotNull List<Player> members() {
        return members;
    }

    @Override
    public @NotNull List<Location> locations() {
        return locations;
    }

    @Override
    public @NotNull ChatColor color() {
        return color;
    }

    @Override
    public void addPlayer(@NotNull Player player) {
        members.add(player);
        player.setDisplayName(color + player.getName() + ChatColor.RESET);
        player.setPlayerListName(color + player.getName() + ChatColor.RESET);
        teamBroadcast(Message.get("team.join", player.getDisplayName()));
        if (CoreManager.isGameStart()) player.setScoreboard(CoreManager.game.getScoreboardMapping().get(this).getBukkit());
    }

    @Override
    public void removePlayer(@NotNull Player player) {
        members.remove(player);
        teamBroadcast(Message.get("team.leave", player.getDisplayName()));
        player.setDisplayName(player.getName());
        player.setPlayerListName(player.getName());
        player.setScoreboard(CoreManager.emptyScoreboard);
    }

    @Override
    public void clear() {
        List<Player> m = List.of(members.toArray(new Player[0]));
        for (Player player : m) removePlayer(player);
    }

    @Override
    public void init() {
        score = 0;
        point = 0;
        coin = 0;
        quests.clear();
        locations.clear();
        initUpgrade();
    }

    @Override
    public void teamBroadcast(@NotNull String message) {
        for (Player player : members()) player.sendMessage(message);
    }

    @Override
    public double score() {
        return score;
    }

    @Override
    public double point() {
        return point;
    }

    @Override
    public double coin() {
        return coin;
    }

    @Override
    public void setScore(double score) {
        this.score = score;
    }

    @Override
    public void setPoint(double point) {
        this.point = point;
    }

    @Override
    public void setCoin(double coin) {
        this.coin = coin;
    }
}
