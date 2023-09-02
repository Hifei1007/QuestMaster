package me.hifei.questmaster.running.gsoncfg.rolling;

import java.util.Random;

public class DoubleBoundConfig {
    public double origin;
    public double bound;

    public double next() {
        if (Math.abs(bound + 1) <= 1e-9) return origin;
        Random random = new Random();
        return random.nextDouble(origin, bound);
    }
}
