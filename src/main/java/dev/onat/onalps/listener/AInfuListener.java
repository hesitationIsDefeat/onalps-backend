//package dev.onat.onalps.listener;
//
//import dev.onat.onalps.entity.AiInfu;
//import jakarta.persistence.PrePersist;
//
//import java.util.UUID;
//
//public class AInfuListener {
//    @PrePersist
//    public void aiInfuPrePersist(AiInfu aiInfu) {
//        if (aiInfu.getPublicId() == null || aiInfu.getPublicId().isEmpty()) {
//            aiInfu.setPublicId(UUID.randomUUID().toString());
//        }
//    }
//}
