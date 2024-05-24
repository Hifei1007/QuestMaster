package me.hifei.questmaster.running.listeners;

import me.hifei.questmaster.api.CoreManager;
import me.hifei.questmaster.api.bukkitevent.QuestCompleteEvent;
import me.hifei.questmaster.api.bukkitevent.QuestTimeUpEvent;
import me.hifei.questmaster.api.quest.Quest;
import me.hifei.questmaster.api.team.QuestTeam;
import me.hifei.questmaster.running.config.Message;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class QuestBroadcastListener implements Listener {
    @EventHandler
    public void onQuestComplete(QuestCompleteEvent event) {
        Quest quest = event.getQuest();
        QuestTeam team = event.getQuest().getTeam();
        CoreManager.game.runEachTeam(t -> {
            if (t == team) {
                t.teamBroadcast(Message.get("game.task.complete1", quest.getName()));
            } else {
                t.teamBroadcast(Message.get("game.task.complete2", team.name(), quest.getName()));
            }
        });
        CoreManager.game.runEachPlayer(p -> {
            if (CoreManager.manager.getTeam(p) == team) {
                p.sendTitle("", Message.get("game.task.complete1", quest.getName()), 10, 120, 20);
                p.playSound(p, Sound.BLOCK_ANVIL_USE, 10.0f, 1.0f);
            } else {
                p.sendTitle("", Message.get("game.task.complete2", team.name(), quest.getName()), 10, 50, 20);
                p.playSound(p, Sound.ENTITY_ARROW_HIT_PLAYER, 10.0f, 1.0f);
            }
        });
    }

    @EventHandler
    public void onQuestTimeup(QuestTimeUpEvent event) {
        Quest quest = event.getQuest();
        QuestTeam team = event.getQuest().getTeam();
        CoreManager.game.runEachTeam(t -> {
            if (t == team) {
                t.teamBroadcast(Message.get("game.task.timeup1", quest.getName()));
            } else {
                t.teamBroadcast(Message.get("game.task.timeup2", team.name(), quest.getName()));
            }
        });
    }
}
