package me.hifei.questmaster.manager;

import me.hifei.questmaster.api.CoreManager;
import me.hifei.questmaster.api.QuestGame;
import me.hifei.questmaster.api.event.EventComingQuestEvent;
import me.hifei.questmaster.api.event.EventScheduler;
import me.hifei.questmaster.api.event.NormalQuestEvent;
import me.hifei.questmaster.api.event.QuestEvent;
import me.hifei.questmaster.api.state.State;
import me.hifei.questmaster.api.state.Stateful;
import me.hifei.questmaster.api.team.QuestTeam;
import me.hifei.questmaster.running.config.Message;
import me.hifei.questmaster.running.gsoncfg.rolling.RollingConfig;
import me.hifei.questmaster.running.gsoncfg.rolling.TeleportConfig;
import me.hifei.questmaster.shop.Upgrade;
import me.hifei.questmaster.tools.ActionTool;
import me.hifei.questmaster.api.ui.DynamicPanel;
import me.hifei.questmaster.api.ui.UIManager;
import me.hifei.questmaster.dynamicui.DPRootNotStarted;
import me.rockyhawk.commandpanels.openpanelsmanager.PanelPosition;
import org.bukkit.*;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CQuestGame implements QuestGame {
    private final int goal;
    private @NotNull State state = State.WAIT;
    private final List<NormalQuestEvent> events = new ArrayList<>();

    CQuestGame(int goal) {
        this.goal = goal;
        for (QuestTeam team : CoreManager.manager.getTeams()) {
            team.setScoreboard(CoreManager.manager.createScoreboard(CoreManager.game, team));
        }
    }

    @Override
    public List<NormalQuestEvent> getEvents() {
        return events;
    }

    @Override
    public void appendEvent(QuestEvent event) {
        EventComingQuestEvent e = new EventComingQuestEvent(event);
        e.startup();
        events.add(e);
    }


    @Override
    public int getGoal() {
        return goal;
    }

    @Override
    public List<Player> getPlayers() {
        ArrayList<Player> players = new ArrayList<>();
        CoreManager.manager.runEachTeam(team ->
                players.addAll(team.members())
        );
        return players;
    }

    @Override
    public void checkScore(@NotNull QuestTeam team) {
        if (team.score() >= goal) {
            CoreManager.manager.runEachPlayer(player -> {
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
        player.setScoreboard(Objects.requireNonNull(CoreManager.manager.getTeam(player)).getScoreboard().getBukkit());
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

        ActionTool.actionMap.clear();

        World overworld = Bukkit.getWorld("world");
        assert overworld != null;

        CoreManager.manager.runEachPlayer(player -> {
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 10 * 60 * 20, 0, false, false, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 10 * 60 * 20, 0, false, false, false));
            player.teleport(new Location(overworld, 0, 10000, 0));
        });

        Location c;
        TeleportConfig config = RollingConfig.cfg.teleport;
        do {
            c = new Location(overworld, config.globalX.next(), 60, config.globalZ.next());
        } while (overworld.getBlockAt(c).getType() == Material.WATER);
        Location center = c;

        CoreManager.manager.runEachTeam(team -> {
            team.init();
            for (int i = 1; i <= 3; i++) {
                team.makeNewQuest();
            }
            for (Upgrade upgrade : team.getUpgrades().values()) {
                upgrade.startup();
            }
            Location teamCenter;
            do {
                teamCenter = new Location(overworld,
                        center.getBlockX() + config.teamX.next(), 60, center.getBlockZ() + config.teamZ.next());
            } while (overworld.getBlockAt(teamCenter).getType() == Material.WATER);
            for (Player player : team.members()) {
                Location playerPos = new Location(overworld,
                        teamCenter.getBlockX() + config.playerX.next(),0, teamCenter.getBlockZ() + config.playerZ.next());
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

        events.add(new EventScheduler());

        Bukkit.broadcastMessage(Message.get("game.start.message", goal));
    }

    @Override
    public void drop() {
        if (state != State.STARTUP) return;
        state = State.DROP;
        CoreManager.manager.runEachTeam((t) -> {
            t.getQuests().forEach(Stateful::drop);
            for (Upgrade upgrade : t.getUpgrades().values()) upgrade.drop();
            t.init();
        });
        for (NormalQuestEvent event : new ArrayList<>(events)) event.drop();
        Bukkit.broadcastMessage(Message.get("game.stop.message"));
        CoreManager.manager.runEachPlayer(p -> {
            p.setScoreboard(CoreManager.emptyScoreboard);
            if (UIManager.API.isPanelOpen(p) && UIManager.API.getOpenPanel(p, PanelPosition.Top) instanceof DynamicPanel dp) {
                UIManager.ins.changeClear(dp, () -> {});
                DPRootNotStarted.openDynamic(p, PanelPosition.Top);
            }
        });
    }
}
