package me.hifei.questmaster.ui.core;

import me.hifei.questmaster.QuestMasterPlugin;
import me.hifei.questmaster.running.config.Message;
import me.rockyhawk.commandpanels.CommandPanels;
import me.rockyhawk.commandpanels.api.CommandPanelsAPI;
import me.rockyhawk.commandpanels.api.PanelCommandEvent;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;

public class UIManager {
    public final static UIManager ins = new UIManager();
    public static final CommandPanelsAPI API = CommandPanels.getAPI();
    private final Map<String, List<Consumer<PanelCommandEvent>>> m = new HashMap<>();
    private final List<Consumer<PanelCommandEvent>> listeners = new LinkedList<>();

    static {
        ins.registerEvent("questmaster_license", (event) -> {
            Player player = event.getPlayer();

            String[] licenses = {
                    "Copyright (C) 2023 Hifei1007",
                    "",
                    "This program is free software: you can redistribute it and/or modify",
                    "it under the terms of the GNU Affero General Public License as published",
                    "by the Free Software Foundation, either version 3 of the License, or",
                    "(at your option) any later version.",
                    "",
                    "This program is distributed in the hope that it will be useful,",
                    "but WITHOUT ANY WARRANTY; without even the implied warranty of",
                    "MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the",
                    "GNU Affero General Public License for more details.",
                    "",
                    "You should have received a copy of the GNU Affero General Public License",
                    "along with this program.  If not, see <https://www.gnu.org/licenses/>.",
                    ""
            };
            for (String line : licenses) {
                player.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + line);
            }
            BaseComponent component = new TextComponent("https://github.com/Hifei1007/QuestMaster");
            component.setColor(ChatColor.RED.asBungee());
            component.setBold(true);
            component.setUnderlined(true);
            component.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://github.com/Hifei1007/QuestMaster"));
            component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.RED.toString() + ChatColor.BOLD + "CLICK!")));
            player.spigot().sendMessage(
                    component
            );
        });
    }

    public void changeClear(@NotNull DynamicPanel panel, @NotNull Runnable runnable) {
        new BukkitRunnable(){
            @Override
            public void run() {
                try {
                    panel.close();
                } catch (RuntimeException e) {
                    panel.player.sendMessage(Message.get("error.cant_close", panel.getName()));
                }
                runnable.run();
            }
        }.runTask(QuestMasterPlugin.instance);
    }

    public void sendEvent(@NotNull PanelCommandEvent e) {
        for (Consumer<PanelCommandEvent> consumer : listeners) consumer.accept(e);
        String message = e.getMessage();
        if (m.containsKey(message)) {
            for (Consumer<PanelCommandEvent> c : m.get(message)) {
                c.accept(e);
            }
        }
    }

    public void registerEvent(String id, @NotNull Consumer<PanelCommandEvent> c) {
        if (m.containsKey(id)) m.get(id).add(c);
        else m.put(id, new ArrayList<>(List.of(c)));
    }

    public void registerListener(Consumer<PanelCommandEvent> consumer) {
        listeners.add(consumer);
    }

    public <T extends DynamicPanel> void registerListener(Class<T> clazzOnly, Consumer<PanelCommandEvent> consumer) {
        Consumer<PanelCommandEvent> newConsumer = (event) -> {
            if (event.getPanel().getClass() == clazzOnly) consumer.accept(event);
        };
        registerListener(newConsumer);
    }

    @SuppressWarnings("unused")
    public void unregisterEventAll(String id) {
        m.remove(id);
    }

    @SuppressWarnings("unused")
    public void unregisterEventAll(Consumer<PanelCommandEvent> c) {
        m.forEach((key, value) -> value.remove(c));
    }

    @SuppressWarnings("unused")
    public void unregisterEventAll(String id, Consumer<PanelCommandEvent> c) {
        if (m.containsKey(id)) m.get(id).remove(c);
    }
}
