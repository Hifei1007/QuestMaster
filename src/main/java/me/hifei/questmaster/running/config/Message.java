package me.hifei.questmaster.running.config;

import org.jetbrains.annotations.NotNull;

import java.util.UnknownFormatConversionException;

public class Message {
    private final static Config messagesConfig = new Config("message/messages.yml", false);

    public static @NotNull String get(@NotNull String path) {
        String str = messagesConfig.getConfiguration().getString(path);
        return (str == null ? path : str.replace('&', '§'));
    }

    public static String get(@NotNull String path, Object... format) {
        try {
            return get(path).formatted(format);
        } catch (UnknownFormatConversionException e) {
            return path;
        }
    }
}
