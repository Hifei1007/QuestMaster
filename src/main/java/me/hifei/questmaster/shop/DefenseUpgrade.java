package me.hifei.questmaster.shop;

import me.hifei.questmaster.api.CoreManager;
import me.hifei.questmaster.api.team.QuestTeam;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.List;

public class DefenseUpgrade extends Upgrade {
    public enum DamageType {
        //todo: message
        MAGIC("魔法伤害防御", 4,
                EntityDamageEvent.DamageCause.DRAGON_BREATH,
                EntityDamageEvent.DamageCause.MAGIC,
                EntityDamageEvent.DamageCause.SONIC_BOOM,
                EntityDamageEvent.DamageCause.POISON,
                EntityDamageEvent.DamageCause.WITHER,
                EntityDamageEvent.DamageCause.THORNS
        ),
        FIRE("火焰伤害防御",5,
                EntityDamageEvent.DamageCause.FIRE,
                EntityDamageEvent.DamageCause.FIRE_TICK,
                EntityDamageEvent.DamageCause.HOT_FLOOR,
                EntityDamageEvent.DamageCause.LAVA
        ),
        PROJECTILE("弹射物伤害防御",3,
                EntityDamageEvent.DamageCause.PROJECTILE
        ),
        EXPLOSION("爆炸伤害防御",4,
                EntityDamageEvent.DamageCause.BLOCK_EXPLOSION,
                EntityDamageEvent.DamageCause.ENTITY_EXPLOSION
        ),
        FALL("摔落伤害防御", 5,
                EntityDamageEvent.DamageCause.FALL
        ),
        WEAPON("近战伤害防御",3,
                EntityDamageEvent.DamageCause.ENTITY_ATTACK,
                EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK
        )
        ;
        private final List<EntityDamageEvent.DamageCause> causes;
        public final int maxLevel;
        public final String name;
        DamageType(String name, int maxLevel, EntityDamageEvent.DamageCause... causes) {
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

    public DefenseUpgrade(DamageType target, QuestTeam team) {
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
            public void onDamage(EntityDamageEvent event) {
                Entity entity = event.getEntity();
                if (!(entity instanceof Player)) return;
                QuestTeam t = CoreManager.manager.getTeam((Player) entity);
                if (t != team) return;
                EntityDamageEvent.DamageCause cause = event.getCause();
                if (target == DamageType.findType(cause)) {
                    event.setDamage(event.getDamage() * (1 - (getLevel() * 0.2)));
                }
            }
        };
    }
}
