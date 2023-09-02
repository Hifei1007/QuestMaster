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
        RewardConfig config = RollingConfig.cfg.reward;
        return new Reward(
                config.score.next(), config.point.next(), config.coin.next(), config.time.next()
        );
    }

    @Override
    public int time() {
        return (int) (difficultValue * RollingConfig.cfg.time.next());
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
