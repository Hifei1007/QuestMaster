package me.hifei.questmaster.ui.dynamic;

import me.hifei.questmaster.api.CoreManager;
import me.hifei.questmaster.api.quest.Quest;
import me.hifei.questmaster.api.team.QuestTeam;
import me.hifei.questmaster.running.config.Message;
import me.hifei.questmaster.shop.Upgrade;
import me.hifei.questmaster.shop.teamchest.TeamChestUpgrade;
import me.hifei.questmaster.tools.ActionTool;
import me.hifei.questmaster.tools.LocationTool;
import me.hifei.questmaster.ui.core.DPConfirm;
import me.hifei.questmaster.ui.core.DynamicPanel;
import me.hifei.questmaster.ui.core.UIManager;
import me.hifei.questmaster.ui.dynamic.shop.DPLocate;
import me.hifei.questmaster.ui.dynamic.shop.DPShopItem;
import me.hifei.questmaster.ui.dynamic.shop.DPShopTeamUpgrade;
import me.rockyhawk.commandpanels.api.PanelCommandEvent;
import me.rockyhawk.commandpanels.openpanelsmanager.PanelPosition;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DPRoot extends DynamicPanel {

    static {
        UIManager.ins.registerEvent("root_open_quest", (event -> DPQuest.openDynamic(event.getPlayer(), PanelPosition.Top)));
        UIManager.ins.registerEvent("root_open_teleport", (event -> DPTeleport.openDynamic(event.getPlayer(), PanelPosition.Top)));
        UIManager.ins.registerEvent("root_open_teamupgrade", (event -> DPShopTeamUpgrade.openDynamic(event.getPlayer(), PanelPosition.Top)));
        UIManager.ins.registerEvent("root_switch_auto_submit",
                (event -> CoreManager.autoSubmitMode.put(event.getPlayer().getName(),
                        !CoreManager.autoSubmitMode.getOrDefault(event.getPlayer().getName(), true))));
        UIManager.ins.registerEvent("root_open_teamchest_1", (event -> onOpenTeamChest(event, 1)));
        UIManager.ins.registerEvent("root_upgrade_teamchest_1", (event -> onUpgradeTeamChest(event, 1)));
        UIManager.ins.registerEvent("root_open_teamchest_2", (event -> onOpenTeamChest(event, 2)));
        UIManager.ins.registerEvent("root_upgrade_teamchest_2", (event -> onUpgradeTeamChest(event, 2)));
        UIManager.ins.registerEvent("root_open_teamchest_3", (event -> onOpenTeamChest(event, 3)));
        UIManager.ins.registerEvent("root_upgrade_teamchest_3", (event -> onUpgradeTeamChest(event, 3)));
        UIManager.ins.registerEvent("root_send_location", (event -> {
            Player player = event.getPlayer();
            QuestTeam team = CoreManager.manager.getTeam(player);
            assert team != null;
            Location location = player.getLocation();
            for (Player p : team.members()) {
                p.sendMessage(Message.get("team.send_location.message1", player.getDisplayName()));
                p.spigot().sendMessage(
                        Message.getComponent("team.send_location.message2", LocationTool.formatLocation(location)),
                        ActionTool.addAction(Message.getComponent("team.send_location.action"), (sender) -> {
                            if (!(sender instanceof Player pl)) return;
                            pl.teleport(location);
                            pl.sendMessage(Message.get("team.send_location.teleport", LocationTool.formatLocation(location)));
                        }));
            }
        }));
        UIManager.ins.registerEvent("root_open_locate", (event -> DPLocate.openDynamic(event.getPlayer(), PanelPosition.Top)));
        UIManager.ins.registerEvent("root_open_itemshop", (event -> DPShopItem.openDynamic(event.getPlayer(), PanelPosition.Top)));
        UIManager.ins.registerEvent("root_forcestop", (event -> DPConfirm.openDynamic(
                event.getPlayer(), PanelPosition.Top,
                () -> CoreManager.game.drop(),
                () -> DPRoot.openDynamic(event.getPlayer(), PanelPosition.Top)
        )));
    }

    private final static int[][] teamchest_cost = {{}, {200, 300, 400, 500, 600, 700, 0},
            {500, 600, 700, 800, 1000, 1200, 0},
            {1200, 1300, 1400, 1600, 1800, 2000, 0}};

    public static <T extends Player> void openDynamic(T player, PanelPosition panelPosition) {
        DynamicPanel.openDynamic(panelPosition, new DPRoot(player));
    }

    private static void onOpenTeamChest(PanelCommandEvent event, int id) {
        Player player = event.getPlayer();
        QuestTeam team = CoreManager.manager.getTeam(player);
        assert team != null;
        TeamChestUpgrade upgrade = (TeamChestUpgrade) team.getUpgrades().get("teamchest_" + id);
        player.openInventory(upgrade.getInventory());
    }

    private static void onUpgradeTeamChest(PanelCommandEvent event, int id) {
        Player player = event.getPlayer();
        QuestTeam team = CoreManager.manager.getTeam(player);
        assert team != null;
        TeamChestUpgrade upgrade = (TeamChestUpgrade) team.getUpgrades().get("teamchest_" + id);
        int cost = teamchest_cost[id][upgrade.getLevel()];
        if (cost <= team.coin()) {
            team.teamBroadcast(Message.get("upgrade.upgrade", player.getDisplayName(), upgrade.getName(),
                    upgrade.getLevel(), upgrade.getLevel() + 1));
            team.setCoin(team.coin() - cost);
            player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 10.0f, 1.0f);
            upgrade.addLevel();
            upgrade.reloadInventory();
        }
    }

    private void modifyTeamChest(int pos, int id) {
        setItem(pos, getDynamic("teamchest"));
        modifyItem(pos, null, null, getMessage("teamchest.name", id), null);
        QuestTeam team = CoreManager.manager.getTeam(player);
        assert team != null;
        Upgrade upgrade = team.getUpgrades().get("teamchest_" + id);
        int cost = teamchest_cost[id][upgrade.getLevel()];
        List<String> lore = getItem(pos).getStringList("lore");
        if (upgrade.getLevel() == 0) lore.set(1, getMessage("teamchest.locked"));
        else
            lore.set(1, getMessage("teamchest.size", upgrade.getLevel() * 9, upgrade.getLevel(), upgrade.getMaxLevel()));
        if (upgrade.getLevel() != 0) lore.add(getMessage("teamchest.open"));
        if (upgrade.getLevel() == 0) {
            if (cost <= team.coin()) lore.add(getMessage("teamchest.unlock", cost));
            else lore.add(getMessage("teamchest.require", cost));
        } else if (upgrade.getLevel() != upgrade.getMaxLevel()) {
            if (cost <= team.coin()) lore.add(getMessage("teamchest.upgrade", cost));
            else lore.add(getMessage("teamchest.require", cost));
        }
        modifyItem(pos, null, lore, null, null);
        List<String> commands = getItem(pos).getStringList("commands");
        if (upgrade.getLevel() != 0) commands.add("left= event= root_open_teamchest_" + id);
        if (upgrade.getLevel() != upgrade.getMaxLevel()) commands.add("right= event= root_upgrade_teamchest_" + id);
        getItem(pos).set("commands", commands);
    }

    @Override
    protected void dynamicModify(@NotNull Player player) {
        loadTemplate("panels/root.yml");
        setItem(22, getDynamic("quest"));
        ConfigurationSection item = getItem(22);
        List<String> list = item.getStringList("lore");
        QuestTeam team = CoreManager.manager.getTeam(player);
        assert team != null;
        List<Quest> quests = team.getQuests();
        for (int i = 1; i <= quests.size(); i++) {
            list.set(i, quests.get(i - 1).getName());
        }
        modifyItem(22, null, list, null, null);
        modifyItem(2, null, null, getMessage("score.name", team.score()), null);
        modifyItem(4, null, null, getMessage("point.name", team.point()), null);
        modifyItem(6, null, null, getMessage("coin.name", team.coin()), null);
        boolean autoSubmit = CoreManager.autoSubmitMode.getOrDefault(player.getName(), true);
        if (autoSubmit) {
            modifyItem(53, Material.LIME_DYE, null,"&e自动提交&7: &a开启", null);
        } else {
            modifyItem(53, Material.GRAY_DYE, null, "&e自动提交&7: &c关闭", null);
        }
        modifyTeamChest(30, 1);
        modifyTeamChest(31, 2);
        modifyTeamChest(32, 3);
    }

    public DPRoot(@NotNull Player player) {
        super(player);
        startAutoUpdate(5);
    }
}
