package me.hifei.questmaster.running.commands;

import me.hifei.questmaster.CoreManager;
import me.hifei.questmaster.api.state.State;
import me.hifei.questmaster.running.config.Message;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class StartCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        if (strings.length != 1) {
            commandSender.sendMessage(Message.get("command.start.usage"));
            return true;
        }
        int n;
        try {
            n = Integer.parseInt(strings[0]);
            if (n <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            commandSender.sendMessage(Message.get("command.start.fail.wrong_number"));
            return true;
        }
        if (CoreManager.isGameStart()) {
            commandSender.sendMessage(Message.get("command.start.fail.game_started"));
            return true;
        }
        commandSender.sendMessage(Message.get("command.start.success"));
        CoreManager.game = CoreManager.manager.createGame(List.of(
                CoreManager.red,
                CoreManager.blue
        ), n);
        CoreManager.game.startup();
        return true;
    }
}
