package me.hifei.questmaster.quest.questkillmob;

import com.google.common.base.CaseFormat;
import me.hifei.questmaster.CoreManager;
import me.hifei.questmaster.api.quest.TableItem;
import me.hifei.questmaster.api.quest.TableItemGroup;
import me.hifei.questmaster.manager.QuestTableTool;
import me.hifei.questmaster.running.config.Config;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import java.util.Map;
import java.util.Random;

public class QuestTableKillMob extends QuestTableTool<EntityType> {
    public final static QuestTableKillMob ins = new QuestTableKillMob();

    @Override
    protected TableItem<EntityType> buildItem(String string, double diff) {
        Map<String, String> trans = CoreManager.translateMaterialTool.translate_file;
        EntityType type = EntityType.valueOf(string.toUpperCase());
        return new TableItem<>(type, trans.get(type.getTranslationKey()), diff);
    }

    public Material getIcon(TableItem<EntityType> tableItem) {
        return Material.valueOf(tableItem.obj().name() + "_SPAWN_EGG");
    }

    public TableItem<EntityType> nextItem() {
        Random random = new Random();
        TableItemGroup<EntityType> group = itemGroups.get(random.nextInt(itemGroups.size()));
        return group.items().get(random.nextInt(group.items().size()));
    }

    public void loadConfig() {
        Config config = new Config("table/killmob.yml", true);
        loadConfig0(config);
    }
}
