package ir.stts.bajet.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ir.stts.bajet.redis.service.RedisService;
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

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RedisServiceTest {

    private RedisService redisService;

    @InjectMocks
    private ObjectMapper objectMapper;

    @Mock
    private RedisTemplate<String, String> redisTemplate;
    @Mock
    private ValueOperations<String, String> valueOperations;

    private static final String PLAIN_KEY = "testKey";
    private static final String VALUE = "{\"name\":\"test\"}";
    private static final TestObject EXPECTED_OBJECT = new TestObject("test");

    @BeforeEach
    void setUp() throws Exception {

        AutoCloseable mocks = MockitoAnnotations.openMocks(this);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(PLAIN_KEY)).thenReturn(VALUE);

        redisService = new RedisService(objectMapper, redisTemplate);

        mocks.close();
    }

    @Test
    void testGetWithNonSecureValue() throws JsonProcessingException {

        TestObject result = redisService.get(PLAIN_KEY, TestObject.class);

        assertNotNull(result);
        assertEquals(EXPECTED_OBJECT, result);
        verify(redisTemplate).opsForValue();
        verify(redisTemplate.opsForValue()).get(PLAIN_KEY);
    }

    @Test
    void testSetWithValue() throws JsonProcessingException {

        redisService.set(PLAIN_KEY, EXPECTED_OBJECT);
        TestObject result = redisService.get(PLAIN_KEY, TestObject.class);

        assertNotNull(result);
        assertEquals(EXPECTED_OBJECT, result);
        verify(redisTemplate.opsForValue()).set(PLAIN_KEY, VALUE);
    }

    @Test
    void testSetWithValueAndTtl() throws JsonProcessingException {

        long ttl = 3600;
        redisService.set(PLAIN_KEY, EXPECTED_OBJECT, ttl);
        TestObject result = redisService.get(PLAIN_KEY, TestObject.class);

        assertNotNull(result);
        assertEquals(EXPECTED_OBJECT, result);
        verify(redisTemplate.opsForValue()).set(PLAIN_KEY, VALUE, ttl, TimeUnit.SECONDS);
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