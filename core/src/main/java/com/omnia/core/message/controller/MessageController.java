package com.omnia.core.message.controller;

import com.omnia.core.annotation.ApiMetadata;
import com.omnia.core.dto.OmniaResponseDto;
import com.omnia.core.message.dto.MessageResponseDto;
import com.omnia.core.message.dto.MessageUpdateRequestDto;
import com.omnia.core.message.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/infra/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @GetMapping("/all-messages")
    @ApiMetadata(gatewayBypass = true, authenticated = false)
    public ResponseEntity<OmniaResponseDto<List<MessageResponseDto>>> getAllMessages() {
        List<MessageResponseDto> messages = messageService.findAll();
        return ResponseEntity.ok(OmniaResponseDto.success(messages));
    }

    @PutMapping("/update")
    @ApiMetadata(gatewayBypass = true, authenticated = false)
    public ResponseEntity<OmniaResponseDto<MessageResponseDto>> updateMessage(@Valid @RequestBody MessageUpdateRequestDto requestDto) {
        MessageResponseDto updated = messageService.update(requestDto);
        return ResponseEntity.ok(OmniaResponseDto.success(updated));
    }
}
