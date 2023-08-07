package me.hifei.questmaster.ui.dynamic.shop;

import me.hifei.questmaster.ui.core.DynamicPanel;
import me.hifei.questmaster.ui.core.UIManager;
import me.hifei.questmaster.ui.dynamic.DPRoot;
import me.rockyhawk.commandpanels.openpanelsmanager.PanelPosition;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DPShopRoot extends DynamicPanel {
    static {
        UIManager.ins.registerEvent("shop_root_open_teamupgrade", (event -> DPShopTeamUpgrade.openDynamic(event.getPlayer(), PanelPosition.Top)));
        UIManager.ins.registerEvent("shop_root_back", (event -> DPRoot.openDynamic(event.getPlayer(), PanelPosition.Top)));
    }

    public static <T extends Player> void openDynamic(T player, PanelPosition panelPosition) {
       DynamicPanel.openDynamic(panelPosition, new DPShopRoot(player));
    }

    public DPShopRoot(@NotNull Player player) {
        super(player);
    }

    @Override
    protected void dynamicModify(Player player) {
        loadTemplate("panels/shop/root.yml");
    }
}
