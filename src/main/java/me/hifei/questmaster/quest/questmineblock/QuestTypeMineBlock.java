package me.hifei.questmaster.quest.questmineblock;

import me.hifei.questmaster.QuestMasterPlugin;
import me.hifei.questmaster.api.quest.*;
import me.hifei.questmaster.running.config.Message;
import me.hifei.questmaster.tools.DifficultTool;
import me.rockyhawk.commandpanels.openpanelsmanager.PanelPosition;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class QuestTypeMineBlock implements QuestType {
    final TableItem<Material> item;
    private Quest quest;
    private final double difficultValue;
    private final int count;
    private int totalItemCount = 0;

    public final Map<String, Integer> combo = new HashMap<>();
    public final Map<String, Long> lastCombo = new HashMap<>();

    static {
        Bukkit.getPluginManager().registerEvents(new QuestListenerMineBlock(), QuestMasterPlugin.instance);
    }

    public static @NotNull QuestTypeMineBlock create() {
        TableItem<Material> item;
        double target = DifficultTool.nextDifficult();
        int tryCount = 0;
        do {
            item = QuestTableMineBlock.ins.nextItem();
            tryCount++;
            if (tryCount > 100) {
                target = DifficultTool.nextDifficult();
                tryCount = 0;
            }
        } while (target < item.diff() * 8 || target > item.diff() * 64 * 5);
        int count = (int) Math.ceil((target) / (item.diff()));
        return new QuestTypeMineBlock(item, count, target);
    }

    private QuestTypeMineBlock(TableItem<Material> req, int count, double difficultValue) {
        item = req;
        this.count = count;
        this.difficultValue = difficultValue;
    }

    public void addCount() {
        totalItemCount++;
        if (totalItemCount == count) quest.complete();
    }

    @Override
    public @NotNull Quest quest() {
        return quest;
    }

    @Override
    public void sendQuestObject(Quest q) {
        quest = q;
    }

    @Override
    public boolean isCompleted() {
        return totalItemCount == count;
    }

    @Override
    public double progress() {
        return totalItemCount / (double) (count);
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
    public @NotNull String name() {
        return Message.get("quest.mineblock.name", difficult().name, item.name(), count);
    }

    @Override
    public int totalCount() {
        return count;
    }

    @Override
    public int currentCount() {
        return totalItemCount;
    }

    @Override
    public @NotNull ConfigurationSection item() {
        ConfigurationSection item = new MemoryConfiguration();
        item.set("material", this.item.obj().toString());
        item.set("stack", 1);
        item.set("name", name());
        return item;
    }

    @Override
    public void openPanel(@NotNull Player player) {
        QuestDPMineBlock.openDynamic(player, PanelPosition.Top, quest);
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        // This feature is already in todo state.
        // We won't support this feature for a long time.
        // And it means we won't write any new code for new feature of this feature.
        return new HashMap<>();
    }
}
