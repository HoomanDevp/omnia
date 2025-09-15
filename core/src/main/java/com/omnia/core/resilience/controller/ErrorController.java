package com.omnia.core.resilience.controller;

import com.omnia.core.annotation.ApiMetadata;
import com.omnia.core.dto.OmniaResponseDto;
import com.omnia.core.resilience.dto.ErrorResponseDto;
import com.omnia.core.resilience.dto.ErrorUpdateRequestDto;
import com.omnia.core.resilience.service.ErrorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/infra/errors")
@RequiredArgsConstructor
public class ErrorController {

    private final ErrorService errorService;

    @GetMapping("/all-errors")
    @ApiMetadata(gatewayBypass = true,authenticated = false)
    public ResponseEntity<OmniaResponseDto<List<ErrorResponseDto>>> getAllErrors() {
        List<ErrorResponseDto> errors = errorService.findAll();
        return ResponseEntity.ok(OmniaResponseDto.success(errors));
    }

    @PutMapping("/update")
    @ApiMetadata(gatewayBypass = true,authenticated = false)
    public ResponseEntity<OmniaResponseDto<ErrorResponseDto>> updateError(@Valid @RequestBody ErrorUpdateRequestDto requestDto) {
        ErrorResponseDto updated = errorService.update(requestDto);
        return ResponseEntity.ok(OmniaResponseDto.success(updated));
    }
}
