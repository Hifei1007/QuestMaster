package me.hifei.questmaster.event.normal;

import me.hifei.questmaster.api.CoreManager;
import me.hifei.questmaster.api.event.NormalQuestEvent;
import me.hifei.questmaster.running.gsoncfg.event.SingleEventConfig;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;

@SuppressWarnings("unused")
public class EffectQuestEvent extends NormalQuestEvent {
    public PotionEffectType type;


    public EffectQuestEvent(SingleEventConfig config) {
        super(config);
    }

    @Override
    protected void preprocess() {
        Random random = new Random();
        type = PotionEffectType.values()[random.nextInt(PotionEffectType.values().length)];
    }

    public String getName() {
        return super.getName().formatted(
                CoreManager.translateMaterialTool.translate_file.get(
                        "effect.minecraft." + type.getKey().getKey())
        );
    }

    protected void tick() {
        super.tick();
        if (timer.totalRemainingSecond() > 10)
        CoreManager.game.runEachPlayer((player) -> player.addPotionEffect(
                new PotionEffect(type, 200, 0)
        ));
    }
}
