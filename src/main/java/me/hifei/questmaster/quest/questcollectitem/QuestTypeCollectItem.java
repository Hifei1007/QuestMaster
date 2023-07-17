package me.hifei.questmaster.quest.questcollectitem;

import me.hifei.questmaster.api.quest.*;
import me.hifei.questmaster.running.config.Message;
import me.hifei.questmaster.tools.DifficultTool;
import me.rockyhawk.commandpanels.openpanelsmanager.PanelPosition;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class QuestTypeCollectItem implements QuestType {
    final QuestTableCollectItem item;
    private final int count;
    private int totalItemCount = 0;
    private Quest quest;
    private final double difficultValue;

    public static @NotNull QuestTypeCollectItem create() {
        QuestTableCollectItem item;
        QuestTableCollectItem[] table = QuestTableCollectItem.values();
        Random random = new Random();
        double target = DifficultTool.nextDifficult();
        do {
            item = table[random.nextInt(table.length)];
        } while (target < item.difficult);
        int count = (int)Math.ceil((target) / (item.difficult));
        return new QuestTypeCollectItem(item, count, target);
    }

    private QuestTypeCollectItem(QuestTableCollectItem req, int count, double difficultValue) {
        item = req;
        this.count = count;
        this.difficultValue = difficultValue;
    }

    public QuestTypeCollectItem(@NotNull Map<String, Object> serializer) {
        item = QuestTableCollectItem.valueOf((String) serializer.get("item"));
        count = (int) serializer.get("count");
        difficultValue = (double) serializer.get("difficultValue");
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> serializer = new HashMap<>();
        serializer.put("item", item.name());
        serializer.put("count", count);
        serializer.put("difficultValue", difficultValue);
        return serializer;
    }

    @Override
    public @NotNull Quest quest() {
        return quest;
    }

    public void sendQuestObject(Quest q) {
        quest = q;
    }

    public void addItem(@NotNull Player player, int c) {
        quest.getTeam().teamBroadcast(Message.get("quest.collectitem.submit", player.getDisplayName(), quest.getName(), item.name, c));
        int req = count - totalItemCount;
        if (req >= c) {
            totalItemCount += c;
            if (count == totalItemCount) quest.complete();
        } else {
            totalItemCount = count;
            quest.complete();
        }
    }

    public int maxRequire() {
        return count - totalItemCount;
    }

    @Override
    public boolean isCompleted() {
        return totalItemCount == count;
    }

    @Override
    public double progress() {
        return totalItemCount / (double)(count);
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
                (int) (random.nextInt(300, 360) * Math.log10(difficultValue) +
                                        (int)(difficultValue * random.nextDouble(5, 10)));
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
        return Message.get("quest.collectitem.name", difficult().name, item.name, count);
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
        item.set("material", this.item.material.toString());
        item.set("stack", 1);
        item.set("name", name());
        return item;
    }

    @Override
    public void openPanel(@NotNull Player player) {
        QuestDPCollectItem.openDynamic(player, PanelPosition.Top, quest);
    }
}
