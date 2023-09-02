package me.hifei.questmaster.api.event;

import me.hifei.questmaster.CoreManager;
import me.hifei.questmaster.QuestMasterPlugin;
import me.hifei.questmaster.api.ExceptionLock;
import me.hifei.questmaster.api.quest.Timer;
import me.hifei.questmaster.api.state.State;
import me.hifei.questmaster.running.config.Message;
import me.hifei.questmaster.running.gsoncfg.event.EventConfig;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public final class EventComingQuestEvent extends NormalQuestEvent {
    private final QuestEvent event;

    private static String makeName(QuestEvent event) {
        if (event instanceof InstantQuestEvent) return Message.get("event.prefix.coming_instant") + event.getName();
        else return Message.get("event.prefix.coming_normal") + event.getName();
    }

    public EventComingQuestEvent(QuestEvent event) {
        super(makeName(event), event.getDescriptions(), new HashMap<>(), EventConfig.cfg.comingDelay.next(),
                EventConfig.cfg.comingColor, EventConfig.cfg.comingStyle);
        this.event = event;
    }

    @Override
    protected void onTimeUp() {
        this.event.startup();
    }

    @Override
    public void startup() {
        if (state != State.WAIT) return;
        if (!CoreManager.isGameStart()) throw new RuntimeException("Can't startup a event when the game not started yet.");
        state = State.STARTUP;
        QuestMasterPlugin.logger.info("<STARTUP> " + this.getName());
        CoreManager.game.getEvents().add(this);
        CoreManager.game.runEachPlayer((player) -> {
            player.sendMessage(getName());
            player.sendMessage(getDescriptions().toArray(new String[]{}));
        });
        onStartup();
        timer = new Timer(time());
        timer.start();

        bossBar = Bukkit.createBossBar(
                getName() + Message.get("event.bossbar.suffix", timer.hour(), timer.minute(), timer.second()),
                getBarColor(),
                getBarStyle()
        );
        bossBar.setVisible(true);
        CoreManager.game.runEachPlayer((player) -> bossBar.addPlayer(player));
        bossBar.setProgress(1);

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
    public void drop() {
        if (state != State.STARTUP) return;
        state = State.DROP;
        QuestMasterPlugin.logger.info("<DROP> " + this.getName());
        CoreManager.game.getEvents().remove(this);
        bossBar.removeAll();
        bossBar.setVisible(false);
        runnable.cancel();
    }
}
