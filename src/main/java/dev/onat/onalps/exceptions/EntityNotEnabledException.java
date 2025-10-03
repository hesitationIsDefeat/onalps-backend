package dev.onat.onalps.exceptions;

import lombok.Getter;

@Getter
public class EntityNotEnabledException extends RuntimeException {
    private final EntityType entityType;

    public EntityNotEnabledException(EntityType entityType) {
        super("%s not enabled".formatted(entityType.name()));
        this.entityType = entityType;
    }
}
