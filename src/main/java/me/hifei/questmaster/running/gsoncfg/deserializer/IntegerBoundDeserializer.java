package me.hifei.questmaster.running.gsoncfg.deserializer;

import com.google.gson.*;
import me.hifei.questmaster.running.gsoncfg.rolling.IntegerBoundConfig;

import java.lang.reflect.Type;

public class IntegerBoundDeserializer implements JsonDeserializer<IntegerBoundConfig> {
    @Override
    public IntegerBoundConfig deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json.isJsonPrimitive()) {
            IntegerBoundConfig cfg = new IntegerBoundConfig();
            cfg.origin = json.getAsInt();
            cfg.bound = -1;
            return cfg;
        }
        JsonObject object = json.getAsJsonObject();
        int origin = object.get("origin").getAsInt();
        int bound = object.get("bound").getAsInt();
        IntegerBoundConfig cfg = new IntegerBoundConfig();
        cfg.origin = origin;
        cfg.bound = bound;
        return cfg;
    }
}
