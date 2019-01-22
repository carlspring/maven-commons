package org.carlspring.maven.commons.util;

/**
 * Copyright 2013 Martin Todorov.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.carlspring.maven.commons.DetachedArtifact;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.handler.DefaultArtifactHandler;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.model.Dependency;
import org.apache.maven.project.artifact.PluginArtifact;

/**
 * @author mtodorov
 */
public class ArtifactUtils
{

    private static final Pattern versionPattern = Pattern.compile("^(?i)(\\d+((\\.\\d+)+)?)(((?!-\\d{8})-\\w+)*)?(-(\\d{8})((.|-)(\\d+)((.|-)(\\d+))?)?)?$");
    private static final Pattern BASE_VERSION_PATTERN = Pattern.compile("^(\\d+((\\.\\d+)+)?)(((?!-\\d{8})-\\w+)*)?");
    private static final Pattern TIMESTAMP_BUILDNUMBER_PATTERN = Pattern.compile("((\\d{8})(.|-)(\\d+))((.|-)(\\d+))?$");


    public static boolean isPom(String path)
    {
        return path.endsWith(".pom");
    }

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
        return new PathParser(path).getArtifact();
    }

    public static boolean pathIsCompleteGAV(String path)
    {
        String[] pathElements = path.replaceAll("\\\\", "/").split("/");
        if (pathElements.length >= 3)
        {
            String groupId = "";
            for (int i = 0; i < (pathElements.length - 3); i++)
            {
                groupId += groupId.length() == 0 ? pathElements[i] : "." + pathElements[i];
            }

            String artifactId = pathElements[pathElements.length - 3];
            String version = pathElements[pathElements.length - 2];
            version = isSnapshot(version) ? getSnapshotBaseVersion(version, false) : version;

            String fileName = pathElements[pathElements.length - 1];

            if (fileName.startsWith(artifactId) && fileName.contains(version) && fileName.contains("."))
            {
                return true;
            }
        }

        return false;
    }

    public static String convertArtifactToPath(Artifact artifact)
    {
        return convertArtifactToPath(artifact, File.separator);
    }

    public static String convertArtifactToPath(Artifact artifact, String separator)
    {
        StringJoiner path = new StringJoiner(separator);

        path.add(StringUtils.replace(artifact.getGroupId(), ".", separator));
        path.add(artifact.getArtifactId());

        if (artifact.getVersion() != null)
        {
            if (isReleaseVersion(artifact.getVersion()))
            {
                path.add(artifact.getVersion());
            }
            else
            {
                path.add(getSnapshotBaseVersion(artifact.getVersion()));
            }

            path.add(getArtifactFileName(artifact));
        }

        return path.toString();
    }

    public static String getArtifactFileName(Artifact artifact)
    {
        String path = "";

        path += artifact.getArtifactId() + "-";
        path += artifact.getVersion();
        path += artifact.getClassifier() != null &&
                !artifact.getClassifier().equals("") &&
                !artifact.getClassifier().equals("null") ?
                "-" + artifact.getClassifier() : "";
        if (artifact instanceof PluginArtifact)
        {
            path += ".jar";
        }
        else
        {
            path += artifact.getType() != null ? "." + artifact.getType() : ".jar";
        }

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

    public static String getPathToArtifact(Artifact artifact,
                                           String localRepositoryDir)
    {
        File localArtifactDir = new File(localRepositoryDir);

        return new File(localArtifactDir,
                        artifact.getArtifactId() + "-" +
                        artifact.getVersion() + (artifact.getClassifier() != null ? "-" + artifact.getClassifier() : "") +
                        "." + artifact.getType()).getAbsolutePath();
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
        String version = gavComponents.length >= 3 ? gavComponents[2] : null;
        String type = gavComponents.length < 4 ? "jar" : gavComponents[3];
        String classifier = gavComponents.length < 5 ? null : gavComponents[4];

        return new DetachedArtifact(groupId,
                                    artifactId,
                                    version,
                                    type,
                                    classifier);
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

        return new DetachedArtifact(groupId, artifactId, version, "pom");
    }

    public static Artifact getPOMArtifact(Artifact artifact)
    {
        return new DetachedArtifact(artifact.getGroupId(),
                                    artifact.getArtifactId(),
                                    artifact.getVersion(),
                                    "pom");
    }

    public static File getPOMFile(Artifact artifact, ArtifactRepository localRepository)
    {
        return new File(getPathToArtifact(getPOMArtifact(artifact), localRepository));
    }

    public static boolean exists(Artifact artifact, ArtifactRepository localRepository)
    {
        return new File(getPathToArtifact(artifact, localRepository)).exists();
    }

    public static boolean exists(String gav, ArtifactRepository localRepository)
    {
        Artifact artifact = getArtifactFromGAVTC(gav);
        return new File(getPathToArtifact(artifact, localRepository)).exists();
    }

    public static boolean isSnapshot(String version)
    {
        Matcher versionMatcher = versionPattern.matcher(version);

        if (versionMatcher.find())
        {
            String middle = versionMatcher.group(4);
            String timestamp = versionMatcher.group(7);

            if ((middle != null && !middle.isEmpty() && middle.toLowerCase().contains("snapshot")) || (timestamp != null && !timestamp.isEmpty()))
            {
                return true;
            }
        }

        return false;
    }

    public static boolean isReleaseVersion(String version)
    {
        return !isSnapshot(version);
    }

    public static String getSnapshotBaseVersion(String version)
    {
        return getSnapshotBaseVersion(version, true);
    }

    public static String getSnapshotBaseVersion(String version, boolean appendSnapshotSuffix)
    {
        Matcher matcher = BASE_VERSION_PATTERN.matcher(version);

        if (matcher.find())
        {
            String versionNumber = matcher.group(1);
            String versionEnding = matcher.group(4);

            String baseVersion = versionNumber;

            if (versionEnding != null && !versionEnding.isEmpty())
            {
                baseVersion += versionEnding.replaceAll("(?i)-snapshot", "");
            }

            return appendSnapshotSuffix ? baseVersion + "-SNAPSHOT" : baseVersion;
        }

        return version;
    }

    public static String getSnapshotTimestamp(String version)
    {
        String timestamp = null;

        if(isSnapshot(version))
        {
            Matcher snapshotMatcher = TIMESTAMP_BUILDNUMBER_PATTERN.matcher(version);

            if (snapshotMatcher.find())
            {
                timestamp = snapshotMatcher.group(1);
            }

        }

        return timestamp;
    }

    public static String getSnapshotBuildNumber(String version)
    {
        String buildNumber = null;

        if(isSnapshot(version))
        {
            Matcher snapshotMatcher = TIMESTAMP_BUILDNUMBER_PATTERN.matcher(version);

            if (snapshotMatcher.find())
            {
                buildNumber = snapshotMatcher.group(7);
            }

        }

        return buildNumber;
    }

    public static String getGroupLevelMetadataPath(Artifact artifact)
    {
        String artifactPath = convertArtifactToPath(artifact);
        // com/foo/bar/1.2.3/bar-1.2.3.jar --> com/foo/

        String path = artifactPath.substring(0, artifactPath.lastIndexOf('/'));
        path = path.substring(0, path.lastIndexOf('/'));
        path = path.substring(0, path.lastIndexOf('/'));

        return path + "/maven-metadata.xml";
    }

    public static String getArtifactLevelMetadataPath(Artifact artifact)
    {
        String artifactPath = convertArtifactToPath(artifact);
        // com/foo/bar/1.2.3/bar-1.2.3.jar --> com/foo/bar/

        String path = artifactPath.substring(0, artifactPath.lastIndexOf('/'));
        path = path.substring(0, path.lastIndexOf('/'));

        return path + "/maven-metadata.xml";
    }

    public static String getVersionLevelMetadataPath(Artifact artifact)
    {
        String artifactPath = convertArtifactToPath(artifact);
        // com/foo/bar/1.2.3-SNAPSHOT/bar-1.2.3-SNAPSHOT.jar --> com/foo/bar/1.2.3-SNAPSHOT

        String path = artifactPath.substring(0, artifactPath.lastIndexOf('/'));

        return path + "/maven-metadata.xml";
    }

}