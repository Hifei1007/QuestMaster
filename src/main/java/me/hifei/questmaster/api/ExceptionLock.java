package me.hifei.questmaster.api;

import me.hifei.questmaster.running.config.Message;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public class ExceptionLock {
    private boolean locked = false;

    public void run(@NotNull Runnable runnable) {
        try {
            if (!locked) runnable.run();
        } catch (RuntimeException e) {
            locked = true;
            Bukkit.broadcastMessage(Message.get("error.runnable_crash"));
            throw new RuntimeException("Exception lock auto locked", e);
        }
    }
}
