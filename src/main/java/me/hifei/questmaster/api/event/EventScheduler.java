package me.hifei.questmaster.api.event;

import me.hifei.questmaster.api.CoreManager;
import me.hifei.questmaster.QuestMasterPlugin;
import me.hifei.questmaster.api.ExceptionLock;
import me.hifei.questmaster.api.bukkitevent.ScheduleEvent;
import me.hifei.questmaster.api.quest.Timer;
import me.hifei.questmaster.api.state.State;
import me.hifei.questmaster.running.gsoncfg.event.EventConfig;
import me.hifei.questmaster.running.gsoncfg.event.SingleEventConfig;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public final class EventScheduler extends NormalQuestEvent {

    private static SingleEventConfig makeConfig() {
        SingleEventConfig cfg = new SingleEventConfig();
        cfg.time = EventConfig.cfg.eventDelay;
        cfg.name = "Scheduler";
        cfg.descriptions = List.of();
        return cfg;
    }

    public EventScheduler() {
        super(makeConfig());
        ScheduleEvent event = new ScheduleEvent(time, this);
        Bukkit.getPluginManager().callEvent(event);
        time = event.getScheduleTime();
        this.startup();
    }

    protected void tick() {
        if (timer.isTimeUp()) {
            onTimeUp();
            drop();
        }
    }

    @Override
    public void startup() {
        if (state != State.WAIT) return;
        if (!CoreManager.isGameStart()) throw new RuntimeException("Can't startup a event when the game not started yet.");
        state = State.STARTUP;
        QuestMasterPlugin.logger.info("<STARTUP> " + this.getName());
        CoreManager.game.getEvents().add(this);
        timer = new Timer(time());
        timer.start();
        runnable = new BukkitRunnable() {
            final ExceptionLock lock = new ExceptionLock();

            @Override
            public void run() {
                lock.run(() -> {if (!this.isCancelled()) tick();});
            }
        };
        runnable.runTaskTimer(QuestMasterPlugin.instance, 0, 1);
    }

    @Override
    protected void onTimeUp() {
        CoreManager.game.appendEvent(CoreManager.manager.createEvent());
        CoreManager.game.getEvents().add(new EventScheduler());
    }

    @Override
    public void drop() {
        if (state != State.STARTUP) return;
        state = State.DROP;
        CoreManager.game.getEvents().remove(this);
        QuestMasterPlugin.logger.info("<DROP> " + this.getName());
        runnable.cancel();
    }
}
