package org.carlspring.maven.commons.utils;

import org.carlspring.maven.commons.util.GAVUtils;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.handler.DefaultArtifactHandler;
import org.junit.Test;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

/**
 * @author mtodorov
 */
public class GAVUtilsTest
{

    @Test
    public void testGAVPatternMatching()
    {
        /**
         * com.foo:bar:jar:x64:1.2-SNAPSHOT:test
         */

        assertTrue("Failed to match GAV!", GAVUtils.matches("com.foo:bar:jar:jdk17:2.3.4", "*"));
        System.out.println("'com.foo:bar:jar:jdk17:2.3.4' matches '*'");

        assertTrue("Failed to match GAV!", GAVUtils.matches("com.foo:bar:jar:jdk17:2.3.4", "*:*:*"));
        System.out.println("'com.foo:bar:jar:jdk17:2.3.4' matches '*:*:*'");

        assertTrue("Failed to match GAV!", GAVUtils.matches("com.foo:bar:jar:jdk17:2.3.4", "*:*:*:jdk17:*"));
        System.out.println("'com.foo:bar:jar:jdk17:2.3.4' matches '*:*:*:jdk17:*'");

        assertTrue("Failed to match GAV!", GAVUtils.matches("com.foo:bar:jar:jdk17:2.3.4", "com.foo*"));
        System.out.println("'com.foo:bar:jar:jdk17:2.3.4' matches 'com.foo*'");

        assertTrue("Failed to match GAV!", GAVUtils.matches("com.foo:bar:jar:1.2.3", "com*foo:*:*:1.2.3"));
        System.out.println("'com.foo:bar:jar:jdk17:1.2.3' matches 'com*foo:*:*:1.2.3'");

        assertTrue("Failed to match GAV!", GAVUtils.matches("com.foo.blah:dot:jar:1.2.3", "com.foo*:*:*:1.2.3"));
        System.out.println("'com.foo.blah:dot:jar:1.2.3' matches 'com.foo*:*:*:1.2.3'");

        assertTrue("Failed to match GAV!", GAVUtils.matches("com.foo.blah:dot:jar:1.2.3", "*foo*:*:*:1.2.3"));
        System.out.println("'com.foo.blah:dot:jar:1.2.3' matches '*foo*:*:*:1.2.3'");

        assertTrue("Failed to match GAV!", GAVUtils.matches("com.foo:bar:jar:2.3.4", "*:bar:*"));
        System.out.println("'com.foo:bar:jar:2.3.4' matches '*:bar:*'");

        assertFalse("Failed to match GAV!", GAVUtils.matches("com.foo:bar:jar:2.3.4", "*:bar:jar:2.0"));
        System.out.println("'com.foo:bar:jar:2.3.4' does not match '*:bar:jar:2.0'.");

        assertFalse("Failed to match GAV!", GAVUtils.matches("com.foo:bar:2.3.4:jar:jdk17", "*:*:*:*:jdk12"));
        System.out.println("'com.foo:bar:2.3.4:jar:jdk17' does not match '*:*:*:*:jdk12'.");

        assertFalse("Failed to match GAV!", GAVUtils.matches("com.foo:bar:jar:5.4-SNAPSHOT:jar", "*:*:*:5.4"));
        System.out.println("'com.foo:bar:jar:5.4-SNAPSHOT' does not match '*:*:*:5.4'.");

        assertTrue("Failed to match GAV!", GAVUtils.matches("com.foo:bar:jar:5.4-SNAPSHOT:jar", "*:*:*:5.4*"));
        System.out.println("'com.foo:bar:jar:5.4-SNAPSHOT' matches '*:*:*:5.4*'.");

        try
        {
            GAVUtils.matches("com.foo:bar:2.3.4:jar:jdk17", "*:*:*:*:*:*");
            fail();
        }
        catch (IllegalArgumentException e)
        {
            System.out.println("'com.foo:bar:2.3.4:jar:jdk17' does not match '*:*:*:*:*:*' due to incorrect components length.");
            // This is expected.
        }

        Artifact artifact1 = createArtifact("com.foo", "bar", "1.2.3", "jar", null);
        Artifact artifact2 = createArtifact("com.foo", "bar", "1.2-SNAPSHOT", "jar", "x64");


        // System.out.println(artifact1.toString());
        assertTrue("Failed to match GAV!", GAVUtils.matches(artifact1.toString(), "*foo*:*:*:1.2.3"));
        System.out.println("'com.foo:bar:jar:1.2.3' matches '*foo*:*:*:1.2.3'");

        // System.out.println(artifact2.toString());
        assertTrue("Failed to match GAV!", GAVUtils.matches(artifact2.toString(), "*:bar:*"));
        System.out.println("'com.foo:bar:1.2-SNAPSHOT' matches '*:bar:*'");
    }

    private Artifact createArtifact(String groupId,
                                    String artifactId,
                                    String version,
                                    String type,
                                    String classifier)
    {
        DefaultArtifactHandler artifactHandler = new DefaultArtifactHandler("jar");

        return new DefaultArtifact(groupId, artifactId, version, "test", type, classifier, artifactHandler);
    }

}
