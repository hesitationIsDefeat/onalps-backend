package dev.onat.onalps.dto.response;

import java.time.LocalDateTime;

public record ErrorResponseDto(int status,
                               LocalDateTime timestamp) {
}
