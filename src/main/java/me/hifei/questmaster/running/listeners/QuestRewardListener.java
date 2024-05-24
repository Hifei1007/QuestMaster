package me.hifei.questmaster.running.listeners;

import me.hifei.questmaster.api.bukkitevent.QuestCompleteEvent;
import me.hifei.questmaster.api.bukkitevent.QuestTimeUpEvent;
import me.hifei.questmaster.api.bukkitevent.TeamRewardedByQuestEvent;
import me.hifei.questmaster.api.quest.Quest;
import me.hifei.questmaster.api.quest.Reward;
import me.hifei.questmaster.api.team.QuestTeam;
import me.hifei.questmaster.running.config.Message;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class QuestRewardListener implements Listener {
    @EventHandler
    public void onQuestComplete(QuestCompleteEvent event) {
        Quest quest = event.getQuest();
        QuestTeam team = event.getQuest().getTeam();
        TeamRewardedByQuestEvent e = new TeamRewardedByQuestEvent(quest, quest.getReward());
        Bukkit.getPluginManager().callEvent(e);
        Reward reward = e.getReward();
        team.setScore(team.score() + reward.score());
        team.setCoin(team.coin() + reward.coin());
        team.setPoint(team.point() + reward.point());
        team.teamBroadcast(Message.get("game.task.complete.change", reward.score(), reward.point(), reward.coin(), reward.time()));
        for (Quest q : team.getQuests()) q.getTimer().addSec((int) quest.getReward().time());
    }

    @EventHandler
    public void onQuestTimeUp(QuestTimeUpEvent event) {
        Quest quest = event.getQuest();
        QuestTeam team = event.getQuest().getTeam();
        team.setCoin(Math.max(0, team.coin() - quest.getCoinPunish()));
        team.setPoint(Math.max(0, team.point() - quest.getPointPunish()));
        team.teamBroadcast(Message.get("game.task.timeup.change", quest.getPointPunish(), quest.getCoinPunish()));
    }
}
