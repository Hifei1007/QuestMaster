package me.hifei.questmaster.quest.questcollectitem;

import me.hifei.questmaster.CoreManager;
import me.hifei.questmaster.api.quest.Quest;
import me.hifei.questmaster.api.state.State;
import me.hifei.questmaster.ui.core.DynamicPanel;
import me.hifei.questmaster.ui.core.QuestDynamicPanel;
import me.hifei.questmaster.ui.core.UIManager;
import me.hifei.questmaster.ui.dynamic.DPQuest;
import me.rockyhawk.commandpanels.api.Panel;
import me.rockyhawk.commandpanels.openpanelsmanager.PanelPosition;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class QuestDPCollectItem extends QuestDynamicPanel {
    public static void submitItem(int max, Panel panel) {
        QuestDPCollectItem dpCollectItem = (QuestDPCollectItem) panel;
        Player player = dpCollectItem.player;
        Quest quest = dpCollectItem.quest;
        QuestTypeCollectItem qt = ((QuestTypeCollectItem) quest.getType());
        if (CoreManager.manager.getTeam(player) != quest.getTeam()) return;

        int items = 0;
        for (ItemStack stack : player.getInventory()) {
            if (stack == null) continue;
            if (stack.getType().equals(((QuestTypeCollectItem) quest.getType()).item.obj())) {
                items += stack.getAmount();
            }
        }
        int target = Math.min(Math.min(max, items), qt.maxRequire());
        if (target == 0) return;
        int current = target;
        for (ItemStack stack : player.getInventory()) {
            if (current == 0) break;
            if (stack == null) continue;
            if (stack.getType().equals(((QuestTypeCollectItem) quest.getType()).item.obj())) {
                if (current < stack.getAmount()) {
                    stack.setAmount(stack.getAmount() - current);
                    current = 0;
                } else {
                    current -= stack.getAmount();
                    stack.setAmount(0);
                }
            }
        }
        qt.addItem(player, target);
        dpCollectItem.dynamicModify(player);
    }

    static {
        UIManager.ins.registerEvent("collectitem_submit_1", (event -> submitItem(1, event.getPanel())));
        UIManager.ins.registerEvent("collectitem_submit_8", (event -> submitItem(8, event.getPanel())));
        UIManager.ins.registerEvent("collectitem_submit_64", (event -> submitItem(64, event.getPanel())));
        UIManager.ins.registerEvent("collectitem_submit_all", (event -> submitItem(Integer.MAX_VALUE, event.getPanel())));
    }

    public QuestDPCollectItem(@NotNull Player player, Quest quest) {
        super(player, quest);
        startAutoUpdate(5);
    }

    private void modifyAddItemBar() {
        if (CoreManager.manager.getTeam(player) != quest.getTeam()) {
            for (int i : List.of(19, 21, 23, 25)) {
                List<String> list = getItem(i).getStringList("lore");
                list.set(1, getMessage("submit.lore.not_team_member"));
                getItem(i).set("lore", list);
            }
            return;
        }

        int items = 0;
        for (ItemStack stack : player.getInventory()) {
            if (stack == null) continue;
            if (stack.getType().equals(((QuestTypeCollectItem) quest.getType()).item.obj())) {
                items += stack.getAmount();
            }
        }
        if (items >= 1) {
            List<String> list = getItem(19).getStringList("lore");
            list.set(1, getMessage("submit.lore.1"));
            getItem(19).set("lore", list);
        }
        if (items >= 8) {
            List<String> list = getItem(21).getStringList("lore");
            list.set(1, getMessage("submit.lore.8"));
            getItem(21).set("lore", list);
        }
        if (items >= 64) {
            List<String> list = getItem(23).getStringList("lore");
            list.set(1, getMessage("submit.lore.64"));
            getItem(23).set("lore", list);
        }
        if (items >= 1) {
            List<String> list = getItem(25).getStringList("lore");
            list.set(1, getMessage("submit.lore.all", items));
            getItem(25).set("lore", list);
        }
    }

    @Override
    public void dynamicModify(Player player) {
        if (quest.getState() == State.DROP) {
            DPQuest.openDynamic(player, PanelPosition.Top);
        }
        loadTemplate("panels/questtype/collectitem.yml");
        super.dynamicModify(player);
        modifyAddItemBar();
        modifyItemBar(((QuestTypeCollectItem) quest.getType()).item.obj());
    }

    public static void openDynamic(Player player, PanelPosition panelPosition, Quest quest) {
        DynamicPanel.openDynamic(panelPosition, new QuestDPCollectItem(player, quest));
    }
}
