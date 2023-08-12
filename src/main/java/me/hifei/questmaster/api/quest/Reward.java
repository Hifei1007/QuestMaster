package me.hifei.questmaster.api.quest;

import org.jetbrains.annotations.NotNull;

public class Reward {
    private final double score;
    private final double point;
    private final double coin;
    private final double time;

    public @NotNull Reward multi (double multiplier) {
        return new Reward(score() * multiplier, point() * multiplier, coin() * multiplier, time() * multiplier);
    }

    public Reward(double score, double point, double coin, double time) {
        this.score = score;
        this.point = point;
        this.coin = coin;
        this.time = time;
    }

    public double score() {
        return score;
    }

    public double point() {
        return point;
    }

    public double coin() {
        return coin;
    }

    public double time() {
        return time;
    }
}
