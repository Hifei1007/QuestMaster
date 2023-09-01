package me.hifei.questmaster.api.quest;

import me.hifei.questmaster.running.config.Message;

public enum Difficult {
    EASY(Message.get("difficult.easy"), 20, 80),
    NORMAL(Message.get("difficult.normal"), 80, 200),
    HARD(Message.get("difficult.hard"), 200, 500),
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
