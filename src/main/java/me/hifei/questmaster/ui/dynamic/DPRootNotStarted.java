package me.hifei.questmaster.ui.dynamic;

import me.hifei.questmaster.CoreManager;
import me.hifei.questmaster.api.team.QuestTeam;
import me.hifei.questmaster.ui.UIManager;
import me.hifei.questmaster.ui.DynamicPanel;
import me.rockyhawk.commandpanels.openpanelsmanager.PanelPosition;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DPRootNotStarted extends DynamicPanel {
    static {
        UIManager.ins.registerEvent("root_not_started_join_blue", (e) -> {
                CoreManager.manager.setTeam(e.getPlayer(), CoreManager.blue);
                if (CoreManager.isGameStart()) DPRoot.openDynamic(e.getPlayer(), PanelPosition.Top);
                else openDynamic(e.getPlayer(), PanelPosition.Top);
        });
        UIManager.ins.registerEvent("root_not_started_join_red", (e) -> {
                CoreManager.manager.setTeam(e.getPlayer(), CoreManager.red);
                if (CoreManager.isGameStart()) DPRoot.openDynamic(e.getPlayer(), PanelPosition.Top);
                else openDynamic(e.getPlayer(), PanelPosition.Top);
        });
        UIManager.ins.registerEvent("root_not_started_clear", (e) -> {
                CoreManager.manager.clearTeam(e.getPlayer());
                openDynamic(e.getPlayer(), PanelPosition.Top);
        });
    }

    public static <T extends Player> void openDynamic(T player, PanelPosition panelPosition) {
        DynamicPanel.openDynamic(DPRootNotStarted.class, panelPosition, player);
    }

    @Override
    protected void dynamicModify(@NotNull Player player) {
        loadTemplate("panels/root_not_started.yml");
        QuestTeam t = CoreManager.manager.getTeam(player);
        if (t == null) {
            setItem("29", getDynamic("red_not_join"));
            setItem("33", getDynamic("blue_not_join"));
        } else if (t == CoreManager.blue) {
            setItem("29", getDynamic("red_not_join"));
            setItem("33", getDynamic("blue_join"));
        } else if (t == CoreManager.red) {
            setItem("29", getDynamic("red_join"));
            setItem("33", getDynamic("blue_not_join"));
        }
    }

    public DPRootNotStarted(@NotNull Player player) {
        super(player);
    }
}
