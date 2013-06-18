package org.carlspring.maven.commons.util;

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

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.handler.DefaultArtifactHandler;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.model.Dependency;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author mtodorov
 */
public class ArtifactUtils
{


    public static boolean isMetadata(String path)
    {
        return path.endsWith(".pom") || path.endsWith(".xml");
    }

    public static boolean isChecksum(String path)
    {
        return path.endsWith(".sha1") || path.endsWith(".md5");
    }

    public static boolean isArtifact(String path)
    {
        return !isMetadata(path) && !isChecksum(path);
    }

    public static Artifact convertPathToArtifact(String path)
    {
        String groupId = "";
        String version;
        String scope = "compile";
        String classifier = "";
        String type = path.substring(path.lastIndexOf(".") + 1, path.length());

        String[] groupIdElements = path.split("/");
        for (int i = 0; i < (groupIdElements.length - 3); i++)
        {
            if (groupId.length() == 0)
            {
                groupId += groupIdElements[i];
            }
            else
            {
                groupId += "." + groupIdElements[i];
            }
        }

        String[] split = path.substring(path.lastIndexOf("/") + 1, path.length() - 4).split("-");

   		/* Parse the artifactId */
        StringBuilder artifactId = new StringBuilder();
        int i = 0;
        for (; i < split.length; i++)
        {
            String token = split[i];
            try
            {
                Integer.parseInt(token.substring(0, 1));
                break;
            }
            catch (NumberFormatException e)
            {
                // This is okay, as we still haven't reached the version.
            }

            if (artifactId.length() > 0)
            {
                artifactId.append("-");
            }

            artifactId.append(token);
        }

   		/* Parse the artifactId */

   		/* Parse the version */

        version = split[i];

        // If the version starts with a number, all is fine.
        Integer.parseInt(version.substring(0, 1));

        // TODO: Not checking for number format exception

        i++;

        // Check if the version is a SNAPSHOT and append it, if it is.
        if ((i < split.length) && split[i].equals("SNAPSHOT"))
        {
            version += "-" + split[i];
            i++;
        }

   		/* Parse the version */

   		/* Parse the classifier, if any */
        if (i == (split.length - 1))
        {
            classifier = split[i];
        }
        /* Parse the classifier, if any */

        return new DefaultArtifact(groupId,
                                   artifactId.toString(),
                                   VersionRange.createFromVersion(version),
                                   scope,
                                   type,
                                   classifier,
                                   new DefaultArtifactHandler(type));
    }

    public static String convertArtifactToPath(Artifact artifact)
    {
        String path = "";

        path += artifact.getGroupId().replaceAll("\\.", "/") + "/";
        path += artifact.getArtifactId() + "/";
        path += artifact.getVersion() + "/";
        path += artifact.getArtifactId() + "-";
        path += artifact.getVersion();
        path += artifact.getClassifier() != null && !artifact.getClassifier().equals("") ?
                "-" + artifact.getClassifier() : "";
        path += "." + artifact.getType();

        return path;
    }

    public static String getPathToArtifact(Artifact artifact,
                                           ArtifactRepository localRepository)
    {
        File localArtifactDir = new File(localRepository.getBasedir(),
                                         localRepository.pathOf(artifact)).getParentFile();

        return new File(localArtifactDir,
                        artifact.getArtifactId() + "-" +
                        artifact.getVersion() + (artifact.getClassifier() != null ? "-" + artifact.getClassifier() : "") +
                        "." + artifact.getType()).getAbsolutePath();
    }

    public static org.sonatype.aether.artifact.Artifact convertToSonatypeArtifact(Artifact artifact)
    {
        return new org.sonatype.aether.util.artifact.DefaultArtifact(artifact.getGroupId(),
                                                                     artifact.getArtifactId(),
                                                                     artifact.getClassifier(),
                                                                     artifact.getType(),
                                                                     artifact.getVersion());
    }

