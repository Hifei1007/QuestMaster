package me.hifei.questmaster.manager;

import me.hifei.questmaster.CoreManager;
import me.hifei.questmaster.api.QuestGame;
import me.hifei.questmaster.api.quest.Quest;
import me.hifei.questmaster.api.state.State;
import me.hifei.questmaster.api.state.Stateful;
import me.hifei.questmaster.api.team.QuestTeam;
import me.hifei.questmaster.api.team.QuestTeamScoreboard;
import me.hifei.questmaster.running.config.Message;
import me.hifei.questmaster.shop.Upgrade;
import me.hifei.questmaster.tools.ActionTool;
import me.hifei.questmaster.ui.core.DynamicPanel;
import me.hifei.questmaster.ui.core.UIManager;
import me.hifei.questmaster.ui.dynamic.DPRootNotStarted;
import me.rockyhawk.commandpanels.openpanelsmanager.PanelPosition;
import org.bukkit.*;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;

public class CQuestGame implements QuestGame {
    private final List<QuestTeam> teams;
    private final int goal;
    private @NotNull State state = State.WAIT;
    private final Map<QuestTeam, QuestTeamScoreboard> scoreboardMap = new HashMap<>();

    CQuestGame(List<QuestTeam> teams, int goal) {
        this.teams = teams;
        this.goal = goal;
        for (QuestTeam team : teams) {
            scoreboardMap.put(team, CoreManager.manager.createScoreboard(this, team));
        }
    }

    public CQuestGame(@NotNull Map<String, Object> serialized) {
        teams = new ArrayList<>();
        List<?> list = (List<?>) (serialized.get("teams"));
        for (Object s : list) {
            String str = (String) s;
            teams.add(CoreManager.manager.getTeam(str));
        }
        goal = (int) serialized.get("goal");
        for (QuestTeam team : teams) {
            scoreboardMap.put(team, CoreManager.manager.createScoreboard(this, team));
        }
        state = State.STARTUP;
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> serializer = new HashMap<>();
        List<String> nameList = new ArrayList<>();
        runEachTeam(t -> nameList.add(t.name()));
        serializer.put("teams", nameList);
        serializer.put("goal", goal);
        return serializer;
    }

    @Override
    public List<QuestTeam> getTeams() {
        return teams;
    }

    @Override
    public void runEachTeam(@NotNull Consumer<QuestTeam> consumer) {
        for (QuestTeam team : getTeams()) consumer.accept(team);
    }

    @Override
    public @NotNull Map<QuestTeam, QuestTeamScoreboard> getScoreboardMapping() {
        return scoreboardMap;
    }


    @Override
    public int getGoal() {
        return goal;
    }

    @Override
    public @NotNull List<Player> getPlayers() {
        List<Player> list = new ArrayList<>();
        runEachTeam((t) -> list.addAll(t.members()));
        return list;
    }

    @Override
    public void runEachPlayer(@NotNull Consumer<Player> consumer) {
        for (Player player : getPlayers()) consumer.accept(player);
    }

