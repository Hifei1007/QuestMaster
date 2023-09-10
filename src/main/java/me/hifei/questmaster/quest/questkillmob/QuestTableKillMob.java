package me.hifei.questmaster.quest.questkillmob;

import me.hifei.questmaster.api.CoreManager;
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
    protected TableItem<EntityType> buildItem(String string, double diff, double basediff) {
        Map<String, String> trans = CoreManager.translateMaterialTool.translate_file;
        EntityType type = EntityType.valueOf(string.toUpperCase());
        return new TableItem<>(type, trans.get(type.getTranslationKey()), diff, basediff);
    }

    public Material getIcon(TableItem<EntityType> tableItem) {
        return Material.valueOf(tableItem.obj().name() + "_SPAWN_EGG");
    }

    public void loadConfig() {
        Config config = new Config("table/killmob.yml", true);
        loadConfig0(config);
    }
}
