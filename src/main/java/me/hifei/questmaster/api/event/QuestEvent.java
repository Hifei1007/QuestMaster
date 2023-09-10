package me.hifei.questmaster.api.event;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import me.hifei.questmaster.QuestMasterPlugin;
import me.hifei.questmaster.api.state.Stateful;
import me.hifei.questmaster.running.gsoncfg.deserializer.DoubleBoundDeserializer;
import me.hifei.questmaster.running.gsoncfg.deserializer.IntegerBoundDeserializer;
import me.hifei.questmaster.running.gsoncfg.event.SingleEventConfig;
import me.hifei.questmaster.running.gsoncfg.rolling.DoubleBoundConfig;
import me.hifei.questmaster.running.gsoncfg.rolling.IntegerBoundConfig;
import org.bukkit.ChatColor;

import java.util.List;

public abstract class QuestEvent implements Stateful {
    public final String name;
    public final List<String> descriptions;
    public final JsonObject settings;

    public QuestEvent(SingleEventConfig config) {
        this.settings = config.settings;
        this.name = config.name.replace('&', ChatColor.COLOR_CHAR);
        this.descriptions = config.descriptions.stream().map((s) -> s.replace('&', ChatColor.COLOR_CHAR)).toList();
        preprocess();
        QuestMasterPlugin.logger.info("<INIT> " + this.getName());
    }

    protected void preprocess() {}

    public String getName() {
        return name;
    }

    public List<String> getDescriptions() {
        return descriptions;
    }

    public <T> T loadSettings(Class<T> clazz) {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(IntegerBoundConfig.class, new IntegerBoundDeserializer());
        builder.registerTypeAdapter(DoubleBoundConfig.class, new DoubleBoundDeserializer());
        Gson gson = builder.create();
        return gson.fromJson(settings, clazz);
    }
}
