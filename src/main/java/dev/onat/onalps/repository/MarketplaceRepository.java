package dev.onat.onalps.repository;

import dev.onat.onalps.entity.Marketplace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MarketplaceRepository extends JpaRepository<Marketplace, UUID> {
    Optional<Marketplace> findByPublicIdAndHashedSecretKey(String publicId, String hashedSecretKey);
    Optional<Marketplace> findByPublicId(String publicId);
}
