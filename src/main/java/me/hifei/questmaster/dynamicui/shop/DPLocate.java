package me.hifei.questmaster.dynamicui.shop;

import me.hifei.questmaster.api.CoreManager;
import me.hifei.questmaster.api.team.QuestTeam;
import me.hifei.questmaster.running.config.Config;
import me.hifei.questmaster.running.config.Message;
import me.hifei.questmaster.tools.LocateTool;
import me.hifei.questmaster.tools.LocationTool;
import me.hifei.questmaster.api.ui.DynamicPanel;
import me.hifei.questmaster.api.ui.UIManager;
import me.hifei.questmaster.dynamicui.DPRoot;
import me.rockyhawk.commandpanels.openpanelsmanager.PanelPosition;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
public class DPLocate extends DynamicPanel {
    public enum LocateType {
        STRUCTURE,
        BIOMES
    }

    public record LocateElement(LocateType type, Material icon, String id, String name) {}

    public final static List<LocateElement> elements = new ArrayList<>();
    public int page;

    static {
        UIManager.ins.registerEvent("shop_locate_front", (event -> ((DPLocate) event.getPanel()).page--));
        UIManager.ins.registerEvent("shop_locate_next", (event -> ((DPLocate) event.getPanel()).page++));
        UIManager.ins.registerEvent("shop_locate_back", (event -> DPRoot.openDynamic(event.getPlayer(), PanelPosition.Top)));

        UIManager.ins.registerListener(DPLocate.class, (event -> {
            String msg = event.getMessage();
            if (!msg.startsWith("shop_locate_do_")) return;
            int t = Integer.parseInt(msg.substring(15));
            LocateElement element = elements.get(t);
            QuestTeam team = CoreManager.manager.getTeam(event.getPlayer());
            assert team != null;
            if (team.point() < 250) return;
            LocateTool.LocateResult result = element.type == LocateType.STRUCTURE
                    ? LocateTool.locateStructure(element.id, event.getPlayer().getLocation())
                    : LocateTool.locateBiomes(element.id, event.getPlayer().getLocation());
            if (result.isFail()) {
                event.getPlayer().sendMessage(Message.get("locate.failed"));
                return;
            }
            team.teamBroadcast(Message.get("locate.success", event.getPlayer().getDisplayName(),
                    element.name.replace('&', ChatColor.COLOR_CHAR), LocationTool.formatLocation(result.location()), result.away()));
            team.setPoint(team.point() - 250);

        }));
        Config config = new Config("locate.yml", false);
        for (Map<?, ?> m : config.getConfiguration().getMapList("structure")) {
            Map<String, ?> map = (Map<String, ?>) m;
            elements.add(new LocateElement(LocateType.STRUCTURE, Material.valueOf((String) map.get("icon")), (String) map.get("id"), (String) map.get("name")));
        }
        for (Map<?, ?> m : config.getConfiguration().getMapList("biomes")) {
            Map<String, ?> map = (Map<String, ?>) m;
            elements.add(new LocateElement(LocateType.BIOMES, Material.valueOf((String) map.get("icon")), (String) map.get("id"), (String) map.get("name")));
        }
    }

    public DPLocate(Player player) {
        super(player);
        page = 0;
        startAutoUpdate(1);
    }

    @Override
    protected void dynamicModify(Player player) {
        loadTemplate("panels/shop/locate.yml");
        int[] positions = new int[]{
                10, 11, 12, 13, 14, 15, 16,
                19, 20, 21, 22, 23, 24, 25,
                28, 29, 30, 31, 32, 33, 34,
                37, 38, 39, 40, 41, 42, 43
        };
        if (page < 0 || page > (elements.size() - 1) / 28) {
            page = 0;
            return;
        }
        if (page > 0) {
            setItem(46, getDynamic("front"));
        }
        if (page < (elements.size() - 1) / 28) {
            setItem(52, getDynamic("next"));
        }
        int startPos = page * 28;
        for (int i = startPos; i < startPos + 28; i++) {
             if (i == elements.size()) break;
             int pos = positions[i - startPos];
             LocateElement element = elements.get(i);
             setItem(pos, getDynamic("object"));
             modifyItem(pos, element.icon, null, element.name, null);
             QuestTeam team = CoreManager.manager.getTeam(player);
             assert team != null;
             if (team.point() >= 250) {
                 getItem(pos).set("commands", List.of("event= shop_locate_do_" + i));
                 modifyItem(pos, null, List.of("", getMessage("object.lore.ok")), null, null);
             } else {
                 modifyItem(pos, null, List.of("", getMessage("object.lore.require")), null, null);
             }
        }
    }

    public static <T extends Player> void openDynamic(T player, PanelPosition position) {
        DPLocate.openDynamic(position, new DPLocate(player));
    }
}
