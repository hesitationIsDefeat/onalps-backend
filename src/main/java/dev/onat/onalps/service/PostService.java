package dev.onat.onalps.service;

import dev.onat.onalps.dto.request.*;
import dev.onat.onalps.dto.response.*;
import dev.onat.onalps.entity.AiInfu;
import dev.onat.onalps.entity.Brand;
import dev.onat.onalps.entity.Marketplace;
import dev.onat.onalps.entity.Post;
import dev.onat.onalps.repository.PostRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class PostService {
    private final PostRepository repository;

    PostService(PostRepository repository) {
        this.repository = repository;
    }

    @Transactional
    CreatePostResponseDto createPost(AiInfu aiInfu, CreatePostRequestDto body) {
        String publicId = UUID.randomUUID().toString();
        Post post = new Post();
        post.setPublicId(publicId);
        post.setMarketplace(aiInfu.getMarketplace());
        post.setBrand(aiInfu.getBrand());
        post.setAiInfu(aiInfu);
        post.setImageUrl(body.postImageUrl());
        post.setProductUrl(body.productImageUrl());
        post.setPrompt(body.prompt());
        this.repository.save(post);
        return new CreatePostResponseDto(true);
    }

    public List<GetMarketplacePostsResponseDto> getPosts(Marketplace marketplace) {
        List<Post> posts = this.repository.findAllByMarketplaceId(marketplace.getId());
        return posts.stream()
                .filter(Post::isActive)
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .map(post -> {
                    Brand brand = post.getBrand();
                    AiInfu aiInfu = post.getAiInfu();
                    return new GetMarketplacePostsResponseDto(
                            post.getPublicId(),
                            brand.getPublicId(),
                            brand.getName(),
                            aiInfu.getPublicId(),
                            aiInfu.getName(),
                            aiInfu.getImageUrl(),
                            post.getImageUrl(),
                            post.getProductUrl(),
                            post.getCreatedAt().toString());
                }).toList();
    }

    public List<GetBrandPostsResponseDto> getBrandPosts(Marketplace marketplace, Brand brand) {
        return this.repository.findAllByMarketplaceIdAndBrandId(marketplace.getId(), brand.getId())
                .stream()
                .filter(Post::isActive)
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .map(post -> {
                    AiInfu aiInfu = post.getAiInfu();
                    return new GetBrandPostsResponseDto(
                            post.getPublicId(),
                            brand.getPublicId(),
                            brand.getName(),
                            aiInfu.getPublicId(),
                            aiInfu.getName(),
                            aiInfu.getImageUrl(),
                            post.getImageUrl(),
                            post.getProductUrl(),
                            post.getCreatedAt().toString());
                }).toList();
    }
}
