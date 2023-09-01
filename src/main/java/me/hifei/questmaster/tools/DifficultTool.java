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
        NORMAL: 30%
        HARD: 5%
         */

        Difficult difficult;
        if (i < 650) difficult = Difficult.EASY;
        else if (i < 950) difficult = Difficult.NORMAL;
        else difficult = Difficult.HARD;

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
