package me.hifei.questmaster.ui;

import me.hifei.questmaster.api.quest.Quest;
import me.hifei.questmaster.running.config.Message;
import me.hifei.questmaster.ui.dynamic.DPQuest;
import me.rockyhawk.commandpanels.openpanelsmanager.PanelPosition;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class QuestDynamicPanel extends DynamicPanel {
    static {
        UIManager.ins.registerEvent("quest_main_back", (event -> DPQuest.openDynamic(event.getPlayer(), PanelPosition.Top)));
    }

    protected final Quest quest;

    protected QuestDynamicPanel(@NotNull Player player, Quest quest) {
        super(player, false);
        this.quest = quest;
    }

    public void dynamicModify(Player player) {
        getConfig().set("title", quest.getName());
        getItem(1).set("name", Message.get("quest.score", quest.getReward().score()));
        getItem(3).set("name", Message.get("quest.point", quest.getReward().point(), quest.getReward().point() / 10));
        getItem(5).set("name", Message.get("quest.coin", quest.getReward().coin(), quest.getReward().coin() / 10));
        getItem(7).set("name", Message.get("quest.time", quest.getReward().time()));
        setItem(13, quest.getItem());
    }
}
