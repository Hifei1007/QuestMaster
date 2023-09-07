package me.hifei.questmaster.api.quest;

import me.hifei.questmaster.QuestMasterPlugin;
import me.hifei.questmaster.api.state.State;
import me.hifei.questmaster.api.state.Stateful;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class QuestInterface implements Stateful {
    public Quest quest;
    public BukkitRunnable runnable;
    public @Nullable Listener listener;
    protected @NotNull State state = State.WAIT;


    public void tick () {
    }

    public @Nullable Listener listener () {
        return null;
    }

    @SuppressWarnings("EmptyMethod")
    public void onStartup() {
    }

    public void onComplete() {
    }

    @SuppressWarnings("EmptyMethod")
    public void onDrop() {
    }

    public void onTimeUp() {

    }

    public void startup(@NotNull Quest q) {
        quest = q;
        onStartup();
        runnable = new BukkitRunnable() {
            @Override
            public void run() {
                if (!this.isCancelled()) tick();
            }
        };
        listener = listener();
        if (listener != null) Bukkit.getPluginManager().registerEvents(listener, QuestMasterPlugin.instance);
        runnable.runTaskTimer(QuestMasterPlugin.instance, 0, 1);
        startup();
    }

    @Override
    public @NotNull State getState() {
        return state;
    }

    @Override
    public void startup() {
        if (state != State.WAIT) return;
        state = State.STARTUP;
    }

    public void drop() {
        if (state != State.STARTUP) return;
        state = State.DROP;
        onDrop();
        if (listener != null) HandlerList.unregisterAll(listener);
        runnable.cancel();
    }

    public void complete() {
        onComplete();
    }

    public void timeUp() {
        onTimeUp();
    }
}
