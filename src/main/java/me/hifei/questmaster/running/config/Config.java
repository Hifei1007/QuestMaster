package me.hifei.questmaster.running.config;

import me.hifei.questmaster.QuestMasterPlugin;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class Config {
    private final @NotNull String name;
    private final @NotNull Configuration configuration;

    public Config(@NotNull String name, boolean needSave) {
        this.name = name;
        InputStream stream = QuestMasterPlugin.instance.getResource(name);
        if (stream == null) {
            configuration = new MemoryConfiguration();
            return;
        }
        Configuration streamConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(stream, StandardCharsets.UTF_8));
        if (needSave) {
            File file = new File(QuestMasterPlugin.instance.getDataFolder(), name);
            if (!file.exists()) {
                QuestMasterPlugin.instance.saveResource(name, false);
            }
            configuration = YamlConfiguration.loadConfiguration(file);
            configuration.setDefaults(streamConfig);
        } else {
            configuration = streamConfig;
        }
        try {
            stream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public @NotNull String getName() {
        return name;
    }

    public @NotNull Configuration getConfiguration() {
        return configuration;
    }
}
