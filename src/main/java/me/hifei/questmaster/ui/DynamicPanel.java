package me.hifei.questmaster.ui;

import me.hifei.questmaster.QuestMasterPlugin;
import me.hifei.questmaster.api.ExceptionLock;
import me.hifei.questmaster.api.quest.Quest;
import me.hifei.questmaster.api.team.QuestTeam;
import me.hifei.questmaster.running.config.Config;
import me.hifei.questmaster.running.config.Message;
import me.rockyhawk.commandpanels.api.Panel;
import me.rockyhawk.commandpanels.openpanelsmanager.PanelPosition;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public abstract class DynamicPanel extends Panel {
    private final static List<DynamicPanel> activePanels = new ArrayList<>();
    private final static Map<Class<? extends DynamicPanel>, Config> configMap = new HashMap<>();
    private BukkitRunnable autoUpdater;
    protected final @NotNull Player player;

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

    protected @NotNull ConfigurationSection getItem(@NotNull String i) {
        ConfigurationSection section = getConfig().getConfigurationSection("item");
        if (section == null) throw new NullPointerException();
        ConfigurationSection s = section.getConfigurationSection(i);
        if (s == null) throw new NullPointerException();
        return s;
    }

    protected void setItem(int i, ConfigurationSection section) {
        setItem(String.valueOf(i), section);
    }

    protected void setItem(@NotNull String i, ConfigurationSection section) {
        Objects.requireNonNull(getConfig().getConfigurationSection("item")).set(i, section);
    }

    private static final List<String> names = new ArrayList<>();

    private static @NotNull String makeName(@NotNull Player player) {
        String name;
        do {
            name = "dynamic_" + player.getName() + "_" + new Random().nextInt(1000000000);
        } while (names.contains(name));
        names.add(name);
        return name;
    }

    protected static <T extends DynamicPanel> void openDynamic(@NotNull Class<T> clazz, PanelPosition panelPosition, Object @NotNull ... args) {
        Panel p = UIManager.API.getOpenPanel((Player) args[0], panelPosition);
        try {
            // 挺麻烦的, 向前兼容
            List<Class<?>> target_interfaces = List.of(Player.class, Quest.class, QuestTeam.class);
            Class<?>[] classes = new Class<?>[args.length];
            for (int i = 0; i < args.length; i++) {
                Class<?> c = args[i].getClass();
                classes[i] = c;
                for (Class<?> cl : c.getInterfaces()) {
                    if (target_interfaces.contains(cl)) {
                        classes[i] = cl;
                        break;
                    }
                }
            }
            Constructor<T> constructor = clazz.getConstructor(classes);
            T panel = constructor.newInstance(args);
            try {
                if (p instanceof DynamicPanel)
                    UIManager.ins.changeClear((DynamicPanel) p, () -> panel.open((Player) args[0], panelPosition));
                else panel.open((Player) args[0], panelPosition);
            } catch (RuntimeException e) {
                    panel.player.sendMessage(Message.get("error.cant_open", panel.getName()));
                }
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public void close() {
        names.remove(this.getName());
        activePanels.remove(this);
        if (autoUpdater != null) autoUpdater.cancel();
        QuestMasterPlugin.logger.info("Closed dynamic panel " + this.getName());
    }

    public DynamicPanel(@NotNull Player player) {
        this(player, true);
    }

    protected DynamicPanel(@NotNull Player player, boolean autoModify) {
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
}
