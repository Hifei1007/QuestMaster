package me.hifei.questmaster.ui.dynamic.shop;

import me.hifei.questmaster.CoreManager;
import me.hifei.questmaster.api.team.QuestTeam;
import me.hifei.questmaster.running.config.Config;
import me.hifei.questmaster.running.config.Message;
import me.hifei.questmaster.tools.LocateTool;
import me.hifei.questmaster.tools.LocationTool;
import me.hifei.questmaster.ui.core.DynamicPanel;
import me.hifei.questmaster.ui.core.UIManager;
import me.hifei.questmaster.ui.dynamic.DPRoot;
import me.rockyhawk.commandpanels.openpanelsmanager.PanelPosition;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DPLocate extends DynamicPanel {
    public enum LocateType {
        STRUCTURE,
        BIOMES
    }

    public record LocateElement(LocateType type, Material icon, String id, String name) {}

    public static List<LocateElement> elements = new ArrayList<>();
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
        new Runnable() {
        @Override
        @SuppressWarnings("unchecked")
        public void run() {
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
    }.run();}

    public DPLocate(Player player) {
        super(player);
        page = 0;
        startAutoUpdate(5);
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
             getItem(pos).set("name", element.name);
             getItem(pos).set("material", element.icon.name().toUpperCase());
             QuestTeam team = CoreManager.manager.getTeam(player);
             assert team != null;
             if (team.point() >= 250) {
                 getItem(pos).set("commands", List.of("event= shop_locate_do_" + i));
                 getItem(pos).set("lore", List.of("", getMessage("object.lore.ok")));
             } else {
                 getItem(pos).set("lore", List.of("", getMessage("object.lore.require")));
             }
        }
    }

    public static <T extends Player> void openDynamic(T player, PanelPosition position) {
        DPLocate.openDynamic(position, new DPLocate(player));
    }
}
