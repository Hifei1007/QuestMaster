package me.hifei.questmaster.quest.questcollectitem;

import me.hifei.questmaster.CoreManager;
import me.hifei.questmaster.QuestMasterPlugin;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public enum QuestTableCollectItem {
    // Stones
    DIRT(0.5),
    SAND(0.5),
    CLAY(4),
    CLAY_BALL(1),
    POLISHED_GRANITE(1.5),
    GRANITE(1),
    POLISHED_DIORITE(1.5),
    DIORITE(1),
    POLISHED_ANDESITE(1.5),
    ANDESITE(1),
    COBBLESTONE(1),
    STONE(4),
    STONE_BRICK(4.5),
    COBBLED_DEEPSLATE(1.5),
    DEEPSLATE(4.5),
    POLISHED_DEEPSLATE(5),


    // Woods
    STRIPPED_OAK_LOG(3.5),
    OAK_LOG(3),
    STRIPPED_BIRCH_LOG(3.5),
    BIRCH_LOG(3),
    STRIPPED_SPRUCE_LOG(3.5),
    SPRUCE_LOG(3),
    STRIPPED_ACACIA_LOG(3.5),
    ACACIA_LOG(3),
    STRIPPED_JUNGLE_LOG(3.5),
    JUNGLE_LOG(3),
    STICK(0.5),

    // Nether
    NETHERRACK(0.5),
    WARPED_STEM(3),
    STRIPPED_WARPED_STEM(3.5),
    CRIMSON_STEM(3),
    STRIPPED_CRIMSON_STEM(3.5),
    SOUL_SAND(1.5),
    SOUL_SOIL(1.5),
    BASALT(1.5),
    POLISHED_BASALT(2),
    BLACKSTONE(1),
    POLISHED_BLACKSTONE(1.5),

    // End
    END_STONE(7),
    END_ROD(25),
    END_STONE_BRICKS(9),
    PURPUR_BLOCK(15),
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
