package me.hifei.questmaster.ui.core;

import me.rockyhawk.commandpanels.openpanelsmanager.PanelPosition;
import org.bukkit.entity.Player;

public class DPConfirm extends DynamicPanel {
    static {
        UIManager.ins.registerEvent("confirm_confirm", (event) -> {
            DPConfirm dp = (DPConfirm) event.getPanel();
            dp.confirmCallback.run();
        });
        UIManager.ins.registerEvent("confirm_cancel", (event) -> {
            DPConfirm dp = (DPConfirm) event.getPanel();
            dp.cancelCallback.run();
        });
    }

    private final Runnable confirmCallback;
    private final Runnable cancelCallback;

    public DPConfirm(Player player, Runnable confirmCallback, Runnable cancelCallback) {
        super(player);
        this.confirmCallback = confirmCallback;
        this.cancelCallback = cancelCallback;
    }

    @Override
    protected void dynamicModify(Player player) {
        loadTemplate("panels/confirm.yml");
    }

    public static <T extends Player> void openDynamic(T player, PanelPosition panelPosition, Runnable confirmCallback, Runnable cancelCallback) {
        DynamicPanel.openDynamic(panelPosition, new DPConfirm(player, confirmCallback, cancelCallback));
    }
}
