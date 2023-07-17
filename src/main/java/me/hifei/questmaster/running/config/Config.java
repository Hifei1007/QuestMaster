package me.hifei.questmaster.running.config;

import me.hifei.questmaster.QuestMasterPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class Config {
    private final @NotNull File file;
    private final @NotNull String name;
    private final @NotNull FileConfiguration configuration;

    public Config(@NotNull String name) {
        this.name = name;
        file = new File(QuestMasterPlugin.instance.getDataFolder(), name);
        if (!file.exists()) {
            QuestMasterPlugin.instance.saveResource(name, false);
        }
        configuration = YamlConfiguration.loadConfiguration(file);
        InputStream stream = QuestMasterPlugin.instance.getResource(name);
        if (stream == null) return;
        configuration.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(stream, StandardCharsets.UTF_8)));
    }

    public @NotNull File getFile() {
        return file;
    }

    public String getName() {
        return name;
    }

    public @NotNull FileConfiguration getConfiguration() {
        return configuration;
    }

    public void save() {
        try {
            configuration.save(getFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
