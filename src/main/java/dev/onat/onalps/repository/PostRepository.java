package dev.onat.onalps.repository;

import dev.onat.onalps.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, UUID> {
    List<Post> findAllByMarketplaceId(UUID marketplaceId);
    List<Post> findAllByMarketplaceIdAndBrandId(UUID marketplaceId, UUID brandId);
}
