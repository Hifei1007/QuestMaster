package me.hifei.questmaster.event.normal;

import me.hifei.questmaster.api.event.NormalQuestEvent;
import me.hifei.questmaster.running.gsoncfg.event.SingleEventConfig;
import me.hifei.questmaster.running.gsoncfg.rolling.DoubleBoundConfig;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.Random;

@SuppressWarnings("unused")
public class BlockChanceQuestEvent extends NormalQuestEvent {
    public double chance;

    public BlockChanceQuestEvent(SingleEventConfig config) {
        super(config);
    }

    public String getName() {
        return super.getName().formatted(chance * 100, "%");
    }

    @Override
    protected void preprocess() {
        chance = loadSettings(DoubleBoundConfig.class).next();
    }

    protected Listener getListener() {
        return new Listener() {
            @EventHandler
            public void onBlockBreak(BlockBreakEvent event) {
                Random random = new Random();
                if (random.nextDouble() < chance) event.setDropItems(false);
            }
        };
    }
}
