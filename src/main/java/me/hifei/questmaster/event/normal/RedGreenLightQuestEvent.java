package me.hifei.questmaster.event.normal;

import me.hifei.questmaster.api.CoreManager;
import me.hifei.questmaster.api.event.NormalQuestEvent;
import me.hifei.questmaster.api.quest.Timer;
import me.hifei.questmaster.running.gsoncfg.event.SingleEventConfig;
import me.hifei.questmaster.running.gsoncfg.rolling.IntegerBoundConfig;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

@SuppressWarnings("unused")
public class RedGreenLightQuestEvent extends NormalQuestEvent {
    private enum LightState {
        RED,
        YELLOW,
        GREEN
    }

    private LightState state;
    private Timer lightTimer;
    private final Settings settings;
    private int actionTime;

    private static class Settings {
        public IntegerBoundConfig redTime;
        public IntegerBoundConfig yellowTime;
        public IntegerBoundConfig greenTime;
        public String redMessage;
        public String yellowMessage;
        public String greenMessage;
    }

    public RedGreenLightQuestEvent(SingleEventConfig config) {
        super(config);
        state = LightState.GREEN;
        settings = loadSettings(Settings.class);
        lightTimer = new Timer(settings.greenTime.next());
        lightTimer.start();
    }

    protected void tick() {
        super.tick();
        if (lightTimer.isTimeUp()) {
            CoreManager.manager.runEachPlayer((player) -> player.playSound(player, Sound.ENTITY_ARROW_HIT_PLAYER, 1, 0));
            switch (state) {
                case GREEN -> {
                    state = LightState.YELLOW;
                    lightTimer = new Timer(settings.yellowTime.next());
                    lightTimer.start();
                }
                case YELLOW -> {
                    state = LightState.RED;
                    lightTimer = new Timer(settings.redTime.next());
                    lightTimer.start();
                }
                case RED -> {
                    state = LightState.GREEN;
                    lightTimer = new Timer(settings.greenTime.next());
                    lightTimer.start();
                }
            }
        }
        if (actionTime == 0) actionTime = 20;
        actionTime--;
        if (actionTime == 0) return;
        CoreManager.manager.runEachPlayer((player) -> {
            switch (state) {
                case GREEN -> player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                        new TextComponent(settings.greenMessage.replace('&', ChatColor.COLOR_CHAR)));
                case YELLOW -> player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                        new TextComponent(settings.yellowMessage.replace('&', ChatColor.COLOR_CHAR)));
                case RED -> player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                        new TextComponent(settings.redMessage.replace('&', ChatColor.COLOR_CHAR)));
            }
        });
    }

    protected Listener getListener() {
        return new Listener() {
            @EventHandler
            public void onPlayerMove(PlayerMoveEvent event) {
                if (state != LightState.RED) return;
                event.getPlayer().damage(10000.0);
            }
        };
    }
}
