package me.hifei.questmaster.quest.questcollectitem;

import me.hifei.questmaster.CoreManager;
import me.hifei.questmaster.QuestMasterPlugin;
import me.hifei.questmaster.running.config.Config;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class QuestTableCollectItem {
    public static List<TableItemGroup> itemGroups = new ArrayList<>();

    public record TableItem(Material material, String name, double diff) {}
    public record TableItemGroup(List<TableItem> items, double diff) {}

    private static String getTranslate(Material material) {
        String name;
        String ret = CoreManager.translateMaterialTool.translate_file
                .get(String.format("block.minecraft.%s", material.getKey().getKey()));
        if (ret == null) {
            QuestMasterPlugin.logger.warning("Can't find translate key: " + material);
            name = material.toString();
        } else {
            name = ret;
        }
        return name;
    }

    private static Material getMaterialByString(String string) {
        return Material.valueOf(string);
    }

    private static TableItem buildItem(String string, double diff) {
        Material material = getMaterialByString(string);
        return new TableItem(material, getTranslate(material), diff);
    }

    private static List<TableItem> processAliases(List<String> aliases, double diff) {
        return processAliases(aliases, List.of(""), diff);
    }

    private static List<TableItem> processAliases(List<String> aliases, List<String> placeHolders, double diff) {
        List<TableItem> res = new ArrayList<>();
        for (String alias : aliases) {
            for (String placeHolder : placeHolders) {
                res.add(buildItem(alias.replace("$", placeHolder), diff));
            }
        }
        return res;
    }

    public static TableItem nextItem() {
        Random random = new Random();
        TableItemGroup group = itemGroups.get(random.nextInt(itemGroups.size()));
        return group.items.get(random.nextInt(group.items.size()));
    }

    @SuppressWarnings("unchecked")
    public static void loadConfig() {
        Config config = new Config("table/collectitem.yml", true);
        List<Map<?, ?>> groups = config.getConfiguration().getMapList("items");
        List<TableItemGroup> res = new ArrayList<>();
        for (Map<?, ?> group : groups) {
            double diff = (double) group.get("diff");
            if (group.containsKey("name")) {
                res.add(new TableItemGroup(List.of(buildItem((String) group.get("name"), diff)), diff));
            }
            List<String> aliases = (List<String>) group.get("aliases");
            if (group.containsKey("placeholders")) {
                res.add(new TableItemGroup(processAliases(aliases, (List<String>) group.get("placeholders"), diff), diff));
            } else {
                res.add(new TableItemGroup(processAliases(aliases, diff), diff));
            }
        }
        itemGroups = res;
    }
}
