package org.carlspring.maven.commons.util;

import org.apache.maven.project.MavenProject;

/**
 * @author mtodorov
 */
public class ProjectUtils
{

    public static boolean isPomless(MavenProject project)
    {
        return project.getArtifact().getGroupId().equals("org.apache.maven") &&
               project.getArtifact().getArtifactId().equals("standalone-pom");
    }

}
