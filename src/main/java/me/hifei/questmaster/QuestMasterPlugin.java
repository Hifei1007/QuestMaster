package me.hifei.questmaster;

import me.hifei.questmaster.api.quest.Quest;
import me.hifei.questmaster.api.team.QuestTeam;
import me.hifei.questmaster.running.commands.ForceStopCommand;
import me.hifei.questmaster.running.commands.StartCommand;
import me.hifei.questmaster.running.listeners.ChatListener;
import me.hifei.questmaster.running.runners.MainUpdater;
import me.hifei.questmaster.ui.GUIListener;
import me.hifei.questmaster.ui.DynamicPanel;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.logging.Logger;

public class QuestMasterPlugin extends JavaPlugin {
    public static QuestMasterPlugin instance;
    public static Logger logger;

    public static void main(String[] args) {
        throw new RuntimeException("IT'S A SPIGOT PLUGIN!");
    }

    @Override
    public void onEnable() {
        instance = this;
        logger = getLogger();

        Objects.requireNonNull(this.getCommand("start")).setExecutor(new StartCommand());
        Objects.requireNonNull(this.getCommand("forcestop")).setExecutor(new ForceStopCommand());
        new MainUpdater().runTaskTimer(QuestMasterPlugin.instance, 0, 1);

        Bukkit.getPluginManager().registerEvents(new GUIListener(), this);
        Bukkit.getPluginManager().registerEvents(new ChatListener(), this);

        // todo: GameSavingCore.registerClass();
        // todo: GameSavingCore.load();
    }

    @Override
    public void onDisable() {
        // todo: GameSavingCore.save();
        for (QuestTeam team : CoreManager.manager.getTeams()) {
            {
                team.clear();
                for (Quest q : team.getQuests()) {
                    q.drop();
                }
            }
        }
        DynamicPanel.clear();
        if (CoreManager.isGameStart()) CoreManager.game.drop();
    }
}
