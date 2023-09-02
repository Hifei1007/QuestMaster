package me.hifei.questmaster.quest.questkillmob;

import me.hifei.questmaster.QuestMasterPlugin;
import me.hifei.questmaster.api.quest.TableItem;
import me.hifei.questmaster.manager.AbstractQuestType;
import me.hifei.questmaster.running.config.Message;
import me.hifei.questmaster.tools.DifficultTool;
import me.rockyhawk.commandpanels.openpanelsmanager.PanelPosition;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class QuestTypeKillMob extends AbstractQuestType<EntityType> {

    static {
        Bukkit.getPluginManager().registerEvents(new QuestListenerKillMob(), QuestMasterPlugin.instance);
    }
    public final Map<String, Integer> combo = new HashMap<>();
    public final Map<String, Long> lastCombo = new HashMap<>();

    protected QuestTypeKillMob(TableItem<EntityType> item, int totalCount, double difficultValue) {
        super(item, totalCount, difficultValue);
    }

    public static QuestTypeKillMob create() {
        TableItem<EntityType> item;
        double target = DifficultTool.nextDifficult();
        int tryCount = 0;
        do {
            item = QuestTableKillMob.ins.nextItem();
            tryCount++;
            if (tryCount > 100) {
                target = DifficultTool.nextDifficult();
                tryCount = 0;
            }
        } while (target < item.diff() * 3 || target > item.diff() * 16);
        int count = (int) Math.ceil((target) / (item.diff()));
        return new QuestTypeKillMob(item, count, target);
    }

    public void addCount() {
        currentCount++;
        if (currentCount == totalCount) quest.complete();
    }

    @Override
    public @NotNull String name() {
        return Message.get("quest.killmob.name", difficult().name,
                item.name(), totalCount);
    }

    @Override
    public @NotNull ConfigurationSection item() {
        ConfigurationSection item = new MemoryConfiguration();
        item.set("material", QuestTableKillMob.ins.getIcon(this.item));
        item.set("stack", 1);
        item.set("name", name());
        return item;
    }

    @Override
    public void openPanel(@NotNull Player player) {
        QuestDPKillMob.openDynamic(player, PanelPosition.Top, quest);
    }
}
