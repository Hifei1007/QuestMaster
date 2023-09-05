package me.hifei.questmaster.event.normal;

import me.hifei.questmaster.api.event.NormalQuestEvent;
import me.hifei.questmaster.running.gsoncfg.event.SingleEventConfig;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class DeathWhenDamageQuestEvent extends NormalQuestEvent {
    public DeathWhenDamageQuestEvent(SingleEventConfig config) {
        super(config);
    }

    protected Listener getListener() {
        return new Listener() {
            @EventHandler(priority = EventPriority.HIGHEST)
            public void onPlayerHurt(EntityDamageEvent event) {
                if (!event.getEntityType().equals(EntityType.PLAYER)) return;
                event.setDamage(10000.0);
            }
        };
    }
}
