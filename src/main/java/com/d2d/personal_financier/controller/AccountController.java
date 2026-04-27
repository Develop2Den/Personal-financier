package com.d2d.personal_financier.controller;

import com.d2d.personal_financier.dto.account_dto.AccountRequestDto;
import com.d2d.personal_financier.dto.account_dto.AccountResponseDto;
import com.d2d.personal_financier.dto.error.ErrorResponse;
import com.d2d.personal_financier.dto.page_dto.PageResponseDto;
import com.d2d.personal_financier.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@Tag(name = "Accounts", description = "Operations with user accounts")
@SecurityRequirement(name = "bearerAuth")
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    @Operation(summary = "Create a new account")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Account created successfully"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<AccountResponseDto> createAccount(
            @Valid @RequestBody AccountRequestDto dto) {

        AccountResponseDto response = accountService.createAccount(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    @Operation(summary = "Get all accounts")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Accounts retrieved successfully"),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<PageResponseDto<AccountResponseDto>> getAllAccounts(
            @ParameterObject
            @Parameter(description = "Pagination and sorting parameters")
            @PageableDefault(size = 20, sort = "id") Pageable pageable) {

        return ResponseEntity.ok(PageResponseDto.from(accountService.getAllAccounts(pageable)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get account by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Account found"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Account not found with id: {id}",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<AccountResponseDto> getAccountById(
            @PathVariable Long id) {

        return ResponseEntity.ok(accountService.getAccountById(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete account")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Account deleted successfully"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Account not found with id: {id}",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<Void> deleteAccount(
            @PathVariable Long id) {

        accountService.deleteAccount(id);

        return ResponseEntity.noContent().build();
    }

}
