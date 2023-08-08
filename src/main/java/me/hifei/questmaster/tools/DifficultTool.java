package me.hifei.questmaster.tools;

import me.hifei.questmaster.api.quest.Difficult;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class DifficultTool {

    public static double nextDifficult() {
        Random random = new Random();
        int i = random.nextInt(1000);
        /*
        EASY: 65%
        NORMAL: 33%
        HARD: 1.3%
        VERY_HARD 0.5%
        HELL 0.2%
         */

        Difficult difficult;
        if (i < 650) difficult = Difficult.EASY;
        else if (i < 980) difficult = Difficult.NORMAL;
        else if (i < 993) difficult = Difficult.HARD;
        else if (i < 998) difficult = Difficult.VERY_HARD;
        else difficult = Difficult.HELL;

        double value = difficult.start * random.nextDouble(1, 1.5);
        while ((random.nextBoolean() || random.nextBoolean()) && value <= difficult.end) {
            value *= random.nextDouble(1, 1.5);
        }
        return Math.min(difficult.end, value);
    }

    public static @NotNull Difficult getDifficult(double value) {
        Difficult[] test = {Difficult.HELL,Difficult.VERY_HARD,Difficult.HARD,Difficult.NORMAL,Difficult.EASY};
        for (Difficult difficult : test) {
            if (value >= difficult.start) return difficult;
        }
        return Difficult.EASY;
    }
}
