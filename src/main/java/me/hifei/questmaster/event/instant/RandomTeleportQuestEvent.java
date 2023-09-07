package me.hifei.questmaster.event.instant;

import me.hifei.questmaster.api.CoreManager;
import me.hifei.questmaster.api.event.InstantQuestEvent;
import me.hifei.questmaster.running.gsoncfg.event.SingleEventConfig;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;

import java.util.List;
import java.util.Random;

@SuppressWarnings("unused")
public class RandomTeleportQuestEvent extends InstantQuestEvent {
    public RandomTeleportQuestEvent(SingleEventConfig config) {
        super(config);
    }

    @Override
    public void doChange() {
        CoreManager.game.runEachPlayer((player) -> {
            List<Entity> targets = Bukkit.selectEntities(player, "@e[distance=..128]");
            if (targets.isEmpty()) return;
            Entity target = targets.get(new Random().nextInt(targets.size()));
            player.teleport(target);
        });
    }
}
