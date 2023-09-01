package me.hifei.questmaster.quest.questcollectitem;

import me.hifei.questmaster.api.quest.*;
import me.hifei.questmaster.manager.AbstractQuestType;
import me.hifei.questmaster.running.config.Message;
import me.hifei.questmaster.tools.DifficultTool;
import me.rockyhawk.commandpanels.openpanelsmanager.PanelPosition;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class QuestTypeCollectItem extends AbstractQuestType<Material> {
    protected QuestTypeCollectItem(TableItem<Material> item, int totalCount, double difficultValue) {
        super(item, totalCount, difficultValue);
    }

    public static @NotNull QuestTypeCollectItem create() {
        TableItem<Material> item;
        double target = DifficultTool.nextDifficult();
        int tryCount = 0;
        do {
            item = QuestTableCollectItem.ins.nextItem();
            tryCount++;
            if (tryCount > 100) {
                target = DifficultTool.nextDifficult();
                tryCount = 0;
            }
        } while (target < item.diff() * 4 || target > item.diff() * 48);
        int count = (int) Math.ceil((target) / (item.diff()));
        return new QuestTypeCollectItem(item, count, target);
    }

    public void addItem(@NotNull Player player, int c) {
        player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 10.0f, 1.0f);
        quest.getTeam().teamBroadcast(Message.get("quest.collectitem.submit", player.getDisplayName(), quest.getName(), item.name(), c));
        currentCount += c;
        if (currentCount >= totalCount) quest.complete();
    }

    public int maxRequire() {
        return totalCount - currentCount;
    }

    @Override
    public @NotNull String name() {
        return Message.get("quest.collectitem.name", difficult().name, item.name(), totalCount);
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
        QuestDPCollectItem.openDynamic(player, PanelPosition.Top, quest);
    }
}
