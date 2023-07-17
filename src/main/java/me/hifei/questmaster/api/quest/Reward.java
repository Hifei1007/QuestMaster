package me.hifei.questmaster.api.quest;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class Reward implements ConfigurationSerializable {
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

    public Reward(Map<String, Object> serializer) {
        this.score = (double) serializer.get("score");
        this.point = (double) serializer.get("point");
        this.coin = (double) serializer.get("coin");
        this.time = (double) serializer.get("time");
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> serializer = new HashMap<>();
        serializer.put("score", score);
        serializer.put("point", point);
        serializer.put("coin", coin);
        serializer.put("time", time);
        return serializer;
    }
}
