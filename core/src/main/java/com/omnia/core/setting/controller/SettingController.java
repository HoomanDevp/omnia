package com.omnia.core.setting.controller;

import com.omnia.core.annotation.ApiMetadata;
import com.omnia.core.dto.OmniaResponseDto;
import com.omnia.core.setting.dto.SettingResponseDto;
import com.omnia.core.setting.dto.SettingUpdateRequestDto;
import com.omnia.core.setting.service.SettingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/infra/settings")
@RequiredArgsConstructor
public class SettingController {

    private final SettingService settingService;

    @GetMapping("/all-setting")
    @ApiMetadata(gatewayBypass = true, authenticated = false)
    public ResponseEntity<OmniaResponseDto<List<SettingResponseDto>>> getAllSettings() {
        List<SettingResponseDto> settings = settingService.findAll();
        return ResponseEntity.ok(OmniaResponseDto.success(settings));
    }

    @PutMapping("/update")
    @ApiMetadata(gatewayBypass = true, authenticated = false)
    public ResponseEntity<OmniaResponseDto<SettingResponseDto>> updateSetting(@Valid @RequestBody SettingUpdateRequestDto requestDto) {
        SettingResponseDto updated = settingService.update(requestDto);
        return ResponseEntity.ok(OmniaResponseDto.success(updated));
    }
}