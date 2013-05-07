package org.carlspring.maven.commons.util;

import org.apache.maven.artifact.Artifact;
import org.junit.Test;

import static junit.framework.Assert.*;

/**
 * @author mtodorov
 */
public class ArtifactUtilsTest
{

    @Test
    public void testIsMetadata()
    {
        assertTrue("Failed metadata check!", ArtifactUtils.isMetadata("org/carlspring/maven/foo/1.0-SNAPSHOT/maven-metadata.xml"));
        assertFalse("Failed metadata check!",
                    ArtifactUtils.isMetadata("org/carlspring/maven/foo/1.0-SNAPSHOT/foo-1.0-SNAPSHOT.jar"));
        assertFalse("Failed metadata check!",
                    ArtifactUtils.isMetadata("org/carlspring/maven/foo/1.0-SNAPSHOT/foo-1.0-SNAPSHOT.jar.sha1"));
        assertFalse("Failed metadata check!", ArtifactUtils.isMetadata("org/carlspring/maven/foo/1.0-SNAPSHOT/foo-1.0-SNAPSHOT.jar.md5"));
    }

    @Test
    public void testIsCheckSum()
    {
        assertFalse("Failed checksum check!", ArtifactUtils.isChecksum("org/carlspring/maven/foo/1.0-SNAPSHOT/maven-metadata.xml"));
        assertFalse("Failed checksum check!", ArtifactUtils.isChecksum("org/carlspring/maven/foo/1.0-SNAPSHOT/foo-1.0-SNAPSHOT.jar"));
        assertTrue("Failed checksum check!",
                   ArtifactUtils.isChecksum("org/carlspring/maven/foo/1.0-SNAPSHOT/foo-1.0-SNAPSHOT.jar.sha1"));
        assertTrue("Failed checksum check!",
                   ArtifactUtils.isChecksum("org/carlspring/maven/foo/1.0-SNAPSHOT/foo-1.0-SNAPSHOT.jar.md5"));
    }

    @Test
    public void testIsArtifact()
    {
        assertFalse("Failed artifact check!", ArtifactUtils.isArtifact("org/carlspring/maven/foo/1.0-SNAPSHOT/maven-metadata.xml"));
        assertTrue("Failed artifact check!",
                   ArtifactUtils.isArtifact("org/carlspring/maven/foo/1.0-SNAPSHOT/foo-1.0-SNAPSHOT.jar"));
        assertFalse("Failed artifact check!",
                    ArtifactUtils.isArtifact("org/carlspring/maven/foo/1.0-SNAPSHOT/foo-1.0-SNAPSHOT.jar.sha1"));
        assertFalse("Failed artifact check!",
                    ArtifactUtils.isArtifact("org/carlspring/maven/foo/1.0-SNAPSHOT/foo-1.0-SNAPSHOT.jar.md5"));
    }

    @Test
    public void testConvertPathToArtifact()
    {
        Artifact artifact = ArtifactUtils.convertPathToArtifact("org/carlspring/maven/foo/1.0-SNAPSHOT/foo-1.0-SNAPSHOT.jar");
        assertNotNull("Failed to covert path to artifact!", artifact);
        assertNotNull("Failed to covert path to artifact!", artifact.getGroupId());
        assertNotNull("Failed to covert path to artifact!", artifact.getArtifactId());
        assertNotNull("Failed to covert path to artifact!", artifact.getVersion());
        assertNotNull("Failed to covert path to artifact!", artifact.getType());

        System.out.println(artifact.toString());
    }

    @Test
    public void testArtifactToPathWithoutClassifier()
    {
        final String pathForObjectConstruction = "org/carlspring/maven/foo/1.0-SNAPSHOT/foo-1.0-SNAPSHOT.jar";
        Artifact artifact = ArtifactUtils.convertPathToArtifact(pathForObjectConstruction);
        final String path = ArtifactUtils.convertArtifactToPath(artifact);

        assertEquals("Failed to properly convert the artifact to a path!", pathForObjectConstruction, path);
    }

    @Test
    public void testArtifactToPathWithClassifier()
    {
        final String pathForObjectConstruction = "org/carlspring/maven/foo/1.0-SNAPSHOT/foo-1.0-SNAPSHOT-jdk15.jar";
        Artifact artifact = ArtifactUtils.convertPathToArtifact(pathForObjectConstruction);
        final String path = ArtifactUtils.convertArtifactToPath(artifact);

        assertEquals("Failed to properly convert the artifact to a path!", pathForObjectConstruction, path);
    }

}
