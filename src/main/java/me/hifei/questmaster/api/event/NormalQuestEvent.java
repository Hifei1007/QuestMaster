package me.hifei.questmaster.api.event;

import me.hifei.questmaster.api.CoreManager;
import me.hifei.questmaster.QuestMasterPlugin;
import me.hifei.questmaster.api.ExceptionLock;
import me.hifei.questmaster.api.quest.Quest;
import me.hifei.questmaster.api.quest.QuestInterface;
import me.hifei.questmaster.api.quest.Timer;
import me.hifei.questmaster.api.state.State;
import me.hifei.questmaster.running.config.Message;
import me.hifei.questmaster.running.gsoncfg.event.SingleEventConfig;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class NormalQuestEvent extends QuestEvent {
    protected State state = State.WAIT;
    protected Timer timer;
    protected BukkitRunnable runnable;
    protected @Nullable Listener listener;
    protected BossBar bossBar;
    protected int time;
    @SuppressWarnings("CanBeFinal")
    protected BarColor barColor;
    @SuppressWarnings("CanBeFinal")
    protected BarStyle barStyle;

    public NormalQuestEvent(SingleEventConfig config) {
        super(config);
        barColor = config.barColor;
        barStyle = config.barStyle;
        this.time = config.time.next();
    }

    protected @Nullable Listener getListener() {
        return null;
    }

    protected void tick() {
        if (timer.isTimeUp()) {
            onTimeUp();
            drop();
            return;
        }
        CoreManager.game.runEachPlayer((player) -> bossBar.addPlayer(player));
        bossBar.setProgress(timer.getProcess());
        bossBar.setTitle(getName() + Message.get("event.bossbar.suffix", timer.hour(), timer.minute(), timer.second()));
    }

    public @NotNull List<QuestInterface> buildInterface(Quest quest) {
        return List.of();
    }

    protected int time() {
        return time;
    }

    @SuppressWarnings("EmptyMethod")
    protected void onStartup() {
    }

    @SuppressWarnings("EmptyMethod")
    protected void onTimeUp() {

    }

    @SuppressWarnings("EmptyMethod")
    protected void onDrop() {

    }

    protected BarColor getBarColor() {
        return barColor;
    }

    protected BarStyle getBarStyle() {
        return barStyle;
    }

    public Timer getTimer() {
        return timer;
    }

    @Override
    public void startup() {
        if (state != State.WAIT) return;
        if (!CoreManager.isGameStart()) throw new RuntimeException("Can't startup a event when the game not started yet.");
        state = State.STARTUP;
        QuestMasterPlugin.logger.info("<STARTUP> " + this.getName());
        CoreManager.game.getEvents().add(this);
        CoreManager.game.runEachPlayer((player) -> {
            player.sendMessage(Message.get("event.prefix.normal_start") + getName());
            getDescriptions().forEach(player::sendMessage);
            player.sendTitle("", Message.get("event.prefix.normal_start") + getName(), 10, 120, 20);
            player.playSound(player, Sound.ENTITY_ARROW_HIT_PLAYER, SoundCategory.MASTER, 1, 0);
        });
        onStartup();
        listener = getListener();
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
        if (listener != null) Bukkit.getPluginManager().registerEvents(listener, QuestMasterPlugin.instance);
        runnable.runTaskTimer(QuestMasterPlugin.instance, 0, 1);
    }

    @Override
    public final @NotNull State getState() {
        return state;
    }

    @Override
    public void drop() {
        if (state != State.STARTUP) return;
        state = State.DROP;
        QuestMasterPlugin.logger.info("<DROP> " + this.getName());
        CoreManager.game.getEvents().remove(this);
        CoreManager.game.runEachPlayer((player) -> {
            player.sendMessage(Message.get("event.prefix.normal_stop") + getName());
            player.sendTitle("", Message.get("event.prefix.normal_stop") + getName(), 10, 120, 20);
            player.playSound(player, Sound.ENTITY_ARROW_HIT_PLAYER, SoundCategory.MASTER, 1, 0);
        });
        onDrop();
        bossBar.removeAll();
        bossBar.setVisible(false);
        if (listener != null) HandlerList.unregisterAll(listener);
        runnable.cancel();
    }
}
