package me.hifei.questmaster.event.normal;

import me.hifei.questmaster.api.event.NormalQuestEvent;
import me.hifei.questmaster.running.gsoncfg.event.SingleEventConfig;
import me.hifei.questmaster.running.gsoncfg.rolling.DoubleBoundConfig;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

@SuppressWarnings("unused")
public class EntityDamageModifyQuestEvent extends NormalQuestEvent {
    public enum ModifyTarget {
        DAMAGER,
        ENTITY
    }

    public enum ModifyEntity {
        PLAYER,
        NON_PLAYER
    }

    private static class Settings {
        public ModifyEntity entity;
        public ModifyTarget target;
        public DoubleBoundConfig bound;
    }

    public double modifyValue;

    public EntityDamageModifyQuestEvent(SingleEventConfig config) {
        super(config);
    }

    @Override
    protected void preprocess() {
        modifyValue = loadSettings(Settings.class).bound.next();
    }

    public String getName() {
        return super.getName().formatted(modifyValue * 100, "%");
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean isModifyTarget(Entity entity) {
        Settings config = loadSettings(Settings.class);
        if (entity.getType() == EntityType.PLAYER) return config.entity == ModifyEntity.PLAYER;
        else return config.entity == ModifyEntity.NON_PLAYER;
    }

    protected Listener getListener() {
        return new Listener() {
            @EventHandler
            public void onDamageByEntity(EntityDamageByEntityEvent event) {
                Settings config = loadSettings(Settings.class);
                if (config.target != ModifyTarget.DAMAGER) return;
                if (!isModifyTarget(event.getEntity())) return;
                event.setDamage(event.getDamage() * modifyValue);
            }

            @EventHandler
            public void onDamage(EntityDamageEvent event) {
                Settings config = loadSettings(Settings.class);
                if (config.target != ModifyTarget.ENTITY) return;
                if (!isModifyTarget(event.getEntity())) return;
                event.setDamage(event.getDamage() * modifyValue);
            }
        };
    }
}
