package me.hifei.questmaster.running.listeners;

import me.hifei.questmaster.CoreManager;
import me.hifei.questmaster.running.config.Message;
import me.hifei.questmaster.tools.ActionTool;
import net.md_5.bungee.api.chat.TextComponent;
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
                    new TextComponent(Message.get("teleport.death.message")),
                    ActionTool.addAction(new TextComponent(Message.get("teleport.death.action")), (sender) -> {
                        if (!sender.equals(player)) return;
                        player.teleport(location);
                    })
            );
        }
    }
}
