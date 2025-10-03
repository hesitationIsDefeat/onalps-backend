package dev.onat.onalps.dto;

public record MarketplaceAuthDto(
        String marketplacePublicId,
        String marketplaceHashedSecretKey,
        String brandPublicId
) { }
