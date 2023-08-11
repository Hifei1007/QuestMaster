package me.hifei.questmaster.tools;

// This class is using NET.MINECRAFT.SERVER

import me.hifei.questmaster.QuestMasterPlugin;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.ICommandListener;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.chat.IChatMutableComponent;
import net.minecraft.network.chat.contents.LiteralContents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.Vec2F;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

public class CommandTool {
    private final static String version = Bukkit.getServer().getClass().getPackage().toString().split("\\.")[3].split(",")[0];

    private final static Class<?> classCraftServer;
    private final static Class<?> classProxiedNativeCommandSender;

    static {
        Class<?> ClassCraftServer1 = null;
        Class<?> ClassProxiedNativeCommandSender1 = null;
        try {
            ClassCraftServer1 = Class.forName("org.bukkit.craftbukkit." + version + ".CraftServer");
            ClassProxiedNativeCommandSender1 = Class.forName("org.bukkit.craftbukkit." + version + ".command.ProxiedNativeCommandSender");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        classCraftServer = ClassCraftServer1;
        classProxiedNativeCommandSender = ClassProxiedNativeCommandSender1;
    }

    public static MinecraftServer getServer () {
        try {
            return (MinecraftServer) classCraftServer.getMethod("getServer").invoke(classCraftServer.cast(Bukkit.getServer()));
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static @NotNull String runCommand (String command, Vec3D where, org.bukkit.World world) {

        CommandSender sender = Bukkit.getConsoleSender();
        StringBuilder builder = new StringBuilder();

        MinecraftServer minecraftServer = null;
        WorldServer worldServer = null;
        Constructor<?> constructor = null;

        try {
            minecraftServer = getServer();
            assert minecraftServer != null;
            ResourceKey<World> w = null;
            switch (world.getName()) {
                case "world" -> w = World.h;
                case "world_nether" -> w = World.i;
                case "world_the_end" -> w = World.j;
            }
            worldServer = minecraftServer.a(w);
            constructor = classProxiedNativeCommandSender.getConstructor(CommandListenerWrapper.class, CommandSender.class, CommandSender.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        CommandListenerWrapper wrapper = new CommandListenerWrapper(new ICommandListener() {
            @Override
            public void a(IChatBaseComponent iChatBaseComponent) {
                builder.append(iChatBaseComponent.getString());
            }

            @Override
            public boolean e_() {
                return true;
            }

            @Override
            public boolean q_() {
                return true;
            }

            @Override
            public boolean N_() {
                return true;
            }

            @Override
            public CommandSender getBukkitSender(CommandListenerWrapper commandListenerWrapper) {
                return sender;
            }
        }, where, new Vec2F(0.0f, 0.0f), worldServer,
                4, "QuestMaster Executor", IChatMutableComponent.a(new LiteralContents("QuestMaster Executor")), minecraftServer,
                null);


        try {
            assert constructor != null;
            Bukkit.dispatchCommand((CommandSender) constructor.newInstance(wrapper, sender, sender), command);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        QuestMasterPlugin.logger.info("Requesting run command " + command + " at position " + where + ", with result " + builder);

        return builder.toString();
    }

    public static @NotNull String runCommand (String command, Location where) {
        Vec3D vec = new Vec3D(where.getX(), where.getY(), where.getZ());
        return runCommand(command, vec, Objects.requireNonNull(where.getWorld()));
    }
}
