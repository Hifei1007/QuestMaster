package me.hifei.questmaster.running.listeners;

import me.hifei.questmaster.api.CoreManager;
import me.hifei.questmaster.api.team.QuestTeam;
import me.hifei.questmaster.running.config.Message;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        event.setCancelled(true);
        String msg = event.getMessage();
        String name = event.getPlayer().getDisplayName();
        if (CoreManager.isGameStart()) {
            if (msg.startsWith("@")) {
                String m = Message.get("chat.started.shout", name, msg.substring(1));
                CoreManager.game.runEachPlayer(p -> p.sendMessage(m));
            } else {
                QuestTeam team = CoreManager.manager.getTeam(event.getPlayer());
                if (team == null) return;
                team.teamBroadcast(Message.get("chat.started.team", team.name(), name, msg));
            }
        } else {
            Bukkit.broadcastMessage(Message.get("chat.not_started", name, msg));
        }
    }
}
