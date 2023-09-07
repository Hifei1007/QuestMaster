package me.hifei.questmaster.shop;

import me.hifei.questmaster.api.CoreManager;
import me.hifei.questmaster.api.team.QuestTeam;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.List;

public class DamageUpgrade extends Upgrade {
    public enum DamageType {
        //todo: message
        MAGIC("魔法伤害增加", 5,
                EntityDamageEvent.DamageCause.MAGIC,
                EntityDamageEvent.DamageCause.THORNS
        ),
        PROJECTILE("弹射物伤害增加",5,
                EntityDamageEvent.DamageCause.PROJECTILE
        ),
        EXPLOSION("爆炸伤害增加",5,
                EntityDamageEvent.DamageCause.BLOCK_EXPLOSION,
                EntityDamageEvent.DamageCause.ENTITY_EXPLOSION
        ),
        WEAPON("近战伤害增加",5,
                EntityDamageEvent.DamageCause.ENTITY_ATTACK,
                EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK
        )
        ;
        private final List<EntityDamageEvent.DamageCause> causes;
        public final int maxLevel;
        public final String name;
        DamageType(String name, @SuppressWarnings("SameParameterValue") int maxLevel, EntityDamageEvent.DamageCause... causes) {
            this.name = name;
            this.causes = List.of(causes);
            this.maxLevel = maxLevel;
        }
        public static DamageType findType(EntityDamageEvent.DamageCause cause) {
            for (DamageType type : DamageType.values()) {
                if (type.causes.contains(cause)) return type;
            }
            return null;
        }
    }

    private final int maxLevel;
    private final QuestTeam team;
    private final DamageType target;

    public DamageUpgrade(DamageType target, QuestTeam team) {
        this.maxLevel = target.maxLevel;
        this.team = team;
        this.target = target;
    }

    @Override
    public int getMaxLevel() {
        return maxLevel;
    }

    @Override
    public String getName() {
        return target.name;
    }

    @Override
    protected Listener getListener() {
        return new Listener() {
            @EventHandler
            public void onDamage(EntityDamageByEntityEvent event) {
                Entity entity = event.getDamager();
                if (!(entity instanceof Player)) return;
                QuestTeam t = CoreManager.manager.getTeam((Player) entity);
                if (t != team) return;
                EntityDamageEvent.DamageCause cause = event.getCause();
                if (target == DamageType.findType(cause)) {
                    event.setDamage(event.getDamage() * (1 + (getLevel() * 0.2)));
                }
            }
        };
    }
}
