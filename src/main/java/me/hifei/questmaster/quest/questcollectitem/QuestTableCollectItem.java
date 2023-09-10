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
    protected TableItem<Material> buildItem(String string, double diff, double basediff) {
        Material material = getMaterialByString(string);
        return new TableItem<>(material, getTranslate(material), diff, basediff);
    }

    public void loadConfig() {
        Config config = new Config("table/collectitem.yml", true);
        loadConfig0(config);
    }
}
