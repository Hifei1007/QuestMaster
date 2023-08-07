package me.hifei.questmaster.manager;

import me.hifei.questmaster.CoreManager;
import me.hifei.questmaster.api.QuestGame;
import me.hifei.questmaster.api.quest.Quest;
import me.hifei.questmaster.api.state.State;
import me.hifei.questmaster.api.state.Stateful;
import me.hifei.questmaster.api.team.QuestTeam;
import me.hifei.questmaster.api.team.QuestTeamScoreboard;
import me.hifei.questmaster.running.config.Message;
import me.hifei.questmaster.shop.Upgrade;
import me.hifei.questmaster.ui.DynamicPanel;
import me.hifei.questmaster.ui.UIManager;
import me.hifei.questmaster.ui.dynamic.DPRootNotStarted;
import me.rockyhawk.commandpanels.openpanelsmanager.PanelPosition;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;

public class CQuestGame implements QuestGame {
    private final List<QuestTeam> teams;
    private final int goal;
    private @NotNull State state = State.WAIT;
    private final Map<QuestTeam, QuestTeamScoreboard> scoreboardMap = new HashMap<>();

    CQuestGame(List<QuestTeam> teams, int goal) {
        this.teams = teams;
        this.goal = goal;
        for (QuestTeam team : teams) {
            scoreboardMap.put(team, CoreManager.manager.createScoreboard(this, team));
        }
    }

    public CQuestGame(@NotNull Map<String, Object> serialized) {
        teams = new ArrayList<>();
        List<?> list = (List<?>) (serialized.get("teams"));
        for (Object s : list) {
            String str = (String) s;
            teams.add(CoreManager.manager.getTeam(str));
        }
        goal = (int) serialized.get("goal");
        for (QuestTeam team : teams) {
            scoreboardMap.put(team, CoreManager.manager.createScoreboard(this, team));
        }
        state = State.STARTUP;
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> serializer = new HashMap<>();
        List<String> nameList = new ArrayList<>();
        runEachTeam(t -> nameList.add(t.name()));
        serializer.put("teams", nameList);
        serializer.put("goal", goal);
        return serializer;
    }

    @Override
    public List<QuestTeam> getTeams() {
        return teams;
    }

    @Override
    public void runEachTeam(@NotNull Consumer<QuestTeam> consumer) {
        for (QuestTeam team : getTeams()) consumer.accept(team);
    }

    @Override
    public @NotNull Map<QuestTeam, QuestTeamScoreboard> getScoreboardMapping() {
        return scoreboardMap;
    }



    @Override
    public int getGoal() {
        return goal;
    }

    @Override
    public @NotNull List<Player> getPlayers() {
        List<Player> list = new ArrayList<>();
        runEachTeam((t) -> list.addAll(t.members()));
        return list;
    }

    @Override
    public void runEachPlayer(@NotNull Consumer<Player> consumer) {
        for (Player player : getPlayers()) consumer.accept(player);
    }

    @Override
    public void checkScore(@NotNull QuestTeam team) {
        if (team.score() >= goal) {
            runEachPlayer(player -> {
                player.sendMessage(Message.get("game.victory.message", team.name()));
                player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1, 1);
                player.sendTitle(
                        Message.get("game.victory.title", team.name()),
                        Message.get("game.victory.subtitle", team.name(), team.score()),
                        10, 70, 20
                );
            });
            this.drop();
        }
    }

    @Override
    public @NotNull State getState() {
        return state;
    }

    @Override
    public void startup() {
        if (state != State.WAIT) return;
        state = State.STARTUP;

        Bukkit.broadcastMessage(Message.get("game.start.message", goal));
        runEachPlayer(player -> {
            QuestTeam team = CoreManager.manager.getTeam(player);
            player.setScoreboard(scoreboardMap.get(team).getBukkit());
            player.playSound(player, Sound.ENTITY_ENDER_DRAGON_GROWL, 1, 1);
            player.sendTitle(
                    Message.get("game.start.title"),
                    Message.get("game.start.subtitle", CoreManager.game.getGoal(), Objects.requireNonNull(CoreManager.manager.getTeam(player)).name()), 10, 70, 20);
        });

        runEachTeam((team) -> {
            team.init();
            for (int i = 1; i <= 3; i++) {
                addQuest(team);
            }
            for (Upgrade upgrade : team.getUpgrades().values()) {
                upgrade.startup();
            }
        });
    }

    void addQuest(@NotNull QuestTeam team) {
        Quest quest = CoreManager.manager.createQuest(CoreManager.manager.createType(), team, new GameMainQuestInterface(team));
        quest.startup();
        team.getQuests().add(quest);
        runEachTeam(t -> {
            if (t == team) {
                    t.teamBroadcast(Message.get("game.task.get1", quest.getName()));
                } else {
                    t.teamBroadcast(Message.get("game.task.get2", team.name(), quest.getName()));
                }
        });
    }

    @Override
    public void drop() {
        if (state != State.STARTUP) return;
        state = State.DROP;
        runEachTeam((t) -> {
            t.getQuests().forEach(Stateful::drop);
            for (Upgrade upgrade : t.getUpgrades().values()) upgrade.drop();
            t.init();
        });
        Bukkit.broadcastMessage(Message.get("game.stop.message"));
        runEachPlayer(p -> {
            p.setScoreboard(CoreManager.emptyScoreboard);
            if (UIManager.API.isPanelOpen(p) && UIManager.API.getOpenPanel(p, PanelPosition.Top) instanceof DynamicPanel dp) {
                dp.close();
                DPRootNotStarted.openDynamic(p, PanelPosition.Top);
            }
        });
    }
}