    @Override
    public void checkScore(@NotNull QuestTeam team) {
        if (team.score() >= goal) {
            runEachPlayer(player -> {
                player.sendMessage(Message.get("game.victory.message", team.name()));
                player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1, 1);
                player.sendTitle(
                        Message.get("game.victory.title", team.name()),
                        Message.get("game.victory.subtitle", team.name(), team.score()),
                        10, 70, 20
                );
            });
            this.drop();
        }
    }

    @Override
    public @NotNull State getState() {
        return state;
    }

    public void modifyPlayer(Player player) {
        for (PotionEffectType type : PotionEffectType.values()) {
            player.removePotionEffect(type);
        }
        player.getInventory().clear();
        player.getInventory().addItem(new ItemStack(Material.BREAD, 8),
                new ItemStack(Material.STONE_AXE),
                new ItemStack(Material.STONE_PICKAXE),
                new ItemStack(Material.STONE_SHOVEL));
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setExp(0);
        player.setLevel(0);
        Bukkit.advancementIterator().forEachRemaining(advancement -> {
            AdvancementProgress progress = player.getAdvancementProgress(advancement);
            for (String string : progress.getAwardedCriteria()) {
                progress.revokeCriteria(string);
            }
        });
        player.setScoreboard(scoreboardMap.get(CoreManager.manager.getTeam(player)).getBukkit());
        player.playSound(player, Sound.ENTITY_ENDER_DRAGON_GROWL, 1, 1);
        player.sendTitle(
                Message.get("game.start.title"),
                Message.get("game.start.subtitle", CoreManager.game.getGoal(), Objects.requireNonNull(CoreManager.manager.getTeam(player)).name()), 10, 70, 20);
        player.addPotionEffects(List.of(
                new PotionEffect(PotionEffectType.SLOW_FALLING, 40 * 20, 0, true, false, true),
                new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 90 * 20, 4, true, false, true),
                new PotionEffect(PotionEffectType.BLINDNESS, 10 * 20, 0, true, false, true)
        ));
    }

    @Override
    public void startup() {
        if (state != State.WAIT) return;

        Bukkit.broadcastMessage(Message.get("game.starting.message"));

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!CoreManager.manager.hasTeam(player)) {
                Bukkit.broadcastMessage(Message.get("game.missing_team.message"));
                return;
            }
        }

        Bukkit.broadcastMessage(Message.get("game.teleporting.message"));

        Random random = new Random();
        World overworld = Bukkit.getWorld("world");
        assert overworld != null;

        runEachPlayer(player -> {
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 10 * 60 * 20, 0, false, false, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 10 * 60 * 20, 0, false, false, false));
            player.teleport(new Location(overworld, 0, 10000, 0));
        });

        Location c;
        do {
            c = new Location(overworld,
                    random.nextInt(-100000, 100000), 60, random.nextInt(-100000, 100000));
        } while (overworld.getBlockAt(c).getType() == Material.WATER);
        Location center = c;

        runEachTeam(team -> {
            team.init();
            for (int i = 1; i <= 3; i++) {
                addQuest(team);
            }
            for (Upgrade upgrade : team.getUpgrades().values()) {
                upgrade.startup();
            }
            Location teamCenter;
            do {
                teamCenter = new Location(overworld,
                        center.getBlockX() + random.nextInt(-200, 200), 0, center.getBlockZ() + random.nextInt(-200, 200));
            } while (overworld.getBlockAt(teamCenter).getType() == Material.WATER);
            for (Player player : team.members()) {
                Location playerPos = new Location(overworld,
                        teamCenter.getBlockX() + random.nextInt(-20, 20), 0, teamCenter.getBlockZ() + random.nextInt(-20, 20));
                for (int i = 300; i >= -64; i--) {
                    playerPos.setY(i);
                    if (overworld.getBlockAt(playerPos).getType() != Material.AIR) {
                        playerPos.setY(i + 1);
                        player.setBedSpawnLocation(playerPos, true);
                        playerPos.setY(i + 180);
                        break;
                    }
                }
                player.teleport(playerPos);
                modifyPlayer(player);
            }
        });

        state = State.STARTUP;

        Bukkit.broadcastMessage(Message.get("game.start.message", goal));
    }

    void addQuest(@NotNull QuestTeam team) {
        Quest quest = CoreManager.manager.createQuest(CoreManager.manager.createType(), team, new GameMainQuestInterface(team));
        quest.startup();
        team.getQuests().add(quest);
        runEachTeam(t -> {
            if (t == team) {
                for (Player player : t.members()) {
                    player.spigot().sendMessage(
                            Message.getComponent("game.task.get1", quest.getName()),
                            ActionTool.addAction(Message.getComponent("game.task.get.action"), sender -> {
                                        if (!(sender instanceof Player p)) return;
                                        if (quest.getState() == State.DROP) return;
                                        quest.openPanel(p);
                                    }));
                }
            } else {
                t.teamBroadcast(Message.get("game.task.get2", team.name(), quest.getName()));
            }
        });
    }

    @Override
    public void drop() {
        if (state != State.STARTUP) return;
        state = State.DROP;
        runEachTeam((t) -> {
            t.getQuests().forEach(Stateful::drop);
            for (Upgrade upgrade : t.getUpgrades().values()) upgrade.drop();
            t.init();
        });
        Bukkit.broadcastMessage(Message.get("game.stop.message"));
        runEachPlayer(p -> {
            p.setScoreboard(CoreManager.emptyScoreboard);
            if (UIManager.API.isPanelOpen(p) && UIManager.API.getOpenPanel(p, PanelPosition.Top) instanceof DynamicPanel dp) {
                dp.close();
                DPRootNotStarted.openDynamic(p, PanelPosition.Top);
            }
        });
    }
}
