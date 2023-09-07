package me.hifei.questmaster.event.instant;

import me.hifei.questmaster.api.CoreManager;
import me.hifei.questmaster.api.event.InstantQuestEvent;
import me.hifei.questmaster.running.gsoncfg.event.SingleEventConfig;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class SwapQuestEvent extends InstantQuestEvent {
    public enum SwapType {
        LOCATION,
        INVENTORY,
        HEALTH
    }

    private static class Settings {
        public SwapType type;
    }

    public SwapQuestEvent(SingleEventConfig config) {
        super(config);
    }

    private void swapLocation() {
        List<Location> locations = CoreManager.game.getPlayers()
                .stream()
                .map(Entity::getLocation)
                .collect(Collectors.toList());
        Collections.shuffle(locations);
        Iterator<Player> playerIterator = CoreManager.game.getPlayers().iterator();
        Iterator<Location> locationIterator = locations.iterator();
        while (playerIterator.hasNext() && locationIterator.hasNext()) {
            playerIterator.next().teleport(locationIterator.next());
        }
    }

    private void swapHealth() {
        List<Double> healths = CoreManager.game.getPlayers()
                .stream()
                .map(LivingEntity::getHealth)
                .collect(Collectors.toList());
        Collections.shuffle(healths);
        Iterator<Player> playerIterator = CoreManager.game.getPlayers().iterator();
        Iterator<Double> healthIterator = healths.iterator();
        while (playerIterator.hasNext() && healthIterator.hasNext()) {
            Player player = playerIterator.next();
            double health = healthIterator.next();
            if (player.getHealth() <= 0) {
                continue;
            }
            player.setHealth(health);
        }
    }

    private void swapInventory() {
        List<ItemStack[]> inventories = CoreManager.game.getPlayers()
                .stream()
                .map((player) -> player.getInventory().getContents())
                .collect(Collectors.toList());
        Collections.shuffle(inventories);
        Iterator<Player> playerIterator = CoreManager.game.getPlayers().iterator();
        Iterator<ItemStack[]> inventoryIterator = inventories.iterator();
        while (playerIterator.hasNext() && inventoryIterator.hasNext()) {
            PlayerInventory origin = playerIterator.next().getInventory();
            origin.setContents(inventoryIterator.next());
        }
    }

    @Override
    public void doChange() {
        SwapType type = loadSettings(Settings.class).type;
        switch (type) {
            case LOCATION -> swapLocation();
            case HEALTH -> swapHealth();
            case INVENTORY -> swapInventory();
        }
    }
}
