package me.hifei.questmaster.ui.core;

import me.hifei.questmaster.QuestMasterPlugin;
import me.hifei.questmaster.api.ExceptionLock;
import me.hifei.questmaster.running.config.Config;
import me.hifei.questmaster.running.config.Message;
import me.hifei.questmaster.running.gsoncfg.rolling.RollingConfig;
import me.rockyhawk.commandpanels.api.Panel;
import me.rockyhawk.commandpanels.openpanelsmanager.PanelPosition;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public abstract class DynamicPanel extends Panel {
    private final static List<DynamicPanel> activePanels = new ArrayList<>();
    private final static Map<Class<? extends DynamicPanel>, Config> configMap = new HashMap<>();
    private BukkitRunnable autoUpdater;
    protected final Player player;

    protected abstract void dynamicModify(Player player);

    protected void startAutoUpdate(int period) {
        autoUpdater = new BukkitRunnable() {
            final ExceptionLock lock = new ExceptionLock();

            @Override
            public void run() {
                lock.run(() -> dynamicModify(player));
            }
        };
        autoUpdater.runTaskTimer(QuestMasterPlugin.instance, 0, period);
        dynamicModify(player);
    }

    protected @NotNull String getMessage(@NotNull String path) {
        Config config = configMap.get(getClass());
        if (config == null) throw new RuntimeException("Config was not loaded.");
        String message = config.getConfiguration().getString(path);
        if (message == null) throw new NullPointerException("Failed to load message: Find null");
        return message.replace('&', '§');
    }

    protected String getMessage(@NotNull String path, Object... format) {
        return getMessage(path).formatted(format);
    }

    protected void loadTemplate(@NotNull String path) {
        Config config = new Config(path, false);
        setConfig(config.getConfiguration());
        if (!configMap.containsKey(getClass())) {
            try {
                configMap.put(getClass(), new Config("message/" + path, false));
            } catch (RuntimeException ignored) {
            }
        }
    }

    protected @NotNull ConfigurationSection getDynamic(@NotNull String key) {
        ConfigurationSection section = Objects.requireNonNull(getConfig().getConfigurationSection("_dynamic")).getConfigurationSection(key);
        assert section != null;
        return section.createSection("copy_target", section.getValues(false));
    }

    protected @NotNull ConfigurationSection getItem(int i) {
        return getItem(String.valueOf(i));
    }

    private @NotNull ConfigurationSection getItem(@NotNull String i) {
        ConfigurationSection section = getConfig().getConfigurationSection("item");
        if (section == null) throw new NullPointerException();
        ConfigurationSection s = section.getConfigurationSection(i);
        if (s == null) throw new NullPointerException();
        return s;
    }

    protected void setItem(int i, ConfigurationSection section) {
        setItem(String.valueOf(i), section);
    }

    private void setItem(@NotNull String i, ConfigurationSection section) {
        Objects.requireNonNull(getConfig().getConfigurationSection("item")).set(i, section);
    }

    private static final List<String> names = new ArrayList<>();

    private static @NotNull String makeName(Player player) {
        String name, playerName;
        if (player == null) playerName = "sync";
        else playerName = player.getName();
        do {
            name = "dynamic_" + playerName + "_" + RollingConfig.cfg.dynamicPanelRange.next();
        } while (names.contains(name));
        names.add(name);
        return name;
    }

    protected static void openDynamic(PanelPosition panelPosition, DynamicPanel panel) {
        Panel p = UIManager.API.getOpenPanel(panel.player, panelPosition);
        try {
            if (p instanceof DynamicPanel)
                UIManager.ins.changeClear((DynamicPanel) p, () -> panel.open(panel.player, panelPosition));
            else panel.open(panel.player, panelPosition);
        } catch (RuntimeException e) {
            panel.player.sendMessage(Message.get("error.cant_open", panel.getName()));
        }
    }

    public void close() {
        names.remove(this.getName());
        activePanels.remove(this);
        if (autoUpdater != null) autoUpdater.cancel();
        QuestMasterPlugin.logger.info("Closed dynamic panel " + this.getName());
    }

    public DynamicPanel(Player player) {
        this(player, true);
    }

    protected DynamicPanel(Player player, boolean autoModify) {
        super(makeName(player));
        this.player = player;
        if (autoModify) dynamicModify(player);
        activePanels.add(this);
        QuestMasterPlugin.logger.info("Created dynamic panel " + this.getName());
    }

    public static void clear() {
        List<DynamicPanel> panelList = List.of(activePanels.toArray(new DynamicPanel[0]));
        for (DynamicPanel panel : panelList) {
            panel.close();
        }
    }

    public void modifyItem(int i, Material material, List<String> lore, String name, Integer stack) {
        if (material != null) getItem(i).set("material", material);
        if (lore != null) getItem(i).set("lore", lore);
        if (name != null) getItem(i).set("name", name);
        if (stack != null) getItem(i).set("stack", stack);
    }
}
