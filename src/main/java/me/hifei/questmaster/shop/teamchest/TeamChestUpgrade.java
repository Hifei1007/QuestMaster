package me.hifei.questmaster.shop.teamchest;

import me.hifei.questmaster.running.config.Message;
import me.hifei.questmaster.shop.Upgrade;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class TeamChestUpgrade extends Upgrade {
    public final int id;
    private Inventory inventory;

    public TeamChestUpgrade(int id) {
        this.id = id;
    }


    @Override
    public int getMaxLevel() {
        return 6;
    }

    @Override
    public String getName() {
        return Message.get("upgrade.teamchest.name", id);
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void reloadInventory() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getOpenInventory().getTopInventory() == inventory) player.closeInventory();
        }
        Inventory inv = Bukkit.createInventory(null, getLevel() * 9, getName());
        if (inventory != null)
            for (int i = 0; i < inventory.getSize(); i++) {
                inv.setItem(i, inventory.getItem(i));
            }
        inventory = inv;
    }
}
