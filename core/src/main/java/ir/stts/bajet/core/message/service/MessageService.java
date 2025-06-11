package ir.stts.bajet.core.message.service;

import ir.stts.bajet.core.message.dto.MessageUpdateRequestDto;
import ir.stts.bajet.core.message.dto.MessageResponseDto;
import ir.stts.bajet.core.message.entity.Message;
import ir.stts.bajet.core.message.repository.MessageRepository;
import ir.stts.bajet.core.resilience.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;

    public List<MessageResponseDto> findAll() {

        List<Message> messages = messageRepository.findAll();
        List<MessageResponseDto> messageResponseDtoList = new ArrayList<>();
        for (Message message : messages) {

            MessageResponseDto messageResponseDto = new MessageResponseDto();
            messageResponseDto.setId(message.getId());
            messageResponseDto.setVersion(message.getVersion());
            messageResponseDto.setKey(message.getKey());
            messageResponseDto.setValue(message.getValue());
            messageResponseDtoList.add(messageResponseDto);
        }
        return messageResponseDtoList;
    }

    public MessageResponseDto get(String key) {

        Message message = messageRepository
                .findByKey(key)
                .orElseThrow(NotFoundException::new);
        MessageResponseDto messageResponseDto = new MessageResponseDto();
        messageResponseDto.setId(message.getId());
        messageResponseDto.setVersion(message.getVersion());
        messageResponseDto.setKey(message.getKey());
        messageResponseDto.setValue(message.getValue());
        return messageResponseDto;
    }

    public MessageResponseDto update(MessageUpdateRequestDto messageUpdateRequestDto) {

        Message message = messageRepository
                .findByIdAndKey(messageUpdateRequestDto.getId(), messageUpdateRequestDto.getKey())
                .orElseThrow(NotFoundException::new);
        message.setVersion(messageUpdateRequestDto.getVersion());
        message.setValue(messageUpdateRequestDto.getValue());
        return save(message);
    }

    private MessageResponseDto save(Message message) {

        Message saved = messageRepository.save(message);
        MessageResponseDto messageResponseDto = new MessageResponseDto();
        messageResponseDto.setId(saved.getId());
        messageResponseDto.setVersion(saved.getVersion());
        return messageResponseDto
                .setKey(saved.getKey())
                .setValue(saved.getValue());
    }
}