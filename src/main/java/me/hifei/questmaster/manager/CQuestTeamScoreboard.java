package me.hifei.questmaster.manager;

import me.hifei.questmaster.CoreManager;
import me.hifei.questmaster.api.QuestGame;
import me.hifei.questmaster.api.quest.Quest;
import me.hifei.questmaster.api.team.QuestTeam;
import me.hifei.questmaster.api.team.QuestTeamScoreboard;
import me.hifei.questmaster.running.config.Message;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

class CQuestTeamScoreboard implements QuestTeamScoreboard {
    private final static int SCORE_COUNT = 13;
    private final static ScoreboardManager manager = Bukkit.getScoreboardManager();
    private final QuestTeam team;
    private final QuestGame game;
    private final Scoreboard scoreboard;
    private final List<Team> scores = new ArrayList<>();

    CQuestTeamScoreboard(QuestGame game, QuestTeam team) {
        this.team = team;
        this.game = game;
        assert manager != null;
        scoreboard = manager.getNewScoreboard();
        Objective mainObjective = scoreboard.registerNewObjective("questmaster", Criteria.DUMMY, Message.get("scoreboard.name"));
        mainObjective.setDisplaySlot(DisplaySlot.SIDEBAR);

        for (int i = 0; i < SCORE_COUNT; i++) {
            Team t = scoreboard.registerNewTeam(String.valueOf(i));
            String name = ChatColor.values()[i].toString();
            t.addEntry(name);
            mainObjective.getScore(name).setScore(SCORE_COUNT - i);
            scores.add(t);
        }
    }

    private void setScore(int i, String s) {
        if (i >= SCORE_COUNT || i < 0) return;
        scores.get(i).setPrefix(s);
    }

    @Override
    public void refresh() {
        if (!CoreManager.isGameStart()) return;
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yy");
        setScore(0, Message.get("scoreboard.date", format.format(date)));
        setScore(1, Message.get("scoreboard.empty"));
        setScore(2, Message.get("scoreboard.score", CoreManager.game.getGoal(), CoreManager.red.score(), CoreManager.blue.score()));
        setScore(3, Message.get("scoreboard.team", team.name(), team.score(), team.point(), team.coin()));
        setScore(4, Message.get("scoreboard.empty"));

        List<Quest> quests = team.getQuests();
        setScore(5, quests.get(0).getProcessBar());
        setScore(6, quests.get(0).getName());
        setScore(7, quests.get(1).getProcessBar());
        setScore(8, quests.get(1).getName());
        setScore(9, quests.get(2).getProcessBar());
        setScore(10, quests.get(2).getName());
        setScore(11, Message.get("scoreboard.empty"));
        setScore(12, Message.get("scoreboard.plugin"));
    }

    @Override
    public QuestTeam getTeam() {
        return team;
    }

    @Override
    public QuestGame getGame() {
        return game;
    }

    @Override
    public Scoreboard getBukkit() {
        return scoreboard;
    }
}
