package ir.stts.bajet.core;

import ir.stts.bajet.core.constant.BajetConstants;
import ir.stts.bajet.core.resource.ResourceManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ResourceManagerTest {

    private ResourceManager resourceManager;
    private static final String TEST_TXT = "test.txt";
    private static final String TEST_LOG = "test.log";
    private static final String TEST_TXT_CONTENT = TEST_LOG + " content";

    @BeforeEach
    void setUp() throws IOException {

        String path = Objects
                .requireNonNull(getClass()
                        .getClassLoader()
                        .getResource("")
                )
                .getPath();
        if (System.getProperty("os.name").toLowerCase().contains("win"))
            path = path.replaceAll("^/(.*)$", "$1");

        Path filePath1 = Path.of(path, TEST_TXT);
        Files.writeString(filePath1, TEST_TXT_CONTENT);
        Path filePath2 = Path.of(path, TEST_LOG);
        Files.writeString(filePath2, "Test content 2");

        resourceManager = new ResourceManager();
    }

    @Test
    void testGetResource() {

        String resourcePath = resourceManager.getResource(TEST_TXT);

        assertThat(resourcePath).isNotNull();
        assertThat(new File(resourcePath)).exists();
    }

    @Test
    void testGetResourceNotFound() {

        String resourcePath = resourceManager.getResource("nonexistent.txt");

        assertThat(resourcePath).isNull();
    }

    @Test
    void testGetResourceAsStream() throws IOException {

        InputStream inputStream = resourceManager.getResourceAsStream(TEST_TXT);

        assertThat(inputStream).isNotNull();
        String content = new String(inputStream.readAllBytes());
        assertThat(content).isEqualTo(TEST_TXT_CONTENT);
    }

    @Test
    void testGetResourceFilesByRegex() throws IOException {

        List<String> matchedFiles = resourceManager.getResourceFilesByRegex(".*test-classes.*");

        assertThat(matchedFiles.stream().anyMatch(file -> file.contains(TEST_TXT)))
                .withFailMessage("No file contains substring: " + TEST_TXT)
                .isTrue();

        assertThat(matchedFiles.stream().anyMatch(file -> file.contains(TEST_LOG)))
                .withFailMessage("No file contains substring: " + TEST_LOG)
                .isTrue();
    }

    @Test
    void testGetResourceFilesByPath() throws IOException {

        List<String> files = resourceManager.getResourceFilesByPath("");

        assertThat(files).contains(TEST_TXT);
        assertThat(files).contains(TEST_LOG);
    }

    @Test
    void testGetResourceFilesByPathInvalidPath() {

        assertThatThrownBy(() -> resourceManager.getResourceFilesByPath("nonexistent"))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void testGetResourceFilesByPathNotADirectory() throws IOException {

        assertThatThrownBy(() -> resourceManager.getResourceFilesByPath(TEST_TXT))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(TEST_TXT + " is not a directory");
    }
}