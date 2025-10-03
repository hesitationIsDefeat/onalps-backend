//package dev.onat.onalps.listener;
//
//import dev.onat.onalps.entity.Marketplace;
//import jakarta.persistence.PrePersist;
//
//import java.util.UUID;
//
//public class MarketplaceListener {
//    @PrePersist
//    public void marketplacePrePersist(Marketplace marketplace) {
//        if (marketplace.getPublicId() == null || marketplace.getPublicId().isEmpty()) {
//            marketplace.setPublicId(UUID.randomUUID().toString());
//        }
//    }
//}
