package me.hifei.questmaster.running.gsoncfg.deserializer;

import com.google.gson.*;
import me.hifei.questmaster.running.gsoncfg.rolling.DoubleBoundConfig;

import java.lang.reflect.Type;

public class DoubleBoundDeserializer implements JsonDeserializer<DoubleBoundConfig> {
    @Override
    public DoubleBoundConfig deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json.isJsonPrimitive()) {
            DoubleBoundConfig cfg = new DoubleBoundConfig();
            cfg.origin = json.getAsDouble();
            cfg.bound = -1;
            return cfg;
        }
        JsonObject object = json.getAsJsonObject();
        double origin = object.get("origin").getAsDouble();
        double bound = object.get("bound").getAsDouble();
        DoubleBoundConfig cfg = new DoubleBoundConfig();
        cfg.origin = origin;
        cfg.bound = bound;
        return cfg;
    }
}
