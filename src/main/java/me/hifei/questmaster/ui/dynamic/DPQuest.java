package me.hifei.questmaster.ui.dynamic;

import me.hifei.questmaster.api.CoreManager;
import me.hifei.questmaster.api.quest.Quest;
import me.hifei.questmaster.api.team.QuestTeam;
import me.hifei.questmaster.ui.core.DynamicPanel;
import me.hifei.questmaster.ui.core.UIManager;
import me.rockyhawk.commandpanels.api.PanelCommandEvent;
import me.rockyhawk.commandpanels.openpanelsmanager.PanelPosition;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DPQuest extends DynamicPanel {
    public static void openQuestMenu(@NotNull PanelCommandEvent event, int i) {
        DPQuest panel = (DPQuest) event.getPanel();
        panel.team.getQuests().get(i).openPanel(event.getPlayer());
    }

    static {
        UIManager.ins.registerEvent("quest_back", (event -> DPRoot.openDynamic(event.getPlayer(), PanelPosition.Top)));
        UIManager.ins.registerEvent("open_quest_1", (event -> openQuestMenu(event, 0)));
        UIManager.ins.registerEvent("open_quest_2", (event -> openQuestMenu(event, 1)));
        UIManager.ins.registerEvent("open_quest_3", (event -> openQuestMenu(event, 2)));
        UIManager.ins.registerEvent("quest_to_red", (event -> openDynamic(event.getPlayer(), CoreManager.red, PanelPosition.Top)));
        UIManager.ins.registerEvent("quest_to_blue", (event -> openDynamic(event.getPlayer(), CoreManager.blue, PanelPosition.Top)));
    }
    private final QuestTeam team;

    public DPQuest(@NotNull Player player, QuestTeam team) {
        super(player, false);
        this.team = team;
        startAutoUpdate(5);
    }

    protected void dynamicModify(Player player) {
        loadTemplate("panels/quest.yml");
        if (team == CoreManager.red) setItem(8, getDynamic("red"));
        else if (team == CoreManager.blue) setItem(8, getDynamic("blue"));
        List<Quest> quests = team.getQuests();
        try {
            setItem(29, quests.get(0).getItem(true));
            getItem(29).set("commands", List.of("event= open_quest_1"));
            setItem(31, quests.get(1).getItem(true));
            getItem(31).set("commands", List.of("event= open_quest_2"));
            setItem(33, quests.get(2).getItem(true));
            getItem(33).set("commands", List.of("event= open_quest_3"));
        } catch (IndexOutOfBoundsException e) {
            close();
        }
    }

    public static <T extends Player> void openDynamic(T player, PanelPosition panelPosition) {
        openDynamic(player, CoreManager.manager.getTeam(player), panelPosition);
    }

    public static <T extends Player> void openDynamic(T player, QuestTeam team, PanelPosition panelPosition) {
        DynamicPanel.openDynamic(panelPosition, new DPQuest(player, team));
    }
}
