package me.hifei.questmaster.running.gsoncfg.rolling;

import java.util.List;

public class RollingConfig {
    public static RollingConfig cfg;

    public DifficultConfig easy;
    public DifficultConfig normal;
    public DifficultConfig hard;
    public RewardConfig reward;
    public BoundConfig time;
    public TeleportConfig teleport;
    public IntegerBoundConfig dynamicPanelRange;
    public List<QuestTypeConfig> questType;
}
