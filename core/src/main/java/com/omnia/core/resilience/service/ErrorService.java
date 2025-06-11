package com.omnia.core.resilience.service;

import com.omnia.core.resilience.dto.ErrorUpdateRequestDto;
import com.omnia.core.resilience.dto.ErrorResponseDto;
import com.omnia.core.resilience.entity.Error;
import com.omnia.core.resilience.exception.NotFoundException;
import com.omnia.core.resilience.repository.ErrorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ErrorService {

    private final ErrorRepository errorRepository;

    public List<ErrorResponseDto> findAll() {

        List<Error> errors = errorRepository.findAll();
        List<ErrorResponseDto> errorResponseDtoList = new ArrayList<>();
        for (Error error : errors) {

            ErrorResponseDto errorResponseDto = new ErrorResponseDto();
            errorResponseDto.setId(error.getId());
            errorResponseDto.setVersion(error.getVersion());
            errorResponseDto.setErrorCode(error.getErrorCode());
            errorResponseDto.setErrorMessage(error.getErrorMessage());
            errorResponseDto.setSeverity(error.getSeverity());
            errorResponseDto.setThreshold(error.getThreshold());
            errorResponseDto.setTimeBoxInMinutes(error.getTimeBoxInMinutes());
            errorResponseDtoList.add(errorResponseDto);
        }
        return errorResponseDtoList;
    }

    public ErrorResponseDto get(String errorCode) {

        Error error = errorRepository
                .findByErrorCode(errorCode)
                .orElseThrow(NotFoundException::new);
        ErrorResponseDto errorResponseDto = new ErrorResponseDto();
        errorResponseDto.setId(error.getId());
        errorResponseDto.setVersion(error.getVersion());
        errorResponseDto.setErrorCode(error.getErrorCode());
        errorResponseDto.setErrorMessage(error.getErrorMessage());
        errorResponseDto.setSeverity(error.getSeverity());
        errorResponseDto.setThreshold(error.getThreshold());
        errorResponseDto.setTimeBoxInMinutes(error.getTimeBoxInMinutes());
        return errorResponseDto;
    }

    public ErrorResponseDto update(ErrorUpdateRequestDto errorUpdateRequestDto) {

        Error error = errorRepository
                .findByIdAndErrorCode(errorUpdateRequestDto.getId(), errorUpdateRequestDto.getErrorCode())
                .orElseThrow(NotFoundException::new);
        error.setVersion(errorUpdateRequestDto.getVersion());
        error.setErrorMessage(errorUpdateRequestDto.getErrorMessage());
        error.setSeverity(errorUpdateRequestDto.getSeverity());
        error.setThreshold(errorUpdateRequestDto.getThreshold());
        error.setTimeBoxInMinutes(errorUpdateRequestDto.getTimeBoxInMinutes());
        return save(error);
    }

    private ErrorResponseDto save(Error error) {

        Error saved = errorRepository.save(error);
        ErrorResponseDto errorResponseDto = new ErrorResponseDto();
        errorResponseDto.setId(saved.getId());
        errorResponseDto.setVersion(saved.getVersion());
        return errorResponseDto
                .setErrorCode(error.getErrorCode())
                .setErrorMessage(error.getErrorMessage())
                .setSeverity(error.getSeverity())
                .setThreshold(error.getThreshold())
                .setTimeBoxInMinutes(error.getTimeBoxInMinutes());
    }
}