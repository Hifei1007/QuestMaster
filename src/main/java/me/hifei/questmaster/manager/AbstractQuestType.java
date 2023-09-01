package me.hifei.questmaster.manager;

import me.hifei.questmaster.api.quest.*;
import me.hifei.questmaster.running.gsoncfg.rolling.RewardConfig;
import me.hifei.questmaster.running.gsoncfg.rolling.RollingConfig;
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
        RewardConfig config = RollingConfig.cfg.reward;
        return new Reward(
                random.nextDouble(config.score.origin, config.score.bound),
                random.nextDouble(config.point.origin, config.point.bound),
                random.nextDouble(config.coin.origin, config.coin.bound),
                random.nextDouble(config.time.origin, config.time.bound)
        );
    }

    @Override
    public int time() {
        Random random = new Random();
        return (int) (difficultValue * random.nextDouble(RollingConfig.cfg.time.origin,
                RollingConfig.cfg.time.bound));
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

    public TableItem<T> getTableItem() {
        return item;
    }
}
