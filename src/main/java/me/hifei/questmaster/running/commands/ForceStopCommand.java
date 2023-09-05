package me.hifei.questmaster.running.commands;

import me.hifei.questmaster.api.CoreManager;
import me.hifei.questmaster.running.config.Message;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ForceStopCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!CoreManager.isGameStart()) {
            commandSender.sendMessage(Message.get("command.forcestop.fail.game_not_started"));
            return true;
        }
        commandSender.sendMessage(Message.get("command.forcestop.success"));
        if (commandSender instanceof Player)
            Bukkit.broadcastMessage(Message.get("command.forcestop.success_broadcast", ((Player) commandSender).getDisplayName()));
        CoreManager.game.drop();
        return true;
    }
}
