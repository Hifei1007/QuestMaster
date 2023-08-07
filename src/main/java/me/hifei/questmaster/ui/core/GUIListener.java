package me.hifei.questmaster.ui.core;

import me.hifei.questmaster.CoreManager;
import me.hifei.questmaster.ui.dynamic.DPRoot;
import me.hifei.questmaster.ui.dynamic.DPRootNotStarted;
import me.rockyhawk.commandpanels.api.PanelClosedEvent;
import me.rockyhawk.commandpanels.api.PanelCommandEvent;
import me.rockyhawk.commandpanels.openpanelsmanager.PanelPosition;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.jetbrains.annotations.NotNull;

public class GUIListener implements Listener {
    @EventHandler
    public void onOpenMenu(@NotNull PlayerSwapHandItemsEvent event) {
        if (event.getPlayer().isSneaking()) {
            event.setCancelled(true);
            if (CoreManager.isGameStart() && CoreManager.manager.hasTeam(event.getPlayer())) DPRoot.openDynamic(event.getPlayer(), PanelPosition.Top);
            else DPRootNotStarted.openDynamic(event.getPlayer(), PanelPosition.Top);
        }
    }

    @EventHandler
    public void onCommandEvent(@NotNull PanelCommandEvent event) {
        UIManager.ins.sendEvent(event);
    }

    @EventHandler
    public void onClosePanel(@NotNull PanelClosedEvent event) {
        if (event.getPanel() instanceof DynamicPanel) {
            ((DynamicPanel) event.getPanel()).close();
        }
    }
}
