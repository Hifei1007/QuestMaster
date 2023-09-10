package me.hifei.questmaster.api.quest;

import java.util.List;

public record TableItemGroup<T> (List<TableItem<T>> items, double diff, double basediff) {
}
