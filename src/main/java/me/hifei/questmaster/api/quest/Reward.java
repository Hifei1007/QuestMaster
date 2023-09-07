package me.hifei.questmaster.api.quest;

import org.jetbrains.annotations.NotNull;

public record Reward(double score, double point, double coin, double time) {
    public @NotNull Reward multi(double multiplier) {
        return new Reward(score() * multiplier, point() * multiplier, coin() * multiplier, time() * multiplier);
    }

}
