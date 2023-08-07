package me.hifei.questmaster.manager;

import me.hifei.questmaster.CoreManager;
import me.hifei.questmaster.QuestMasterPlugin;
import me.hifei.questmaster.api.quest.TableItem;
import me.hifei.questmaster.api.quest.TableItemGroup;
import me.hifei.questmaster.running.config.Config;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class QuestTableTool<T> {
     protected QuestTableTool() {

     }

     public List<TableItemGroup<T>> itemGroups = new ArrayList<>();

     protected static String getTranslate(Material material) {
        String name;
        Map<String, String> trans = CoreManager.translateMaterialTool.translate_file;
        String key = material.getKey().getKey();
        String ret = trans.getOrDefault(String.format("block.minecraft.%s", key), trans.getOrDefault(String.format("item.minecraft.%s", key), null));
        if (ret == null) {
            QuestMasterPlugin.logger.warning("Can't find translate key: " + material);
            name = material.toString();
        } else {
            name = ret;
        }
        return name;
    }

    protected static Material getMaterialByString(String string) {
        try {
            return Material.valueOf(string.toUpperCase());
        } catch (IllegalArgumentException e) {
            QuestMasterPlugin.logger.warning("Can't find material: " + string);
            return Material.AIR;
        }
    }

    protected abstract TableItem<T> buildItem(String string, double diff);

     protected List<TableItem<T>> processAliases(List<String> aliases, double diff) {
        return processAliases(aliases, List.of(""), diff);
    }

    protected List<TableItem<T>> processAliases(List<String> aliases, List<String> placeHolders, double diff) {
        List<TableItem<T>> res = new ArrayList<>();
        for (String alias : aliases) {
            if (alias.contains("$")) {
                for (String placeHolder : placeHolders) {
                    res.add(buildItem(alias.replace("$", placeHolder), diff));
                }
            } else {
                res.add(buildItem(alias, diff));
            }
        }
        return res;
    }

    @SuppressWarnings("unchecked")
    protected void loadConfig0(Config config) {
        List<Map<?, ?>> groups = config.getConfiguration().getMapList("items");
        List<TableItemGroup<T>> res = new ArrayList<>();
        for (Map<?, ?> group : groups) {
            double diff = ((Number) group.get("diff")).doubleValue();
            if (group.containsKey("name")) {
                res.add(new TableItemGroup<>(List.of(buildItem((String) group.get("name"), diff)), diff));
            } else if (group.containsKey("aliases")) {
                List<String> aliases = (List<String>) group.get("aliases");
                if (group.containsKey("placeholders")) {
                    res.add(new TableItemGroup<>(processAliases(aliases, (List<String>) group.get("placeholders"), diff), diff));
                } else {
                    res.add(new TableItemGroup<>(processAliases(aliases, diff), diff));
                }
            } else {
                QuestMasterPlugin.logger.warning("Found unknown group");
            }
        }
        itemGroups = res;
    }
}
