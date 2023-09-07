package me.hifei.questmaster.api.bukkitevent;

import me.hifei.questmaster.api.quest.Quest;
import me.hifei.questmaster.api.quest.Reward;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class TeamRewardedByQuestEvent extends QuestMasterEvent {
    private final static HandlerList HANDLERS = new HandlerList();
    private final Quest quest;
    private Reward reward;

    @SuppressWarnings("unused")
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public TeamRewardedByQuestEvent(Quest quest, Reward reward) {
        this.quest = quest;
        this.reward = reward;
    }

    @SuppressWarnings("unused")
    public Quest getQuest() {
        return quest;
    }
    public Reward getReward() {
        return reward;
    }

    public void setReward(Reward reward) {
        this.reward = reward;
    }
}
