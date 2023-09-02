package me.hifei.questmaster.api.event;

import me.hifei.questmaster.CoreManager;
import me.hifei.questmaster.QuestMasterPlugin;
import me.hifei.questmaster.api.state.State;
import me.hifei.questmaster.running.config.Message;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public abstract class InstantQuestEvent extends QuestEvent {
    protected State state = State.WAIT;

    public InstantQuestEvent(String name, List<String> descriptions, Map<String, Object> settings) {
        super(name, descriptions, settings);
    }

    public abstract void doChange();

    @Override
    public final void startup() {
        if (state != State.WAIT) return;
        if (!CoreManager.isGameStart()) throw new RuntimeException("Can't startup a event when the game not started yet.");
        state = State.STARTUP;
        QuestMasterPlugin.logger.info("<STARTUP> " + this.getName());
        CoreManager.game.runEachPlayer((player) -> {
            player.sendMessage(Message.get("event.prefix.instant") + getName());
            player.sendMessage(getDescriptions().toArray(new String[]{}));
        });
        doChange();
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
