package me.hifei.questmaster.ui.dynamic;

import me.hifei.questmaster.CoreManager;
import me.hifei.questmaster.api.quest.Quest;
import me.hifei.questmaster.api.team.QuestTeam;
import me.hifei.questmaster.ui.core.DynamicPanel;
import me.hifei.questmaster.ui.core.UIManager;
import me.hifei.questmaster.ui.dynamic.shop.DPShopRoot;
import me.rockyhawk.commandpanels.openpanelsmanager.PanelPosition;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DPRoot extends DynamicPanel {

    static {
        UIManager.ins.registerEvent("root_open_quest", (event -> DPQuest.openDynamic(event.getPlayer(), PanelPosition.Top)));
        UIManager.ins.registerEvent("root_open_teleport", (event -> DPTeleport.openDynamic(event.getPlayer(), PanelPosition.Top)));
        UIManager.ins.registerEvent("root_open_shop", (event -> DPShopRoot.openDynamic(event.getPlayer(), PanelPosition.Top)));
        UIManager.ins.registerEvent("root_switch_auto_submit",
                (event -> CoreManager.autoSubmitMode.put(event.getPlayer().getName(),
                        !CoreManager.autoSubmitMode.getOrDefault(event.getPlayer().getName(), true))));
    }

    public static <T extends Player> void openDynamic(T player, PanelPosition panelPosition) {
       DynamicPanel.openDynamic(panelPosition, new DPRoot(player));
    }

    @Override
    protected void dynamicModify(@NotNull Player player) {
        loadTemplate("panels/root.yml");
        setItem(31, getDynamic("quest"));
        ConfigurationSection item = getItem(31);
        List<String> list = item.getStringList("lore");
        QuestTeam team = CoreManager.manager.getTeam(player);
        assert team != null;
        List<Quest> quests = team.getQuests();
        for (int i = 1; i <= quests.size(); i++) {
            list.set(i, quests.get(i-1).getName());
        }
        item.set("lore", list);
        getItem(11).set("name", getMessage("score.name", team.score()));
        getItem(13).set("name", getMessage("point.name", team.point()));
        getItem(15).set("name", getMessage("coin.name", team.coin()));
        boolean autoSubmit = CoreManager.autoSubmitMode.getOrDefault(player.getName(), true);
        if (autoSubmit) {
            getItem(53).set("material", "LIME_DYE");
            getItem(53).set("name", "&e自动提交&7: &a开启");
        }
        else {
            getItem(53).set("material", "GRAY_DYE");
            getItem(53).set("name", "&e自动提交&7: &c关闭");
        }
    }

    public DPRoot(@NotNull Player player) {
        super(player);
        startAutoUpdate(5);
    }
}
