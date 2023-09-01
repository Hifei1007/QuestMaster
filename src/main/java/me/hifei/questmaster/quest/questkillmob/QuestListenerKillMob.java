package me.hifei.questmaster.quest.questkillmob;

import me.hifei.questmaster.manager.QuestListenerTool;
import me.hifei.questmaster.running.config.Message;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;

public class QuestListenerKillMob implements Listener {
    @EventHandler
    public void onKillMob(EntityDeathEvent eventDeath) {
        EntityDamageEvent e = eventDeath.getEntity().getLastDamageCause();
        if (!(e instanceof EntityDamageByEntityEvent eventDamage)) return;
        if (!(eventDamage.getDamager() instanceof Player player)) return;
        QuestListenerTool.findQuest(player, QuestTypeKillMob.class, (quest, qt) -> {
            if (eventDeath.getEntity().getType() != qt.getTableItem().obj()) return false;
            if (QuestListenerTool.checkAutoSubmit(player)) return true;
            QuestListenerTool.updateCombo(qt.combo, qt.lastCombo, player.getName());
            qt.addCount();
            player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 10.0f, 1.0f);
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Message.get(
                    "quest.killmob.add_count",
                    quest.getName(), quest.getCurrentCount(), qt.combo.get(player.getName()),
                    quest.getTotalCount(), quest.getProgress() * 100, "%")));
            eventDeath.getDrops().clear();
            eventDeath.setDroppedExp(0);
            return true;
        });
    }
}
