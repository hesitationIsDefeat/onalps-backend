package dev.onat.onalps.service;

import dev.onat.onalps.dto.MarketplaceAuthDto;
import dev.onat.onalps.dto.request.*;
import dev.onat.onalps.dto.response.*;
import dev.onat.onalps.entity.Brand;
import dev.onat.onalps.entity.Marketplace;
import dev.onat.onalps.exceptions.EntityNotFoundException;
import dev.onat.onalps.exceptions.EntityType;
import dev.onat.onalps.exceptions.QueryType;
import dev.onat.onalps.repository.MarketplaceRepository;
import dev.onat.onalps.utils.Hasher;
import dev.onat.onalps.utils.ImagePathConverter;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class MarketplaceService {
    private final MarketplaceRepository repository;
    private final AsyncFalService falService;
    private final BrandService brandService;
    private final PostService postService;
    private final AiInfuService aiInfuService;

    MarketplaceService(MarketplaceRepository repository, AsyncFalService falService, BrandService brandService, PostService postService, AiInfuService aiInfuService) {
        this.repository = repository;
        this.falService = falService;
        this.brandService = brandService;
        this.postService = postService;
        this.aiInfuService = aiInfuService;
    }

    public Marketplace findBy(String publicId, String hashedSecretKey) {
        return this.repository.findByPublicIdAndHashedSecretKey(publicId, hashedSecretKey).orElse(null);
    }

    public Brand findBrandBy(Marketplace marketplace, String brandPublicId) {
        return this.brandService.getBy(marketplace.getId(), brandPublicId);
    }

    @Transactional
    public InitMarketplaceResponseDto createMarketplace(InitMarketplaceRequestDto body) {
        String secretKey = UUID.randomUUID().toString();
        Marketplace marketplace = new Marketplace();
        String publicId = UUID.randomUUID().toString();
        marketplace.setPublicId(publicId);
        marketplace.setName(body.name());
        marketplace.setHashedSecretKey(Hasher.hashIdCRC(secretKey));
        this.repository.save(marketplace);
        return new InitMarketplaceResponseDto(publicId, secretKey);
    }

    Marketplace getBy(String marketplacePublicId) {
        return this.repository.findByPublicId(marketplacePublicId)
                .orElseThrow(() -> new EntityNotFoundException(EntityType.MARKETPLACE, QueryType.PUBLIC_ID));
    }

    public InitBrandResponseDto createBrand(InitBrandRequestDto body) {
        Marketplace marketplace = getBy(body.marketplaceId());
        return this.brandService.createBrand(marketplace, body);
    }

    public CompletableFuture<String> generatePreviewImage(PreviewRequestDto body) {
        return this.falService.asyncGeneratePreview(body.prompt());
    }

    Marketplace getBy(String marketplacePublicId, String marketplaceSecretKey) {
        return this.repository.findByPublicIdAndHashedSecretKey(marketplacePublicId, marketplaceSecretKey)
                .orElseThrow(() -> new EntityNotFoundException(EntityType.MARKETPLACE, QueryType.MIXED));
    }

    public SaveAiInfuResponseDto saveInfuImage(String marketplacePublicId, String marketplaceSecretKey, String brandPublicId, SaveAiInfuRequestDto body) {
        Marketplace marketplace = getBy(marketplacePublicId, marketplaceSecretKey);
        return this.brandService.createAiInfu(marketplace, brandPublicId, body);
    }

    public boolean generateAiInfu(GenerateRequestDto body) {
        this.falService.asyncGenerateAiInfu(body.prompt()).thenAccept(urls -> {});
        return true;
    }

    public CreatePostImagesResponseDto createPostImages(MarketplaceAuthDto marketplaceAuthDto, CreatePostImagesRequestDto body) {
        return this.brandService.createPostImages(marketplaceAuthDto, body);
    }

    public CreatePostResponseDto createPost(MarketplaceAuthDto marketplaceAuthDto, CreatePostRequestDto body) {
        return this.brandService.createPost(marketplaceAuthDto, body);
    }

    public List<GetMarketplacesResponseDto> getMarketplaces() {
        return this.repository.findAll().stream().map(marketplace -> new GetMarketplacesResponseDto(marketplace.getName(), marketplace.getPublicId())).toList();
    }

    public List<GetBrandsResponseDto> getBrands(GetBrandsRequestDto body) {
        Marketplace marketplace = getBy(body.marketplaceId());
        return this.brandService.getBrands(marketplace.getId());
    }

    public List<GetAllAiInfusResponseDto> getAiInfus(GetAiInfusRequestDto body) {
        Marketplace marketplace = getBy(body.marketplaceId());
        return this.brandService.getAiInfus(marketplace.getId(), body);
    }

    public List<GetAllAiInfusResponseDto> getAllAiInfus(GetAllAiInfusRequestDto body) {
        Marketplace marketplace = getBy(body.marketplaceId());
        return this.brandService.getAllAiInfus(marketplace.getId(), body);
    }

    public GetAiInfuImagePathResponseDto getAiInfuImagePath(GetAiInfuImagePathRequestDto body) {
        Marketplace marketplace = getBy(body.marketplaceId());
        return this.brandService.getAiInfuImagePath(marketplace, body);
    }

    public GetProductImagePathResponseDto getProductImagePath(GetProductImagePathRequestDto body) {
        return new GetProductImagePathResponseDto(ImagePathConverter.getPostProductImagePath(body.marketplaceId(), body.brandId(), body.imageExt()));
    }

    public UpdateAiInfuImageResponseDto updateAiInfuImage(UpdateAiInfuImageRequestDto body) {
        Marketplace marketplace = getBy(body.marketplaceId());
        return this.brandService.updateAiInfuImage(marketplace, body);
    }

    public UpdateAiInfuPromptResponseDto updateAiInfuPrompt(MarketplaceAuthDto authDto, UpdateAiInfuPromptRequestDto body) {
        Marketplace marketplace = getBy(authDto.marketplacePublicId());
        return this.brandService.updateAiInfuPrompt(marketplace, authDto, body);
    }

    public FinishAiInfuResponseDto finishAiInfu(MarketplaceAuthDto authDto, FinishAiInfuRequestDto body) {
        Marketplace marketplace = getBy(authDto.marketplacePublicId());
        return this.brandService.finishAiInfu(marketplace, authDto, body);
    }

    public List<GetBrandAiInfusResponseDto> getBrandAiInfus(String brandId) {
        return this.brandService.getBrandAiInfus(brandId);
    }

    public List<GetMarketplacePostsResponseDto> getMarketplacePosts(String marketplaceId) {
        Marketplace marketplace = getBy(marketplaceId);
        return this.postService.getPosts(marketplace);
    }

    public GetBrandNameResponseDto getBrandName(GetBrandNameRequestDto request) {
        Marketplace marketplace = getBy(request.marketplaceId());
        return this.brandService.getBrandName(marketplace, request);
    }

    public List<GetBrandPostsResponseDto> getBrandPosts(GetBrandPostsRequestDto request) {
        Marketplace marketplace = getBy(request.marketplaceId());
        return this.brandService.getBrandPosts(marketplace, request);
    }
}
