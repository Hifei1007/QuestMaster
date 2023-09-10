package me.hifei.questmaster.manager;

import me.hifei.questmaster.api.CoreManager;
import me.hifei.questmaster.QuestMasterPlugin;
import me.hifei.questmaster.api.quest.MultiTableItemGroup;
import me.hifei.questmaster.api.quest.TableItem;
import me.hifei.questmaster.api.quest.TableItemGroup;
import me.hifei.questmaster.running.config.Config;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public abstract class QuestTableTool<T> {
     protected QuestTableTool() {

     }

     public List<MultiTableItemGroup<T>> multiItemGroups = new ArrayList<>();

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

    protected abstract TableItem<T> buildItem(String string, double diff, double basediff);
    protected List<TableItem<T>> processAliases(List<String> aliases, double diff, double basediff) {
        return processAliases(aliases, List.of(""), diff, basediff);
    }

    protected List<TableItem<T>> processAliases(List<String> aliases, List<String> placeHolders, double diff, double basediff) {
        List<TableItem<T>> res = new ArrayList<>();
        for (String alias : aliases) {
            if (alias.contains("$")) {
                for (String placeHolder : placeHolders) {
                    res.add(buildItem(alias.replace("$", placeHolder), diff, basediff));
                }
            } else {
                res.add(buildItem(alias, diff, basediff));
            }
        }
        return res;
    }

    @SuppressWarnings("unchecked")
    protected TableItemGroup<T> processItemGroup(Map<?, ?> group) {
        double diff = ((Number) group.get("diff")).doubleValue();
        double basediff = 0;
        try {
            basediff = ((Number) group.get("basediff")).doubleValue();
        } catch (NullPointerException ignored) {
        }
        if (group.containsKey("name")) {
            return new TableItemGroup<>(List.of(buildItem((String) group.get("name"), diff, basediff)), diff, basediff);
        } else if (group.containsKey("aliases")) {
            List<String> aliases = (List<String>) group.get("aliases");
            if (group.containsKey("placeholders")) {
                return new TableItemGroup<>(processAliases(aliases, (List<String>) group.get("placeholders"), diff, basediff), diff, basediff);
            } else {
                return new TableItemGroup<>(processAliases(aliases, diff, basediff), diff, basediff);
            }
        } else {
            throw new RuntimeException("Found unknown group");
        }
    }

    public TableItem<T> nextItem() {
        Random random = new Random();
        MultiTableItemGroup<T> multiGroup = multiItemGroups.get(random.nextInt(multiItemGroups.size()));
        TableItemGroup<T> group = multiGroup.groups().get(random.nextInt(multiGroup.groups().size()));
        return group.items().get(random.nextInt(group.items().size()));
    }

    @SuppressWarnings("unchecked")
    protected void loadConfig0(Config config) {
        List<Map<?, ?>> multiGroups = config.getConfiguration().getMapList("items");
        List<MultiTableItemGroup<T>> res = new ArrayList<>();
        for (Map<?, ?> multi : multiGroups) {
            if (multi.containsKey("multi")) {
                List<TableItemGroup<T>> tmp = new ArrayList<>();
                for (Map<?, ?> group : (List<Map<?, ?>>) multi.get("multi")) {
                    tmp.add(processItemGroup(group));
                }
                res.add(new MultiTableItemGroup<>(tmp));
            } else {
                res.add(new MultiTableItemGroup<>(List.of(processItemGroup(multi))));
            }
        }
        multiItemGroups = res;
    }
}
