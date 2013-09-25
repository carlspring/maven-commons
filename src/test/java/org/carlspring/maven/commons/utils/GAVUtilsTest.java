package org.carlspring.maven.commons.utils;

import org.carlspring.maven.commons.util.GAVUtils;

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
        assertTrue("Failed to match GAV!", GAVUtils.matches("com.foo:bar:2.3.4:jar:jdk17", "*"));
        System.out.println("'com.foo:bar:2.3.4:jar:jdk17' matches '*'");
        assertTrue("Failed to match GAV!", GAVUtils.matches("com.foo:bar:2.3.4:jar:jdk17", "*:*:*"));
        System.out.println("'com.foo:bar:2.3.4:jar:jdk17' matches '*:*:*'");
        assertTrue("Failed to match GAV!", GAVUtils.matches("com.foo:bar:2.3.4:jar:jdk17", "*:*:*:*:jdk17"));
        System.out.println("'com.foo:bar:2.3.4:jar:jdk17' matches '*:*:*:*:jdk17'");
        assertTrue("Failed to match GAV!", GAVUtils.matches("com.foo:bar:2.3.4:jar:jdk17", "com.foo*"));
        System.out.println("'com.foo:bar:2.3.4:jar:jdk17' matches 'com.foo*'");
        assertTrue("Failed to match GAV!", GAVUtils.matches("com.foo:bar:1.2.3", "com*foo:*:1.2.3"));
        System.out.println("'com.foo:bar:2.3.4:jar:jdk17' matches 'com*foo:*:1.2.3'");
        assertTrue("Failed to match GAV!", GAVUtils.matches("com.foo.blah:dot:1.2.3", "com.foo*:*:1.2.3"));
        System.out.println("'com.foo.blah:dot:1.2.3' matches 'com.foo*:*:1.2.3'");
        assertTrue("Failed to match GAV!", GAVUtils.matches("com.foo.blah:dot:1.2.3", "*foo*:*:1.2.3"));
        System.out.println("'com.foo.blah:dot:1.2.3' matches '*foo*:*:1.2.3'");
        assertTrue("Failed to match GAV!", GAVUtils.matches("com.foo:bar:2.3.4", "*:bar:*"));
        System.out.println("'com.foo:bar:2.3.4' matches '*:bar:*'");

        assertFalse("Failed to match GAV!", GAVUtils.matches("com.foo:bar:2.3.4", "*:bar:2.0"));
        System.out.println("'com.foo:bar:2.3.4' does not match '*:bar:2.0'.");
        assertFalse("Failed to match GAV!", GAVUtils.matches("com.foo:bar:2.3.4:jar:jdk17", "*:*:*:*:jdk12"));
        System.out.println("'com.foo:bar:2.3.4:jar:jdk17' does not match '*:*:*:*:jdk12'.");
        assertFalse("Failed to match GAV!", GAVUtils.matches("com.foo:bar:5.4-SNAPSHOT:jar", "*:*:5.4"));
        System.out.println("'com.foo:bar:5.4-SNAPSHOT:jar' does not match '*:*:5.4'.");

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
    }

}
