package me.hifei.questmaster.ui.dynamic;

import me.hifei.questmaster.CoreManager;
import me.hifei.questmaster.api.team.QuestTeam;
import me.hifei.questmaster.running.config.Message;
import me.hifei.questmaster.tools.LocationTool;
import me.hifei.questmaster.ui.UIManager;
import me.hifei.questmaster.ui.DynamicPanel;
import me.rockyhawk.commandpanels.api.PanelCommandEvent;
import me.rockyhawk.commandpanels.openpanelsmanager.PanelPosition;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class DPTeleport extends DynamicPanel {
    public static void onEvent(PanelCommandEvent e) {
        DPTeleport dp = (DPTeleport) e.getPanel();
        Player player = e.getPlayer();
        Location location = dp.select;
        String formatLocation = LocationTool.formatLocation(location);
        QuestTeam team = CoreManager.manager.getTeam(e.getPlayer());
        String message = e.getMessage();
        dp.needConfirm = false;
        assert team != null;

        if (message.startsWith("teleport_player_")) {
            String name = message.substring(16);
            Player p = Bukkit.getPlayerExact(name);
            if (p == null) return;
            player.teleport(p);
            player.sendMessage(Message.get("teleport.player1", p.getDisplayName()));
            p.sendMessage(Message.get("teleport.player2", player.getDisplayName()));
        } else if (message.startsWith("teleport_location_")) {
            String name = message.substring(18);
            Location loc = team.locations().get(Integer.parseInt(name));
            if (loc.equals(location)) {
                if (dp.mode == MenuMode.REMOVE) {
                    team.locations().remove(location);
                    team.teamBroadcast(Message.get("teleport.remove_location", player.getDisplayName(), formatLocation));
                } else if (dp.mode == MenuMode.SELECT) {
                    dp.select = null;
                }
            } else if (dp.mode == MenuMode.TELEPORT) {
                player.teleport(loc);
                player.sendMessage(Message.get("teleport.location", LocationTool.formatLocation(loc)));
            } else dp.select = loc;
        } else {
            switch (message) {
                case "teleport_back" -> DPRoot.openDynamic(player, PanelPosition.Top);
                case "teleport_select_location" -> dp.type = MenuType.LOCATION;
                case "teleport_select_player" -> dp.type = MenuType.PLAYER;
                case "teleport_remove_location" -> dp.needConfirm = true;
                case "teleport_mode_select" -> dp.mode = MenuMode.SELECT;
                case "teleport_mode_teleport" -> dp.mode = MenuMode.TELEPORT;
                case "teleport_mode_remove" -> dp.mode = MenuMode.REMOVE;
                case "teleport_remove_location_confirm" -> {
                    team.locations().remove(location);
                    team.teamBroadcast(Message.get("teleport.remove_location", player.getDisplayName(), formatLocation));
                }
                case "teleport_teleport_location" -> {
                    if (location == null) break;
                    player.teleport(location);
                    player.sendMessage(Message.get("teleport.location", formatLocation));
                }
                case "teleport_add_location" -> {
                    Location loc = player.getLocation();
                    if (team.locations().contains(loc)) break;
                    if (team.locations().size() >= 27) break;
                    team.locations().add(loc);
                    dp.select = loc;
                    team.teamBroadcast(Message.get("teleport.add_location", player.getDisplayName(), LocationTool.formatLocation(loc)));
                }
            }
        }
        settings.put(player, new MenuSetting(dp.type, dp.mode, dp.select));
    }

    static {
        UIManager.ins.registerListener(DPTeleport.class, DPTeleport::onEvent);
    }

    private MenuType type;
    private Location select;
    private MenuMode mode;
    private boolean needConfirm;
    private static final Map<Player, MenuSetting> settings = new HashMap<>();

    public record MenuSetting (MenuType type, MenuMode mode, Location select) {
    }

    public enum MenuType {
        LOCATION,
        PLAYER
    }

    public enum MenuMode {
        SELECT,
        TELEPORT,
        REMOVE
    }

    public DPTeleport(@NotNull Player player) {
        this(player, MenuType.PLAYER);
        MenuSetting setting = settings.getOrDefault(player, null);
        if (setting == null) return;
        type = setting.type;
        mode = setting.mode;
        select = setting.select;
        dynamicModify(player);
    }

    public DPTeleport(@NotNull Player player, MenuType type) {
        super(player, false);
        this.type = type;
        this.mode = MenuMode.SELECT;
        startAutoUpdate(1);
    }

    public static <T extends Player> void openDynamic(T player, PanelPosition panelPosition) {
        DynamicPanel.openDynamic(DPTeleport.class, panelPosition, player);
    }

    public static <T extends Player> void openDynamic(T player, PanelPosition panelPosition, MenuType type) {
        DynamicPanel.openDynamic(DPTeleport.class, panelPosition, player, type);
    }

    @Override
    protected void dynamicModify(Player player) {
        loadTemplate("panels/teleport.yml");

        setItem(48, getDynamic("select_player"));
        QuestTeam team = CoreManager.manager.getTeam(player);
        if (team == CoreManager.red) {
            getItem(48).set("material", "RED_WOOL");
        } else if (team == CoreManager.blue) {
            getItem(48).set("material", "BLUE_WOOL");
        }
        setItem(50, getDynamic("select_location"));

        assert team != null;
        if (select != null && !team.locations().contains(select))
            openDynamic(player, PanelPosition.Top, MenuType.LOCATION);

        int selectLoc = type == MenuType.LOCATION ? 50 : 48;
        List<String> lore = getItem(selectLoc).getStringList("lore");
        lore.set(1, getMessage("menu.current_lore"));
        getItem(selectLoc).set("lore", lore);
        getItem(selectLoc).set("enchanted", true);

        int[] positions = new int[]{
                10, 11, 12, 13, 14, 15, 16,
                19, 20, 21, 22, 23, 24, 25,
                28, 29, 30, 31, 32, 33, 34
        };
        if (type == MenuType.LOCATION) {
            Iterator<Location> iterator = team.locations().iterator();
            int i = 0;
            for (int pos : positions) {
                if (!iterator.hasNext()) break;
                Location loc = iterator.next();
                setItem(pos, getDynamic("location"));
                String world = Objects.requireNonNull(loc.getWorld()).getName();
                switch (world) {
                    case "world" -> getItem(pos).set("material", "GRASS_BLOCK");
                    case "world_nether" -> getItem(pos).set("material", "NETHERRACK");
                    case "world_the_end" -> getItem(pos).set("material", "END_STONE");
                }
                if (mode != MenuMode.TELEPORT && loc.equals(select)) {
                    getItem(pos).set("enchanted", true);
                    lore = getItem(pos).getStringList("lore");
                    lore.set(2, getMessage("location.select"));
                    getItem(pos).set("lore", lore);
                }

                lore = getItem(pos).getStringList("lore");
                if (mode == MenuMode.TELEPORT) {
                    lore.set(2, "&e点击传送!");
                } else if (mode == MenuMode.REMOVE && loc.equals(select)) {
                    lore.set(2, "&e再次点击删除!");
                } else if (loc.equals(select)) {
                    lore.set(2, "&e点击取消选择!");
                } else {
                    lore.set(2, "&e点击选择!");
                }
                getItem(pos).set("lore", lore);

                getItem(pos).set("name", LocationTool.formatLocation(loc));
                getItem(pos).set("commands", List.of("event= teleport_location_" + i));
                i++;
            }
            setItem(45, getDynamic("add_location"));
            if (team.locations().size() >= 21) {
                lore = getItem(45).getStringList("lore");
                lore.set(1, getMessage("location.full"));
                getItem(45).set("lore", lore);
            }

            if (mode == MenuMode.SELECT) setItem(53, getDynamic("mode_select"));
            else if (mode == MenuMode.TELEPORT) setItem(53, getDynamic("mode_teleport"));
            else if (mode == MenuMode.REMOVE) setItem(53, getDynamic("mode_remove"));

            if (select != null && mode == MenuMode.SELECT) {
                setItem(52, getDynamic("teleport_location"));
                if (!needConfirm) setItem(46, getDynamic("remove_location"));
                else setItem(46, getDynamic("remove_location_confirm"));
            }
            if (mode == MenuMode.REMOVE) {
                if (select != null) setItem(46, getDynamic("remove_location_confirm"));
            }
        } else if (type == MenuType.PLAYER) {
            Iterator<Player> iterator = team.members().iterator();
            for (int pos : positions) {
                if (!iterator.hasNext()) break;
                Player p = iterator.next();
                setItem(pos, getDynamic("player"));
                getItem(pos).set("material", "cps= " + p.getName());
                getItem(pos).set("name", p.getDisplayName());

                lore = getItem(pos).getStringList("lore");
                lore.set(1, LocationTool.formatLocation(player.getLocation()));
                if (p == player) lore.set(3, getMessage("player.self"));
                getItem(pos).set("lore", lore);
                if (p != player) getItem(pos).set("commands", List.of("event= teleport_player_" + p.getName(), "cpc"));
            }
        }
    }

    public void close() {
        super.close();
        settings.put(player, new MenuSetting(
                type,
                mode == MenuMode.REMOVE ? MenuMode.SELECT : mode,
                mode == MenuMode.SELECT ? select : null
        ));
    }
}