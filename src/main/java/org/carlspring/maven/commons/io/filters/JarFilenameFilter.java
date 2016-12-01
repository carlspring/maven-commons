package org.carlspring.maven.commons.io.filters;

import java.io.File;
import java.io.FilenameFilter;

/**
 * @author Kate Novik.
 */
public class JarFilenameFilter
        implements FilenameFilter
{

    @Override
    public boolean accept(File dir, String name)
    {
        return name.endsWith(".jar");
    }

}
