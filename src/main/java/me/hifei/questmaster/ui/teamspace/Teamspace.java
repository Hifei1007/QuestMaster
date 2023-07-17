package me.hifei.questmaster.ui.teamspace;

import me.hifei.questmaster.ui.DynamicPanel;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public abstract class Teamspace extends DynamicPanel {
    protected Teamspace(@NotNull Player player) {
        super(player);
    }

    protected Teamspace(@NotNull Player player, boolean autoModify) {
        super(player, autoModify);
    }
}
