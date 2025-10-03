package dev.onat.onalps.dto.request;

import java.util.List;

public record CreatePostApiRequestDto(String prompt, List<String> image_urls) {
}
