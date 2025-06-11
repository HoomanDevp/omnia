package ir.stts.bajet.core;

import ir.stts.bajet.core.resilience.constant.ErrorSeverity;
import ir.stts.bajet.core.resilience.dto.ErrorResponseDto;
import ir.stts.bajet.core.resilience.dto.ErrorUpdateRequestDto;
import ir.stts.bajet.core.resilience.entity.Error;
import ir.stts.bajet.core.resilience.exception.NotFoundException;
import ir.stts.bajet.core.resilience.repository.ErrorRepository;
import ir.stts.bajet.core.resilience.service.ErrorService;
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

class ErrorServiceTest {

    @InjectMocks
    private ErrorService errorService;

    @Mock
    private ErrorRepository errorRepository;

    @BeforeEach
    void setUp() throws Exception {

        AutoCloseable mocks = MockitoAnnotations.openMocks(this);
        mocks.close();
    }

    @Test
    void testFindAll() {

        Error error1 = new Error();
        error1.setId(1L);
        error1.setVersion(1);
        error1.setErrorCode("E001");
        error1.setErrorMessage("Error 1");
        error1.setSeverity(ErrorSeverity.HIGH);
        error1.setThreshold(5);
        error1.setTimeBoxInMinutes(10);

        Error error2 = new Error();
        error2.setId(2L);
        error2.setVersion(2);
        error2.setErrorCode("E002");
        error2.setErrorMessage("Error 2");
        error2.setSeverity(ErrorSeverity.MEDIUM);
        error2.setThreshold(3);
        error2.setTimeBoxInMinutes(15);

        when(errorRepository.findAll()).thenReturn(Arrays.asList(error1, error2));
        List<ErrorResponseDto> result = errorService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("E001", result.get(0).getErrorCode());
        assertEquals("Error 1", result.get(0).getErrorMessage());
        verify(errorRepository, times(1)).findAll();
    }

    @Test
    void testGetSuccess() {

        Error error = new Error();
        error.setId(1L);
        error.setVersion(1);
        error.setErrorCode("E001");
        error.setErrorMessage("Error 1");
        error.setSeverity(ErrorSeverity.HIGH);
        error.setThreshold(5);
        error.setTimeBoxInMinutes(10);

        when(errorRepository.findByErrorCode("E001")).thenReturn(Optional.of(error));
        ErrorResponseDto result = errorService.get("E001");

        assertNotNull(result);
        assertEquals("E001", result.getErrorCode());
        assertEquals("Error 1", result.getErrorMessage());
        verify(errorRepository, times(1)).findByErrorCode("E001");
    }

    @Test
    void testGetNotFound() {

        when(errorRepository.findByErrorCode("E001")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> errorService.get("E001"));
        verify(errorRepository, times(1)).findByErrorCode("E001");
    }

    @Test
    void testUpdateSuccess() {

        Error error = new Error();
        error.setId(1L);
        error.setVersion(1);
        error.setErrorCode("E001");
        error.setErrorMessage("Error 1");
        error.setSeverity(ErrorSeverity.HIGH);
        error.setThreshold(5);
        error.setTimeBoxInMinutes(10);

        ErrorUpdateRequestDto requestDto = new ErrorUpdateRequestDto();
        requestDto.setId(1L);
        requestDto.setVersion(2);
        requestDto.setErrorCode("E001");
        requestDto.setErrorMessage("Updated Error 1");
        requestDto.setSeverity(ErrorSeverity.HIGHEST);
        requestDto.setThreshold(7);
        requestDto.setTimeBoxInMinutes(20);

        when(errorRepository.findByIdAndErrorCode(1L, "E001")).thenReturn(Optional.of(error));
        when(errorRepository.save(any(Error.class))).thenAnswer(invocation -> invocation.getArgument(0));
        ErrorResponseDto result = errorService.update(requestDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("E001", result.getErrorCode());
        assertEquals("Updated Error 1", result.getErrorMessage());
        assertEquals(ErrorSeverity.HIGHEST, result.getSeverity());
        verify(errorRepository, times(1)).findByIdAndErrorCode(1L, "E001");
        verify(errorRepository, times(1)).save(any(Error.class));
    }

    @Test
    void testUpdateNotFound() {

        ErrorUpdateRequestDto requestDto = new ErrorUpdateRequestDto();
        requestDto.setId(1L);
        requestDto.setErrorCode("E001");

        when(errorRepository.findByIdAndErrorCode(1L, "E001")).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> errorService.update(requestDto));
        verify(errorRepository, times(1)).findByIdAndErrorCode(1L, "E001");
    }
}