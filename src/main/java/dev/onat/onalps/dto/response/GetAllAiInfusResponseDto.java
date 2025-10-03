package dev.onat.onalps.dto.response;

public record GetAllAiInfusResponseDto(String name, String id, String imageUrl, String prompt, boolean isActive, int state) {
}
