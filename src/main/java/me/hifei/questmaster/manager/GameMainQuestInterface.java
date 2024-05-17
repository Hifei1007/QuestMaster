package me.hifei.questmaster.manager;

import me.hifei.questmaster.api.CoreManager;
import me.hifei.questmaster.api.bukkitevent.TeamRewardedByQuestEvent;
import me.hifei.questmaster.api.quest.Quest;
import me.hifei.questmaster.api.quest.QuestInterface;
import me.hifei.questmaster.api.quest.Reward;
import me.hifei.questmaster.api.state.State;
import me.hifei.questmaster.api.team.QuestTeam;
import me.hifei.questmaster.running.config.Message;
import org.bukkit.Bukkit;
import org.bukkit.Sound;

public class GameMainQuestInterface extends QuestInterface {
    private final QuestTeam team;

    public GameMainQuestInterface(QuestTeam team) {
        this.team = team;
    }

    @Override
    public void onComplete() {
        this.quest.drop();
        team.getQuests().remove(this.quest);
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
        TeamRewardedByQuestEvent event = new TeamRewardedByQuestEvent(quest, quest.getReward());
        Bukkit.getPluginManager().callEvent(event);
        Reward reward = event.getReward();
        team.setScore(team.score() + reward.score());
        team.setCoin(team.coin() + reward.coin());
        team.setPoint(team.point() + reward.point());
        team.teamBroadcast(Message.get("game.task.complete.change", reward.score(), reward.point(), reward.coin(), reward.time()));
        for (Quest q : team.getQuests()) q.getTimer().addSec((int) quest.getReward().time());
        ((CQuestGame) (CoreManager.game)).addQuest(team);
        CoreManager.game.checkScore(team);
    }

    @Override
    public void onTimeUp() {
        this.quest.drop();
        team.getQuests().remove(this.quest);
        CoreManager.game.runEachTeam(t -> {
            if (t == team) {
                t.teamBroadcast(Message.get("game.task.timeup1", quest.getName()));
            } else {
                t.teamBroadcast(Message.get("game.task.timeup2", team.name(), quest.getName()));
            }
        });
        team.setCoin(Math.max(0, team.coin() - quest.getCoinPunish()));
        team.setPoint(Math.max(0, team.point() - quest.getPointPunish()));
        team.teamBroadcast(Message.get("game.task.timeup.change", quest.getPointPunish(), quest.getCoinPunish()));
        ((CQuestGame) (CoreManager.game)).addQuest(team);
    }

    @Override
    public void tick() {
        if (quest.getTimer().isTimeUp() && quest.getState() == State.STARTUP) quest.timeUp();
    }
}
