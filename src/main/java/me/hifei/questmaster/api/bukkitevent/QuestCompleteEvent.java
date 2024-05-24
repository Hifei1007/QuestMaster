package me.hifei.questmaster.api.bukkitevent;

import me.hifei.questmaster.api.quest.Quest;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class QuestCompleteEvent extends QuestMasterEvent {
    private final static HandlerList HANDLERS = new HandlerList();

    private final Quest quest;

    public QuestCompleteEvent(Quest quest) {
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
