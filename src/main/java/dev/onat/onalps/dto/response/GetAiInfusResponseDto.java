package dev.onat.onalps.dto.response;

public record GetAiInfusResponseDto(String name, String id, String imageUrl, String prompt, boolean isActive) {
}
