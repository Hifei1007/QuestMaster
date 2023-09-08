package me.hifei.questmaster;

import me.hifei.questmaster.api.CoreManager;
import me.hifei.questmaster.api.quest.QuestType;
import me.hifei.questmaster.api.team.QuestTeam;
import me.hifei.questmaster.quest.questcollectitem.QuestTableCollectItem;
import me.hifei.questmaster.quest.questkillmob.QuestTableKillMob;
import me.hifei.questmaster.quest.questmineblock.QuestTableMineBlock;
import me.hifei.questmaster.running.commands.QuestActionCommand;
import me.hifei.questmaster.running.commands.StartCommand;
import me.hifei.questmaster.running.gsoncfg.GsonConfigLoader;
import me.hifei.questmaster.running.gsoncfg.event.EventConfig;
import me.hifei.questmaster.running.gsoncfg.event.SingleEventConfig;
import me.hifei.questmaster.running.gsoncfg.rolling.QuestTypeConfig;
import me.hifei.questmaster.running.gsoncfg.rolling.RollingConfig;
import me.hifei.questmaster.running.listeners.ChatListener;
import me.hifei.questmaster.running.listeners.DeathListener;
import me.hifei.questmaster.running.runners.MainUpdater;
import me.hifei.questmaster.ui.core.DynamicPanel;
import me.hifei.questmaster.ui.core.GUIListener;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.logging.Logger;

public class QuestMasterPlugin extends JavaPlugin {
    public static QuestMasterPlugin instance;
    public static Logger logger;

    public static void main(String[] args) {
        throw new RuntimeException("IT'S A SPIGOT PLUGIN!");
    }

    public void loadTables() {
        QuestTableCollectItem.ins.loadConfig();
        QuestTableMineBlock.ins.loadConfig();
        QuestTableKillMob.ins.loadConfig();
    }

    @SuppressWarnings("unchecked")
    public void registerQuestType() {
        for (QuestTypeConfig questTypeConfig : RollingConfig.cfg.questType) {
            if (!questTypeConfig.enabled) continue;
            try {
                Class<?> clazz = Class.forName(questTypeConfig.classPath);
                Class<? extends QuestType> c = (Class<? extends QuestType>) clazz;
                CoreManager.manager.registerType(c, questTypeConfig.weight);
                logger.info("Success registered quest type %s".formatted(c.getName()));
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void registerEvents() {
        for (SingleEventConfig config : EventConfig.cfg.events) {
            if (!config.enabled) continue;
            logger.info("Success registered event %s".formatted(config.classPath));
            CoreManager.manager.registerEvent(config);
        }
    }

    @Override
    public void onEnable() {
        instance = this;
        logger = getLogger();

        RollingConfig.cfg = GsonConfigLoader.loadConfig(RollingConfig.class, "rolling.json");
        EventConfig.cfg = GsonConfigLoader.loadConfig(EventConfig.class, "event.json");

        for (World world : Bukkit.getWorlds()) {
            world.setGameRule(GameRule.KEEP_INVENTORY, true);
        }

        Objects.requireNonNull(this.getCommand("start")).setExecutor(new StartCommand());
        Objects.requireNonNull(this.getCommand("questaction")).setExecutor(new QuestActionCommand());
        new MainUpdater().runTaskTimer(QuestMasterPlugin.instance, 0, 1);

        Bukkit.getPluginManager().registerEvents(new GUIListener(), this);
        Bukkit.getPluginManager().registerEvents(new ChatListener(), this);
        Bukkit.getPluginManager().registerEvents(new DeathListener(), this);

        loadTables();
        registerQuestType();
        registerEvents();
    }

    @Override
    public void onDisable() {
        if (CoreManager.isGameStart()) CoreManager.game.drop();
        for (QuestTeam team : CoreManager.manager.getTeams()) {
            {
                team.clear();
            }
        }
        DynamicPanel.clear();
    }
}
