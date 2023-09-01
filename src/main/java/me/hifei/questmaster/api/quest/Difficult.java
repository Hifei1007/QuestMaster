package me.hifei.questmaster.api.quest;

import me.hifei.questmaster.running.config.Message;
import me.hifei.questmaster.running.gsoncfg.rolling.RollingConfig;

public enum Difficult {
    EASY(Message.get("difficult.easy"),
            RollingConfig.cfg.easy.start,
            RollingConfig.cfg.easy.end),
    NORMAL(Message.get("difficult.normal"),
            RollingConfig.cfg.normal.start,
            RollingConfig.cfg.normal.end),
    HARD(Message.get("difficult.hard"),
            RollingConfig.cfg.hard.start,
            RollingConfig.cfg.hard.end),
    ;
    public final String name;
    public final double start;
    public final double end;

    Difficult (String name, double start, double end) {
        this.name = name;
        this.start = start;
        this.end = end;
    }
}
