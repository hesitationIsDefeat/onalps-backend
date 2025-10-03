package dev.onat.onalps.repository;

import dev.onat.onalps.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BrandRepository extends JpaRepository<Brand, UUID> {
    Optional<Brand> findByPublicId(String publicId);
    Optional<Brand> findByPublicIdAndMarketplaceId(String publicId, UUID marketplaceId);
    boolean existsByName(String name);
    boolean existsByNameAndMarketplaceId(String name, UUID marketplaceID);
    List<Brand> findAllByMarketplaceId(UUID marketplaceId);
}
