package dev.onat.onalps.service;

import dev.onat.onalps.dto.MarketplaceAuthDto;
import dev.onat.onalps.dto.request.*;
import dev.onat.onalps.dto.response.*;
import dev.onat.onalps.entity.AiInfu;
import dev.onat.onalps.entity.Brand;
import dev.onat.onalps.entity.Marketplace;
import dev.onat.onalps.enums.AiInfuState;
import dev.onat.onalps.exceptions.*;
import dev.onat.onalps.repository.AiInfuRepository;
import dev.onat.onalps.utils.ImagePathConverter;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class AiInfuService {
    private final AiInfuRepository repository;
    private final PostService postService;
    private final AsyncFalService falService;

    AiInfuService(AiInfuRepository repository, PostService postService, AsyncFalService falService) {
        this.repository = repository;
        this.postService = postService;
        this.falService = falService;
    }

    @Transactional
    SaveAiInfuResponseDto saveAiInfu(Marketplace marketplace, Brand brand, SaveAiInfuRequestDto body) {
        AiInfu aiInfu = new AiInfu();
        String publicId = UUID.randomUUID().toString();
        aiInfu.setPublicId(publicId);
        aiInfu.setMarketplace(marketplace);
        aiInfu.setBrand(brand);
        aiInfu.setName(body.infuName());
        aiInfu.setPrompt(body.prompt());
        this.repository.save(aiInfu);
        return new SaveAiInfuResponseDto(publicId);
    }

    AiInfu getBy(String publicId) {
        return this.repository.findByPublicId(publicId)
                .orElseThrow(() -> new EntityNotFoundException(EntityType.AI_INFU, QueryType.PUBLIC_ID));
    }

    CreatePostImagesResponseDto createPostImages(MarketplaceAuthDto marketplaceAuthDto, CreatePostImagesRequestDto body) {
        AiInfu aiInfu = getBy(body.aiInfuId());
        if (!Objects.equals(aiInfu.getMarketplace().getPublicId(), marketplaceAuthDto.marketplacePublicId()) || !Objects.equals(aiInfu.getBrand().getPublicId(), marketplaceAuthDto.brandPublicId())) {
            throw new EntityNotFoundException(EntityType.AI_INFU, QueryType.MIXED);
        }
        CreatePostApiResponseDto responseDto = this.falService.generateAiInfuPostApiCall(aiInfu.getImageUrl(), body.productImageUrl(), body.prompt());
        return new CreatePostImagesResponseDto(responseDto.urls());
    }

    @Transactional
    CreatePostResponseDto createPost(MarketplaceAuthDto marketplaceAuthDto, CreatePostRequestDto body) {
        AiInfu aiInfu = getBy(body.aiInfuId());
        if (!Objects.equals(aiInfu.getMarketplace().getPublicId(), marketplaceAuthDto.marketplacePublicId()) || !Objects.equals(aiInfu.getBrand().getPublicId(), marketplaceAuthDto.brandPublicId())) {
            throw new EntityNotFoundException(EntityType.AI_INFU, QueryType.MIXED);
        }
        return this.postService.createPost(aiInfu, body);
    }

    List<GetAllAiInfusResponseDto> getAiInfus(UUID marketplaceId, UUID brandId) {
        return this.repository.findAllByMarketplaceIdAndBrandId(marketplaceId, brandId).stream()
//                .filter(aiInfu -> aiInfu.getState().equals(AiInfuState.FINISHED))
                .map(aiInfu -> new GetAllAiInfusResponseDto(aiInfu.getName(), aiInfu.getPublicId(), aiInfu.getImageUrl(), aiInfu.getPrompt(), aiInfu.isActive(), aiInfu.getState().ordinal())).toList();
    }

    List<GetAllAiInfusResponseDto> getAllAiInfus(UUID marketplaceId, UUID brandId) {
        return this.repository.findAllByMarketplaceIdAndBrandId(marketplaceId, brandId).stream()
                .map(aiInfu -> new GetAllAiInfusResponseDto(aiInfu.getName(), aiInfu.getPublicId(), aiInfu.getImageUrl(), aiInfu.getPrompt(), aiInfu.isActive(), aiInfu.getState().ordinal())).toList();
    }

    public GetAiInfuImagePathResponseDto getAiInfuImagePath(Marketplace marketplace, Brand brand, GetAiInfuImagePathRequestDto body) {
        AiInfu aiInfu = getBy(body.aiInfuId());
        if (aiInfu.getBrand() != brand) throw new EntityNotFoundException(EntityType.AI_INFU, QueryType.PUBLIC_ID);
        return new GetAiInfuImagePathResponseDto(ImagePathConverter.getAiInfuImagePath(marketplace, brand,aiInfu, body.imageExt()));
    }

    @Transactional
    public UpdateAiInfuImageResponseDto updateAiInfuImage(Brand brand, UpdateAiInfuImageRequestDto body) {
        AiInfu aiInfu = getBy(body.aiInfuId());
        if (aiInfu.getBrand() != brand) throw new EntityNotFoundException(EntityType.AI_INFU, QueryType.PUBLIC_ID);
        aiInfu.setImageUrl(body.imageUrl());
        aiInfu.setState(AiInfuState.UPDATED);
        return new UpdateAiInfuImageResponseDto(true);
    }

    @Transactional
    public UpdateAiInfuPromptResponseDto updateAiInfuPrompt(Brand brand, UpdateAiInfuPromptRequestDto body) {
        AiInfu aiInfu = getBy(body.aiInfuId());
        if (aiInfu.getBrand() != brand) throw new EntityNotFoundException(EntityType.AI_INFU, QueryType.PUBLIC_ID);
        if (aiInfu.getState().equals(AiInfuState.FINISHED)) throw new EntityAlreadyExistsException(EntityType.AI_INFU);
        aiInfu.setPrompt(body.prompt());
        aiInfu.setState(AiInfuState.PENDING);
        return new UpdateAiInfuPromptResponseDto(true);
    }

    @Transactional
    public FinishAiInfuResponseDto finishAiInfu(Brand brand, FinishAiInfuRequestDto body) {
        AiInfu aiInfu = getBy(body.aiInfuId());
        if (aiInfu.getBrand() != brand) throw new EntityNotFoundException(EntityType.AI_INFU, QueryType.PUBLIC_ID);
        if (aiInfu.getState().equals(AiInfuState.PENDING)) throw new EntityNotEnabledException(EntityType.AI_INFU);
        if (aiInfu.getState().equals(AiInfuState.FINISHED)) throw new EntityAlreadyExistsException(EntityType.AI_INFU);
        aiInfu.setState(AiInfuState.FINISHED);
        return new FinishAiInfuResponseDto(true);
    }

    List<GetBrandAiInfusResponseDto> getBrandAiInfus(String brandId) {
        System.out.println("ai infu service");
        return this.repository.findAllByBrandPublicId(brandId).stream()
                .filter(aiInfu -> aiInfu.getState().equals(AiInfuState.FINISHED))
                .map(aiInfu -> new GetBrandAiInfusResponseDto(aiInfu.getName(), aiInfu.getPublicId(), aiInfu.getImageUrl(), aiInfu.isActive()))
                .toList();
    }
}
