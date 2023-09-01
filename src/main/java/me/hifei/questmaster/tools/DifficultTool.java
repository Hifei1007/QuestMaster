package me.hifei.questmaster.tools;

import me.hifei.questmaster.api.quest.Difficult;
import me.hifei.questmaster.running.gsoncfg.rolling.RollingConfig;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class DifficultTool {

    public static double nextDifficult() {
        Random random = new Random();
        int i = random.nextInt(1000);
        /*
        EASY: 65%
        NORMAL: 30%
        HARD: 5%
         */

        Difficult difficult;
        if (i < RollingConfig.cfg.easy.selectWhen) difficult = Difficult.EASY;
        else if (i < RollingConfig.cfg.normal.selectWhen) difficult = Difficult.NORMAL;
        else if (i < RollingConfig.cfg.hard.selectWhen) difficult = Difficult.HARD;
        else throw new RuntimeException("The max value of selectWhen doesn't match 1000!");

        double value = difficult.start * random.nextDouble(1, 1.5);
        while ((random.nextBoolean() || random.nextBoolean()) && value <= difficult.end) {
            value *= random.nextDouble(1, 1.5);
        }
        return Math.min(difficult.end, value);
    }

    public static @NotNull Difficult getDifficult(double value) {
        Difficult[] test = {Difficult.HARD,Difficult.NORMAL,Difficult.EASY};
        for (Difficult difficult : test) {
            if (value >= difficult.start) return difficult;
        }
        return Difficult.EASY;
    }
}
