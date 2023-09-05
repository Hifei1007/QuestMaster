package me.hifei.questmaster.event.instant;

import me.hifei.questmaster.api.CoreManager;
import me.hifei.questmaster.api.event.InstantQuestEvent;
import me.hifei.questmaster.running.gsoncfg.event.SingleEventConfig;
import me.hifei.questmaster.running.gsoncfg.rolling.IntegerBoundConfig;
import org.bukkit.inventory.Inventory;

public class InventoryBreakQuestEvent extends InstantQuestEvent {

    public InventoryBreakQuestEvent(SingleEventConfig config) {
        super(config);
    }

    @Override
    public void doChange() {
        IntegerBoundConfig boundConfig = loadSettings(IntegerBoundConfig.class);
        CoreManager.game.runEachPlayer((player) -> {
            Inventory inventory = player.getInventory();
            for (int i = 0; i < inventory.getSize(); i++) {
                if (boundConfig.next() == 0) inventory.setItem(i, null);
            }
        });
    }
}
