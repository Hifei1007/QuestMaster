package me.hifei.questmaster.running.commands;

import me.hifei.questmaster.CoreManager;
import me.hifei.questmaster.api.team.QuestTeam;
import me.hifei.questmaster.running.config.Message;
import me.hifei.questmaster.tools.ActionTool;
import me.hifei.questmaster.tools.LocateTool;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class QuestActionCommand implements CommandExecutor {
    public boolean runDebug(String name) {
        switch (name) {
            case "D$LOCATE_PLAINS" -> LocateTool.locateBiomes("plains", Objects.requireNonNull(Bukkit.getWorld("world")).getSpawnLocation());
            case "D$LOCATE_BEACH" -> LocateTool.locateBiomes("beach", Objects.requireNonNull(Bukkit.getWorld("world")).getSpawnLocation());
            case "D$GET_MONEY" -> {
                for (QuestTeam team : CoreManager.manager.getTeams()) {
                    team.setCoin(1000000);
                    team.setPoint(1000000);
                }
            }
            default -> {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length != 1) return true;
        String name = args[0];
        // DEBUG ENTRANCE
        if (runDebug(name)) {
            sender.sendMessage(ChatColor.GREEN + "Debug content success executed.");
            return true;
        }
        ActionTool.Action action = ActionTool.actionMap.get(name);
        if (action == null) {
            sender.sendMessage(Message.get("command.action.unknown"));
            return true;
        }
        action.callback.accept(sender);
        return true;
    }
}
