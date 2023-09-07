package me.hifei.questmaster.event.normal;

import me.hifei.questmaster.api.bukkitevent.ScheduleEvent;
import me.hifei.questmaster.api.event.NormalQuestEvent;
import me.hifei.questmaster.running.gsoncfg.event.SingleEventConfig;
import me.hifei.questmaster.running.gsoncfg.rolling.DoubleBoundConfig;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

@SuppressWarnings("unused")
public class ScheduleDelayChangeQuestEvent extends NormalQuestEvent {
    public double mul;

    public ScheduleDelayChangeQuestEvent(SingleEventConfig config) {
        super(config);
    }

    @Override
    protected void preprocess() {
        mul = loadSettings(DoubleBoundConfig.class).next();
    }

    public String getName() {
        return super.getName().formatted(mul * 100, "%");
    }

    protected Listener getListener() {
        return new Listener() {
            @EventHandler
            public void onSchedule(ScheduleEvent event) {
                event.setScheduleTime((int) (event.getScheduleTime() * mul));
            }
        };
    }
}
