package me.hifei.questmaster.event;

import me.hifei.questmaster.api.event.InstantQuestEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.List;
import java.util.Map;

public class TestInstantQuestEvent extends InstantQuestEvent {
    public TestInstantQuestEvent(String name, List<String> descriptions, Map<String, Object> settings) {
        super(name, descriptions, settings);
    }

    @Override
    public void doChange() {
        Bukkit.broadcastMessage(ChatColor.GOLD + "Hello World!");
    }
}
