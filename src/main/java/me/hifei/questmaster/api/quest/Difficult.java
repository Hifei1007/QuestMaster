package me.hifei.questmaster.api.quest;

import me.hifei.questmaster.running.config.Message;

public enum Difficult {
    EASY(Message.get("difficult.easy"), 20, 150),
    NORMAL(Message.get("difficult.normal"), 150, 500),
    HARD(Message.get("difficult.hard"), 500, 1500),
    VERY_HARD(Message.get("difficult.very_hard"), 1500, 4000),
    HELL(Message.get("difficult.hell"), 4000, 10000)
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
