package me.hifei.questmaster.running.config;

import me.hifei.questmaster.CoreManager;
import me.hifei.questmaster.QuestMasterPlugin;
import me.hifei.questmaster.api.QuestGame;
import me.hifei.questmaster.api.quest.*;
import me.hifei.questmaster.api.team.QuestTeam;
import me.hifei.questmaster.manager.CQuest;
import me.hifei.questmaster.manager.CQuestGame;
import me.hifei.questmaster.manager.CQuestTeam;
import me.hifei.questmaster.manager.GameMainQuestInterface;
import me.hifei.questmaster.quest.questcollectitem.QuestTypeCollectItem;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

public class GameSavingCore {
    public static void registerClass() {
        ConfigurationSerialization.registerClass(QuestGame.class);
        ConfigurationSerialization.registerClass(QuestTeam.class);
        ConfigurationSerialization.registerClass(Quest.class);
        ConfigurationSerialization.registerClass(QuestType.class);
        ConfigurationSerialization.registerClass(QuestInterface.class);
        ConfigurationSerialization.registerClass(Reward.class);
        ConfigurationSerialization.registerClass(Timer.class);
        ConfigurationSerialization.registerClass(GameMainQuestInterface.class);
        ConfigurationSerialization.registerClass(QuestTypeCollectItem.class);
        ConfigurationSerialization.registerClass(CQuest.class);
        ConfigurationSerialization.registerClass(CQuestTeam.class);
        ConfigurationSerialization.registerClass(CQuestGame.class);

    }

    public static void load() {
        try {
            QuestMasterPlugin.logger.info("Loading game data...");
            Config config = new Config("data.yml");
            Configuration configuration = config.getConfiguration();
            if (configuration.getBoolean("started")) {
                CoreManager.red = configuration.getSerializable("red", CQuestTeam.class);
                CoreManager.blue = configuration.getSerializable("blue", CQuestTeam.class);
                CoreManager.game = configuration.getSerializable("game", CQuestGame.class);
            }
            QuestMasterPlugin.logger.info("Loaded game data!");
        } catch (RuntimeException e) {
            QuestMasterPlugin.logger.severe("* ERROR - I HOPE YOU REPORT THIS! *");
            QuestMasterPlugin.logger.severe("* QuestMaster: [Loading]");
            QuestMasterPlugin.logger.severe("* FAIL: Can't load game data");
            QuestMasterPlugin.logger.severe("* Stack:");
            e.printStackTrace();
            QuestMasterPlugin.logger.severe("* Try:");
            QuestMasterPlugin.logger.severe("*   Remove other plugin");
            QuestMasterPlugin.logger.severe("*   Report this issue");
            QuestMasterPlugin.logger.severe("*   Clear file data.yml");
            QuestMasterPlugin.logger.severe("*   Or just ignore it!");
            QuestMasterPlugin.logger.severe("* The plugin maybe will load some data and drop some data");
            QuestMasterPlugin.logger.severe("* And load the game");
            QuestMasterPlugin.logger.severe("* ERROR - I HOPE YOU REPORT THIS! *");
        }
    }

    public static void save() {
        if (CoreManager.isGameStart()) {
            QuestMasterPlugin.logger.info("Saving game data...");
            Config config = new Config("data.yml");
            Configuration configuration = config.getConfiguration();
            configuration.set("started", true);
            configuration.set("red", CoreManager.red);
            configuration.set("blue", CoreManager.blue);
            configuration.set("game", CoreManager.game);
            config.save();
            QuestMasterPlugin.logger.info("Saved game data!");
        } else {
            QuestMasterPlugin.instance.saveResource("data.yml", true);
        }
    }
}