package me.hifei.questmaster.api.event;

import me.hifei.questmaster.QuestMasterPlugin;
import me.hifei.questmaster.api.state.Stateful;

import java.util.List;
import java.util.Map;

public abstract class QuestEvent implements Stateful {
    public final String name;
    public final List<String> descriptions;
    public final Map<String, Object> settings;

    public QuestEvent(String name, List<String> descriptions, Map<String, Object> settings) {
        QuestMasterPlugin.logger.info("<INIT> " + this.getClass().getName());
        this.name = name;
        this.descriptions = descriptions;
        this.settings = settings;
    }


    public String getName() {
        return name;
    }

    public List<String> getDescriptions() {
        return descriptions;
    }
}
