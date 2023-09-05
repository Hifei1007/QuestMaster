package me.hifei.questmaster.running.runners;

import me.hifei.questmaster.api.CoreManager;
import me.hifei.questmaster.api.ExceptionLock;
import me.hifei.questmaster.api.team.QuestTeam;
import me.hifei.questmaster.api.team.QuestTeamScoreboard;
import me.hifei.questmaster.running.config.Message;
import me.hifei.questmaster.tools.ActionTool;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public class MainUpdater extends BukkitRunnable {
    private final ExceptionLock lock = new ExceptionLock();

    private void updateTab(@NotNull Player player) {
        if (!CoreManager.isGameStart()) {
            player.setPlayerListHeader(Message.get("tablist.not_started.header"));
            player.setPlayerListFooter(Message.get("tablist.not_started.footer",
                    (CoreManager.manager.hasTeam(player) ? CoreManager.manager.getTeam(player).name() : Message.get("team.null"))
            ));
        } else {
            player.setPlayerListHeader(Message.get("tablist.started.header", CoreManager.game.getGoal(),
                    CoreManager.red.score(), CoreManager.blue.score()));
            QuestTeam team = CoreManager.manager.getTeam(player);
            player.setPlayerListFooter(Message.get("tablist.started.footer",
                            CoreManager.manager.hasTeam(player) ?
                            Message.get("tablist.started.footer_bar", team.name(), team.score(), team.point(), team.coin()) :
                            Message.get("team.null")
            ));
        }
    }

    @Override
    public void run() {
        lock.run(() -> {
            ActionTool.tick();
            if (CoreManager.isGameStart()) {
                for (QuestTeamScoreboard scoreboard : CoreManager.game.getScoreboardMapping().values()) {
                    scoreboard.refresh();
                }
            }
            for (Player player : Bukkit.getOnlinePlayers()) updateTab(player);
        });
    }
}
