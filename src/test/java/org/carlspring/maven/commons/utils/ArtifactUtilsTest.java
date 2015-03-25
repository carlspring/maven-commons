package org.carlspring.maven.commons.utils;

import org.apache.maven.artifact.Artifact;
import org.carlspring.maven.commons.util.ArtifactUtils;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static junit.framework.Assert.*;

/**
 * @author mtodorov
 */
public class ArtifactUtilsTest
{

    @Test
    public void testIsMetadata()
    {
        assertTrue("Failed metadata check!",
                   ArtifactUtils.isMetadata("org/carlspring/maven/foo/1.0-SNAPSHOT/maven-metadata.xml"));
        assertFalse("Failed metadata check!",
                    ArtifactUtils.isMetadata("org/carlspring/maven/foo/1.0-SNAPSHOT/foo-1.0-SNAPSHOT.jar"));
        assertFalse("Failed metadata check!",
                    ArtifactUtils.isMetadata("org/carlspring/maven/foo/1.0-SNAPSHOT/foo-1.0-SNAPSHOT.jar.sha1"));
        assertFalse("Failed metadata check!",
                    ArtifactUtils.isMetadata("org/carlspring/maven/foo/1.0-SNAPSHOT/foo-1.0-SNAPSHOT.jar.md5"));
    }

    @Test
    public void testIsCheckSum()
    {
        assertFalse("Failed checksum check!",
                    ArtifactUtils.isChecksum("org/carlspring/maven/foo/1.0-SNAPSHOT/maven-metadata.xml"));
        assertFalse("Failed checksum check!",
                    ArtifactUtils.isChecksum("org/carlspring/maven/foo/1.0-SNAPSHOT/foo-1.0-SNAPSHOT.jar"));
        assertTrue("Failed checksum check!",
                   ArtifactUtils.isChecksum("org/carlspring/maven/foo/1.0-SNAPSHOT/foo-1.0-SNAPSHOT.jar.sha1"));
        assertTrue("Failed checksum check!",
                   ArtifactUtils.isChecksum("org/carlspring/maven/foo/1.0-SNAPSHOT/foo-1.0-SNAPSHOT.jar.md5"));
    }

    @Test
    public void testIsArtifact()
    {
        assertFalse("Failed artifact check!",
                    ArtifactUtils.isArtifact("org/carlspring/maven/foo/1.0-SNAPSHOT/maven-metadata.xml"));
        assertTrue("Failed artifact check!",
                   ArtifactUtils.isArtifact("org/carlspring/maven/foo/1.0-SNAPSHOT/foo-1.0-SNAPSHOT.jar"));
        assertFalse("Failed artifact check!",
                    ArtifactUtils.isArtifact("org/carlspring/maven/foo/1.0-SNAPSHOT/foo-1.0-SNAPSHOT.jar.sha1"));
        assertFalse("Failed artifact check!",
                    ArtifactUtils.isArtifact("org/carlspring/maven/foo/1.0-SNAPSHOT/foo-1.0-SNAPSHOT.jar.md5"));
    }

