package dev.onat.onalps.exceptions;

import lombok.Getter;

@Getter
public class EntityAlreadyExistsException extends RuntimeException {
    private final EntityType entityType;

    public EntityAlreadyExistsException(EntityType entityType) {
        super("%s already exists".formatted(entityType.name()));
        this.entityType = entityType;
    }
}
