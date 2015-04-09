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

        assertFalse(ArtifactUtils.isSnapshot("1.0"));
        assertFalse(ArtifactUtils.isSnapshot("1.0.10"));
        assertFalse(ArtifactUtils.isSnapshot("1.0.10-alpha-3"));

        assertTrue(ArtifactUtils.isReleaseVersion("1.0.10-alpha-3"));

        assertTrue(ArtifactUtils.isSnapshot("1.0-SNAPSHOT"));
        assertTrue(ArtifactUtils.isSnapshot("1.0-20131004.115330-1"));
        assertTrue(ArtifactUtils.isSnapshot("1.0-20131004"));
        assertTrue(ArtifactUtils.isSnapshot("1.0-20131004.115330"));
        assertTrue(ArtifactUtils.isSnapshot("1.10.10-20131004.115330-1"));

        assertEquals("1.0-SNAPSHOT", ArtifactUtils.getSnapshotBaseVersion("1.0-SNAPSHOT"));
        assertEquals("1.10-SNAPSHOT", ArtifactUtils.getSnapshotBaseVersion("1.10-20131004.115330-1"));

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
        Artifact artifact1 = ArtifactUtils.convertPathToArtifact("org/carlspring/maven/foo-bar/1.0-SNAPSHOT/foo-1.0-SNAPSHOT.jar");
        assertNotNull("Failed to covert path to artifact!", artifact1);
        assertNotNull("Failed to covert path to artifact!", artifact1.getGroupId());
        assertNotNull("Failed to covert path to artifact!", artifact1.getArtifactId());
        assertNotNull("Failed to covert path to artifact!", artifact1.getVersion());
        assertNotNull("Failed to covert path to artifact!", artifact1.getType());

        assertEquals("Failed to convert path to artifact!", "org.carlspring.maven", artifact1.getGroupId());
        assertEquals("Failed to convert path to artifact!", "foo", artifact1.getArtifactId());
        assertEquals("Failed to convert path to artifact!", "1.0-SNAPSHOT", artifact1.getVersion());
        assertEquals("Failed to convert path to artifact!", "", artifact1.getClassifier());

        Artifact artifact2 = ArtifactUtils.convertPathToArtifact("org/carlspring/maven/foo/1.0-SNAPSHOT/foo-1.0-SNAPSHOT-jdk15.jar");
        assertNotNull("Failed to covert path to artifact!", artifact2);
        assertNotNull("Failed to covert path to artifact!", artifact2.getGroupId());
        assertNotNull("Failed to covert path to artifact!", artifact2.getArtifactId());
        assertNotNull("Failed to covert path to artifact!", artifact2.getVersion());
        assertNotNull("Failed to covert path to artifact!", artifact2.getType());

        assertEquals("Failed to convert path to artifact!", "org.carlspring.maven", artifact2.getGroupId());
        assertEquals("Failed to convert path to artifact!", "foo", artifact2.getArtifactId());
        assertEquals("Failed to convert path to artifact!", "1.0-SNAPSHOT", artifact2.getVersion());
        assertEquals("Failed to convert path to artifact!", "jdk15", artifact2.getClassifier());


        Artifact artifact3 = ArtifactUtils.convertPathToArtifact("org/carlspring/maven/foo/1.0-SNAPSHOT/foo-bar-1.0-20131004.115330.jar");
        assertNotNull("Failed to covert path to artifact!", artifact3);
        assertNotNull("Failed to covert path to artifact!", artifact3.getGroupId());
        assertNotNull("Failed to covert path to artifact!", artifact3.getArtifactId());
        assertNotNull("Failed to covert path to artifact!", artifact3.getVersion());
        assertNotNull("Failed to covert path to artifact!", artifact3.getType());

        assertEquals("Failed to convert path to artifact!", "org.carlspring.maven", artifact3.getGroupId());
        assertEquals("Failed to convert path to artifact!", "foo-bar", artifact3.getArtifactId());
        assertEquals("Failed to convert path to artifact!", "1.0-20131004.115330", artifact3.getVersion());
        assertEquals("Failed to convert path to artifact!", "", artifact3.getClassifier());

        Artifact artifact4 = ArtifactUtils.convertPathToArtifact("org/carlspring/maven/foo/1.0-SNAPSHOT/foo-bar-bar-1.0-20131004.115330-1.jar");
        assertNotNull("Failed to covert path to artifact!", artifact4);
        assertNotNull("Failed to covert path to artifact!", artifact4.getGroupId());
        assertNotNull("Failed to covert path to artifact!", artifact4.getArtifactId());
        assertNotNull("Failed to covert path to artifact!", artifact4.getVersion());
        assertNotNull("Failed to covert path to artifact!", artifact4.getType());

        assertEquals("Failed to convert path to artifact!", "org.carlspring.maven", artifact4.getGroupId());
        assertEquals("Failed to convert path to artifact!", "foo-bar-bar", artifact4.getArtifactId());
        assertEquals("Failed to convert path to artifact!", "1.0-20131004.115330-1", artifact4.getVersion());
        assertEquals("Failed to convert path to artifact!", "", artifact4.getClassifier());

        Artifact artifact5 = ArtifactUtils.convertPathToArtifact("org/carlspring/maven/foo/1.0-alpha-SNAPSHOT/foo-1.0-alpha-20131004.115330-1.jar");
        assertNotNull("Failed to covert path to artifact!", artifact5);
        assertNotNull("Failed to covert path to artifact!", artifact5.getGroupId());
        assertNotNull("Failed to covert path to artifact!", artifact5.getArtifactId());
        assertNotNull("Failed to covert path to artifact!", artifact5.getVersion());
        assertNotNull("Failed to covert path to artifact!", artifact5.getType());

        assertEquals("Failed to convert path to artifact!", "org.carlspring.maven", artifact5.getGroupId());
        assertEquals("Failed to convert path to artifact!", "foo", artifact5.getArtifactId());
        assertEquals("Failed to convert path to artifact!", "1.0-alpha-20131004.115330-1", artifact5.getVersion());
        assertEquals("Failed to convert path to artifact!", "", artifact5.getClassifier());

        Artifact artifact6 = ArtifactUtils.convertPathToArtifact("org/carlspring/maven/foo/1.0-alpha-SNAPSHOT/foo-bar-1.0-alpha-20131004.115330-1-javadoc.jar");
        assertNotNull("Failed to covert path to artifact!", artifact6);
        assertNotNull("Failed to covert path to artifact!", artifact6.getGroupId());
        assertNotNull("Failed to covert path to artifact!", artifact6.getArtifactId());
        assertNotNull("Failed to covert path to artifact!", artifact6.getVersion());
        assertNotNull("Failed to covert path to artifact!", artifact6.getType());

        assertEquals("Failed to convert path to artifact!", "org.carlspring.maven", artifact6.getGroupId());
        assertEquals("Failed to convert path to artifact!", "foo-bar", artifact6.getArtifactId());
        assertEquals("Failed to convert path to artifact!", "1.0-alpha-20131004.115330-1", artifact6.getVersion());
        assertEquals("Failed to convert path to artifact!", "javadoc", artifact6.getClassifier());

        Artifact artifact7 = ArtifactUtils.convertPathToArtifact("org/carlspring/maven/foo/1.0-alpha-SNAPSHOT/foo-bar-with-a-n0n-standard-name-1.0-alpha-20131004.115330-1-custom-classifier-type.jar");
        assertNotNull("Failed to covert path to artifact!", artifact7);
        assertNotNull("Failed to covert path to artifact!", artifact7.getGroupId());
        assertNotNull("Failed to covert path to artifact!", artifact7.getArtifactId());
        assertNotNull("Failed to covert path to artifact!", artifact7.getVersion());
        assertNotNull("Failed to covert path to artifact!", artifact7.getType());

        assertEquals("Failed to convert path to artifact!", "org.carlspring.maven", artifact7.getGroupId());
        assertEquals("Failed to convert path to artifact!", "foo-bar-with-a-n0n-standard-name", artifact7.getArtifactId());
        assertEquals("Failed to convert path to artifact!", "1.0-alpha-20131004.115330-1", artifact7.getVersion());
        assertEquals("Failed to convert path to artifact!", "custom-classifier-type", artifact7.getClassifier());

        Artifact artifact8 = ArtifactUtils.convertPathToArtifact("org/carlspring/maven/foo/1.0.1/foo-bar-with-a-n0n-standard-name-1.0.1-custom-classifier-type.jar");
        assertNotNull("Failed to covert path to artifact!", artifact8);
        assertNotNull("Failed to covert path to artifact!", artifact8.getGroupId());
        assertNotNull("Failed to covert path to artifact!", artifact8.getArtifactId());
        assertNotNull("Failed to covert path to artifact!", artifact8.getVersion());
        assertNotNull("Failed to covert path to artifact!", artifact8.getType());

        assertEquals("Failed to convert path to artifact!", "org.carlspring.maven", artifact8.getGroupId());
        assertEquals("Failed to convert path to artifact!", "foo-bar-with-a-n0n-standard-name", artifact8.getArtifactId());
        assertEquals("Failed to convert path to artifact!", "1.0.1", artifact8.getVersion());
        assertEquals("Failed to convert path to artifact!", "custom-classifier-type", artifact8.getClassifier());

        Artifact artifact9 = ArtifactUtils.convertPathToArtifact("org/carlspring/maven/foo/1.0.2-alpha/foo-bar-with-a-n0n-standard-name-1.0.2-alpha-custom-classifier-type.jar");
        assertNotNull("Failed to covert path to artifact!", artifact9);
        assertNotNull("Failed to covert path to artifact!", artifact9.getGroupId());
        assertNotNull("Failed to covert path to artifact!", artifact9.getArtifactId());
        assertNotNull("Failed to covert path to artifact!", artifact9.getVersion());
        assertNotNull("Failed to covert path to artifact!", artifact9.getType());

        assertEquals("Failed to convert path to artifact!", "org.carlspring.maven", artifact9.getGroupId());
        assertEquals("Failed to convert path to artifact!", "foo-bar-with-a-n0n-standard-name", artifact9.getArtifactId());
        assertEquals("Failed to convert path to artifact!", "1.0.2-alpha", artifact9.getVersion());
        assertEquals("Failed to convert path to artifact!", "custom-classifier-type", artifact9.getClassifier());


        System.out.println(artifact1.toString());
        System.out.println(artifact2.toString());
        System.out.println(artifact3.toString());
        System.out.println(artifact4.toString());
        System.out.println(artifact5.toString());
        System.out.println(artifact6.toString());
        System.out.println(artifact7.toString());
        System.out.println(artifact8.toString());
        System.out.println(artifact9.toString());
    }

    @Test
    public void testConvertArtifactToPath()
    {
        Artifact artifact = ArtifactUtils.getArtifactFromGAVTC("org.carlspring.foo:bar:1.2.3-20150409-125301-1:jar:javadoc");

        String path = ArtifactUtils.convertArtifactToPath(artifact);

        assertEquals("org/carlspring/foo/bar/1.2.3-SNAPSHOT/bar-1.2.3-20150409-125301-1-javadoc.jar", path);

        System.out.println(artifact.toString() + " -> " + path);
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