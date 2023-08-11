package me.hifei.questmaster.tools;

import org.bukkit.Location;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LocateTool {
    public record LocateResult(boolean isFail, int away, Location location) {
    }

    private final static Pattern pattern = Pattern.compile("The nearest #?\\w+:\\w+ (\\(\\w+:\\w+\\) )?is at \\[([-0-9]+), ([~\\-0-9]+), ([-0-9]+)] \\(([0-9]+) blocks away\\)");

    public static LocateResult locateBiomes(String id, Location location) {
        return locate("locate biome " + id, location);
    }

    public static LocateResult locateStructure(String id, Location location) {
        return locate("locate structure " + id, location);
    }

    private static LocateResult locate(String command, Location location) {
        String str = CommandTool.runCommand(command, location);
        Matcher matcher = pattern.matcher(str);
        if (!matcher.find()) return new LocateResult(true, 0, null);
        Location result = new Location(location.getWorld(), Integer.parseInt(matcher.group(2)), matcher.group(3).equals("~") ? -256 : Integer.parseInt(matcher.group(3)), Integer.parseInt(matcher.group(4)));
        return new LocateResult(false, Integer.parseInt(matcher.group(5)), result);
    }
}
