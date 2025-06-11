package ir.stts.bajet.core.setting.service;

import ir.stts.bajet.core.resilience.exception.NotFoundException;
import ir.stts.bajet.core.setting.dto.SettingUpdateRequestDto;
import ir.stts.bajet.core.setting.dto.SettingResponseDto;
import ir.stts.bajet.core.setting.entity.Setting;
import ir.stts.bajet.core.setting.repository.SettingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SettingService {

    private final SettingRepository settingRepository;

    public List<SettingResponseDto> findAll() {

        List<Setting> settings = settingRepository.findAll();
        List<SettingResponseDto> settingResponseDtoList = new ArrayList<>();
        for (Setting setting : settings) {

            SettingResponseDto settingResponseDto = new SettingResponseDto();
            settingResponseDto.setId(setting.getId());
            settingResponseDto.setVersion(setting.getVersion());
            settingResponseDto.setKey(setting.getKey());
            settingResponseDto.setValue(setting.getValue());
            settingResponseDtoList.add(settingResponseDto);
        }
        return settingResponseDtoList;
    }

    public SettingResponseDto get(String key) {

        Setting setting = settingRepository
                .findByKey(key)
                .orElseThrow(NotFoundException::new);
        SettingResponseDto settingResponseDto = new SettingResponseDto();
        settingResponseDto.setId(setting.getId());
        settingResponseDto.setVersion(setting.getVersion());
        settingResponseDto.setKey(setting.getKey());
        settingResponseDto.setValue(setting.getValue());
        return settingResponseDto;
    }

    public SettingResponseDto update(SettingUpdateRequestDto settingUpdateRequestDto) {

        Setting setting = settingRepository
                .findByIdAndKey(settingUpdateRequestDto.getId(), settingUpdateRequestDto.getKey())
                .orElseThrow(NotFoundException::new);
        setting.setVersion(settingUpdateRequestDto.getVersion());
        setting.setValue(settingUpdateRequestDto.getValue());
        return save(setting);
    }

    private SettingResponseDto save(Setting setting) {

        Setting saved = settingRepository.save(setting);
        SettingResponseDto settingResponseDto = new SettingResponseDto();
        settingResponseDto.setId(saved.getId());
        settingResponseDto.setVersion(saved.getVersion());
        return settingResponseDto
                .setKey(saved.getKey())
                .setValue(saved.getValue());
    }
}