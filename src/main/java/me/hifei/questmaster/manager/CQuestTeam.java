package me.hifei.questmaster.manager;

import eu.endercentral.crazy_advancements.advancement.AdvancementDisplay;
import eu.endercentral.crazy_advancements.advancement.ToastNotification;
import me.hifei.questmaster.api.CoreManager;
import me.hifei.questmaster.api.quest.Quest;
import me.hifei.questmaster.api.state.State;
import me.hifei.questmaster.api.team.QuestTeam;
import me.hifei.questmaster.api.team.QuestTeamScoreboard;
import me.hifei.questmaster.running.config.Message;
import me.hifei.questmaster.shop.DamageUpgrade;
import me.hifei.questmaster.shop.DefenseUpgrade;
import me.hifei.questmaster.shop.Upgrade;
import me.hifei.questmaster.shop.teamchest.TeamChestUpgrade;
import me.hifei.questmaster.tools.ActionTool;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
    private QuestTeamScoreboard scoreboard;

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

    @SuppressWarnings("unused")
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
        if (CoreManager.isGameStart()) player.setScoreboard(getScoreboard().getBukkit());
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
    public void makeNewQuest() {
        Quest quest = CoreManager.manager.createQuest(CoreManager.manager.createType(), this);
        quest.startup();
        getQuests().add(quest);
        CoreManager.manager.runEachTeam(t -> {
            if (t == this) {
                for (Player player : t.members()) {
                    player.spigot().sendMessage(
                            Message.getComponent("game.task.get1", quest.getName()),
                            ActionTool.addAction(Message.getComponent("game.task.get.action"), sender -> {
                                        if (!(sender instanceof Player p)) return;
                                        if (quest.getState() == State.DROP) return;
                                        quest.openPanel(p);
                                    }));
                }
                t.teamToast(
                        Material.valueOf(Objects.requireNonNull(quest.getItem().getString("material")).toUpperCase()),
                        Message.get("quest.toast.create.default", quest.getName()), AdvancementDisplay.AdvancementFrame.TASK);
            } else {
                t.teamBroadcast(Message.get("game.task.get2", name(), quest.getName()));
                t.teamToast(
                        Material.valueOf(Objects.requireNonNull(quest.getItem().getString("material")).toUpperCase()),
                        Message.get("quest.toast.create.other_team", t.name(), quest.getName()),
                        AdvancementDisplay.AdvancementFrame.TASK);
            }
        });
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
    public void teamToast(@NotNull ItemStack icon, @NotNull String description, AdvancementDisplay.@NotNull AdvancementFrame frame) {
        ToastNotification notification = new ToastNotification(icon, description, frame);
        for (Player player : members()) notification.send(player);
    }

    @Override
    public void teamToast(@NotNull Material icon, @NotNull String description, AdvancementDisplay.@NotNull AdvancementFrame frame) {
        teamToast(new ItemStack(icon), description, frame);
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

    @Override
    public QuestTeamScoreboard getScoreboard() {
        return scoreboard;
    }

    @Override
    public void setScoreboard(QuestTeamScoreboard scoreboard) {
        this.scoreboard = scoreboard;
    }
}
