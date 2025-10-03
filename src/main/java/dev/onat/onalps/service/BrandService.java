package dev.onat.onalps.service;

import dev.onat.onalps.dto.MarketplaceAuthDto;
import dev.onat.onalps.dto.request.*;
import dev.onat.onalps.dto.response.*;
import dev.onat.onalps.entity.Brand;
import dev.onat.onalps.entity.Marketplace;
import dev.onat.onalps.exceptions.EntityAlreadyExistsException;
import dev.onat.onalps.exceptions.EntityNotFoundException;
import dev.onat.onalps.exceptions.EntityType;
import dev.onat.onalps.exceptions.QueryType;
import dev.onat.onalps.repository.BrandRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class BrandService {
    private final BrandRepository repository;
    private final AiInfuService aiInfuService;
    private final PostService postService;

    BrandService(BrandRepository repository, AiInfuService aiInfuService, PostService postService) {
        this.repository = repository;
        this.aiInfuService = aiInfuService;
        this.postService = postService;
    }

    public InitBrandResponseDto createBrand(Marketplace marketplace, InitBrandRequestDto body) {
        if (this.repository.existsByNameAndMarketplaceId(body.brandName(), marketplace.getId())) throw new EntityAlreadyExistsException(EntityType.BRAND);
        Brand brand = new Brand();
        String publicId = UUID.randomUUID().toString();
        brand.setPublicId(publicId);
        brand.setMarketplace(marketplace);
        brand.setName(body.brandName());
        this.repository.save(brand);
        return new InitBrandResponseDto(publicId);
    }

    Brand getBy(String publicId) {
        return this.repository.findByPublicId(publicId)
                .orElseThrow(() -> new EntityNotFoundException(EntityType.BRAND, QueryType.PUBLIC_ID));
    }

    Brand getBy(UUID marketplaceId, String brandPublicId) {
        return this.repository.findByPublicIdAndMarketplaceId(brandPublicId, marketplaceId)
                .orElseThrow(() -> new EntityNotFoundException(EntityType.BRAND, QueryType.MIXED));
    }

    SaveAiInfuResponseDto createAiInfu(Marketplace marketplace, String brandPublicId, SaveAiInfuRequestDto body) {
        Brand brand = getBy(marketplace.getId(), brandPublicId);
        return this.aiInfuService.saveAiInfu(marketplace, brand, body);
    }

    public CreatePostImagesResponseDto createPostImages(MarketplaceAuthDto marketplaceAuthDto, CreatePostImagesRequestDto body) {
        return this.aiInfuService.createPostImages(marketplaceAuthDto, body);
    }

    CreatePostResponseDto createPost(MarketplaceAuthDto marketplaceAuthDto, CreatePostRequestDto body) {
        return this.aiInfuService.createPost(marketplaceAuthDto, body);
    }

    List<GetBrandsResponseDto> getBrands(UUID marketplaceId) {
        return this.repository.findAllByMarketplaceId(marketplaceId).stream()
                .map(brand -> new GetBrandsResponseDto(brand.getName(), brand.getPublicId())).toList();
    }

    List<GetAllAiInfusResponseDto> getAiInfus(UUID marketplaceId, GetAiInfusRequestDto body) {
        Brand brand = getBy(body.brandId());
        if (brand.getMarketplace().getId() != marketplaceId) throw new EntityNotFoundException(EntityType.BRAND, QueryType.PUBLIC_ID);
        return this.aiInfuService.getAiInfus(marketplaceId, brand.getId());
    }

    List<GetAllAiInfusResponseDto> getAllAiInfus(UUID marketplaceId, GetAllAiInfusRequestDto body) {
        Brand brand = getBy(body.brandId());
        if (brand.getMarketplace().getId() != marketplaceId) throw new EntityNotFoundException(EntityType.BRAND, QueryType.PUBLIC_ID);
        return this.aiInfuService.getAllAiInfus(marketplaceId, brand.getId());
    }

    public GetAiInfuImagePathResponseDto getAiInfuImagePath(Marketplace marketplace, GetAiInfuImagePathRequestDto body) {
        Brand brand = getBy(body.brandId());
        if (brand.getMarketplace() != marketplace) throw new EntityNotFoundException(EntityType.BRAND, QueryType.PUBLIC_ID);
        return this.aiInfuService.getAiInfuImagePath(marketplace, brand, body);
    }

    public UpdateAiInfuImageResponseDto updateAiInfuImage(Marketplace marketplace, UpdateAiInfuImageRequestDto body) {
        Brand brand = getBy(body.brandId());
        if (brand.getMarketplace() != marketplace) throw new EntityNotFoundException(EntityType.BRAND, QueryType.PUBLIC_ID);
        return this.aiInfuService.updateAiInfuImage(brand, body);
    }

    public UpdateAiInfuPromptResponseDto updateAiInfuPrompt(Marketplace marketplace, MarketplaceAuthDto authDto, UpdateAiInfuPromptRequestDto body) {
        Brand brand = getBy(authDto.brandPublicId());
        if (brand.getMarketplace() != marketplace) throw new EntityNotFoundException(EntityType.BRAND, QueryType.PUBLIC_ID);
        return this.aiInfuService.updateAiInfuPrompt(brand, body);
    }

    public FinishAiInfuResponseDto finishAiInfu(Marketplace marketplace, MarketplaceAuthDto authDto, FinishAiInfuRequestDto body) {
        Brand brand = getBy(authDto.brandPublicId());
        if (brand.getMarketplace() != marketplace) throw new EntityNotFoundException(EntityType.BRAND, QueryType.PUBLIC_ID);
        return this.aiInfuService.finishAiInfu(brand, body);
    }

    List<GetBrandAiInfusResponseDto> getBrandAiInfus(String brandId) {
        return this.aiInfuService.getBrandAiInfus(brandId);
    }

    public GetBrandNameResponseDto getBrandName(Marketplace marketplace, GetBrandNameRequestDto request) {
        return new GetBrandNameResponseDto(getBy(marketplace.getId(), request.brandId()).getName());
    }

    public List<GetBrandPostsResponseDto> getBrandPosts(Marketplace marketplace, GetBrandPostsRequestDto request) {
        Brand brand = getBy(marketplace.getId(), request.brandId());
        return this.postService.getBrandPosts(marketplace, brand);
    }
}
