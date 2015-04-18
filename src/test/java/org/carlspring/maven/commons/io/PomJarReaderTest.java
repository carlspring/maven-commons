package org.carlspring.maven.commons.io;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertNotNull;

/**
 * @author mtodorov
 */
public class PomJarReaderTest
{

    public static final Path BASEDIR = Paths.get("target/test-resources/jars");

    private static final Path JAR = Paths.get(BASEDIR.toAbsolutePath().toString(), "foo-maven-plugin.jar");

    private static final String PLUGIN_XML = "META-INF/maven/plugin.xml";

    private PomJarReader pomJarReader = new PomJarReader(JAR);


    @Before
    public void setUp()
            throws Throwable
    {
        File basedir = BASEDIR.toAbsolutePath().toFile();
        if (!basedir.exists())
        {
            //noinspection ResultOfMethodCallIgnored
            basedir.mkdirs();

            Path jarLocation = FileSystems.getDefault().getPath(JAR.toString()).toAbsolutePath();

            createTestJar(jarLocation, PLUGIN_XML);
        }
    }

    @Test
    public void testGetInputStream()
            throws IOException, URISyntaxException
    {
        InputStream is = pomJarReader.getInputStream();

        assertNotNull(is);
    }

    private void createTestJar(Path jarPath, String internalPath)
            throws Throwable
    {
        Map<String, String> env = new HashMap<>();
        env.put("create", String.valueOf(!jarPath.toFile().exists()));

        URI fileUri = jarPath.toUri();
        URI zipUri = new URI("jar:" + fileUri.getScheme(), fileUri.getPath(), null);

        System.out.println(zipUri);

        try (FileSystem zipfs = FileSystems.newFileSystem(zipUri, env))
        {
            // Create an internal path in the zipfs
            Path internalTargetPath = zipfs.getPath(internalPath);
            if (!Files.exists(internalTargetPath.getParent()))
            {
                // Create directory
                Files.createDirectories(internalTargetPath.getParent());
            }

            ByteArrayInputStream bais = new ByteArrayInputStream("<plugin/>\n".getBytes());

            // Copy a file into the zip file
            Files.copy(bais, internalTargetPath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

}
