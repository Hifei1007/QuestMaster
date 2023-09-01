package me.hifei.questmaster.manager;

import me.hifei.questmaster.api.quest.*;
import me.hifei.questmaster.tools.DifficultTool;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;

public abstract class AbstractQuestType<T> implements QuestType {
    protected final TableItem<T> item;
    protected int currentCount = 0;
    protected final int totalCount;
    protected Quest quest;
    protected final double difficultValue;

    protected AbstractQuestType(TableItem<T> item, int totalCount, double difficultValue) {
        this.item = item;
        this.totalCount = totalCount;
        this.difficultValue = difficultValue;
    }

    @Override
    public @NotNull Quest quest() {
        return quest;
    }

    public void sendQuestObject(Quest q) {
        quest = q;
    }

    @Override
    public boolean isCompleted() {
        return totalCount == currentCount;
    }

    @Override
    public double progress() {
        return currentCount / (double) (totalCount);
    }

    @Override
    public @NotNull Reward baseReward() {
        Random random = new Random();
        return new Reward(
                random.nextDouble(0.1, 0.3),
                random.nextDouble(1.5, 2),
                random.nextDouble(20, 50),
                random.nextDouble(0.5, 2)
        );
    }

    @Override
    public int time() {
        Random random = new Random();
        return
                (int) (random.nextInt(90, 120) * Math.log10(difficultValue) +
                        (int) (difficultValue * random.nextDouble(4, 7)));
    }

    @Override
    public @NotNull Difficult difficult() {
        return DifficultTool.getDifficult(difficultValue);
    }

    @Override
    public double difficultValue() {
        return difficultValue;
    }

    @Override
    public @NotNull List<QuestInterface> interfaces() {
        return List.of();
    }

    @Override
    public int totalCount() {
        return totalCount;
    }

    @Override
    public int currentCount() {
        return currentCount;
    }
}
