package me.hifei.questmaster.running.listeners;

import me.hifei.questmaster.QuestMasterPlugin;
import me.hifei.questmaster.api.CoreManager;
import me.hifei.questmaster.running.config.Message;
import me.hifei.questmaster.tools.ActionTool;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class DeathListener implements Listener {
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        event.setDeathMessage(ChatColor.RED + "☠ " + event.getDeathMessage() + ".");
        if (CoreManager.isGameStart()) {
            Player player = event.getEntity();
            Location location = player.getLocation();
            player.spigot().sendMessage(
                    Message.getComponent("teleport.death.message"),
                    ActionTool.addAction(Message.getComponent("teleport.death.action"), (sender) -> {
                        if (!sender.equals(player)) return;
                        player.teleport(location);
                    })
            );
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        new BukkitRunnable() {
            public void run() {
                event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 200, 4, false, false, false));
            }
        }.runTask(QuestMasterPlugin.instance);
    }
}
