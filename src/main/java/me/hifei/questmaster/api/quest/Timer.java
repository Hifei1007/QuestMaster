package me.hifei.questmaster.api.quest;

public class Timer{
    private long startTime;
    private int time;
    private boolean isStarted;

    public Timer(int sec) {
        startTime = 0;
        time = sec;
    }

    public double getProcess() {
        checkState();
        return Math.min(Math.max(((System.currentTimeMillis() - startTime) / 1000.0) / time, 0), 1);
    }

    public void start() {
        if (isStarted) return;
        startTime = System.currentTimeMillis();
        isStarted = true;
    }

    private void checkState() {
        if (!isStarted) throw new RuntimeException("Timer was not started.");
    }

    public String remaining() throws RuntimeException {
        checkState();
        long remainSec = time - (System.currentTimeMillis() - startTime) / 1000;
        long remainMin = remainSec / 60;
        remainSec %= 60;
        long remainHor = remainMin / 60;
        remainMin %= 60;
        return String.format("%d:%d:%d", remainHor, remainMin, remainSec);
    }

    public double totalRemainingSecond() {
        checkState();
        if (isTimeUp()) return 0;
        return time - (System.currentTimeMillis() - startTime) / 1000.0;
    }

    public long second() throws RuntimeException {
        checkState();
        return (time - (System.currentTimeMillis() - startTime) / 1000) % 60;
    }

    public long minute() throws RuntimeException {
        checkState();
        long remainSec = time - (System.currentTimeMillis() - startTime) / 1000;
        return (remainSec / 60) % 60;
    }

    public long hour() throws RuntimeException {
        checkState();
        long remainSec = time - (System.currentTimeMillis() - startTime) / 1000;
        long remainMin = remainSec / 60;
        return remainMin / 60;
    }

    public boolean isTimeUp() throws RuntimeException {
        checkState();
        return time <= (System.currentTimeMillis() - startTime) / 1000;
    }

    public void addSec(int sec) {
        if (!isTimeUp()) time += sec;
    }
}
