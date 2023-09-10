package me.hifei.questmaster.api.quest;

import java.util.List;

public record MultiTableItemGroup<T>(List<TableItemGroup<T>> groups) {
}
