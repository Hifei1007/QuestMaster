package me.hifei.questmaster.quest.questcollectitem;

import me.hifei.questmaster.api.quest.TableItem;
import me.hifei.questmaster.api.quest.TableItemGroup;
import me.hifei.questmaster.manager.QuestTableTool;
import me.hifei.questmaster.running.config.Config;
import org.bukkit.Material;

import java.util.Random;

public class QuestTableCollectItem extends QuestTableTool<Material> {
    public final static QuestTableCollectItem ins = new QuestTableCollectItem();

    @Override
    protected TableItem<Material> buildItem(String string, double diff) {
        Material material = getMaterialByString(string);
        return new TableItem<>(material, getTranslate(material), diff);
    }

    public TableItem<Material> nextItem() {
        Random random = new Random();
        TableItemGroup<Material> group = itemGroups.get(random.nextInt(itemGroups.size()));
        return group.items().get(random.nextInt(group.items().size()));
    }

    public void loadConfig() {
        Config config = new Config("table/collectitem.yml", true);
        loadConfig0(config);
    }
}