    public static Artifact convertSonatypeArtifactToMavenArtifact(org.sonatype.aether.artifact.Artifact artifact)
    {
        return new DefaultArtifact(artifact.getGroupId(),
                                   artifact.getArtifactId(),
                                   artifact.getVersion(),
                                   "compile",
                                   artifact.getExtension(),
                                   artifact.getClassifier(),
                                   new DefaultArtifactHandler(artifact.getExtension()));
    }

    public static Artifact convertDependencyToArtifact(Dependency dependency)
    {
        return new DefaultArtifact(dependency.getGroupId(),
                                   dependency.getArtifactId(),
                                   VersionRange.createFromVersion(dependency.getVersion()),
                                   dependency.getScope(),
                                   dependency.getType(),
                                   dependency.getClassifier(),
                                   new DefaultArtifactHandler(dependency.getType()));
    }

    public static List<Artifact> convertToArtifactsList(List<Dependency> dependencies)
    {
        List<Artifact> artifacts = new ArrayList<Artifact>();

        for (Dependency dependency : dependencies)
        {
            artifacts.add(convertDependencyToArtifact(dependency));
        }

        return artifacts;
    }

    public static List<org.sonatype.aether.artifact.Artifact> convertToSonatypeArtifacts(Collection<Artifact> mavenArtifacts)
    {
        List<org.sonatype.aether.artifact.Artifact> artifacts = new ArrayList<org.sonatype.aether.artifact.Artifact>();

        for (Artifact artifact : mavenArtifacts)
        {
            artifacts.add(convertToSonatypeArtifact(artifact));
        }

        return artifacts;
    }

    public static List<Artifact> convertToMavenArtifacts(Collection<org.sonatype.aether.artifact.Artifact> mavenArtifacts)
    {
        List<Artifact> artifacts = new ArrayList<Artifact>();

        for (org.sonatype.aether.artifact.Artifact artifact : mavenArtifacts)
        {
            artifacts.add(convertSonatypeArtifactToMavenArtifact(artifact));
        }

        return artifacts;
    }

    /**
     * Converts groupId:artifactId:type:version:scope to an Artifact.
     *
     * @param gav
     * @return
     */
    public static Artifact getArtifactFromGAV(String gav)
    {
        return getArtifactFromGAVTC(gav);
    }

    /**
     * Converts groupId:artifactId:type:version:scope to an Artifact.
     *
     * @param gavtc
     * @return
     */
    public static Artifact getArtifactFromGAVTC(String gavtc)
    {
        String[] gavComponents = gavtc.split(":");

        String groupId = gavComponents[0];
        String artifactId = gavComponents[1];
        String version = gavComponents[2];
        String type = gavComponents.length < 4 ? "jar": gavComponents[3];
        String classifier = gavComponents.length < 5 ? null : gavComponents[4];

        return new DefaultArtifact(groupId,
                                   artifactId,
                                   version,
                                   "compile",
                                   type,
                                   classifier,
                                   new DefaultArtifactHandler(type));
    }

    /**
     * Converts groupId:artifactId:type:version:scope to an Artifact.
     *
     * @param gav
     * @return
     */
    public static Artifact getPOMArtifactFromGAV(String gav)
    {
        String[] gavComponents = gav.split(":");

        String groupId = gavComponents[0];
        String artifactId = gavComponents[1];
        String version = gavComponents[2];

        return new DefaultArtifact(groupId,
                                   artifactId,
                                   version,
                                   "compile",
                                   "pom",
                                   null,
                                   new DefaultArtifactHandler("pom"));
    }

    public static Artifact getPOMArtifact(Artifact artifact)
    {
        return new DefaultArtifact(artifact.getGroupId(),
                                   artifact.getArtifactId(),
                                   artifact.getVersion(),
                                   "compile",
                                   "pom",
                                   null,
                                   new DefaultArtifactHandler("pom"));
    }

    public static File getPOMFile(Artifact artifact, ArtifactRepository localRepository)
    {
        return new File(getPathToArtifact(getPOMArtifact(artifact), localRepository));
    }

}
