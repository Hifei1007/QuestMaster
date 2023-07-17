package me.hifei.questmaster.api.quest;

import me.hifei.questmaster.running.config.Message;

public enum Difficult {
    EASY(Message.get("difficult.easy"), 0, 50),
    NORMAL(Message.get("difficult.normal"), 50, 300),
    HARD(Message.get("difficult.hard"), 300, 1000),
    VERY_HARD(Message.get("difficult.very_hard"), 1000, 2000),
    HELL(Message.get("difficult.hell"), 2000, 4000)
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
