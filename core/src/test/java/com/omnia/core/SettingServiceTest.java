package com.omnia.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.omnia.core.resilience.exception.NotFoundException;
import com.omnia.core.setting.dto.SettingResponseDto;
import com.omnia.core.setting.dto.SettingUpdateRequestDto;
import com.omnia.core.setting.entity.Setting;
import com.omnia.core.setting.repository.SettingRepository;
import com.omnia.core.setting.service.SettingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SettingServiceTest {

    @InjectMocks
    private SettingService settingService;

    @Mock
    private SettingRepository settingRepository;

    @BeforeEach
    void setUp() throws Exception {

        AutoCloseable mocks = MockitoAnnotations.openMocks(this);
        mocks.close();
    }

    @Test
    void testFindAll() throws JsonProcessingException {

        Setting setting1 = new Setting();
        setting1.setId(1L);
        setting1.setVersion(1);
        setting1.setKey("key1");
        setting1.setValue(JsonNodeFactory.instance.textNode("value1"));

        Setting setting2 = new Setting();
        setting2.setId(2L);
        setting2.setVersion(2);
        setting2.setKey("key2");
        setting2.setValue(JsonNodeFactory.instance.textNode("value2"));

        when(settingRepository.findAll()).thenReturn(Arrays.asList(setting1, setting2));
        List<SettingResponseDto> result = settingService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("key1", result.get(0).getKey());
        assertEquals(JsonNodeFactory.instance.textNode("value1"), result.get(0).getValue());
        verify(settingRepository, times(1)).findAll();
    }

    @Test
    void testGet_Success() throws JsonProcessingException {

        Setting setting = new Setting();
        setting.setId(1L);
        setting.setVersion(1);
        setting.setKey("key1");
        setting.setValue(JsonNodeFactory.instance.textNode("value1"));

        when(settingRepository.findByKey("key1")).thenReturn(Optional.of(setting));
        SettingResponseDto result = settingService.get("key1");

        assertNotNull(result);
        assertEquals("key1", result.getKey());
        assertEquals(JsonNodeFactory.instance.textNode("value1"), result.getValue());
        verify(settingRepository, times(1)).findByKey("key1");
    }

    @Test
    void testGet_NotFound() {

        when(settingRepository.findByKey("key1")).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> settingService.get("key1"));
        verify(settingRepository, times(1)).findByKey("key1");
    }

    @Test
    void testUpdate_Success() throws JsonProcessingException {

        Setting setting = new Setting();
        setting.setId(1L);
        setting.setVersion(1);
        setting.setKey("key1");
        setting.setValue(JsonNodeFactory.instance.textNode("value1"));

        SettingUpdateRequestDto requestDto = new SettingUpdateRequestDto();
        requestDto.setId(1L);
        requestDto.setVersion(2);
        requestDto.setKey("key1");
        requestDto.setValue(JsonNodeFactory.instance.textNode("updatedValue"));

        when(settingRepository.findByIdAndKey(1L, "key1")).thenReturn(Optional.of(setting));
        when(settingRepository.save(any(Setting.class))).thenAnswer(invocation -> invocation.getArgument(0));
        SettingResponseDto result = settingService.update(requestDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("key1", result.getKey());
        assertEquals(JsonNodeFactory.instance.textNode("updatedValue"), result.getValue());
        verify(settingRepository, times(1)).findByIdAndKey(1L, "key1");
        verify(settingRepository, times(1)).save(any(Setting.class));
    }

    @Test
    void testUpdate_NotFound() {

        SettingUpdateRequestDto requestDto = new SettingUpdateRequestDto();
        requestDto.setId(1L);
        requestDto.setKey("key1");

        when(settingRepository.findByIdAndKey(1L, "key1")).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> settingService.update(requestDto));
        verify(settingRepository, times(1)).findByIdAndKey(1L, "key1");
    }
}