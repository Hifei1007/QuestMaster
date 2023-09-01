package me.hifei.questmaster.running.gsoncfg;

import com.google.gson.Gson;
import me.hifei.questmaster.QuestMasterPlugin;

import java.io.*;

public class GsonConfigLoader {
    public static <T> T loadConfig(Class<T> clazz, String path) {
        Gson gson = new Gson();
        if (QuestMasterPlugin.instance.getResource("config/" + path) == null) throw new IllegalArgumentException("File doesn't exists!");
        File file = new File(QuestMasterPlugin.instance.getDataFolder(), "config/" + path);
        if (!file.exists()) {
            QuestMasterPlugin.instance.saveResource("config/" + path, false);
        }
        try {
            QuestMasterPlugin.logger.info("Loaded config " + path);
            return gson.fromJson(new FileReader(file), clazz);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
