package me.hifei.questmaster.quest.questmineblock;

import me.hifei.questmaster.api.quest.Quest;
import me.hifei.questmaster.api.state.State;
import me.hifei.questmaster.api.ui.DynamicPanel;
import me.hifei.questmaster.api.ui.QuestDynamicPanel;
import me.hifei.questmaster.dynamicui.DPQuest;
import me.rockyhawk.commandpanels.openpanelsmanager.PanelPosition;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class QuestDPMineBlock extends QuestDynamicPanel {
    protected QuestDPMineBlock(@NotNull Player player, Quest quest) {
        super(player, quest);
        startAutoUpdate(5);
    }

    @Override
    public void dynamicModify(Player player) {
         if (quest.getState() == State.DROP) {
            DPQuest.openDynamic(player, PanelPosition.Top);
        }
        loadTemplate("panels/questtype/mineblock.yml");
        super.dynamicModify(player);
        modifyItemBar(((QuestTypeMineBlock) quest.getType()).getTableItem().obj());
    }

    public static void openDynamic(Player player, PanelPosition panelPosition, Quest quest) {
        DynamicPanel.openDynamic(panelPosition, new QuestDPMineBlock(player, quest));
    }
}
