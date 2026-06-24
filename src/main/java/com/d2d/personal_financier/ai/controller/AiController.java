package com.d2d.personal_financier.ai.controller;

import com.d2d.personal_financier.ai.dto.AiChatRequest;
import com.d2d.personal_financier.ai.dto.AiChatResponse;
import com.d2d.personal_financier.ai.service.AiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@Tag(name = "AI Assistant", description = "AI-powered financial assistant")
@SecurityRequirement(name = "bearerAuth")
public class AiController {

    private final AiService aiService;

    @PostMapping("/chat")
    @Operation(summary = "Ask AI assistant a financial question")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "AI response generated successfully"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request data"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error"
        )
    })
    public ResponseEntity<AiChatResponse> chat(
        @Valid @RequestBody AiChatRequest request) {

        String answer = aiService.ask(request.question());

        return ResponseEntity.ok(
            new AiChatResponse(answer)
        );
    }
}
