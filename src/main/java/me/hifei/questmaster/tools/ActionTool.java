package me.hifei.questmaster.tools;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.command.CommandSender;

import java.util.*;
import java.util.function.Consumer;

public class ActionTool {
    public static class Action {
        public Action(int timer, Consumer<CommandSender> callback) {
            this.timer = timer;
            this.callback = callback;
        }

        public int timer;
        public final Consumer<CommandSender> callback;
    }

    private static int timer = 20;
    public static final Map<String, Action> actionMap = new HashMap<>();
    public static void tick() {
        timer--;
        if (timer == 0) {
            timer = 20;
            List<String> targets = new ArrayList<>();
            for (Map.Entry<String, Action> entry : actionMap.entrySet()) {
                entry.getValue().timer--;
                if (entry.getValue().timer == 0) {
                    targets.add(entry.getKey());
                }
            }
            for (String target : targets) {
                actionMap.remove(target);
            }
        }
    }

    public static String addAction(Consumer<CommandSender> callback) {
        String name = "";
        String chars = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ+/";
        Random random = new Random();
        do {
            for (int i = 1; i <= 16; i++) {
                int t = random.nextInt(64);
                name += chars.substring(t, t+1);
            }
        } while (actionMap.containsKey(name));
        actionMap.put(name, new Action(300, callback));
        return name;
    }

    public static BaseComponent addAction(BaseComponent component, Consumer<CommandSender> callback) {
        String id = addAction(callback);
        component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/questmaster:questaction " + id));
        return component;
    }
}
