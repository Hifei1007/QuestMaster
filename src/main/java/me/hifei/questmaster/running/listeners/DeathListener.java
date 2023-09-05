package me.hifei.questmaster.running.listeners;

import me.hifei.questmaster.api.CoreManager;
import me.hifei.questmaster.running.config.Message;
import me.hifei.questmaster.tools.ActionTool;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

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
}
