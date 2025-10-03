//package dev.onat.onalps.listener;
//
//import dev.onat.onalps.entity.Brand;
//import jakarta.persistence.PrePersist;
//
//import java.util.UUID;
//
//public class BrandListener {
//    @PrePersist
//    public void brandPrePersist(Brand brand) {
//        if (brand.getPublicId() == null || brand.getPublicId().isEmpty()) {
//            brand.setPublicId(UUID.randomUUID().toString());
//        }
//    }
//}
