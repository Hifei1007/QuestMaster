package me.hifei.questmaster.tools;

import me.hifei.questmaster.api.quest.Difficult;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class DifficultTool {

    public static double nextDifficult() {
        Random random = new Random();
        int i = random.nextInt(1000);
        /*
        EASY: 55%
        NORMAL: 40%
        HARD: 4%
        VERY_HARD 0.7%
        HELL 0.3%
         */

        Difficult difficult;
        if (i < 550) difficult = Difficult.EASY;
        else if (i < 950) difficult = Difficult.NORMAL;
        else if (i < 990) difficult = Difficult.HARD;
        else if (i < 997) difficult = Difficult.VERY_HARD;
        else difficult = Difficult.HELL;

        double value = random.nextDouble(difficult.start, difficult.end);
        while (random.nextBoolean()) {
            value *= random.nextDouble(0.5, 2);
        }
        return Math.max(random.nextDouble(3, 8), value);
    }

    public static @NotNull Difficult getDifficult(double value) {
        Difficult[] test = {Difficult.HELL,Difficult.VERY_HARD,Difficult.HARD,Difficult.NORMAL,Difficult.EASY};
        for (Difficult difficult : test) {
            if (value >= difficult.start) return difficult;
        }
        return Difficult.EASY;
    }
}
