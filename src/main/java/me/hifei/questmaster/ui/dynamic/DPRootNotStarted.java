package me.hifei.questmaster.ui.dynamic;

import me.hifei.questmaster.api.CoreManager;
import me.hifei.questmaster.api.team.QuestTeam;
import me.hifei.questmaster.ui.core.DPConfirm;
import me.hifei.questmaster.ui.core.DynamicPanel;
import me.hifei.questmaster.ui.core.UIManager;
import me.rockyhawk.commandpanels.openpanelsmanager.PanelPosition;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class DPRootNotStarted extends DynamicPanel {
    private static int goalSetting = 1;

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
        UIManager.ins.registerEvent("root_not_started_goal_add_1", (e) -> goalSetting += 1);
        UIManager.ins.registerEvent("root_not_started_goal_add_10", (e) -> goalSetting += 10);
        UIManager.ins.registerEvent("root_not_started_goal_remove_1", (e) ->
                goalSetting = Math.max(goalSetting + 1, 1));
        UIManager.ins.registerEvent("root_not_started_goal_remove_10", (e) ->
                goalSetting = Math.max(goalSetting + 10, 1));
        UIManager.ins.registerEvent("root_not_started_goal_clear", (e) -> goalSetting = 1);
        UIManager.ins.registerEvent("root_not_started_start", (e) -> {
            DPConfirm.openDynamic(e.getPlayer(), PanelPosition.Top, () -> {
                CoreManager.game = CoreManager.manager.createGame(List.of(
                    CoreManager.red,
                    CoreManager.blue
                ), goalSetting);
                CoreManager.game.startup();
            }, () -> DPRootNotStarted.openDynamic(e.getPlayer(), PanelPosition.Top));
        });
    }

    public static <T extends Player> void openDynamic(T player, PanelPosition panelPosition) {
        DynamicPanel.openDynamic(panelPosition, new DPRootNotStarted(player));
    }

    @Override
    protected void dynamicModify(@NotNull Player player) {
        loadTemplate("panels/root_not_started.yml");
        QuestTeam t = CoreManager.manager.getTeam(player);
        getItem(31).set("name", Objects.requireNonNull(getItem(31).getString("name")).formatted(goalSetting));
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
        startAutoUpdate(1);
    }
}
