package me.hifei.questmaster.running.gsoncfg;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.hifei.questmaster.QuestMasterPlugin;
import me.hifei.questmaster.running.gsoncfg.deserializer.DoubleBoundDeserializer;
import me.hifei.questmaster.running.gsoncfg.deserializer.IntegerBoundDeserializer;
import me.hifei.questmaster.running.gsoncfg.rolling.DoubleBoundConfig;
import me.hifei.questmaster.running.gsoncfg.rolling.IntegerBoundConfig;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class GsonConfigLoader {
    public static <T> T loadConfig(Class<T> clazz, String path) {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(IntegerBoundConfig.class, new IntegerBoundDeserializer());
        builder.registerTypeAdapter(DoubleBoundConfig.class, new DoubleBoundDeserializer());
        Gson gson = builder.create();
        if (QuestMasterPlugin.instance.getResource("config/" + path) == null)
            throw new IllegalArgumentException("File doesn't exists!");
        File file = new File(QuestMasterPlugin.instance.getDataFolder(), "config/" + path);
        if (!file.exists()) {
            QuestMasterPlugin.instance.saveResource("config/" + path, false);
        }
        try {
            QuestMasterPlugin.logger.info("Loaded config " + path);
            return gson.fromJson(new FileReader(file, StandardCharsets.UTF_8), clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
