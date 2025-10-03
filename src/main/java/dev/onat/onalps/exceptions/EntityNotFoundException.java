package dev.onat.onalps.exceptions;

import lombok.Getter;

@Getter
public class EntityNotFoundException extends RuntimeException {
    private final EntityType entityType;
    private final QueryType queryType;

    public EntityNotFoundException(EntityType entityType, QueryType queryType) {
        super("%s doesn't exist".formatted(entityType.name()));
        this.entityType = entityType;
        this.queryType = queryType;
    }
}
