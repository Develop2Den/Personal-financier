package com.d2d.personal_financier.dto.page_dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Page;

import java.util.List;

@Schema(description = "Stable paginated response")
public record PageResponseDto<T>(

        @Schema(description = "Page content")
        List<T> content,

        @Schema(description = "Zero-based page number", example = "0")
        int page,

        @Schema(description = "Requested page size", example = "20")
        int size,

        @Schema(description = "Total number of elements", example = "125")
        long totalElements,

        @Schema(description = "Total number of pages", example = "7")
        int totalPages,

        @Schema(description = "Whether this is the first page", example = "true")
        boolean first,

        @Schema(description = "Whether this is the last page", example = "false")
        boolean last

) {

    public static <T> PageResponseDto<T> from(Page<T> page) {
        return new PageResponseDto<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );
    }
}
