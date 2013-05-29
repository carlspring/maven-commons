package org.carlspring.maven.commons.io.filters;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author mtodorov
 */
public class DirectoryFilterTest
{

    public static final String TEST_DIR = "target/test-resources";

    Set<String> includes = new LinkedHashSet<String>();

    Set<String> excludes = new LinkedHashSet<String>();


    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Before
    public void setUp()
            throws Exception
    {
        includes.add("*foo*");
        includes.add("*bar");
        includes.add("ab?");
        includes.add("abc*");
        includes.add("?cd*");

        excludes.add("?git");
        excludes.add(".svn");

        new File(TEST_DIR, "path/to/foo.is").mkdirs();
        new File(TEST_DIR, "path/to/my/bar").mkdirs();
        new File(TEST_DIR, "path/to/abc").mkdirs();
        new File(TEST_DIR, "path/to/abcdef").mkdirs();
        new File(TEST_DIR, "path/to/bcd").mkdirs();
        new File(TEST_DIR, "path/to/bcde").mkdirs();
        new File(TEST_DIR, "path/to/1234").mkdirs();
        new File(TEST_DIR, "path/to/54321").mkdirs();

        new File(TEST_DIR, ".git").mkdirs();
        new File(TEST_DIR, ".svn").mkdirs();
    }

    @Test
    public void testDirectoryFilteringWithWildCard()
    {
        DirectoryFilter filter = new DirectoryFilter();
        filter.setIncludes(includes);
        filter.setExcludes(excludes);

        assertTrue(filter.accept(new File(TEST_DIR, "path/to/foo.is")));
        assertTrue(filter.accept(new File(TEST_DIR, "path/to/my/bar")));
        assertTrue(filter.accept(new File(TEST_DIR, "path/to/abc")));
        assertTrue(filter.accept(new File(TEST_DIR, "path/to/abcdef")));
        assertTrue(filter.accept(new File(TEST_DIR, "path/to/bcd")));
        assertTrue(filter.accept(new File(TEST_DIR, "path/to/bcde")));

        assertFalse(filter.accept(new File(TEST_DIR, ".git")));
        assertFalse(filter.accept(new File(TEST_DIR, ".svn")));
        assertFalse(filter.accept(new File(TEST_DIR, "path/to/1234")));
        assertFalse(filter.accept(new File(TEST_DIR, "path/to/54321")));
    }

}
