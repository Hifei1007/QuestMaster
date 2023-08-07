package me.hifei.questmaster.quest.questmineblock;

import me.hifei.questmaster.CoreManager;
import me.hifei.questmaster.api.quest.Quest;
import me.hifei.questmaster.running.config.Message;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.Objects;

public class QuestListenerMineBlock implements Listener {

    @EventHandler
    public void onMineBlock(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (!CoreManager.manager.hasTeam(player)) return;
        for (Quest quest : Objects.requireNonNull(CoreManager.manager.getTeam(player)).getQuests()) {
            if (!(quest.getType() instanceof QuestTypeMineBlock qt)) continue;
            if (event.getBlock().getType() != qt.item.obj()) continue;
            if (!qt.lastCombo.containsKey(player.getName())) {
                qt.lastCombo.put(player.getName(), System.currentTimeMillis());
                qt.combo.put(player.getName(), 1);
            } else if (qt.lastCombo.get(player.getName()) + 5000 >= System.currentTimeMillis()) {
                qt.lastCombo.put(player.getName(), System.currentTimeMillis());
                qt.combo.put(player.getName(), qt.combo.get(player.getName()) + 1);
            } else {
                qt.lastCombo.put(player.getName(), System.currentTimeMillis());
                qt.combo.put(player.getName(), 1);
            }
            boolean autoSubmit = CoreManager.autoSubmitMode.getOrDefault(player.getName(), true);
            if (!autoSubmit) {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Message.get("quest.mineblock.auto_switch.off")));
                break;
            }
            qt.addCount();
            player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 10.0f, 1.0f);
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Message.get(
                    "quest.mineblock.add_count",
                    quest.getName(), quest.getCurrentCount(), qt.combo.get(player.getName()),
                    quest.getTotalCount(), quest.getProgress() * 100, "%")));
            event.setDropItems(false);
            break;
        }
    }
}
