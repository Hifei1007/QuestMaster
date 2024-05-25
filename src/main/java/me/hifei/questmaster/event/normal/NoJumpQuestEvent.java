package me.hifei.questmaster.event.normal;

import me.hifei.questmaster.api.CoreManager;
import me.hifei.questmaster.api.event.NormalQuestEvent;
import me.hifei.questmaster.running.gsoncfg.event.SingleEventConfig;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@SuppressWarnings("unused")
public class NoJumpQuestEvent extends NormalQuestEvent {
    public NoJumpQuestEvent(SingleEventConfig config) {
        super(config);
    }

    protected void tick() {
        super.tick();
        CoreManager.manager.runEachPlayer((player) -> player.addPotionEffect(new PotionEffect(
                PotionEffectType.JUMP,
                3,
                128,
                false,
                false,
                false
        )));
    }
}
