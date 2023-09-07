package me.hifei.questmaster.api.bukkitevent;

import me.hifei.questmaster.api.event.EventScheduler;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ScheduleEvent extends QuestMasterEvent {
    private final static HandlerList HANDLERS = new HandlerList();
    private int scheduleTime;
    private final EventScheduler scheduler;

    public ScheduleEvent(int scheduleTime, EventScheduler scheduler) {
        this.scheduleTime = scheduleTime;
        this.scheduler = scheduler;
    }

    @SuppressWarnings("unused")
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public int getScheduleTime() {
        return scheduleTime;
    }

    public void setScheduleTime(int scheduleTime) {
        this.scheduleTime = scheduleTime;
    }

    @SuppressWarnings("unused")
    public EventScheduler getScheduler() {
        return scheduler;
    }
}
