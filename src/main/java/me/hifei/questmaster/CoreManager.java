package me.hifei.questmaster;

import me.hifei.questmaster.api.QuestGame;
import me.hifei.questmaster.api.QuestManager;
import me.hifei.questmaster.api.state.State;
import me.hifei.questmaster.api.team.QuestTeam;
import me.hifei.questmaster.manager.CQuestManager;
import me.hifei.questmaster.running.config.Message;
import me.hifei.questmaster.tools.TranslateMaterialTool;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Scoreboard;

import java.util.Objects;

public class CoreManager {
    public final static TranslateMaterialTool translateMaterialTool = new TranslateMaterialTool("/zh_cn.json");
    public final static QuestManager manager = new CQuestManager();
    public static QuestTeam red = manager.createTeam(Message.get("team.red"), ChatColor.RED);
    public static QuestTeam blue = manager.createTeam(Message.get("team.blue"), ChatColor.BLUE);
    public final static Scoreboard emptyScoreboard = Objects.requireNonNull(Bukkit.getScoreboardManager()).getNewScoreboard();

    public static QuestGame game;

    public static boolean isGameStart() {
        return CoreManager.game != null && CoreManager.game.getState() != State.DROP;
    }
}
