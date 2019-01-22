package org.carlspring.maven.commons.utils;

import org.carlspring.maven.commons.util.ArtifactUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.maven.artifact.Artifact;
import org.junit.jupiter.api.Test;
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
        Artifact artifact1 = ArtifactUtils.convertPathToArtifact("org/carlspring/maven/foo/10.0-SNAPSHOT/foo-10.0-SNAPSHOT.jar");

        assertNotNull("Failed to covert path to artifact!", artifact1);
        assertEquals("Failed to convert path to artifact!", "org.carlspring.maven", artifact1.getGroupId());
        assertEquals("Failed to convert path to artifact!", "foo", artifact1.getArtifactId());
        assertEquals("Failed to convert path to artifact!", "10.0-SNAPSHOT", artifact1.getVersion());
        assertNull("Failed to convert path to artifact!", artifact1.getClassifier());
        assertEquals("Failed to covert path to artifact!", "jar", artifact1.getType());

        Artifact artifact2 = ArtifactUtils.convertPathToArtifact("org/carlspring/maven/foo/1.0-SNAPSHOT/foo-1.0-SNAPSHOT-jdk15.jar");

        assertNotNull("Failed to covert path to artifact!", artifact2);
        assertEquals("Failed to convert path to artifact!", "org.carlspring.maven", artifact2.getGroupId());
        assertEquals("Failed to convert path to artifact!", "foo", artifact2.getArtifactId());
        assertEquals("Failed to convert path to artifact!", "1.0-SNAPSHOT", artifact2.getVersion());
        assertEquals("Failed to convert path to artifact!", "jdk15", artifact2.getClassifier());
        assertEquals("Failed to covert path to artifact!", "jar", artifact2.getType());

        Artifact artifact3 = ArtifactUtils.convertPathToArtifact("org/carlspring/maven/foo-bar/1.0-SNAPSHOT/foo-bar-1.0-20131004.115330.jar");

        assertNotNull("Failed to covert path to artifact!", artifact3);
        assertEquals("Failed to convert path to artifact!", "org.carlspring.maven", artifact3.getGroupId());
        assertEquals("Failed to convert path to artifact!", "foo-bar", artifact3.getArtifactId());
        assertEquals("Failed to convert path to artifact!", "1.0-20131004.115330", artifact3.getVersion());
        assertNull("Failed to convert path to artifact!", artifact3.getClassifier());
        assertEquals("Failed to covert path to artifact!", "jar", artifact3.getType());

        Artifact artifact4 = ArtifactUtils.convertPathToArtifact("org/carlspring/maven/foo-bar-bar/1.0-SNAPSHOT/foo-bar-bar-1.0-20131004.115330-1.jar");

        assertNotNull("Failed to covert path to artifact!", artifact4);
        assertEquals("Failed to convert path to artifact!", "org.carlspring.maven", artifact4.getGroupId());
        assertEquals("Failed to convert path to artifact!", "foo-bar-bar", artifact4.getArtifactId());
        assertEquals("Failed to convert path to artifact!", "1.0-20131004.115330-1", artifact4.getVersion());
        assertNull("Failed to convert path to artifact!", artifact4.getClassifier());
        assertEquals("Failed to covert path to artifact!", "jar", artifact4.getType());

        Artifact artifact5 = ArtifactUtils.convertPathToArtifact("org/carlspring/maven/foo/1.0-alpha-SNAPSHOT/foo-1.0-alpha-20131004.115330-1.jar");

        assertNotNull("Failed to covert path to artifact!", artifact5);
        assertEquals("Failed to convert path to artifact!", "org.carlspring.maven", artifact5.getGroupId());
        assertEquals("Failed to convert path to artifact!", "foo", artifact5.getArtifactId());
        assertEquals("Failed to convert path to artifact!", "1.0-alpha-20131004.115330-1", artifact5.getVersion());
        assertNull("Failed to convert path to artifact!", artifact5.getClassifier());
        assertEquals("Failed to covert path to artifact!", "jar", artifact5.getType());

        Artifact artifact6 = ArtifactUtils.convertPathToArtifact("org/carlspring/maven/foo-bar/1.0-alpha-SNAPSHOT/foo-bar-1.0-alpha-20131004.115330-1-javadoc.jar");

        assertNotNull("Failed to covert path to artifact!", artifact6);
        assertEquals("Failed to convert path to artifact!", "org.carlspring.maven", artifact6.getGroupId());
        assertEquals("Failed to convert path to artifact!", "foo-bar", artifact6.getArtifactId());
        assertEquals("Failed to convert path to artifact!", "1.0-alpha-20131004.115330-1", artifact6.getVersion());
        assertEquals("Failed to convert path to artifact!", "javadoc", artifact6.getClassifier());
        assertEquals("Failed to covert path to artifact!", "jar", artifact6.getType());

        Artifact artifact7 = ArtifactUtils.convertPathToArtifact("org/carlspring/maven/foo/1.0-alpha-SNAPSHOT/foo-1.0-alpha-20131004.115330-1-custom-classifier-type.jar");

        assertNotNull("Failed to covert path to artifact!", artifact7);
        assertEquals("Failed to convert path to artifact!", "org.carlspring.maven", artifact7.getGroupId());
        assertEquals("Failed to convert path to artifact!", "foo", artifact7.getArtifactId());
        assertEquals("Failed to convert path to artifact!", "1.0-alpha-20131004.115330-1", artifact7.getVersion());
        assertEquals("Failed to convert path to artifact!", "custom-classifier-type", artifact7.getClassifier());
        assertEquals("Failed to covert path to artifact!", "jar", artifact7.getType());

        Artifact artifact8 = ArtifactUtils.convertPathToArtifact("org/carlspring/maven/foo/123.0.1/foo-123.0.1-custom-classifier-type.jar");

        assertNotNull("Failed to covert path to artifact!", artifact8);
        assertEquals("Failed to convert path to artifact!", "org.carlspring.maven", artifact8.getGroupId());
        assertEquals("Failed to convert path to artifact!", "foo", artifact8.getArtifactId());
        assertEquals("Failed to convert path to artifact!", "123.0.1", artifact8.getVersion());
        assertEquals("Failed to convert path to artifact!", "custom-classifier-type", artifact8.getClassifier());
        assertEquals("Failed to covert path to artifact!", "jar", artifact8.getType());

        Artifact artifact9 = ArtifactUtils.convertPathToArtifact("org/carlspring/maven/foo/1.0.2-alpha/foo-1.0.2-alpha-custom-classifier-type.jar");

        assertNotNull("Failed to covert path to artifact!", artifact9);
        assertEquals("Failed to convert path to artifact!", "org.carlspring.maven", artifact9.getGroupId());
        assertEquals("Failed to convert path to artifact!", "foo", artifact9.getArtifactId());
        assertEquals("Failed to convert path to artifact!", "1.0.2-alpha", artifact9.getVersion());
        assertEquals("Failed to convert path to artifact!", "custom-classifier-type", artifact9.getClassifier());
        assertEquals("Failed to covert path to artifact!", "jar", artifact9.getType());

        Artifact artifact10 = ArtifactUtils.convertPathToArtifact("org/carlspring/maven/foo");

        assertNotNull("Failed to covert path to artifact!", artifact10);
        assertEquals("Failed to convert path to artifact!", "org.carlspring.maven", artifact10.getGroupId());
        assertEquals("Failed to convert path to artifact!", "foo", artifact10.getArtifactId());
        assertNull("Failed to covert path to artifact!", artifact10.getVersion());
        assertNotNull("Failed to convert path to artifact!", artifact9.getClassifier());
        assertNull("Failed to covert path to artifact!", artifact10.getType());

        Artifact artifact11 = ArtifactUtils.convertPathToArtifact("org/foo");

        assertNotNull("Failed to covert path to artifact!", artifact11);
        assertEquals("Failed to convert path to artifact!", "org", artifact11.getGroupId());
        assertEquals("Failed to convert path to artifact!", "foo", artifact11.getArtifactId());
        assertNull("Failed to covert path to artifact!", artifact11.getVersion());
        assertNull("Failed to covert path to artifact!", artifact11.getClassifier());
        assertNull("Failed to covert path to artifact!", artifact11.getType());

        Artifact artifact12 = ArtifactUtils.convertPathToArtifact("org/carlspring/strongbox/strongbox-db-snapshot/1.0-SNAPSHOT/strongbox-db-snapshot-1.0-20190114.230421-23-db-snapshot.tar.gz");

        assertNotNull("Failed to covert path to artifact!", artifact12);
        assertEquals("Failed to covert path to artifact!", "org.carlspring.strongbox", artifact12.getGroupId());
        assertEquals("Failed to covert path to artifact!", "strongbox-db-snapshot", artifact12.getArtifactId());
        assertEquals("Failed to covert path to artifact!", "1.0-20190114.230421-23", artifact12.getVersion());
        assertEquals("Failed to covert path to artifact!", "db-snapshot", artifact12.getClassifier());
        assertEquals("Failed to covert path to artifact!", "tar.gz", artifact12.getType());

        Artifact artifact13 = ArtifactUtils.convertPathToArtifact("org/springframework/boot/spring-boot-dependencies/2.0.6.RELEASE/spring-boot-dependencies-2.0.6.RELEASE.pom");

        assertNotNull("Failed to covert path to artifact!", artifact13);
        assertEquals("Failed to covert path to artifact!", "org.springframework.boot", artifact13.getGroupId());
        assertEquals("Failed to covert path to artifact!", "spring-boot-dependencies", artifact13.getArtifactId());
        assertEquals("Failed to covert path to artifact!", "2.0.6.RELEASE", artifact13.getVersion());
        assertNull("Failed to covert path to artifact!", artifact13.getClassifier());
        assertEquals("Failed to covert path to artifact!", "pom", artifact13.getType());

        Artifact artifact14 = ArtifactUtils.convertPathToArtifact("com/foo/bar");

        assertNotNull("Failed to covert path to artifact!", artifact14);
        assertEquals("Failed to covert path to artifact!", "com.foo", artifact14.getGroupId());
        assertEquals("Failed to covert path to artifact!", "bar", artifact14.getArtifactId());
        assertNull("Failed to covert path to artifact!", artifact14.getVersion());
        assertNull("Failed to covert path to artifact!", artifact14.getClassifier());
        assertNull("Failed to covert path to artifact!", artifact14.getType());

        Artifact artifact15 = ArtifactUtils.convertPathToArtifact("com/foo");

        assertNotNull("Failed to covert path to artifact!", artifact15);
        assertEquals("Failed to covert path to artifact!", "com", artifact15.getGroupId());
        assertEquals("Failed to covert path to artifact!", "foo", artifact15.getArtifactId());
        assertNull("Failed to covert path to artifact!", artifact15.getVersion());
        assertNull("Failed to covert path to artifact!", artifact15.getClassifier());
        assertNull("Failed to covert path to artifact!", artifact15.getType());

        // Test the case where the matching logic breaks if it finds a String which repeats near the end of the path assuming its a version.
        // In this case it was incorrectly assuming "metadata" is the version (due to the repetition)
        // and therefore screwing up the GAV components by setting them to "org.carlspring:strongbox:metadata".
        Artifact artifact16 = ArtifactUtils.convertPathToArtifact("org/carlspring/strongbox/metadata/strongbox-metadata");

        assertNotNull("Failed to covert path to artifact!", artifact16);
        assertEquals("Failed to covert path to artifact!", "org.carlspring.strongbox.metadata", artifact16.getGroupId());
        assertEquals("Failed to covert path to artifact!", "strongbox-metadata", artifact16.getArtifactId());
        assertNull("Failed to covert path to artifact!", artifact16.getVersion());
        assertNull("Failed to covert path to artifact!", artifact16.getClassifier());
        assertNull("Failed to covert path to artifact!", artifact16.getType());

        Artifact artifact17 = ArtifactUtils.convertPathToArtifact("org/carlspring/strongbox/metadata/strongbox-metadata/6.0/strongbox-metadata-6.0.jar");

        assertNotNull("Failed to covert path to artifact!", artifact17);
        assertEquals("Failed to covert path to artifact!", "org.carlspring.strongbox.metadata", artifact17.getGroupId());
        assertEquals("Failed to covert path to artifact!", "strongbox-metadata", artifact17.getArtifactId());
        assertEquals("Failed to covert path to artifact!", "6.0", artifact17.getVersion());
        assertNull("Failed to covert path to artifact!", artifact17.getClassifier());
        assertEquals("Failed to covert path to artifact!", "jar", artifact17.getType());

        // Test if we handle properly paths which start and end with a trailing slash
        Artifact artifact18 = ArtifactUtils.convertPathToArtifact("org/carlspring/strongbox/added/");

        assertNotNull("Failed to covert path to artifact!", artifact18);
        assertEquals("Failed to covert path to artifact!", "org.carlspring.strongbox", artifact18.getGroupId());
        assertEquals("Failed to covert path to artifact!", "added", artifact18.getArtifactId());
        assertNull("Failed to covert path to artifact!",  artifact18.getVersion());
        assertNull("Failed to covert path to artifact!", artifact18.getClassifier());
        assertNull("Failed to covert path to artifact!", artifact18.getType());

        Artifact artifact19 = ArtifactUtils.convertPathToArtifact("/org/carlspring/strongbox/added/");

        assertNotNull("Failed to covert path to artifact!", artifact18);
        assertEquals("Failed to covert path to artifact!", "org.carlspring.strongbox", artifact19.getGroupId());
        assertEquals("Failed to covert path to artifact!", "added", artifact19.getArtifactId());
        assertNull("Failed to covert path to artifact!",  artifact19.getVersion());
        assertNull("Failed to covert path to artifact!", artifact19.getClassifier());
        assertNull("Failed to covert path to artifact!", artifact19.getType());

        // Test if we properly handle paths ending with maven-metadata.xml.
        Artifact artifact20 = ArtifactUtils.convertPathToArtifact("org/carlspring/strongbox/added/2.0-SNAPSHOT/maven-metadata.xml");

        assertNotNull("Failed to covert path to artifact!", artifact20);
        assertEquals("Failed to convert path to artifact!", "org.carlspring.strongbox", artifact20.getGroupId());
        assertEquals("Failed to convert path to artifact!", "maven-metadata.xml", artifact20.getArtifactId());
        assertEquals("Failed to convert path to artifact!", "2.0-SNAPSHOT", artifact20.getVersion());
        assertNull("Failed to convert path to artifact!", artifact20.getClassifier());
        assertEquals("Failed to covert path to artifact!", "xml", artifact20.getType());

        // Path ends with the version
        Artifact artifact21 = ArtifactUtils.convertPathToArtifact("org/carlspring/strongbox/2.0-SNAPSHOT");

        assertNotNull("Failed to covert path to artifact!", artifact21);
        assertEquals("Failed to convert path to artifact!", "org.carlspring", artifact21.getGroupId());
        assertEquals("Failed to convert path to artifact!", "strongbox", artifact21.getArtifactId());
        assertEquals("Failed to convert path to artifact!", "2.0-SNAPSHOT", artifact21.getVersion());
        assertNull("Failed to convert path to artifact!", artifact21.getClassifier());
        assertNull("Failed to covert path to artifact!", artifact21.getType());

        // Test windows style paths
        Artifact artifact22 = ArtifactUtils.convertPathToArtifact("org\\carlspring\\strongbox\\2.0-SNAPSHOT");

        assertNotNull("Failed to covert path to artifact!", artifact22);
        assertEquals("Failed to convert path to artifact!", "org.carlspring", artifact22.getGroupId());
        assertEquals("Failed to convert path to artifact!", "strongbox", artifact22.getArtifactId());
        assertEquals("Failed to convert path to artifact!", "2.0-SNAPSHOT", artifact22.getVersion());
        assertNull("Failed to convert path to artifact!", artifact22.getClassifier());
        assertNull("Failed to covert path to artifact!", artifact22.getType());

        Artifact artifact23 = ArtifactUtils.convertPathToArtifact("org\\\\carlspring\\\\strongbox\\\\2.0-SNAPSHOT");

        assertNotNull("Failed to covert path to artifact!", artifact23);
        assertEquals("Failed to convert path to artifact!", "org.carlspring", artifact23.getGroupId());
        assertEquals("Failed to convert path to artifact!", "strongbox", artifact23.getArtifactId());
        assertEquals("Failed to convert path to artifact!", "2.0-SNAPSHOT", artifact23.getVersion());
        assertNull("Failed to convert path to artifact!", artifact23.getClassifier());
        assertNull("Failed to covert path to artifact!", artifact23.getType());
    }

    @Test
    public void testConvertArtifactToPath()
    {
        Artifact artifact1 = ArtifactUtils.getArtifactFromGAVTC("org.carlspring.foo:bar:1.2.3-20150409-125301-1:jar:javadoc");
        String path1 = ArtifactUtils.convertArtifactToPath(artifact1);
        assertEquals("org/carlspring/foo/bar/1.2.3-SNAPSHOT/bar-1.2.3-20150409-125301-1-javadoc.jar", path1);
        System.out.println(artifact1.toString() + " -> " + path1);

        Artifact artifact2 = ArtifactUtils.getArtifactFromGAVTC("org.carlspring.foo:bar:1.2.3-20150409-125301-1:jar");
        String path2 = ArtifactUtils.convertArtifactToPath(artifact2);
        assertEquals("org/carlspring/foo/bar/1.2.3-SNAPSHOT/bar-1.2.3-20150409-125301-1.jar", path2);
        System.out.println(artifact2.toString() + " -> " + path2);

        Artifact artifact3 = ArtifactUtils.getArtifactFromGAVTC("org.carlspring.foo:bar:1.2.3-alpha:tar.gz:javadoc");
        String path3 = ArtifactUtils.convertArtifactToPath(artifact3);
        assertEquals("org/carlspring/foo/bar/1.2.3-alpha/bar-1.2.3-alpha-javadoc.tar.gz", path3);
        System.out.println(artifact3.toString() + " -> " + path3);

        Artifact artifact4 = ArtifactUtils.getArtifactFromGAVTC("org.carlspring.foo:bar:1.2.3:tar.gz");
        String path4 = ArtifactUtils.convertArtifactToPath(artifact4);
        assertEquals("org/carlspring/foo/bar/1.2.3/bar-1.2.3.tar.gz", path4);
        System.out.println(artifact4.toString() + " -> " + path4);

        Artifact artifact5 = ArtifactUtils.getArtifactFromGAVTC("org.carlspring.foo:bar:1.2.3-SNAPSHOT:jar");
        String path5 = ArtifactUtils.convertArtifactToPath(artifact5);
        assertEquals("org/carlspring/foo/bar/1.2.3-SNAPSHOT/bar-1.2.3-SNAPSHOT.jar", path5);
        System.out.println(artifact5.toString() + " -> " + path5);
    }

    @Test
    public void testGetArtifactFileName()
    {
        Artifact artifact = ArtifactUtils.convertPathToArtifact(
                "org/carlspring/maven/foo/1.0-SNAPSHOT/foo-1.0-SNAPSHOT.jar");

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
        assertTrue("Failed to resolve path to artifact!",
                   pathToArtifact.equals("/path/to/repository/bar-1.0.jar") ||
                   pathToArtifact.endsWith("path\\to\\repository\\bar-1.0.jar"));

        System.out.println(artifact);
    }

    @Test
    public void testPathIsCompleteGav()
    {
        assertTrue(ArtifactUtils.pathIsCompleteGAV("com/foo/bar/1.2.3/bar-1.2.3.jar"));
        assertTrue(ArtifactUtils.pathIsCompleteGAV("com/foo/bar/1.2.3/bar-1.2.3-javadoc.jar"));
        assertTrue(ArtifactUtils.pathIsCompleteGAV("com/foo/bar/1.2.3-SNAPSHOT/bar-1.2.3-20150421.050922-1.jar"));
        assertTrue(ArtifactUtils.pathIsCompleteGAV("com/foo/bar/1.2.3-SNAPSHOT/bar-1.2.3-20150421.050922-1-javadoc.jar"));
        assertTrue(ArtifactUtils.pathIsCompleteGAV("com/foo/bar/1.2.3-SNAPSHOT/bar-1.2.3-20150421.050922-1-some-complex-classifier.jar"));

        assertFalse(ArtifactUtils.pathIsCompleteGAV("com/foo/bar"));
        assertFalse(ArtifactUtils.pathIsCompleteGAV("com/bar"));
        assertFalse(ArtifactUtils.pathIsCompleteGAV("com"));
    }

    @Test
    public void testGetSnapshotTimestamp()
    {
        assertEquals("20131004.115330", ArtifactUtils.getSnapshotTimestamp("1.0-20131004.115330-15"));
        assertEquals("20131004.115330", ArtifactUtils.getSnapshotTimestamp("1.0-alpha2-20131004.115330-15"));
    }

    @Test
    public void testGetSnapshotBuildNumber()
    {
        assertEquals("15", ArtifactUtils.getSnapshotBuildNumber("1.0-20131004.115330-15"));
        assertEquals("15", ArtifactUtils.getSnapshotBuildNumber("1.0-alpha2-20131004.115330-15"));
    }

    @Test
    public void testGetGroupLevelMetadataPath() throws Exception
    {
        // com/foo/bar/1.2.3/bar-1.2.3.jar --> com/foo/bar/
        Artifact artifact = ArtifactUtils.convertPathToArtifact("com/foo/bar/1.2.3/bar-1.2.3.jar");
        String groupLevelMetadataPath = ArtifactUtils.getGroupLevelMetadataPath(artifact);

        assertEquals("Failed to get the path the group level metadata",
                     "com/foo/maven-metadata.xml",
                     groupLevelMetadataPath);
    }

    @Test
    public void testGetArtifactLevelMetadataPath()
            throws Exception
    {
        // com/foo/bar/1.2.3/bar-1.2.3.jar --> com/foo/bar/
        Artifact artifact = ArtifactUtils.convertPathToArtifact("com/foo/bar/1.2.3/bar-1.2.3.jar");
        String groupLevelMetadataPath = ArtifactUtils.getArtifactLevelMetadataPath(artifact);

        assertEquals("Failed to get the path the group level metadata",
                     "com/foo/bar/maven-metadata.xml",
                     groupLevelMetadataPath);
    }

    @Test
    public void testGetVersionLevelMetadataPath()
            throws Exception
    {
        // com/foo/bar/1.2.3-SNAPSHOT/bar-1.2.3-SNAPSHOT.jar --> com/foo/bar/1.2.3-SNAPSHOT
        Artifact artifact = ArtifactUtils.convertPathToArtifact("com/foo/bar/1.2.3-SNAPSHOT/bar-1.2.3-SNAPSHOT.jar");
        String groupLevelMetadataPath = ArtifactUtils.getVersionLevelMetadataPath(artifact);

        assertEquals("Failed to get the path the group level metadata",
                     "com/foo/bar/1.2.3-SNAPSHOT/maven-metadata.xml",
                     groupLevelMetadataPath);
    }

}