    @Test
    public void testSnapshots()
    {

        assertEquals("1.0-SNAPSHOT", ArtifactUtils.getSnapshotBaseVersion("1.0-SNAPSHOT"));

        assertTrue(ArtifactUtils.isSnapshotVersion("1.0-SNAPSHOT"));
        assertTrue(ArtifactUtils.isSnapshotVersion("1.0-20131004.115330-1"));
        assertTrue(ArtifactUtils.isSnapshotVersion("1.0-20131004"));
        assertTrue(ArtifactUtils.isSnapshotVersion("1.0-20131004.115330"));

        assertEquals("1.0-SNAPSHOT", ArtifactUtils.getSnapshotBaseVersion("1.0-20131004.115330-1"));
        assertEquals("1.0-SNAPSHOT", ArtifactUtils.getSnapshotBaseVersion("1.0-20131004.115330"));
        assertEquals("1.0-SNAPSHOT", ArtifactUtils.getSnapshotBaseVersion("1.0-SNAPSHOT"));

        assertEquals("1.0-alpha-SNAPSHOT", ArtifactUtils.getSnapshotBaseVersion("1.0-alpha-20131004.115330-1"));
        assertEquals("1.0-alpha-SNAPSHOT", ArtifactUtils.getSnapshotBaseVersion("1.0-alpha-20131004.115330"));
        assertEquals("1.0-alpha-SNAPSHOT", ArtifactUtils.getSnapshotBaseVersion("1.0-alpha-SNAPSHOT"));

        assertEquals("1.0-beta-1-SNAPSHOT", ArtifactUtils.getSnapshotBaseVersion("1.0-beta-1-20131004.115330-1"));
        assertEquals("1.0-beta-1-SNAPSHOT", ArtifactUtils.getSnapshotBaseVersion("1.0-beta-1-20131004.115330"));
        assertEquals("1.0-beta-1-SNAPSHOT", ArtifactUtils.getSnapshotBaseVersion("1.0-beta-1-SNAPSHOT"));

        assertEquals("1.0-rc2-SNAPSHOT", ArtifactUtils.getSnapshotBaseVersion("1.0-rc2-20131004.115330-1"));
        assertEquals("1.0-rc2-SNAPSHOT", ArtifactUtils.getSnapshotBaseVersion("1.0-rc2-20131004.115330"));
        assertEquals("1.0-rc2-SNAPSHOT", ArtifactUtils.getSnapshotBaseVersion("1.0-rc2-SNAPSHOT"));

        assertEquals("1.0-milestone-2-SNAPSHOT", ArtifactUtils.getSnapshotBaseVersion("1.0-milestone-2-20131004.115330-1"));
        assertEquals("1.0-milestone-2-SNAPSHOT", ArtifactUtils.getSnapshotBaseVersion("1.0-milestone-2-20131004.115330"));
        assertEquals("1.0-milestone-2-SNAPSHOT", ArtifactUtils.getSnapshotBaseVersion("1.0-milestone-2-SNAPSHOT"));
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
    public void testGetArtifactFileName()
    {
        Artifact artifact = ArtifactUtils.convertPathToArtifact("org/carlspring/maven/foo/1.0-SNAPSHOT/foo-1.0-SNAPSHOT.jar");

        final String artifactFileName = ArtifactUtils.getArtifactFileName(artifact);

        assertNotNull("Failed to get artifact file's name!", artifactFileName);
        assertEquals("Failed to get artifact file's name!", "foo-1.0-SNAPSHOT.jar", artifactFileName);

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

    @Test
    public void testArtifactToPathWithClassifierAndTimestampedSnapshot()
    {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String timestamp = formatter.format(Calendar.getInstance().getTime());

        String snapshotVersion = "1.0-SNAPSHOT";
        String timestampedSnapshotVersion = snapshotVersion + "-" + timestamp;

        String expectedPath = "org/carlspring/maven/foo/" +
                              snapshotVersion + "/" +
                              "foo-" + timestampedSnapshotVersion + "-jdk15.jar";

        Artifact artifact = ArtifactUtils.getArtifactFromGAVTC("org.carlspring.maven:foo:" + timestampedSnapshotVersion + ":jar:jdk15");
        String path = ArtifactUtils.convertArtifactToPath(artifact);

        assertEquals("Failed to properly convert the artifact to a path!", expectedPath, path);
    }

    @Test
    public void testGAVToArtifact()
    {
        Artifact artifact = ArtifactUtils.getArtifactFromGAV("com.foo:bar:1.0");

        assertNotNull("Failed to properly parse artifact based on GAV!", artifact);
        assertNotNull("Failed to properly parse artifact based on GAV!", artifact.getGroupId());
        assertNotNull("Failed to properly parse artifact based on GAV!", artifact.getArtifactId());
        assertNotNull("Failed to properly parse artifact based on GAV!", artifact.getVersion());

        System.out.println(artifact);
    }

    @Test
    public void testGAVTCWithTypeToArtifact()
    {
        Artifact artifact = ArtifactUtils.getArtifactFromGAV("com.foo:bar:1.0:war");

        assertNotNull("Failed to properly parse artifact based on GAV!", artifact);
        assertNotNull("Failed to properly parse artifact based on GAV!", artifact.getGroupId());
        assertNotNull("Failed to properly parse artifact based on GAV!", artifact.getArtifactId());
        assertNotNull("Failed to properly parse artifact based on GAV!", artifact.getVersion());
        assertEquals("Failed to properly parse artifact based on GAV!", "war", artifact.getType());

        System.out.println(artifact);
    }

    @Test
    public void testGAVTCToArtifact()
    {
        Artifact artifact = ArtifactUtils.getArtifactFromGAV("com.foo:bar:1.0:war:x86");

        assertNotNull("Failed to properly parse artifact based on GAV!", artifact);
        assertNotNull("Failed to properly parse artifact based on GAV!", artifact.getGroupId());
        assertNotNull("Failed to properly parse artifact based on GAV!", artifact.getArtifactId());
        assertNotNull("Failed to properly parse artifact based on GAV!", artifact.getVersion());
        assertEquals("Failed to properly parse artifact based on GAV!", "war", artifact.getType());
        assertEquals("Failed to properly parse artifact based on GAV!", "x86", artifact.getClassifier());

        System.out.println(artifact);
    }

    @Test
    public void testPOMForArtifactFromGAV()
    {
        Artifact artifact = ArtifactUtils.getPOMArtifactFromGAV("com.foo:bar:1.0");

        assertNotNull("Failed to properly parse artifact based on GAV!", artifact);
        assertNotNull("Failed to properly parse artifact based on GAV!", artifact.getGroupId());
        assertNotNull("Failed to properly parse artifact based on GAV!", artifact.getArtifactId());
        assertNotNull("Failed to properly parse artifact based on GAV!", artifact.getVersion());
        assertEquals("Failed to properly parse artifact based on GAV!", "pom", artifact.getType());

        System.out.println(artifact);
    }

    @Test
    public void testGetPathToArtifact()
    {
        Artifact artifact = ArtifactUtils.getArtifactFromGAV("com.foo:bar:1.0");
        final String pathToArtifact = ArtifactUtils.getPathToArtifact(artifact, "/path/to/repository");

        assertNotNull("Failed to resolve path to artifact!", pathToArtifact);
        assertEquals("Failed to resolve path to artifact!", "/path/to/repository/bar-1.0.jar", pathToArtifact);

        System.out.println(artifact);
    }

}