package me.hifei.questmaster.quest.questkillmob;

import me.hifei.questmaster.api.quest.Quest;
import me.hifei.questmaster.api.state.State;
import me.hifei.questmaster.ui.core.DynamicPanel;
import me.hifei.questmaster.ui.core.QuestDynamicPanel;
import me.hifei.questmaster.ui.dynamic.DPQuest;
import me.rockyhawk.commandpanels.openpanelsmanager.PanelPosition;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class QuestDPKillMob extends QuestDynamicPanel {
    protected QuestDPKillMob(@NotNull Player player, Quest quest) {
        super(player, quest);
        startAutoUpdate(5);
    }

    public void dynamicModify(Player player) {
         if (quest.getState() == State.DROP) {
            DPQuest.openDynamic(player, PanelPosition.Top);
        }
        loadTemplate("panels/questtype/mineblock.yml");
        super.dynamicModify(player);
        modifyItemBar(QuestTableKillMob.ins.getIcon(((QuestTypeKillMob) quest.getType()).getTableItem()));
    }

    public static void openDynamic(Player player, PanelPosition panelPosition, Quest quest) {
        DynamicPanel.openDynamic(panelPosition, new QuestDPKillMob(player, quest));
    }
}
