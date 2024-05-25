package me.hifei.questmaster.api.ui;

import me.hifei.questmaster.api.quest.Timer;
import me.rockyhawk.commandpanels.openpanelsmanager.PanelPosition;
import org.bukkit.entity.Player;

import java.util.Objects;

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

    private final Timer timer;
    private final Runnable confirmCallback;
    private final Runnable cancelCallback;

    public DPConfirm(Player player, Runnable confirmCallback, Runnable cancelCallback) {
        super(player, false);
        this.confirmCallback = confirmCallback;
        this.cancelCallback = cancelCallback;
        this.timer = new Timer(5);
        this.timer.start();
        dynamicModify(player);
        startAutoUpdate(1);
    }

    @Override
    protected void dynamicModify(Player player) {
        loadTemplate("panels/confirm.yml");

        if (timer.isTimeUp()) setItem(11, getDynamic("confirm_unlocked"));
        else {
            setItem(11, getDynamic("confirm_locked"));
            getItem(11).set("name",
                    Objects.requireNonNull(getItem(11).getString("name")).formatted(timer.totalRemainingSecond()));
        }
    }

    public static <T extends Player> void openDynamic(T player, PanelPosition panelPosition, Runnable confirmCallback, Runnable cancelCallback) {
        DynamicPanel.openDynamic(panelPosition, new DPConfirm(player, confirmCallback, cancelCallback));
    }
}
