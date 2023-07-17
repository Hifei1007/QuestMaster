package me.hifei.questmaster.api.state;

import org.jetbrains.annotations.NotNull;

public interface Stateful {
    @NotNull State getState();

    void startup();

    void drop();
}
