package me.hifei.questmaster.running.commands;

import me.hifei.questmaster.running.config.Message;
import me.hifei.questmaster.tools.ActionTool;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class QuestActionCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length != 1) return true;
        String name = args[0];
        ActionTool.Action action = ActionTool.actionMap.get(name);
        if (action == null) {
            sender.sendMessage(Message.get("command.action.unknown"));
            return true;
        }
        action.callback.accept(sender);
        return true;
    }
}
