package org.carlspring.maven.commons.io.filters;

/**
 * Copyright 2013 Martin Todorov.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileFilter;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author mtodorov
 */
public class DirectoryFilter
        implements FileFilter
{

    private Set<String> includes = new LinkedHashSet<String>();

    private Set<String> excludes = new LinkedHashSet<String>();


    @Override
    public boolean accept(File file)
    {
        return file.isDirectory() && isIncluded(file.getName()) && !isExcluded(file.getName());
    }

    public boolean isIncluded(String dirName)
    {
        return includes == null || includes.isEmpty() || containsMatches(dirName, includes);
    }

    public boolean isExcluded(String dirName)
    {
        return !(excludes == null || excludes.isEmpty()) && containsMatches(dirName, excludes);
    }

    private boolean containsMatches(String dirName,
                                    Set<String> includes)
    {
        boolean matches = false;
        for (String include : includes)
        {
            if (include.contains("*") || include.contains("?"))
            {
                if (FilenameUtils.wildcardMatch(dirName, include))
                {
                    matches = true;
                    break;
                }
            }
            else
            {
                if (include.equals(dirName))
                {
                    matches = true;
                    break;
                }
            }
        }

        return matches;
    }

    public Set<String> getIncludes()
    {
        return includes;
    }

    public void setIncludes(Set<String> includes)
    {
        this.includes = includes;
    }

    public void addInclude(String include)
    {
        includes.add(include);
    }

    public void removeInclude(String include)
    {
        includes.remove(include);
    }

    public Set<String> getExcludes()
    {
        return excludes;
    }

    public void setExcludes(Set<String> excludes)
    {
        this.excludes = excludes;
    }

    public void addExclude(String exclude)
    {
        includes.add(exclude);
    }

    public void removeExclude(String exclude)
    {
        includes.remove(exclude);
    }

}
