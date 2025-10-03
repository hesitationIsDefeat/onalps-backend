package dev.onat.onalps.utils;

import dev.onat.onalps.entity.AiInfu;
import dev.onat.onalps.entity.Brand;
import dev.onat.onalps.entity.Marketplace;
import org.springframework.format.annotation.DateTimeFormat;

import java.awt.*;
import java.time.LocalDateTime;

public class ImagePathConverter {
    private ImagePathConverter() {}

    public static String getAiInfuImagePath(Marketplace marketplace, Brand brand, AiInfu aiInfu, String imageExt) {
        return String.format("mk_%s/br_%s/ai_%s.%s",
                marketplace.getPublicId(),
                brand.getPublicId(),
                aiInfu.getPublicId(),
                imageExt);
    }

    public static String getPostProductImagePath(Marketplace marketplace, Brand brand, String imageExt) {
        return String.format("mk_%s/br_%s/pr_%s.%s",
                marketplace.getPublicId(),
                brand.getPublicId(),
                LocalDateTime.now(),
                imageExt);
    }

    public static String getPostProductImagePath(String marketplaceId, String brandId, String imageExt) {
        return String.format("mk_%s/br_%s/pr_%s.%s",
                marketplaceId,
                brandId,
                LocalDateTime.now(),
                imageExt);
    }
}
