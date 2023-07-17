package me.hifei.questmaster.quest.questcollectitem;

import me.hifei.questmaster.CoreManager;
import me.hifei.questmaster.QuestMasterPlugin;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public enum QuestTableCollectItem {
    DIRT(0.3),
    GRANITE(0.5),
    DIORITE(0.5),
    ANDESITE(0.5)
    ;

    public final double difficult;
    public final @NotNull Material material;
    public final String name;
    QuestTableCollectItem(double difficult) {
        this.difficult = difficult;
        this.material = Material.valueOf(name());

        String ret = CoreManager.translateMaterialTool.translate_file
                .get(String.format("block.minecraft.%s", material.getKey().getKey()));
        if (ret == null) {
            QuestMasterPlugin.logger.warning("Can't find translate key: " + material);
            name = material.toString();
        } else {
            name = ret;
        }
    }
}
