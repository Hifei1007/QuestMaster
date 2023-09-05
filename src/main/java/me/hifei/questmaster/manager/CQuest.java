package me.hifei.questmaster.manager;

import me.hifei.questmaster.api.CoreManager;
import me.hifei.questmaster.QuestMasterPlugin;
import me.hifei.questmaster.api.event.NormalQuestEvent;
import me.hifei.questmaster.api.quest.*;
import me.hifei.questmaster.api.state.State;
import me.hifei.questmaster.api.team.QuestTeam;
import me.hifei.questmaster.running.config.Message;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CQuest implements Quest {
    private final @NotNull QuestType questType;
    private final QuestTeam team;
    private final @NotNull Timer timer;
    private final @NotNull Reward finalReward;
    private @NotNull State state = State.WAIT;
    private final @NotNull List<QuestInterface> interfaces;

    CQuest(@NotNull QuestType qt, QuestTeam team, QuestInterface... interfaces) {
        questType = qt;
        this.team = team;
        timer = new Timer(questType.time());
        List<QuestInterface> tmp_if = new ArrayList<>(List.of(interfaces));
        tmp_if.addAll(qt.interfaces());
        this.interfaces = tmp_if;
        finalReward = qt.baseReward().multi(qt.difficultValue() / 5);
    }

    CQuest(@NotNull QuestType qt, QuestTeam team) {
        this(qt, team, new QuestInterface[]{});
    }

    @Override
    public @NotNull Timer getTimer() {
        return timer;
    }

    @Override
    public @NotNull QuestType getType() {
        return questType;
    }

    @Override
    public @NotNull QuestTeam getTeam() {
        return team;
    }

    @Override
    public @NotNull Difficult getDifficult() {
        return questType.difficult();
    }

    @Override
    public double getDifficultValue() {
        return questType.difficultValue();
    }

    @Override
    public @NotNull Reward getReward() {
        return finalReward;
    }

    @Override
    public double getProgress() {
        return questType.progress();
    }

    @Override
    public boolean isCompleted() {
        return questType.isCompleted();
    }

    @Override
    public @NotNull List<QuestInterface> interfaces() {
        List<QuestInterface> tmp = new ArrayList<>(interfaces);
        for (NormalQuestEvent event : CoreManager.game.getEvents()) {
            tmp.addAll(event.buildInterface(this));
        }
        return tmp;
    }

    @Override
    public void complete() {
        for (QuestInterface questInterface : interfaces()) {
            questInterface.complete();
        }
    }

    @Override
    public void timeUp() {
        for (QuestInterface questInterface : interfaces()) {
            questInterface.timeUp();
        }
    }

    @Override
    public @NotNull String getName() {
        return questType.name();
    }

    @Override
    public int getTotalCount() {
        return questType.totalCount();
    }

    @Override
    public int getCurrentCount() {
        return questType.currentCount();
    }

    @Override
    public @NotNull ConfigurationSection getItem() {
        return getItem(false);
    }

    public @NotNull ConfigurationSection getItem(boolean addClickText) {
        ConfigurationSection item = questType.item();
        List<String> lore = item.getStringList("lore");

        lore.add(Message.get("quest.empty"));
        lore.add(Message.get("quest.process", getCurrentCount(), getTotalCount(), getProgress() * 100, "%"));
        lore.add(Message.get("quest.difficult", getDifficultValue()));
        lore.add(Message.get("quest.timer", getTimer().remaining()));
        lore.add(Message.get("quest.empty"));
        lore.add(Message.get("quest.score", getReward().score()));
        lore.add(Message.get("quest.point", getReward().point(), getReward().point() / 10));
        lore.add(Message.get("quest.coin", getReward().coin(), getReward().coin() / 10));
        lore.add(Message.get("quest.time", getReward().time()));
        if (addClickText) {
            lore.add(Message.get("quest.empty"));
            lore.add(Message.get("quest.button_text"));
        }
        item.set("lore", lore);
        return item;
    }

    @Override
    public void openPanel(@NotNull Player player) {
        questType.openPanel(player);
    }

    @Override
    public String getProcessBar() {
        return Message.get("quest.process_bar", timer.remaining(), getCurrentCount(), getTotalCount(), getProgress() * 100, "%");
    }

    @Override
    public @NotNull State getState() {
        return state;
    }

    @Override
    public void startup() {
        if (state != State.WAIT) return;
        QuestMasterPlugin.logger.info("Startup quest %s".formatted(getName()));
        state = State.STARTUP;
        timer.start();
        questType.sendQuestObject(this);
        for (QuestInterface questInterface : interfaces()) {
            questInterface.startup(this);
        }
    }

    @Override
    public void drop() {
        if (state != State.STARTUP) return;
        QuestMasterPlugin.logger.info("Drop quest %s".formatted(getName()));
        state = State.DROP;
        for (QuestInterface questInterface : interfaces()) {
            questInterface.drop();
        }
    }
}