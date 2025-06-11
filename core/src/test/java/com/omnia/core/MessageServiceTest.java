package com.omnia.core;

import com.omnia.core.message.dto.MessageResponseDto;
import com.omnia.core.message.dto.MessageUpdateRequestDto;
import com.omnia.core.message.entity.Message;
import com.omnia.core.message.repository.MessageRepository;
import com.omnia.core.message.service.MessageService;
import com.omnia.core.resilience.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @InjectMocks
    private MessageService messageService;

    @BeforeEach
    void setUp() throws Exception {

        AutoCloseable mocks = MockitoAnnotations.openMocks(this);
        mocks.close();
    }

    @Test
    void testFindAll() {

        Message message1 = new Message();
        message1.setId(1L);
        message1.setVersion(1);
        message1.setKey("key1");
        message1.setValue("value1");

        Message message2 = new Message();
        message2.setId(2L);
        message2.setVersion(1);
        message2.setKey("key2");
        message2.setValue("value2");

        when(messageRepository.findAll()).thenReturn(List.of(message1, message2));
        List<MessageResponseDto> result = messageService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("key1", result.get(0).getKey());
        assertEquals("value2", result.get(1).getValue());
        verify(messageRepository, times(1)).findAll();
    }

    @Test
    void testGetWhenMessageExists() {

        String key = "key1";
        Message message = new Message();
        message.setId(1L);
        message.setVersion(1);
        message.setKey(key);
        message.setValue("value1");

        when(messageRepository.findByKey(key)).thenReturn(Optional.of(message));
        MessageResponseDto result = messageService.get(key);

        assertNotNull(result);
        assertEquals(key, result.getKey());
        assertEquals("value1", result.getValue());
        verify(messageRepository, times(1)).findByKey(key);
    }

    @Test
    void testGetWhenMessageDoesNotExist() {

        String key = "invalidKey";

        when(messageRepository.findByKey(key)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> messageService.get(key));
        verify(messageRepository, times(1)).findByKey(key);
    }

    @Test
    void testUpdateWhenMessageExists() {

        MessageUpdateRequestDto updateRequest = new MessageUpdateRequestDto();
        updateRequest.setId(1L);
        updateRequest.setKey("key1");
        updateRequest.setVersion(2);
        updateRequest.setValue("updatedValue");

        Message existingMessage = new Message();
        existingMessage.setId(1L);
        existingMessage.setKey("key1");
        existingMessage.setVersion(1);
        existingMessage.setValue("oldValue");

        when(messageRepository.findByIdAndKey(updateRequest.getId(), updateRequest.getKey()))
                .thenReturn(Optional.of(existingMessage));
        when(messageRepository.save(existingMessage)).thenReturn(existingMessage);
        MessageResponseDto result = messageService.update(updateRequest);

        assertNotNull(result);
        assertEquals(updateRequest.getKey(), result.getKey());
        assertEquals("updatedValue", result.getValue());
        verify(messageRepository, times(1))
                .findByIdAndKey(updateRequest.getId(), updateRequest.getKey());
        verify(messageRepository, times(1)).save(existingMessage);
    }

    @Test
    void testUpdateWhenMessageDoesNotExist() {

        MessageUpdateRequestDto updateRequest = new MessageUpdateRequestDto();
        updateRequest.setId(1L);
        updateRequest.setKey("invalidKey");

        when(messageRepository.findByIdAndKey(updateRequest.getId(), updateRequest.getKey()))
                .thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> messageService.update(updateRequest));
        verify(messageRepository, times(1))
                .findByIdAndKey(updateRequest.getId(), updateRequest.getKey());
    }
}