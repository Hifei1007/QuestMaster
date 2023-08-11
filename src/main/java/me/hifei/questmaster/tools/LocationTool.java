package me.hifei.questmaster.tools;

import me.hifei.questmaster.running.config.Message;
import org.bukkit.Location;

import java.util.Objects;

public class LocationTool {
    public static String formatLocation(Location location) {
        if (location == null) return "";
        String name = Objects.requireNonNull(location.getWorld()).getName();
        String world = switch (name) {
            case "world" -> Message.get("location.overworld");
            case "world_nether" -> Message.get("location.nether");
            case "world_the_end" -> Message.get("location.the_end");
            default -> "";
        };
        if (location.getY() <= -255) {
            return Message.get("location.format_no_y", world, location.getX(), location.getZ());
        } else {
            return Message.get("location.format", world, location.getX(), location.getY(), location.getZ());
        }
    }
}
