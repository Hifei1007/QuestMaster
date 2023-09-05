package me.hifei.questmaster.api.event;

import me.hifei.questmaster.api.CoreManager;
import me.hifei.questmaster.QuestMasterPlugin;
import me.hifei.questmaster.api.state.State;
import me.hifei.questmaster.running.config.Message;
import me.hifei.questmaster.running.gsoncfg.event.SingleEventConfig;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.jetbrains.annotations.NotNull;

public abstract class InstantQuestEvent extends QuestEvent {
    protected State state = State.WAIT;

    public InstantQuestEvent(SingleEventConfig config) {
        super(config);
    }

    public abstract void doChange();

    @Override
    public final void startup() {
        if (state != State.WAIT) return;
        if (!CoreManager.isGameStart()) throw new RuntimeException("Can't startup a event when the game not started yet.");
        state = State.STARTUP;
        QuestMasterPlugin.logger.info("<STARTUP> " + this.getName());
        doChange();
        CoreManager.game.runEachPlayer((player) -> {
            player.sendMessage(Message.get("event.prefix.instant") + getName());
            getDescriptions().forEach(player::sendMessage);
            player.sendTitle("", Message.get("event.prefix.instant") + getName(), 10, 120, 20);
            player.playSound(player, Sound.ENTITY_ARROW_HIT_PLAYER, SoundCategory.MASTER, 1, 0);
        });
        drop();
    }

    @Override
    public final void drop() {
        if (state != State.STARTUP) return;
        state = State.DROP;
        QuestMasterPlugin.logger.info("<DROP> " + this.getName());
    }

    @Override
    public @NotNull State getState() {
        return state;
    }
}
