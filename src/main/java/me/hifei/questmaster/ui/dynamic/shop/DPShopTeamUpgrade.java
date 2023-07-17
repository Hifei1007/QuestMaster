package me.hifei.questmaster.ui.dynamic.shop;

import me.hifei.questmaster.CoreManager;
import me.hifei.questmaster.api.team.QuestTeam;
import me.hifei.questmaster.running.config.Message;
import me.hifei.questmaster.shop.Upgrade;
import me.hifei.questmaster.ui.DynamicPanel;
import me.hifei.questmaster.ui.UIManager;
import me.rockyhawk.commandpanels.openpanelsmanager.PanelPosition;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class DPShopTeamUpgrade extends DynamicPanel {
    public final static int[] magicDefenseUpgradeTable = {
            0, 10, 15, 20, 25, 0
    };
    public final static int[] fireDefenseUpgradeTable = {
            0, 10, 15, 20, 25, 35, 0
    };
    public final static int[] projectileDefenseUpgradeTable = {
            0, 10, 25, 40, 0
    };
    public final static int[] explosionDefenseUpgradeTable = {
            0, 10, 15, 20, 25, 0
    };
    public final static int[] fallDefenseUpgradeTable = {
            0, 10, 15, 20, 25, 35, 0
    };
    public final static int[] weaponDefenseUpgradeTable = {
            0, 10, 25, 40, 0
    };

    static {
        UIManager.ins.registerEvent("shop_teamupgrade_back", (event -> DPShopRoot.openDynamic(event.getPlayer(), PanelPosition.Top)));
        UIManager.ins.registerListener(DPShopTeamUpgrade.class, (event -> {
            QuestTeam team = CoreManager.manager.getTeam(event.getPlayer());
            assert team != null;
            switch (event.getMessage()) {
                case "shop_teamupgrade_defense_magic" -> upgrade(event.getPlayer(), team, team.getUpgrades().get("defense_magic"), magicDefenseUpgradeTable);
                case "shop_teamupgrade_defense_fire" -> upgrade(event.getPlayer(), team, team.getUpgrades().get("defense_fire"), fireDefenseUpgradeTable);
                case "shop_teamupgrade_defense_projectile" -> upgrade(event.getPlayer(), team, team.getUpgrades().get("defense_projectile"), projectileDefenseUpgradeTable);
                case "shop_teamupgrade_defense_explosion" -> upgrade(event.getPlayer(), team, team.getUpgrades().get("defense_explosion"), explosionDefenseUpgradeTable);
                case "shop_teamupgrade_defense_fall" -> upgrade(event.getPlayer(), team, team.getUpgrades().get("defense_fall"), fallDefenseUpgradeTable);
                case "shop_teamupgrade_defense_weapon" -> upgrade(event.getPlayer(), team, team.getUpgrades().get("defense_weapon"), weaponDefenseUpgradeTable);
            }
        }));
    }

    public static <T extends Player> void openDynamic(T player, PanelPosition panelPosition) {
       DynamicPanel.openDynamic(DPShopTeamUpgrade.class, panelPosition, player);
    }

    public DPShopTeamUpgrade(@NotNull Player player) {
        super(player);
        startAutoUpdate(5);
    }

    private static void upgrade(Player player, QuestTeam team, Upgrade upgrade, int[] table) {
        if (upgrade.getLevel() == upgrade.getMaxLevel()) return;
        if (team.point() < table[upgrade.getLevel() + 1]) return;
        team.setPoint(team.point() - table[upgrade.getLevel() + 1]);
        team.teamBroadcast(Message.get("upgrade.upgrade", player.getDisplayName(), upgrade.getName(), upgrade.getLevel(), upgrade.getLevel() + 1));
        upgrade.addLevel(1);
    }

    private void updateDefenseUpgrade(QuestTeam team, int slot, int[] table, Upgrade upgrade, String name) {
        List<String> lore = getItem(slot).getStringList("lore");
        int require = table[upgrade.getLevel() + 1];
        lore.set(1, getMessage("upgrade.level", upgrade.getLevel(), upgrade.getMaxLevel()));
        if (upgrade.getLevel() == upgrade.getMaxLevel()) {
            lore.set(2, getMessage("defense." + name + ".lore_max", upgrade.getLevel() * 20, "%"));
            lore.set(3, getMessage("upgrade.lore.max"));
        } else {
            lore.set(2, getMessage("defense." + name + ".lore", upgrade.getLevel() * 20,
                    "%", (upgrade.getLevel() + 1) * 20, "%"));
            lore.set(3, getMessage("upgrade.lore.point", require));
        }
        if (upgrade.getLevel() == upgrade.getMaxLevel()) lore.set(5, getMessage("upgrade.max"));
        else if (team.point() >= require) lore.set(5, getMessage("upgrade.ok"));
        else lore.set(5, getMessage("upgrade.require"));
        getItem(slot).set("lore", lore);
    }

    @Override
    protected void dynamicModify(Player player) {
        loadTemplate("panels/shop/teamupgrade.yml");

        QuestTeam team = CoreManager.manager.getTeam(player);
        assert team != null;
        Map<String, Upgrade> upgrades = team.getUpgrades();

        Upgrade magicDefenseUpgrade = upgrades.get("defense_magic");
        updateDefenseUpgrade(team, 38, magicDefenseUpgradeTable, magicDefenseUpgrade, "magic");
        Upgrade fireDefenseUpgrade = upgrades.get("defense_fire");
        updateDefenseUpgrade(team, 39, fireDefenseUpgradeTable, fireDefenseUpgrade, "fire");
        Upgrade projectileDefenseUpgrade = upgrades.get("defense_projectile");
        updateDefenseUpgrade(team, 40, projectileDefenseUpgradeTable, projectileDefenseUpgrade, "projectile");
        Upgrade explosionDefenseUpgrade = upgrades.get("defense_explosion");
        updateDefenseUpgrade(team, 41, explosionDefenseUpgradeTable, explosionDefenseUpgrade, "explosion");
        Upgrade fallDefenseUpgrade = upgrades.get("defense_fall");
        updateDefenseUpgrade(team, 42, fallDefenseUpgradeTable, fallDefenseUpgrade, "fall");
        Upgrade weaponDefenseUpgrade = upgrades.get("defense_weapon");
        updateDefenseUpgrade(team, 43, weaponDefenseUpgradeTable, weaponDefenseUpgrade, "weapon");
    }
}
