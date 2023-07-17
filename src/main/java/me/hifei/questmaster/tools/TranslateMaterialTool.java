package me.hifei.questmaster.tools;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import me.hifei.questmaster.QuestMasterPlugin;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class TranslateMaterialTool {
    public final Map<String, String> translate_file;

    public TranslateMaterialTool(@NotNull String name) {
        Map<String, String> tmp_tf;
        try (InputStream stream = QuestMasterPlugin.class.getResourceAsStream(name)) {
            Gson gson = new Gson();
            Type type = new TypeToken<HashMap<String, String>>(){}.getType();
            assert stream != null;
            tmp_tf = gson.fromJson(new InputStreamReader(stream, StandardCharsets.UTF_8), type);
        } catch (IOException | JsonSyntaxException e) {
            tmp_tf = new HashMap<>();
            Bukkit.getLogger().severe("QuestMaster: SEVERE: Can't read translate file in jar. Can't translate.");
            e.printStackTrace();
        }
        translate_file = tmp_tf;
    }
}
