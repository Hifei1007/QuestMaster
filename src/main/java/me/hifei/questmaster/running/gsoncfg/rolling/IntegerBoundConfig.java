package me.hifei.questmaster.running.gsoncfg.rolling;

import java.util.Random;

public class IntegerBoundConfig {
    public int origin;
    public int bound;

    public int next() {
        if (bound == -1) return origin;
        Random random = new Random();
        return random.nextInt(origin, bound);
    }
}
