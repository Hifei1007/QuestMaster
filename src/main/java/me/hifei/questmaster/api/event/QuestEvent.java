package me.hifei.questmaster.api.event;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import me.hifei.questmaster.QuestMasterPlugin;
import me.hifei.questmaster.api.state.Stateful;
import me.hifei.questmaster.running.config.Message;
import me.hifei.questmaster.running.gsoncfg.deserializer.DoubleBoundDeserializer;
import me.hifei.questmaster.running.gsoncfg.deserializer.IntegerBoundDeserializer;
import me.hifei.questmaster.running.gsoncfg.event.EventConfig;
import me.hifei.questmaster.running.gsoncfg.event.SingleEventConfig;
import me.hifei.questmaster.running.gsoncfg.rolling.DoubleBoundConfig;
import me.hifei.questmaster.running.gsoncfg.rolling.IntegerBoundConfig;
import org.bukkit.ChatColor;

import java.util.List;
import java.util.Random;

public abstract class QuestEvent implements Stateful {
    public final String name;
    public final List<String> descriptions;
    public final JsonObject settings;
    public final boolean isHidden;
    public final boolean isCorrupted;

    public QuestEvent(SingleEventConfig config) {
        JsonObject settings1;
        settings1 = config.settings;
        if (settings1 == null) settings1 = new JsonObject();
        this.settings = settings1;

        this.name = config.name.replace('&', ChatColor.COLOR_CHAR);
        this.descriptions = config.descriptions.stream().map((s) -> s.replace('&', ChatColor.COLOR_CHAR)).toList();
        preprocess();
        double hiddenChance = EventConfig.cfg.hiddenChance;
        if (settings.has("hiddenChance")) {
            hiddenChance = config.settings.get("hiddenChance").getAsDouble();
        }
        if (settings.has("corrupted") && settings.get("corrupted").getAsBoolean()) {
            isCorrupted = true;
            hiddenChance = 1;
        } else {
            isCorrupted = false;
        }
        this.isHidden = (new Random().nextDouble() < hiddenChance);
        QuestMasterPlugin.logger.info("<INIT> " + this.getName());
    }

    protected void preprocess() {}

    public String getName() {
        if (isHidden) return Message.get("event.prefix.hidden") + name;
        return name;
    }

    public String getDisplayName() {
        if (isHidden) {
            return Message.get("event.hidden.name");
        } else {
            return getName();
        }
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
