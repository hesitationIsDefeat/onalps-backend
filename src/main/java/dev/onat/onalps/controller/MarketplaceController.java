package dev.onat.onalps.controller;

import dev.onat.onalps.annotation.MarketplaceAuth;
import dev.onat.onalps.dto.MarketplaceAuthDto;
import dev.onat.onalps.dto.request.*;
import dev.onat.onalps.dto.response.*;
import dev.onat.onalps.service.MarketplaceService;
import dev.onat.onalps.utils.Constants;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping(Constants.MARKETPLACE_CONTROLLER)
public class MarketplaceController {
    private final MarketplaceService service;

    MarketplaceController(MarketplaceService service) {
        this.service = service;
    }

    @GetMapping(Constants.MARKETPLACE_CONTROLLER_GET_MARKETPLACES)
    public ResponseEntity<List<GetMarketplacesResponseDto>> getMarketplaces() {
        return ResponseEntity.ok(this.service.getMarketplaces());
    }

    @GetMapping(Constants.MARKETPLACE_CONTROLLER_GET_BRANDS)
    public ResponseEntity<List<GetBrandsResponseDto>> getBrands(@RequestParam String marketplaceId) {
        GetBrandsRequestDto body = new GetBrandsRequestDto(marketplaceId);
        return ResponseEntity.ok(this.service.getBrands(body));
    }

    @GetMapping(Constants.MARKETPLACE_CONTROLLER_GET_AI_INFUS)
    public ResponseEntity<List<GetAllAiInfusResponseDto>> getAiInfus(@RequestParam String marketplaceId, @RequestParam String brandId) {
        GetAiInfusRequestDto body = new GetAiInfusRequestDto(marketplaceId, brandId);
        return ResponseEntity.ok(this.service.getAiInfus(body));
    }

    // TODO: write a function for the brands to retrieve only the FINISHED ai infus

    @GetMapping(Constants.MARKETPLACE_CONTROLLER_GET_ALL_AI_INFUS)
    public ResponseEntity<List<GetAllAiInfusResponseDto>> getAllAiInfus(@MarketplaceAuth MarketplaceAuthDto marketplaceAuthDto) {
        GetAllAiInfusRequestDto body = new GetAllAiInfusRequestDto(marketplaceAuthDto.marketplacePublicId(), marketplaceAuthDto.brandPublicId());
        return ResponseEntity.ok(this.service.getAllAiInfus(body));
    }

    @GetMapping(Constants.MARKETPLACE_CONTROLLER_GET_AI_INFU_IMAGE_PATH)
    public ResponseEntity<GetAiInfuImagePathResponseDto> getAiInfuImagePath(@RequestParam String marketplaceId, @RequestParam String brandId, @RequestParam String aiInfuId, @RequestParam String imageExt) {
        GetAiInfuImagePathRequestDto body = new GetAiInfuImagePathRequestDto(marketplaceId, brandId, aiInfuId, imageExt);
        return ResponseEntity.ok(this.service.getAiInfuImagePath(body));
    }

    @GetMapping(Constants.MARKETPLACE_CONTROLLER_GET_PRODUCT_IMAGE_PATH_ENDPOINT)
    public ResponseEntity<GetProductImagePathResponseDto> getProductImagePath(@MarketplaceAuth MarketplaceAuthDto marketplaceAuthDto, @RequestParam String imageExt) {
        GetProductImagePathRequestDto body = new GetProductImagePathRequestDto(marketplaceAuthDto.marketplacePublicId(), marketplaceAuthDto.brandPublicId(), imageExt);
        return ResponseEntity.ok(this.service.getProductImagePath(body));
    }

    @PostMapping(Constants.MARKETPLACE_CONTROLLER_INIT_MARKETPLACE)
    public ResponseEntity<InitMarketplaceResponseDto> initMarketplace(@RequestBody InitMarketplaceRequestDto body) {
        return ResponseEntity.ok(this.service.createMarketplace(body));
    }

    @PostMapping(Constants.MARKETPLACE_CONTROLLER_INIT_BRAND)
    public ResponseEntity<InitBrandResponseDto> initBrand(@RequestBody InitBrandRequestDto body) {
        return ResponseEntity.ok(this.service.createBrand(body));
    }

    @PostMapping(Constants.MARKETPLACE_CONTROLLER_PREVIEW_ENDPOINT)
    public CompletableFuture<ResponseEntity<PreviewResponseDto>> preview(@MarketplaceAuth MarketplaceAuthDto marketplaceAuthDto, @RequestBody PreviewRequestDto body) {
        return this.service.generatePreviewImage(body).thenApply(url->ResponseEntity.ok(new PreviewResponseDto(url)));
    }

