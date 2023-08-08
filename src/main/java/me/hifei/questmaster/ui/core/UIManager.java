package me.hifei.questmaster.ui.core;

import me.hifei.questmaster.QuestMasterPlugin;
import me.hifei.questmaster.running.config.Message;
import me.rockyhawk.commandpanels.CommandPanels;
import me.rockyhawk.commandpanels.api.CommandPanelsAPI;
import me.rockyhawk.commandpanels.api.PanelCommandEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class UIManager {
    public final static UIManager ins = new UIManager();
    public static final CommandPanelsAPI API = CommandPanels.getAPI();
    private final Map<String, List<Consumer<PanelCommandEvent>>> m = new HashMap<>();
    private final List<Consumer<PanelCommandEvent>> listeners = new LinkedList<>();

    public void changeClear(@NotNull DynamicPanel panel) {
        changeClear(panel, () -> {});
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
        else m.put(id, List.of(c));
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

    public void unregisterEventAll(String id) {
        m.remove(id);
    }

    public void unregisterEventAll(Consumer<PanelCommandEvent> c) {
        m.forEach((key, value) -> value.remove(c));
    }

    public void unregisterEventAll(String id, Consumer<PanelCommandEvent> c) {
        if (m.containsKey(id)) m.get(id).remove(c);
    }
}
