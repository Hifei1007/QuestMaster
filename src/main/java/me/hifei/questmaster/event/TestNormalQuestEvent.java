package me.hifei.questmaster.event;

import me.hifei.questmaster.api.event.NormalQuestEvent;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;

import java.util.List;
import java.util.Map;

public class TestNormalQuestEvent extends NormalQuestEvent {
    public TestNormalQuestEvent(String name, List<String> descriptions, Map<String, Object> settings, int time, BarColor color, BarStyle style) {
        super(name, descriptions, settings, time, color, style);
    }
}
