package me.hifei.questmaster.manager;

import me.hifei.questmaster.CoreManager;
import me.hifei.questmaster.api.quest.Quest;
import me.hifei.questmaster.api.quest.QuestType;
import me.hifei.questmaster.running.config.Message;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Objects;

public class QuestListenerTool {
    public static void updateCombo(Map<String, Integer> combo, Map<String, Long> lastCombo, String playerName) {
        if (!lastCombo.containsKey(playerName)) {
            lastCombo.put(playerName, System.currentTimeMillis());
            combo.put(playerName, 1);
        } else if (lastCombo.get(playerName) + 5000 >= System.currentTimeMillis()) {
            lastCombo.put(playerName, System.currentTimeMillis());
            combo.put(playerName, combo.get(playerName) + 1);
        } else {
            lastCombo.put(playerName, System.currentTimeMillis());
            combo.put(playerName, 1);
        }
    }

    public static boolean checkAutoSubmit(Player player) {
        boolean autoSubmit = CoreManager.autoSubmitMode.getOrDefault(player.getName(), true);
        if (!autoSubmit) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Message.get("quest.auto_switch.off")));
            return true;
        }
        return false;
    }

    public interface QuestExecutor<T extends QuestType> {
        boolean accept(Quest quest, T questType);
    }

    @SuppressWarnings("unchecked")
    public static <T extends QuestType> void findQuest(Player player, Class<T> targetClazz, QuestExecutor<T> executor) {
        if (!CoreManager.manager.hasTeam(player)) return;
        for (Quest quest : Objects.requireNonNull(CoreManager.manager.getTeam(player)).getQuests()) {
            if (quest.getType().getClass() != targetClazz) continue;
            if (executor.accept(quest, (T) quest.getType())) break;
        }
    }
}
