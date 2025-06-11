package ir.stts.bajet.core.resource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@Component
@Slf4j
public class ResourceManager {

    public String getResource(String filename) {

        URL resource = getContextClassLoader().getResource(filename);
        if (resource == null)
            resource = getClass().getResource(filename);

        if (resource == null)
            return null;

        String path = resource.getPath();
        if (System.getProperty("os.name").toLowerCase().contains("win"))
            path = path.replaceAll("^/(.*)$", "$1");

        return path;
    }

    public InputStream getResourceAsStream(String filename) {

        InputStream in = getContextClassLoader().getResourceAsStream(filename);
        if (in == null)
            in = getClass().getResourceAsStream(filename);

        return in;
    }

    public List<String> getResourceFilesByRegex(String jarFileOrDirRegex) throws IOException {

        final List<String> filenames = new ArrayList<>();
        final Pattern pattern = Pattern.compile(jarFileOrDirRegex);
        final String classPath = System.getProperty("java.class.path", ".");
        final String[] classPathElements = classPath.split(File.pathSeparator);
        for (final String element : classPathElements)
            filenames.addAll(this.getResources(element, pattern));

        return filenames;
    }

    public List<String> getResourceFilesByPath(String path) throws IOException {

        File file = new File(this.getResource(path));
        if (!file.exists())
            throw new IllegalArgumentException(path + " is not exists");

        if (!file.isDirectory())
            throw new IllegalArgumentException(path + " is not a directory");

        final List<String> filenames = new ArrayList<>();
        try (InputStream in = this.getResourceAsStream(path);
             BufferedReader br = new BufferedReader(new InputStreamReader(in))) {

            String resource;
            while ((resource = br.readLine()) != null)
                filenames.add(resource);
        }

        return filenames;
    }

    private ClassLoader getContextClassLoader() {
        return ClassLoader.getSystemClassLoader();
    }

    private List<String> getResources(final String element, final Pattern pattern) throws IOException {

        final File file = new File(element);
        return file.isDirectory() ? this.getResourcesFromDirectory(file, pattern) : this.getResourcesFromJarFile(file, pattern);
    }

    private List<String> getResourcesFromJarFile(final File file, final Pattern pattern) throws IOException {

        final List<String> result = new ArrayList<>();
        try (ZipFile zf = new ZipFile(file)) {

            final Enumeration<? extends ZipEntry> e = zf.entries();
            while (e.hasMoreElements()) {

                final ZipEntry ze = e.nextElement();
                final String fileName = ze.getName();
                if (pattern.matcher(fileName).matches())
                    result.add(fileName);
            }
        }

        return result;
    }

    private List<String> getResourcesFromDirectory(final File directory, final Pattern pattern) {

        final List<String> result = new ArrayList<>();
        final File[] fileList = directory.listFiles();
        if (fileList != null)
            result.addAll(
                    Arrays.stream(fileList)
                            .parallel()
                            .flatMap(file -> {

                                try {
                                    if (file.isDirectory())
                                        return this.getResourcesFromDirectory(file, pattern).stream();
                                    else if (pattern.matcher(file.getCanonicalPath()).matches())
                                        return Stream.of(file.getCanonicalPath());
                                } catch (IOException e) {
                                    log.error("Error accessing file: {}", file, e);
                                }

                                return Stream.empty();
                            })
                            .toList()
            );

        return result;
    }
}