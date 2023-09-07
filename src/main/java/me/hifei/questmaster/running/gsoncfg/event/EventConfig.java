package me.hifei.questmaster.running.gsoncfg.event;

import me.hifei.questmaster.running.gsoncfg.rolling.IntegerBoundConfig;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;

import java.util.List;

@SuppressWarnings("unused")
public class EventConfig {
    public static EventConfig cfg;

    public IntegerBoundConfig eventDelay;
    public IntegerBoundConfig comingDelay;
    public List<SingleEventConfig> events;
    public BarStyle comingStyle;
    public BarColor comingColor;
}
