package dev.onat.onalps.dto.response;

public record GetBrandPostsResponseDto(String id, String brandId, String brandName, String aiInfuId, String aiInfuName, String aiInfuImageUrl, String postImageUrl, String productImageUrl, String createdAt) {
}