    @PostMapping(Constants.MARKETPLACE_CONTROLLER_SAVE_AI_INFU_ENDPOINT)
    public ResponseEntity<SaveAiInfuResponseDto> saveInfu(@MarketplaceAuth MarketplaceAuthDto marketplaceAuthDto, @RequestBody SaveAiInfuRequestDto body) {
        return ResponseEntity.ok(this.service.saveInfuImage(marketplaceAuthDto.marketplacePublicId(), marketplaceAuthDto.marketplaceHashedSecretKey(), marketplaceAuthDto.brandPublicId(), body));
    }

    @PostMapping(Constants.MARKETPLACE_CONTROLLER_GENERATE_ENDPOINT)
    public ResponseEntity<GenerateResponseDto> generate(@MarketplaceAuth MarketplaceAuthDto marketplaceAuthDto, @RequestBody GenerateRequestDto body) {
        return ResponseEntity.ok(new GenerateResponseDto(this.service.generateAiInfu(body)));
    }

    @PostMapping(Constants.MARKETPLACE_CONTROLLER_CREATE_POST_IMAGES_ENDPOINT)
    public ResponseEntity<CreatePostImagesResponseDto> createPost(@MarketplaceAuth MarketplaceAuthDto marketplaceAuthDto, @RequestBody CreatePostImagesRequestDto body) {
        return ResponseEntity.ok(this.service.createPostImages(marketplaceAuthDto, body));
    }

    @PostMapping(Constants.MARKETPLACE_CONTROLLER_CREATE_POST)
    public ResponseEntity<CreatePostResponseDto> createPost(@MarketplaceAuth MarketplaceAuthDto marketplaceAuthDto, @RequestBody CreatePostRequestDto body) {
        return ResponseEntity.ok(this.service.createPost(marketplaceAuthDto, body));
    }

    @PostMapping(Constants.MARKETPLACE_CONTROLLER_UPDATE_AI_INFU_IMAGE)
    public ResponseEntity<UpdateAiInfuImageResponseDto> updateAiInfuImage(@RequestBody UpdateAiInfuImageRequestDto body) {
        return ResponseEntity.ok(this.service.updateAiInfuImage(body));
    }

    @PostMapping(Constants.MARKETPLACE_CONTROLLER_UPDATE_AI_INFU_PROMPT)
    public ResponseEntity<UpdateAiInfuPromptResponseDto> updateAiInfuPrompt(@MarketplaceAuth MarketplaceAuthDto marketplaceAuthDto, @RequestBody UpdateAiInfuPromptRequestDto body) {
        return ResponseEntity.ok(this.service.updateAiInfuPrompt(marketplaceAuthDto, body));
    }

    @PostMapping(Constants.MARKETPLACE_CONTROLLER_FINISH_AI_INFU)
    public ResponseEntity<FinishAiInfuResponseDto> finishAiInfu(@MarketplaceAuth MarketplaceAuthDto marketplaceAuthDto, @RequestBody FinishAiInfuRequestDto body) {
        return ResponseEntity.ok(this.service.finishAiInfu(marketplaceAuthDto, body));
    }

    @GetMapping(Constants.MARKETPLACE_CONTROLLER_VALIDATE_BRAND_ENDPOINT)
    public ResponseEntity<ValidateBrandResponseDto> validateBrand(@MarketplaceAuth MarketplaceAuthDto marketplaceAuthDto) {
        return ResponseEntity.ok(new ValidateBrandResponseDto(true));
    }

    @GetMapping(Constants.MARKETPLACE_CONTROLLER_GET_BRAND_AI_INFUS_ENDPOINT)
    public ResponseEntity<List<GetBrandAiInfusResponseDto>> getBrandAiInfus(@MarketplaceAuth MarketplaceAuthDto marketplaceAuthDto) {
        System.out.println(marketplaceAuthDto);
        return ResponseEntity.ok(this.service.getBrandAiInfus(marketplaceAuthDto.brandPublicId()));
    }

    @GetMapping(Constants.MARKETPLACE_CONTROLLER_GET_MARKETPLACE_POSTS_ENDPOINT)
    public ResponseEntity<List<GetMarketplacePostsResponseDto>> getMarketplacePosts(@RequestParam String marketplaceId) {
        return ResponseEntity.ok(this.service.getMarketplacePosts(marketplaceId));
    }

    @GetMapping(Constants.MARKETPLACE_CONTROLLER_GET_BRAND_NAME_ENDPOINT)
    public ResponseEntity<GetBrandNameResponseDto> getBrandName(@RequestParam String marketplaceId, @RequestParam String brandId) {
        GetBrandNameRequestDto request = new GetBrandNameRequestDto(marketplaceId, brandId);
        return ResponseEntity.ok(this.service.getBrandName(request));
    }

    @GetMapping(Constants.MARKETPLACE_CONTROLLER_GET_BRAND_POSTS_ENDPOINT)
    public ResponseEntity<List<GetBrandPostsResponseDto>> getBrandPosts(@RequestParam String marketplaceId, @RequestParam String brandId) {
        GetBrandPostsRequestDto request = new GetBrandPostsRequestDto(marketplaceId, brandId);
        return ResponseEntity.ok(this.service.getBrandPosts(request));
    }
}
