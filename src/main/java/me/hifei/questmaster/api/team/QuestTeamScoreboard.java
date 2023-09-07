package me.hifei.questmaster.api.team;

import me.hifei.questmaster.api.QuestGame;
import org.bukkit.scoreboard.Scoreboard;

public interface QuestTeamScoreboard {
    void refresh();
    @SuppressWarnings("unused")
    QuestTeam getTeam();
    @SuppressWarnings("unused")
    QuestGame getGame();

    Scoreboard getBukkit();
}
