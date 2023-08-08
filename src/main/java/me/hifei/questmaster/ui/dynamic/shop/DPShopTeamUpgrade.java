package me.hifei.questmaster.ui.dynamic.shop;

import me.hifei.questmaster.CoreManager;
import me.hifei.questmaster.api.team.QuestTeam;
import me.hifei.questmaster.running.config.Message;
import me.hifei.questmaster.shop.Upgrade;
import me.hifei.questmaster.ui.core.DynamicPanel;
import me.hifei.questmaster.ui.core.UIManager;
import me.rockyhawk.commandpanels.openpanelsmanager.PanelPosition;
import org.bukkit.Sound;
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
    public final static int[] magicDamageUpgradeTable = {
            0, 10, 20, 30, 40, 50, 0
    };
    public final static int[] projectileDamageUpgradeTable = {
            0, 10, 25, 40, 60, 80, 0
    };
    public final static int[] explosionDamageUpgradeTable = {
            0, 10, 20, 30, 40, 50, 0
    };
    public final static int[] weaponDamageUpgradeTable = {
            0, 10, 25, 40, 60, 80, 0
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
                case "shop_teamupgrade_damage_magic" -> upgrade(event.getPlayer(), team, team.getUpgrades().get("damage_magic"), magicDamageUpgradeTable);
                case "shop_teamupgrade_damage_projectile" -> upgrade(event.getPlayer(), team, team.getUpgrades().get("damage_projectile"), projectileDamageUpgradeTable);
                case "shop_teamupgrade_damage_explosion" -> upgrade(event.getPlayer(), team, team.getUpgrades().get("damage_explosion"), explosionDamageUpgradeTable);
                case "shop_teamupgrade_damage_weapon" -> upgrade(event.getPlayer(), team, team.getUpgrades().get("damage_weapon"), weaponDamageUpgradeTable);
            }
        }));
    }

    public static <T extends Player> void openDynamic(T player, PanelPosition panelPosition) {
       DynamicPanel.openDynamic(panelPosition, new DPShopTeamUpgrade(player));
    }

    public DPShopTeamUpgrade(@NotNull Player player) {
        super(player);
        startAutoUpdate(5);
    }

    private static void upgrade(Player player, QuestTeam team, Upgrade upgrade, int[] table) {
        if (upgrade.getLevel() == upgrade.getMaxLevel()) return;
        if (team.point() < table[upgrade.getLevel() + 1]) return;
        team.setPoint(team.point() - table[upgrade.getLevel() + 1]);
        player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 10.0f, 1.0f);
        team.teamBroadcast(Message.get("upgrade.upgrade", player.getDisplayName(), upgrade.getName(), upgrade.getLevel(), upgrade.getLevel() + 1));
        upgrade.addLevel(1);
    }

    private void updateUpgrade(QuestTeam team, int slot, int[] table, Upgrade upgrade, String name) {
        List<String> lore = getItem(slot).getStringList("lore");
        int require = table[upgrade.getLevel() + 1];
        lore.set(1, getMessage("upgrade.level", upgrade.getLevel(), upgrade.getMaxLevel()));
        if (upgrade.getLevel() == upgrade.getMaxLevel()) {
            lore.set(2, getMessage(name + ".lore_max", upgrade.getLevel() * 20, "%"));
            lore.set(3, getMessage("upgrade.lore.max"));
        } else {
            lore.set(2, getMessage(name + ".lore", upgrade.getLevel() * 20,
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
        updateUpgrade(team, 38, magicDefenseUpgradeTable, magicDefenseUpgrade, "defense.magic");
        Upgrade fireDefenseUpgrade = upgrades.get("defense_fire");
        updateUpgrade(team, 39, fireDefenseUpgradeTable, fireDefenseUpgrade, "defense.fire");
        Upgrade projectileDefenseUpgrade = upgrades.get("defense_projectile");
        updateUpgrade(team, 40, projectileDefenseUpgradeTable, projectileDefenseUpgrade, "defense.projectile");
        Upgrade explosionDefenseUpgrade = upgrades.get("defense_explosion");
        updateUpgrade(team, 41, explosionDefenseUpgradeTable, explosionDefenseUpgrade, "defense.explosion");
        Upgrade fallDefenseUpgrade = upgrades.get("defense_fall");
        updateUpgrade(team, 42, fallDefenseUpgradeTable, fallDefenseUpgrade, "defense.fall");
        Upgrade weaponDefenseUpgrade = upgrades.get("defense_weapon");
        updateUpgrade(team, 43, weaponDefenseUpgradeTable, weaponDefenseUpgrade, "defense.weapon");
        Upgrade magicDamageUpgrade = upgrades.get("damage_magic");
        updateUpgrade(team, 29, magicDamageUpgradeTable, magicDamageUpgrade, "damage.magic");
        Upgrade projectileDamageUpgrade = upgrades.get("damage_projectile");
        updateUpgrade(team, 30, projectileDamageUpgradeTable, projectileDamageUpgrade, "damage.projectile");
        Upgrade explosionDamageUpgrade = upgrades.get("damage_explosion");
        updateUpgrade(team, 31, explosionDamageUpgradeTable, explosionDamageUpgrade, "damage.explosion");
        Upgrade weaponDamageUpgrade = upgrades.get("damage_weapon");
        updateUpgrade(team, 32, weaponDamageUpgradeTable, weaponDamageUpgrade, "damage.weapon");
    }
}
