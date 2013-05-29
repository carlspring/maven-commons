package org.carlspring.maven.commons.util;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.model.Dependency;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.carlspring.maven.commons.util.ArtifactUtils.convertDependencyToArtifact;
import static org.carlspring.maven.commons.util.ArtifactUtils.getPathToArtifact;

/**
 * @author mtodorov
 */
public class DependencyUtils
{

    public static String getPomForDependency(Dependency dependency,
                                             ArtifactRepository localRepository)
    {
        Artifact artifact = convertDependencyToArtifact(dependency);

        return getPomForArtifact(artifact, localRepository);
    }

    public static String getPomForArtifact(Artifact artifact,
                                           ArtifactRepository localRepository)
    {
        File localArtifactDir = new File(localRepository.getBasedir(),
                                         localRepository.pathOf(artifact)).getParentFile();

        return new File(localArtifactDir,
                        artifact.getArtifactId() + "-" + artifact.getVersion() + ".pom").getAbsolutePath();
    }

    public static String getPathToDependency(org.apache.maven.model.Dependency dependency,
                                             ArtifactRepository localRepository)
    {
        return getPathToArtifact(convertDependencyToArtifact(dependency), localRepository);
    }

    public static Dependency convertArtifactToDependency(Artifact artifact)
    {
        Dependency dependency = new Dependency();

        dependency.setGroupId(artifact.getGroupId());
        dependency.setArtifactId(artifact.getArtifactId());
        dependency.setVersion(artifact.getVersion());
        dependency.setScope(artifact.getScope());
        dependency.setClassifier(artifact.getClassifier());
        dependency.setType(artifact.getType());

        return dependency;
    }

    public static List<Dependency> convertToArtifactsList(List<Artifact> artifacts)
    {
        List<Dependency> dependencies = new ArrayList<Dependency>();

        for (Artifact artifact : artifacts)
        {
            dependencies.add(convertArtifactToDependency(artifact));
        }

        return dependencies;
    }

}
