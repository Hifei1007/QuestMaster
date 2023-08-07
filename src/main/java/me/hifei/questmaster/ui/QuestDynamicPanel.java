package me.hifei.questmaster.ui;

import me.hifei.questmaster.api.quest.Quest;
import me.hifei.questmaster.api.quest.Timer;
import me.hifei.questmaster.running.config.Message;
import me.hifei.questmaster.ui.dynamic.DPQuest;
import me.rockyhawk.commandpanels.openpanelsmanager.PanelPosition;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class QuestDynamicPanel extends DynamicPanel {
    static {
        UIManager.ins.registerEvent("quest_main_back", (event -> DPQuest.openDynamic(event.getPlayer(), PanelPosition.Top)));
    }

    protected final Quest quest;

    protected QuestDynamicPanel(@NotNull Player player, Quest quest) {
        super(player, false);
        this.quest = quest;
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

    protected void modifyItemBar(Material material) {
        int items = quest.getTotalCount() - quest.getCurrentCount();
        String item = getMessage("item.name", items);
        int shulkers = items / (64 * 27);
        items %= (64 * 27);
        int stacks = items / 64;
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
            if (shulkers != 0) {
                shulkers--;
                setItem(i, getDynamic("item_shulker"));
                modifyItem(i, null, lore, item, null);
            } else if (stacks != 0) {
                setItem(i, getDynamic("item_stack"));
                modifyItem(i, material, lore, item, stacks);
                stacks = 0;
            } else if (items != 0) {
                setItem(i, getDynamic("item_single"));
                modifyItem(i, material, lore, item, items);
                items = 0;
            }
        }
    }


    public void dynamicModify(Player player) {
        getConfig().set("title", quest.getName());
        getItem(1).set("name", Message.get("quest.score", quest.getReward().score()));
        getItem(3).set("name", Message.get("quest.point", quest.getReward().point(), quest.getReward().point() / 10));
        getItem(5).set("name", Message.get("quest.coin", quest.getReward().coin(), quest.getReward().coin() / 10));
        getItem(7).set("name", Message.get("quest.time", quest.getReward().time()));
        setItem(13, quest.getItem());
        modifyTimerBar();
    }
}
