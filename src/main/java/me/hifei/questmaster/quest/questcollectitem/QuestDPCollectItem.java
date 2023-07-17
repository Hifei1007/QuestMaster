package me.hifei.questmaster.quest.questcollectitem;

import me.hifei.questmaster.CoreManager;
import me.hifei.questmaster.api.quest.Quest;
import me.hifei.questmaster.api.quest.Timer;
import me.hifei.questmaster.api.state.State;
import me.hifei.questmaster.ui.UIManager;
import me.hifei.questmaster.ui.DynamicPanel;
import me.hifei.questmaster.ui.QuestDynamicPanel;
import me.hifei.questmaster.ui.dynamic.DPQuest;
import me.rockyhawk.commandpanels.api.Panel;
import me.rockyhawk.commandpanels.openpanelsmanager.PanelPosition;
import org.bukkit.Material;
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
            if (stack.getType().equals(((QuestTypeCollectItem) quest.getType()).item.material)) {
                items += stack.getAmount();
            }
        }
        int target = Math.min(Math.min(max, items), qt.maxRequire());
        if (target == 0) return;
        int current = target;
        for (ItemStack stack : player.getInventory()) {
            if (current == 0) break;
            if (stack == null) continue;
            if (stack.getType().equals(((QuestTypeCollectItem) quest.getType()).item.material)) {
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
            if (stack.getType().equals(((QuestTypeCollectItem) quest.getType()).item.material)) {
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

    private void modifyTimerBar() {
        Timer timer = quest.getTimer();
        long hours = timer.hour();
        String time = getMessage("timer.name", timer.remaining());
        for (int i = 46; i <= 50; i++) {
            setItem(i, getDynamic("timer_empty"));
            getItem(i).set("name", time);
        }
        for (int i = 46; i <= 50; i++) {
            if (hours != 0) {
                hours--;
                setItem(i, getDynamic("timer_1"));
                getItem(i).set("stack", 60);
                getItem(i).set("name", time);
            } else {
                setItem(i, getDynamic("timer_1"));
                getItem(i).set("stack", timer.minute());
                getItem(i).set("name", time);
                break;
            }
        }
        setItem(51, getDynamic("timer_empty"));
        getItem(51).set("name", time);

        long second = timer.second();
        if (second == 0) second = 60;
        setItem(52, getDynamic("timer_2"));
        getItem(52).set("name", time);
        getItem(52).set("stack", second);
    }

    public void modifyItemBar() {
        int items = quest.getTotalCount() - quest.getCurrentCount();
        String item = getMessage("item.name", items);
        long shulkers = items / (64 * 27);
        items %= (64 * 27);
        long stacks = items / 64;
        items %= 64;
        List<String> lore = List.of(
                getMessage("item.lore.1"),
                getMessage("item.lore.2", shulkers),
                getMessage("item.lore.3", stacks),
                getMessage("item.lore.4", items)
        );

        for (int i = 37; i <= 43; i++) {
            setItem(i, getDynamic("item_empty"));
            getItem(i).set("lore", lore);
            getItem(i).set("name", item);
        }
        for (int i = 37; i <= 43; i++) {
            Material material = ((QuestTypeCollectItem) quest.getType()).item.material;
            if (shulkers != 0) {
                shulkers--;
                setItem(i, getDynamic("item_shulker"));
                getItem(i).set("lore", lore);
                getItem(i).set("name", item);
            } else if (stacks != 0) {
                setItem(i, getDynamic("item_stack"));
                getItem(i).set("material", material);
                getItem(i).set("lore", lore);
                getItem(i).set("name", item);
                getItem(i).set("stack", stacks);
                stacks = 0;
            } else if (items != 0) {
                setItem(i, getDynamic("item_single"));
                getItem(i).set("material", material);
                getItem(i).set("lore", lore);
                getItem(i).set("name", item);
                getItem(i).set("stack", items);
                items = 0;
            }
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
        modifyTimerBar();
        modifyItemBar();
    }

    public static <T extends Player> void openDynamic(T player, PanelPosition panelPosition, Quest quest) {
        DynamicPanel.openDynamic(QuestDPCollectItem.class, panelPosition, player, quest);
    }
}
