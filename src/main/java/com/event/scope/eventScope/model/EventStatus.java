package com.event.scope.eventScope.model;

public enum EventStatus {
    PLANNED("Запланировано"),
    IN_PROGRESS("Идёт сейчас"),
    FINISHED("Завершено"),
    CANCELED("Отменено");

    private final String displayName;

    EventStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
