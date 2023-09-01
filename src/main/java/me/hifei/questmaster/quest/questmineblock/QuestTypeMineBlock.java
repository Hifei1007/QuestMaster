package me.hifei.questmaster.quest.questmineblock;

import me.hifei.questmaster.QuestMasterPlugin;
import me.hifei.questmaster.api.quest.*;
import me.hifei.questmaster.manager.AbstractQuestType;
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

public class QuestTypeMineBlock extends AbstractQuestType<Material> {
    public final Map<String, Integer> combo = new HashMap<>();
    public final Map<String, Long> lastCombo = new HashMap<>();

    static {
        Bukkit.getPluginManager().registerEvents(new QuestListenerMineBlock(), QuestMasterPlugin.instance);
    }

    protected QuestTypeMineBlock(TableItem<Material> item, int totalCount, double difficultValue) {
        super(item, totalCount, difficultValue);
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
        } while (target < item.diff() * 4 || target > item.diff() * 48);
        int count = (int) Math.ceil((target) / (item.diff()));
        return new QuestTypeMineBlock(item, count, target);
    }

    public void addCount() {
        currentCount++;
        if (currentCount == totalCount) quest.complete();
    }

    @Override
    public @NotNull String name() {
        return Message.get("quest.mineblock.name", difficult().name, item.name(), totalCount);
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
}
