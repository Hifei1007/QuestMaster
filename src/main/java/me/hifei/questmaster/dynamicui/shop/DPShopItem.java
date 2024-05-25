package me.hifei.questmaster.dynamicui.shop;

import me.hifei.questmaster.api.CoreManager;
import me.hifei.questmaster.QuestMasterPlugin;
import me.hifei.questmaster.api.team.QuestTeam;
import me.hifei.questmaster.api.ui.DynamicPanel;
import me.hifei.questmaster.api.ui.UIManager;
import me.hifei.questmaster.dynamicui.DPRoot;
import me.rockyhawk.commandpanels.openpanelsmanager.PanelPosition;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public class DPShopItem extends DynamicPanel {

    private record ShopItem (int cost, ItemStack material) {

    }

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

    private static final List<ShopItem> items = List.of(
            new ShopItem(20, new ItemStack(Material.COBBLESTONE, 8)),
            new ShopItem(10, new ItemStack(Material.STICK, 8)),
            new ShopItem(100, new ItemStack(Material.STRIPPED_OAK_WOOD, 8)),
            new ShopItem(100, new ItemStack(Material.COAL, 8)),
            new ShopItem(150, new ItemStack(Material.BONE_MEAL, 8)),
            new ShopItem(10, new ItemStack(Material.STONE_SHOVEL)),
            new ShopItem(15, new ItemStack(Material.STONE_HOE)),
            new ShopItem(20, new ItemStack(Material.STONE_PICKAXE)),
            new ShopItem(20, new ItemStack(Material.STONE_AXE)),
            new ShopItem(50, new ItemStack(Material.SHEARS)),
            new ShopItem(20, new ItemStack(Material.MELON_SLICE,8)),
            new ShopItem(50, new ItemStack(Material.BREAD, 8)),
            new ShopItem(70, new ItemStack(Material.BAKED_POTATO, 8)),
            new ShopItem(120, new ItemStack(Material.COOKED_PORKCHOP, 8)),
            new ShopItem(160, new ItemStack(Material.GOLDEN_CARROT, 8))
    );

    static {
        UIManager.ins.registerEvent("shop_itemshop_back", event -> DPRoot.openDynamic(event.getPlayer(), PanelPosition.Top));
        UIManager.ins.registerListener(DPShopItem.class, event -> {
            if (!event.getMessage().startsWith("shop_buy_")) return;
            QuestTeam team = CoreManager.manager.getTeam(event.getPlayer());
            assert team != null;
            ShopItem item = items.get(Integer.parseInt(event.getMessage().substring(9)));
            if (team.coin() >= item.cost) {
                event.getPlayer().sendMessage(((DPShopItem) event.getPanel()).getMessage("item.buy",
                        getTranslate(item.material.getType()), item.material.getAmount()));
                team.setCoin(team.coin() - item.cost);
                event.getPlayer().getInventory().addItem(item.material);
            }
        });
    }

    public DPShopItem(Player player) {
        super(player);
        startAutoUpdate(5);
    }

    public void modifyItem(int id, int pos) {
        QuestTeam team = CoreManager.manager.getTeam(player);
        assert team != null;
        List<String> lore = getItem(pos).getStringList("lore");
        ShopItem item = items.get(id);
        if (team.coin() >= item.cost) {
            lore.set(1, getMessage("item.lore.ok"));
            getItem(pos).set("commands", List.of("event= shop_buy_" + id));
        } else {
            lore.set(1, getMessage("item.lore.require"));
        }
        modifyItem(pos, null, lore, null, null);
        getItem(pos).set("lore", lore);
    }

    @Override
    protected void dynamicModify(Player player) {
        loadTemplate("panels/shop/itemshop.yml");
        modifyItem(0, 20);
        modifyItem(1, 21);
        modifyItem(2, 22);
        modifyItem(3, 23);
        modifyItem(4, 24);
        modifyItem(5, 29);
        modifyItem(6, 30);
        modifyItem(7, 31);
        modifyItem(8, 32);
        modifyItem(9, 33);
        modifyItem(10, 38);
        modifyItem(11, 39);
        modifyItem(12, 40);
        modifyItem(13, 41);
        modifyItem(14, 42);
    }

    public static <T extends Player> void openDynamic(T player, PanelPosition position) {
        DPShopItem.openDynamic(position, new DPShopItem(player));
    }
}
