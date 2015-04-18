package org.carlspring.maven.commons.io;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * @author mtodorov
 */
public class PomJarReader implements Closeable
{

    public static final String PLUGIN_XML = "/META-INF/maven/plugin.xml";

    private Path jarPath;

    private FileSystem fileSystem;


    public PomJarReader()
    {
    }

    public PomJarReader(Path jarPath)
    {
        this.jarPath = jarPath;
    }

    public InputStream getInputStream()
            throws IOException, URISyntaxException
    {
        URI jarUri = new URI("jar:" + jarPath.toUri().toString());

        Map<String, String> env = new HashMap<>();

        fileSystem = FileSystems.newFileSystem(jarUri, env);

        Path pathInZip = fileSystem.getPath(PLUGIN_XML);

        return Files.newInputStream(pathInZip.toAbsolutePath());
    }

    public Path getJarPath()
    {
        return jarPath;
    }

    public void setJarPath(Path jarPath)
    {
        this.jarPath = jarPath;
    }

    @Override
    public void close()
            throws IOException
    {
        if (fileSystem != null)
        {
            fileSystem.close();
        }
    }

}
