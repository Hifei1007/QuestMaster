package me.hifei.questmaster.manager;

import me.hifei.questmaster.CoreManager;
import me.hifei.questmaster.api.quest.Quest;
import me.hifei.questmaster.api.quest.QuestInterface;
import me.hifei.questmaster.api.quest.Reward;
import me.hifei.questmaster.api.state.State;
import me.hifei.questmaster.api.team.QuestTeam;
import me.hifei.questmaster.running.config.Message;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class GameMainQuestInterface extends QuestInterface {
    private final QuestTeam team;

    public GameMainQuestInterface(QuestTeam team) {
        this.team = team;
    }

    public GameMainQuestInterface(Map<String, Object> serializer) {
        team = CoreManager.manager.getTeam((String) serializer.get("team"));
        state = State.STARTUP;
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> serializer = new HashMap<>();
        serializer.put("team", team.name());
        return serializer;
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
        Reward reward = quest.getReward();
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
        team.setCoin(Math.max(0, team.coin() - quest.getReward().coin() / 10));
        team.setPoint(Math.max(0, team.point() - quest.getReward().point() / 10));
        team.teamBroadcast(Message.get("game.task.timeup.change", quest.getReward().point() / 10, quest.getReward().coin() / 10));
        ((CQuestGame) (CoreManager.game)).addQuest(team);
    }

    @Override
    public void tick() {
        if (quest.getTimer().isTimeUp() && quest.getState() == State.STARTUP) quest.timeUp();
    }
}
