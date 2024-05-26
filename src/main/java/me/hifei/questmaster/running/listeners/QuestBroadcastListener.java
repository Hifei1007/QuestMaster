package me.hifei.questmaster.running.listeners;

import eu.endercentral.crazy_advancements.advancement.AdvancementDisplay;
import me.hifei.questmaster.api.CoreManager;
import me.hifei.questmaster.api.bukkitevent.QuestCompleteEvent;
import me.hifei.questmaster.api.bukkitevent.QuestTimeUpEvent;
import me.hifei.questmaster.api.quest.Quest;
import me.hifei.questmaster.api.team.QuestTeam;
import me.hifei.questmaster.running.config.Message;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Objects;

public class QuestBroadcastListener implements Listener {
    @EventHandler
    public void onQuestComplete(QuestCompleteEvent event) {
        Quest quest = event.getQuest();
        QuestTeam team = event.getQuest().getTeam();
        CoreManager.manager.runEachTeam(t -> {
            if (t == team) {
                t.teamBroadcast(Message.get("game.task.complete1", quest.getName()));
                t.teamToast(
                        Material.valueOf(Objects.requireNonNull(quest.getItem().getString("material")).toUpperCase()),
                        Message.get("quest.toast.complete.default", quest.getName()), AdvancementDisplay.AdvancementFrame.GOAL);
            } else {
                t.teamBroadcast(Message.get("game.task.complete2", team.name(), quest.getName()));
                t.teamToast(
                        Material.valueOf(Objects.requireNonNull(quest.getItem().getString("material")).toUpperCase()),
                        Message.get("quest.toast.complete.other_team", t.name(), quest.getName()),
                        AdvancementDisplay.AdvancementFrame.GOAL);
            }
        });
        CoreManager.manager.runEachPlayer(p -> {
            if (CoreManager.manager.getTeam(p) == team) {
                p.playSound(p, Sound.BLOCK_ANVIL_USE, 10.0f, 1.0f);
            } else {
                p.playSound(p, Sound.ENTITY_ARROW_HIT_PLAYER, 10.0f, 1.0f);
            }
        });
    }

    @EventHandler
    public void onQuestTimeup(QuestTimeUpEvent event) {
        Quest quest = event.getQuest();
        QuestTeam team = event.getQuest().getTeam();
        CoreManager.manager.runEachTeam(t -> {
            if (t == team) {
                t.teamBroadcast(Message.get("game.task.timeup1", quest.getName()));
                t.teamToast(
                        Material.valueOf(Objects.requireNonNull(quest.getItem().getString("material")).toUpperCase()),
                        Message.get("quest.toast.timeup.default", quest.getName()), AdvancementDisplay.AdvancementFrame.TASK);
            } else {
                t.teamBroadcast(Message.get("game.task.timeup2", team.name(), quest.getName()));
                t.teamToast(
                        Material.valueOf(Objects.requireNonNull(quest.getItem().getString("material")).toUpperCase()),
                        Message.get("quest.toast.timeup.other_team", t.name(), quest.getName()),
                        AdvancementDisplay.AdvancementFrame.TASK);
            }
        });
    }
}
