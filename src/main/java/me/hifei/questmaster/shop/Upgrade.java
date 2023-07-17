package me.hifei.questmaster.shop;

import me.hifei.questmaster.QuestMasterPlugin;
import me.hifei.questmaster.api.state.State;
import me.hifei.questmaster.api.state.Stateful;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class Upgrade implements Stateful {
    public State state;
    private BukkitRunnable runnable;
    private Listener listener;
    private int level;

    public Upgrade() {
        state = State.WAIT;
        level = 0;
    }

    public int getLevel() {
        return level;
    }

    public void addLevel() {
        addLevel(1);
    }

    public void addLevel(int level) {
        this.level += level;
        if (this.level > getMaxLevel()) this.level = getMaxLevel();
    }

    public void setLevel(int level) {
        this.level = level;
        if (this.level > getMaxLevel()) this.level = getMaxLevel();
    }

    public abstract int getMaxLevel();

    public abstract String getName();

    protected @Nullable Listener getListener() {
        return null;
    }

    protected void tick() {

    }

    @Override
    public @NotNull State getState() {
        return state;
    }

    @Override
    public void startup() {
        if (state != State.WAIT) return;
        state = State.STARTUP;
        setLevel(0);
        runnable = new BukkitRunnable() {
            @Override
            public void run() {
                tick();
            }
        };
        runnable.runTaskTimer(QuestMasterPlugin.instance, 0, 1);
        listener = getListener();
        Bukkit.getPluginManager().registerEvents(listener, QuestMasterPlugin.instance);
    }

    @Override
    public void drop() {
        if (state != State.STARTUP) return;
        state = State.DROP;
        runnable.cancel();
        HandlerList.unregisterAll(listener);
    }
}
