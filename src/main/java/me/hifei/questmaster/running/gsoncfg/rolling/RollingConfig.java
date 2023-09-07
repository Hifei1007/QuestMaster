package me.hifei.questmaster.running.gsoncfg.rolling;

import java.util.List;

@SuppressWarnings("unused")
public class RollingConfig {
    public static RollingConfig cfg;
    public DifficultConfig easy;
    public DifficultConfig normal;
    public DifficultConfig hard;
    public RewardConfig reward;
    public DoubleBoundConfig time;
    public TeleportConfig teleport;
    public IntegerBoundConfig dynamicPanelRange;
    public List<QuestTypeConfig> questType;
}
