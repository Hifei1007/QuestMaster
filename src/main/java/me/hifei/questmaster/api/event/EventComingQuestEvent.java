package me.hifei.questmaster.api.event;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import me.hifei.questmaster.api.CoreManager;
import me.hifei.questmaster.QuestMasterPlugin;
import me.hifei.questmaster.api.ExceptionLock;
import me.hifei.questmaster.api.quest.Timer;
import me.hifei.questmaster.api.state.State;
import me.hifei.questmaster.running.config.Message;
import me.hifei.questmaster.running.gsoncfg.event.EventConfig;
import me.hifei.questmaster.running.gsoncfg.event.SingleEventConfig;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.scheduler.BukkitRunnable;

@SuppressWarnings("unused")
public final class EventComingQuestEvent extends NormalQuestEvent {
    private final QuestEvent event;

    private static String makeName(QuestEvent event) {
        if (event instanceof InstantQuestEvent) return Message.get("event.prefix.coming_instant") + event.getName();
        else return Message.get("event.prefix.coming_normal") + event.getName();
    }

    private static SingleEventConfig makeConfig(QuestEvent event) {
        SingleEventConfig config = new SingleEventConfig();
        config.name = makeName(event);
        config.descriptions = event.getDescriptions();
        config.time = EventConfig.cfg.comingDelay;
        config.barColor = EventConfig.cfg.comingColor;
        config.barStyle = EventConfig.cfg.comingStyle;
        JsonObject settings = new JsonObject();
        settings.add("hiddenChance", new JsonPrimitive(0.0));
        config.settings = settings;
        return config;
    }

    public EventComingQuestEvent(QuestEvent event) {
        super(makeConfig(event));
        this.event = event;
    }

    @Override
    public String getDisplayName() {
        if (event instanceof InstantQuestEvent) return Message.get("event.prefix.coming_instant") + event.getDisplayName();
        else return Message.get("event.prefix.coming_normal") + event.getDisplayName();
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
            player.sendMessage(getDisplayName());
            getDescriptions().forEach(player::sendMessage);
            player.sendTitle("", getDisplayName(), 10, 120, 20);
            player.playSound(player, Sound.ENTITY_ARROW_HIT_PLAYER, SoundCategory.MASTER, 1, 0);
        });
        onStartup();
        timer = new Timer(time());
        timer.start();

        bossBar = Bukkit.createBossBar(
                getDisplayName() + Message.get("event.bossbar.suffix", timer.hour(), timer.minute(), timer.second()),
                getBarColor(),
                getBarStyle()
        );
        bossBar.setVisible(true);
        CoreManager.game.runEachPlayer((player) -> bossBar.addPlayer(player));
        bossBar.setProgress(1);

        runnable = new BukkitRunnable() {
            private final ExceptionLock lock = new ExceptionLock();

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
