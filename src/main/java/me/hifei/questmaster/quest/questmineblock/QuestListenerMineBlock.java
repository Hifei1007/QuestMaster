package me.hifei.questmaster.quest.questmineblock;

import me.hifei.questmaster.CoreManager;
import me.hifei.questmaster.manager.QuestListenerTool;
import me.hifei.questmaster.running.config.Message;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class QuestListenerMineBlock implements Listener {

    @EventHandler
    public void onMineBlock(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (!CoreManager.manager.hasTeam(player)) return;
        QuestListenerTool.findQuest(player, QuestTypeMineBlock.class, (quest, qt) -> {
            if (event.getBlock().getType() != qt.getTableItem().obj()) return false;
            if (QuestListenerTool.checkAutoSubmit(player)) return true;
            QuestListenerTool.updateCombo(qt.combo, qt.lastCombo, player.getName());
            qt.addCount();
            player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 10.0f, 1.0f);
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Message.get(
                    "quest.mineblock.add_count",
                    quest.getName(), quest.getCurrentCount(), qt.combo.get(player.getName()),
                    quest.getTotalCount(), quest.getProgress() * 100, "%")));
            event.setDropItems(false);
            return true;
        });
    }
}
