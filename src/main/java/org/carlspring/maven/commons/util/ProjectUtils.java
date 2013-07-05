package org.carlspring.maven.commons.util;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.*;

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

    public static MavenProject getProjectForArtifact(Artifact artifact,
                                                     MavenSession session,
                                                     ProjectBuilder projectBuilder)
            throws ProjectBuildingException
    {
        final DefaultProjectBuildingRequest request = new DefaultProjectBuildingRequest();
        request.setRepositorySession(session.getRepositorySession());

        final ProjectBuildingResult result = projectBuilder.build(artifact, request);

        return result.getProject();
    }

}
