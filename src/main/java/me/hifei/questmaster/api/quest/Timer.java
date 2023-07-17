package me.hifei.questmaster.api.quest;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class Timer implements ConfigurationSerializable {
    private long startTime;
    private int time;
    private boolean isStarted;

    public Timer(int sec) {
        startTime = 0;
        time = sec;
    }

    public Timer(@NotNull Map<String, Object> serializer) {
        time = (int) serializer.get("time");
        isStarted = (boolean) serializer.get("isStarted");
        if (isStarted) startTime = System.currentTimeMillis() - (int) serializer.get("startedTime");
        else startTime = 0;
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> serializer = new HashMap<>();
        serializer.put("startedTime", (System.currentTimeMillis() - startTime));
        serializer.put("time", time);
        serializer.put("isStarted", isStarted);
        return serializer;
    }

    public void start() {
        if (isStarted) return;
        startTime = System.currentTimeMillis();
        isStarted = true;
    }

    public boolean isStarted() {
        return isStarted;
    }

    public String remaining() throws RuntimeException {
        if (!isStarted()) throw new RuntimeException("Timer was not started.");
        long remainSec = time - (System.currentTimeMillis() - startTime) / 1000;
        long remainMin = remainSec / 60;
        remainSec %= 60;
        long remainHor = remainMin / 60;
        remainMin %= 60;
        return String.format("%d:%d:%d", remainHor, remainMin, remainSec);
    }

    public long second() throws RuntimeException {
        if (!isStarted()) throw new RuntimeException("Timer was not started.");
        return (time - (System.currentTimeMillis() - startTime) / 1000) % 60;
    }

    public long minute() throws RuntimeException {
        if (!isStarted()) throw new RuntimeException("Timer was not started.");
        long remainSec = time - (System.currentTimeMillis() - startTime) / 1000;
        return (remainSec / 60) % 60;
    }

    public long hour() throws RuntimeException {
        if (!isStarted()) throw new RuntimeException("Timer was not started.");
        long remainSec = time - (System.currentTimeMillis() - startTime) / 1000;
        long remainMin = remainSec / 60;
        return remainMin / 60;
    }

    public boolean isTimeUp() throws RuntimeException {
        if (!isStarted()) throw new RuntimeException("Timer was not started.");
        return time < (System.currentTimeMillis() - startTime) / 1000;
    }

    public void addSec(int sec) {
        if (!isTimeUp()) time += sec;
    }
}
