package ir.stts.bajet.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ir.stts.bajet.redis.service.SecureRedisService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.security.InvalidAlgorithmParameterException;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SecureRedisServiceTest {

    private SecureRedisService secureRedisService;

    @InjectMocks
    private ObjectMapper objectMapper;

    @Mock
    private RedisTemplate<String, String> redisTemplate;
    @Mock
    private ValueOperations<String, String> valueOperations;

    private static final String ENC_KEY = "testENCKey";
    private static final String ENC_TEXT = "3a746d226522747d22612273656e7b";
    private static final String ENC_VALUE = "ENC(" + ENC_TEXT + ")";
    private static final TestObject EXPECTED_OBJECT = new TestObject("test");

    @BeforeEach
    void setUp() throws Exception {

        AutoCloseable mocks = MockitoAnnotations.openMocks(this);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(ENC_KEY)).thenReturn(ENC_VALUE);

        secureRedisService = new SecureRedisService(objectMapper, redisTemplate);

        mocks.close();
    }

    @Test
    void testGetWithSecureValue() throws InvalidAlgorithmParameterException, JsonProcessingException {

        TestObject result = secureRedisService.get(ENC_KEY, TestObject.class);

        assertNotNull(result);
        assertEquals(EXPECTED_OBJECT, result);
        verify(redisTemplate.opsForValue()).get(ENC_KEY);
    }

    @Test
    void testSetWithSecureValue() throws JsonProcessingException, InvalidAlgorithmParameterException {

        secureRedisService.set(ENC_KEY, EXPECTED_OBJECT);
        TestObject result = secureRedisService.get(ENC_KEY, TestObject.class);

        assertNotNull(result);
        assertEquals(EXPECTED_OBJECT, result);
        verify(redisTemplate.opsForValue()).set(ENC_KEY, ENC_VALUE);
    }

    @Test
    void testSetWithSecureValueAndTtl() throws JsonProcessingException, InvalidAlgorithmParameterException {

        long ttl = 3600;
        secureRedisService.set(ENC_KEY, EXPECTED_OBJECT, ttl);
        TestObject result = secureRedisService.get(ENC_KEY, TestObject.class);

        assertNotNull(result);
        assertEquals(EXPECTED_OBJECT, result);
        verify(redisTemplate.opsForValue()).set(ENC_KEY, ENC_VALUE, ttl, TimeUnit.SECONDS);
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TestObject {

        private String name;

        @Override
        public boolean equals(Object o) {

            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TestObject that = (TestObject) o;

            return name.equals(that.name);
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }
    }
}