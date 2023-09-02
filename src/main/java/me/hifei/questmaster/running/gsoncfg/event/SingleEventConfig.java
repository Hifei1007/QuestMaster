package me.hifei.questmaster.running.gsoncfg.event;

import me.hifei.questmaster.api.event.InstantQuestEvent;
import me.hifei.questmaster.api.event.NormalQuestEvent;
import me.hifei.questmaster.api.event.QuestEvent;
import me.hifei.questmaster.running.gsoncfg.rolling.IntegerBoundConfig;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

public class SingleEventConfig {
    public boolean enabled;
    public String classPath;
    public String name;
    public List<String> descriptions;
    public IntegerBoundConfig time;
    public int weight;
    public Map<String, Object> settings;
    public BarColor barColor;
    public BarStyle barStyle;

    public QuestEvent build() {
        try {
            Class<?> clazz = Class.forName(classPath);
            if (NormalQuestEvent.class.isAssignableFrom(clazz)) {
                Constructor<?> constructor = clazz.getDeclaredConstructor(String.class, List.class, Map.class, int.class, BarColor.class, BarStyle.class);
                constructor.setAccessible(true);
                return (QuestEvent) constructor.newInstance(name.replace('&', ChatColor.COLOR_CHAR),
                        descriptions.stream().map((str) -> str.replace('&', ChatColor.COLOR_CHAR)).toList(),
                        settings, time.next(), barColor, barStyle);
            } else if (InstantQuestEvent.class.isAssignableFrom(clazz)) {
                Constructor<?> constructor = clazz.getDeclaredConstructor(String.class, List.class, Map.class);
                constructor.setAccessible(true);
                return (QuestEvent) constructor.newInstance(name.replace('&', ChatColor.COLOR_CHAR),
                        descriptions.stream().map((str) -> str.replace('&', ChatColor.COLOR_CHAR)).toList(),
                        settings);
            } else throw new RuntimeException("The class must be a subclass of InstantQuestEvent or NormalQuestEvent");
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException |
                 IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
