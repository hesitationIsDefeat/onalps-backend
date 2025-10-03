package dev.onat.onalps.repository;

import dev.onat.onalps.entity.AiInfu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AiInfuRepository extends JpaRepository<AiInfu, UUID> {
    Optional<AiInfu> findByPublicId(String publicId);
    List<AiInfu> findAllByMarketplaceIdAndBrandId(UUID marketplaceId, UUID brandId);
    List<AiInfu> findAllByBrandPublicId(String brandPublicId);
}
