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
        Artifact artifact1 = ArtifactUtils.convertPathToArtifact("org/carlspring/maven/foo/1.0-SNAPSHOT/foo-1.0-SNAPSHOT.jar");

        System.out.println(artifact1.toString());

        assertNotNull("Failed to covert path to artifact!", artifact1);
        assertNotNull("Failed to covert path to artifact!", artifact1.getGroupId());
        assertNotNull("Failed to covert path to artifact!", artifact1.getArtifactId());
        assertNotNull("Failed to covert path to artifact!", artifact1.getVersion());
        assertNotNull("Failed to covert path to artifact!", artifact1.getType());

        assertEquals("Failed to convert path to artifact!", "org.carlspring.maven", artifact1.getGroupId());
        assertEquals("Failed to convert path to artifact!", "foo", artifact1.getArtifactId());
        assertEquals("Failed to convert path to artifact!", "1.0-SNAPSHOT", artifact1.getVersion());
        assertNull("Failed to convert path to artifact!", artifact1.getClassifier());

        Artifact artifact2 = ArtifactUtils.convertPathToArtifact("org/carlspring/maven/foo/1.0-SNAPSHOT/foo-1.0-SNAPSHOT-jdk15.jar");

        System.out.println(artifact2.toString());

        assertNotNull("Failed to covert path to artifact!", artifact2);
        assertNotNull("Failed to covert path to artifact!", artifact2.getGroupId());
        assertNotNull("Failed to covert path to artifact!", artifact2.getArtifactId());
        assertNotNull("Failed to covert path to artifact!", artifact2.getVersion());
        assertNotNull("Failed to covert path to artifact!", artifact2.getType());
        assertEquals("Failed to convert path to artifact!", "org.carlspring.maven", artifact2.getGroupId());
        assertEquals("Failed to convert path to artifact!", "foo", artifact2.getArtifactId());
        assertEquals("Failed to convert path to artifact!", "1.0-SNAPSHOT", artifact2.getVersion());
        assertEquals("Failed to convert path to artifact!", "jdk15", artifact2.getClassifier());

        Artifact artifact3 = ArtifactUtils.convertPathToArtifact("org/carlspring/maven/foo-bar/1.0-SNAPSHOT/foo-bar-1.0-20131004.115330.jar");

        System.out.println(artifact3.toString());

        assertNotNull("Failed to covert path to artifact!", artifact3);
        assertNotNull("Failed to covert path to artifact!", artifact3.getGroupId());
        assertNotNull("Failed to covert path to artifact!", artifact3.getArtifactId());
        assertNotNull("Failed to covert path to artifact!", artifact3.getVersion());
        assertNotNull("Failed to covert path to artifact!", artifact3.getType());
        assertEquals("Failed to convert path to artifact!", "org.carlspring.maven", artifact3.getGroupId());
        assertEquals("Failed to convert path to artifact!", "foo-bar", artifact3.getArtifactId());
        assertEquals("Failed to convert path to artifact!", "1.0-20131004.115330", artifact3.getVersion());
        assertNull("Failed to convert path to artifact!", artifact3.getClassifier());

        Artifact artifact4 = ArtifactUtils.convertPathToArtifact("org/carlspring/maven/foo-bar-bar/1.0-SNAPSHOT/foo-bar-bar-1.0-20131004.115330-1.jar");

        System.out.println(artifact4.toString());

        assertNotNull("Failed to covert path to artifact!", artifact4);
        assertNotNull("Failed to covert path to artifact!", artifact4.getGroupId());
        assertNotNull("Failed to covert path to artifact!", artifact4.getArtifactId());
        assertNotNull("Failed to covert path to artifact!", artifact4.getVersion());
        assertNotNull("Failed to covert path to artifact!", artifact4.getType());

        assertEquals("Failed to convert path to artifact!", "org.carlspring.maven", artifact4.getGroupId());
        assertEquals("Failed to convert path to artifact!", "foo-bar-bar", artifact4.getArtifactId());
        assertEquals("Failed to convert path to artifact!", "1.0-20131004.115330-1", artifact4.getVersion());
        assertNull("Failed to convert path to artifact!", artifact4.getClassifier());

        Artifact artifact5 = ArtifactUtils.convertPathToArtifact("org/carlspring/maven/foo/1.0-alpha-SNAPSHOT/foo-1.0-alpha-20131004.115330-1.jar");

        System.out.println(artifact5.toString());

        assertNotNull("Failed to covert path to artifact!", artifact5);
        assertNotNull("Failed to covert path to artifact!", artifact5.getGroupId());
        assertNotNull("Failed to covert path to artifact!", artifact5.getArtifactId());
        assertNotNull("Failed to covert path to artifact!", artifact5.getVersion());
        assertNotNull("Failed to covert path to artifact!", artifact5.getType());
        assertEquals("Failed to convert path to artifact!", "org.carlspring.maven", artifact5.getGroupId());
        assertEquals("Failed to convert path to artifact!", "foo", artifact5.getArtifactId());
        assertEquals("Failed to convert path to artifact!", "1.0-alpha-20131004.115330-1", artifact5.getVersion());
        assertNull("Failed to convert path to artifact!", artifact5.getClassifier());

        Artifact artifact6 = ArtifactUtils.convertPathToArtifact("org/carlspring/maven/foo-bar/1.0-alpha-SNAPSHOT/foo-bar-1.0-alpha-20131004.115330-1-javadoc.jar");

        System.out.println(artifact6.toString());

        assertNotNull("Failed to covert path to artifact!", artifact6);
        assertNotNull("Failed to covert path to artifact!", artifact6.getGroupId());
        assertNotNull("Failed to covert path to artifact!", artifact6.getArtifactId());
        assertNotNull("Failed to covert path to artifact!", artifact6.getVersion());
        assertNotNull("Failed to covert path to artifact!", artifact6.getType());

        assertEquals("Failed to convert path to artifact!", "org.carlspring.maven", artifact6.getGroupId());
        assertEquals("Failed to convert path to artifact!", "foo-bar", artifact6.getArtifactId());
        assertEquals("Failed to convert path to artifact!", "1.0-alpha-20131004.115330-1", artifact6.getVersion());
        assertEquals("Failed to convert path to artifact!", "javadoc", artifact6.getClassifier());

        Artifact artifact7 = ArtifactUtils.convertPathToArtifact("org/carlspring/maven/foo/1.0-alpha-SNAPSHOT/foo-1.0-alpha-20131004.115330-1-custom-classifier-type.jar");

        System.out.println(artifact7.toString());

        assertNotNull("Failed to covert path to artifact!", artifact7);
        assertNotNull("Failed to covert path to artifact!", artifact7.getGroupId());
        assertNotNull("Failed to covert path to artifact!", artifact7.getArtifactId());
        assertNotNull("Failed to covert path to artifact!", artifact7.getVersion());
        assertNotNull("Failed to covert path to artifact!", artifact7.getType());

        assertEquals("Failed to convert path to artifact!", "org.carlspring.maven", artifact7.getGroupId());
        assertEquals("Failed to convert path to artifact!", "foo", artifact7.getArtifactId());
        assertEquals("Failed to convert path to artifact!", "1.0-alpha-20131004.115330-1", artifact7.getVersion());
        assertEquals("Failed to convert path to artifact!", "custom-classifier-type", artifact7.getClassifier());

        Artifact artifact8 = ArtifactUtils.convertPathToArtifact("org/carlspring/maven/foo/1.0.1/foo-1.0.1-custom-classifier-type.jar");

        System.out.println(artifact8.toString());

        assertNotNull("Failed to covert path to artifact!", artifact8);
        assertNotNull("Failed to covert path to artifact!", artifact8.getGroupId());
        assertNotNull("Failed to covert path to artifact!", artifact8.getArtifactId());
        assertNotNull("Failed to covert path to artifact!", artifact8.getVersion());
        assertNotNull("Failed to covert path to artifact!", artifact8.getType());

        assertEquals("Failed to convert path to artifact!", "org.carlspring.maven", artifact8.getGroupId());
        assertEquals("Failed to convert path to artifact!", "foo", artifact8.getArtifactId());
        assertEquals("Failed to convert path to artifact!", "1.0.1", artifact8.getVersion());
        assertEquals("Failed to convert path to artifact!", "custom-classifier-type", artifact8.getClassifier());

        Artifact artifact9 = ArtifactUtils.convertPathToArtifact("org/carlspring/maven/foo/1.0.2-alpha/foo-1.0.2-alpha-custom-classifier-type.jar");

        System.out.println(artifact9.toString());

        assertNotNull("Failed to covert path to artifact!", artifact9);
        assertNotNull("Failed to covert path to artifact!", artifact9.getGroupId());
        assertNotNull("Failed to covert path to artifact!", artifact9.getArtifactId());
        assertNotNull("Failed to covert path to artifact!", artifact9.getVersion());
        assertNotNull("Failed to covert path to artifact!", artifact9.getType());
        assertEquals("Failed to convert path to artifact!", "org.carlspring.maven", artifact9.getGroupId());
        assertEquals("Failed to convert path to artifact!", "foo", artifact9.getArtifactId());
        assertEquals("Failed to convert path to artifact!", "1.0.2-alpha", artifact9.getVersion());
        assertEquals("Failed to convert path to artifact!", "custom-classifier-type", artifact9.getClassifier());

        Artifact artifact10 = ArtifactUtils.convertPathToArtifact("org/carlspring/maven/foo");

        System.out.println(artifact10.toString());

        assertNotNull("Failed to covert path to artifact!", artifact10);
        assertNotNull("Failed to covert path to artifact!", artifact10.getGroupId());
        assertNotNull("Failed to covert path to artifact!", artifact10.getArtifactId());
        assertNull("Failed to covert path to artifact!", artifact10.getVersion());
        assertNull("Failed to covert path to artifact!", artifact10.getType());
        assertEquals("Failed to convert path to artifact!", "org.carlspring.maven", artifact10.getGroupId());
        assertEquals("Failed to convert path to artifact!", "foo", artifact10.getArtifactId());

        Artifact artifact11 = ArtifactUtils.convertPathToArtifact("org/foo");

        System.out.println(artifact11.toString());

        assertNotNull("Failed to covert path to artifact!", artifact11);
        assertNotNull("Failed to covert path to artifact!", artifact11.getGroupId());
        assertNotNull("Failed to covert path to artifact!", artifact11.getArtifactId());
        assertNull("Failed to covert path to artifact!", artifact11.getVersion());
        assertNull("Failed to covert path to artifact!", artifact11.getType());
        assertEquals("Failed to convert path to artifact!", "org", artifact11.getGroupId());
        assertEquals("Failed to convert path to artifact!", "foo", artifact11.getArtifactId());
    }

    @Test
    public void testConvertArtifactToPath()
    {
        Artifact artifact = ArtifactUtils.getArtifactFromGAVTC(
                "org.carlspring.foo:bar:1.2.3-20150409-125301-1:jar:javadoc");

        String path = ArtifactUtils.convertArtifactToPath(artifact);

        assertEquals("org/carlspring/foo/bar/1.2.3-SNAPSHOT/bar-1.2.3-20150409-125301-1-javadoc.jar", path);

        System.out.println(artifact.toString() + " -> " + path);
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

        Artifact artifact1 = ArtifactUtils.convertPathToArtifact("com/foo/bar");
        assertEquals("com.foo", artifact1.getGroupId());
        assertEquals("bar", artifact1.getArtifactId());

        Artifact artifact2 = ArtifactUtils.convertPathToArtifact("com/foo");
        assertEquals("com", artifact2.getGroupId());
        assertEquals("foo", artifact2.getArtifactId());

        Artifact artifact3 = ArtifactUtils.convertPathToArtifact("com/foo/bar/blah");
        assertEquals("com.foo.bar", artifact3.getGroupId());
        assertEquals("blah", artifact3.getArtifactId());

        Artifact artifact4 = ArtifactUtils.convertPathToArtifact("com/foo/bar/yadee-yadda");
        assertEquals("com.foo.bar", artifact4.getGroupId());
        assertEquals("yadee-yadda", artifact4.getArtifactId());

        // Test the case where the matching logic breaks if it finds a String which repeats near the end of the path assuming its a version.
        // In this case it was incorrectly assuming "metadata" is the version (due to the repetition)
        // and therefore screwing up the GAV components by setting them to "org.carlspring:strongbox:metadata".
        Artifact artifact5 = ArtifactUtils.convertPathToArtifact("org/carlspring/strongbox/metadata/strongbox-metadata");
        assertEquals("org.carlspring.strongbox.metadata", artifact5.getGroupId());
        assertEquals("strongbox-metadata", artifact5.getArtifactId());

        Artifact artifact6 = ArtifactUtils.convertPathToArtifact("org/carlspring/strongbox/metadata/strongbox-metadata/6.0/strongbox-metadata-6.0.jar");
        assertEquals("org.carlspring.strongbox.metadata", artifact6.getGroupId());
        assertEquals("strongbox-metadata", artifact6.getArtifactId());
        assertEquals("6.0", artifact6.getVersion());
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
    public void testGetArtifactLevelMetadataPath() throws Exception
    {
        // com/foo/bar/1.2.3/bar-1.2.3.jar --> com/foo/bar/
        Artifact artifact = ArtifactUtils.convertPathToArtifact("com/foo/bar/1.2.3/bar-1.2.3.jar");
        String groupLevelMetadataPath = ArtifactUtils.getArtifactLevelMetadataPath(artifact);

        assertEquals("Failed to get the path the group level metadata",
                     "com/foo/bar/maven-metadata.xml",
                     groupLevelMetadataPath);
    }

    @Test
    public void testGetVersionLevelMetadataPath() throws Exception
    {
        // com/foo/bar/1.2.3-SNAPSHOT/bar-1.2.3-SNAPSHOT.jar --> com/foo/bar/1.2.3-SNAPSHOT
        Artifact artifact = ArtifactUtils.convertPathToArtifact("com/foo/bar/1.2.3-SNAPSHOT/bar-1.2.3-SNAPSHOT.jar");
        String groupLevelMetadataPath = ArtifactUtils.getVersionLevelMetadataPath(artifact);

        assertEquals("Failed to get the path the group level metadata",
                     "com/foo/bar/1.2.3-SNAPSHOT/maven-metadata.xml",
                     groupLevelMetadataPath);
    }

}