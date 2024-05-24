package me.hifei.questmaster.api.bukkitevent;

import me.hifei.questmaster.api.quest.Quest;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class QuestTimeUpEvent extends QuestMasterEvent {
    private final static HandlerList HANDLERS = new HandlerList();
    private final Quest quest;

    public QuestTimeUpEvent(Quest quest) {
        this.quest = quest;
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

    public Quest getQuest() {
        return quest;
    }
}
