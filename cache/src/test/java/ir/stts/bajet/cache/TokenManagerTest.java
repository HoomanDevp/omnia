package ir.stts.bajet.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ir.stts.bajet.cache.config.InMemoryTokenProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class TokenManagerTest {

    private TokenManager tokenManager;

    @InjectMocks
    private ObjectMapper objectMapper;

    @Mock
    private InMemoryTokenProperties properties;

    @BeforeEach
    void setUp() throws Exception {

        AutoCloseable mocks = MockitoAnnotations.openMocks(this);

        when(properties.getExpirationSeconds()).thenReturn(5L);
        tokenManager = new TokenManager(objectMapper, properties);
        tokenManager.init();

        mocks.close();
    }

    @Test
    void testPutAndGetToken() {

        String key = "1234567890";
        String token = "test-token";

        tokenManager.put(key, token);

        String retrievedToken = tokenManager.get(key);
        assertThat(retrievedToken).isEqualTo(token);
    }

    @Test
    void testGetExpiredToken() throws InterruptedException {

        String key = "1234567890";
        String token = "test-token";

        tokenManager.put(key, token, Duration.ofSeconds(3));

        Thread.sleep(1000);
        String retrievedToken = tokenManager.get(key);
        assertThat(retrievedToken).isEqualTo(token);

        Thread.sleep(3000);
        retrievedToken = tokenManager.get(key);
        assertThat(retrievedToken).isNull();
    }

    @Test
    void testGetWhenCacheHit() throws JsonProcessingException {

        String key = "1234567890";
        List<TestObject> expectedData = List.of(
                new TestObject("value1"),
                new TestObject("value2")
        );
        String token = objectMapper.writeValueAsString(expectedData);

        tokenManager.put(key, token);
        List<TestObject> retrievedToken = tokenManager.get(key, Duration.ofMinutes(1), new TypeReference<>() {
        }, Collections::emptyList);

        assertEquals(expectedData, retrievedToken);
    }

    @Test
    void testGetWhenCacheMiss() throws JsonProcessingException {

        String key = "1234567890";
        List<TestObject> expectedData = List.of(
                new TestObject("value1"),
                new TestObject("value2")
        );

        List<TestObject> retrievedToken = tokenManager.get(key, Duration.ofMinutes(1), new TypeReference<>() {
        }, () -> expectedData);

        assertEquals(expectedData, retrievedToken);
    }

    @Test
    void testGetWhenCacheCorrupted() throws JsonProcessingException {

        String key = "1234567890";
        List<TestObject> expectedData = List.of(
                new TestObject("value1"),
                new TestObject("value2")
        );
        String token = "invalid data";

        tokenManager.put(key, token);
        List<TestObject> retrievedToken = tokenManager.get(key, Duration.ofMinutes(1), new TypeReference<>() {
        }, () -> expectedData);

        assertEquals(expectedData, retrievedToken);
    }

    @Test
    void testRemoveToken() {

        String key = "1234567890";
        String token = "test-token";

        tokenManager.put(key, token);
        tokenManager.remove(key);

        String retrievedToken = tokenManager.get(key);
        assertThat(retrievedToken).isNull();
    }

    @Test
    void testRemoveByValue() {

        String key1 = "1234567890";
        String key2 = "0987654321";
        String token = "shared-token";

        tokenManager.put(key1, token);
        tokenManager.put(key2, token);
        tokenManager.removeByValue(token);

        assertThat(tokenManager.get(key1)).isNull();
        assertThat(tokenManager.get(key2)).isNull();
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