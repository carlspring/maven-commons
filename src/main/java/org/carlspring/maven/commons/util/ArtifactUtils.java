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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.apache.maven.artifact.Artifact.SNAPSHOT_VERSION;

/**
 * @author mtodorov
 */
public class ArtifactUtils
{

    private final static Logger logger = LoggerFactory.getLogger(ArtifactUtils.class);

    private static final Pattern SNAPSHOT_VERSION_PATTERN = Pattern.compile("(.*)-(([0-9]{8}.[0-9]{6})(.([0-9]+))?)$" );

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

            if(StringUtils.containsIgnoreCase(version, "-snapshot")) {
                version = version.substring(0, version.length() - 10);
            }

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
        return convertArtifactToPath(artifact, "/");
    }

    public static String convertArtifactToPath(Artifact artifact, String separator)
    {
        StringJoiner path = new StringJoiner(separator);

        path.add(StringUtils.replace(artifact.getGroupId(), ".", separator));
        path.add(artifact.getArtifactId());

        if(artifact.getBaseVersion() != null)
        {
            path.add(artifact.getBaseVersion());
            path.add(getArtifactFileName(artifact));
        }
        // Fallback.
        else if(artifact.getVersion() != null)
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
                        artifact.getVersion() +
                        (artifact.getClassifier() != null ? "-" + artifact.getClassifier() : "") +
                        "." + artifact.getType()).getAbsolutePath();
    }

    public static String getPathToArtifact(Artifact artifact,
                                           String localRepositoryDir)
    {
        File localArtifactDir = new File(localRepositoryDir);

        return new File(localArtifactDir,
                        artifact.getArtifactId() + "-" +
                        artifact.getVersion() +
                        (artifact.getClassifier() != null ? "-" + artifact.getClassifier() : "") +
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
        logger.debug("Parsing string coordinates: {}", gavtc);

        String[] gavComponents = gavtc.split(":");

        String groupId = gavComponents[0];
        String artifactId = gavComponents[1];
        String version = gavComponents.length >= 3 ? gavComponents[2] : null;
        String type = gavComponents.length < 4 ? "jar" : gavComponents[3];
        String classifier = gavComponents.length < 5 ? null : gavComponents[4];
        String baseVersion = version;

        if (version != null)
        {
            Matcher m = SNAPSHOT_VERSION_PATTERN.matcher(version);
            if (m.matches())
            {
                baseVersion = m.group(1) + "-" + SNAPSHOT_VERSION;
            }
            else
            {
                // corner case: testArtifactToPathWithClassifierAndTimestampedSnapshot
                int snapshotIndex = StringUtils.indexOfIgnoreCase(version, "-snapshot");
                if (snapshotIndex > -1 && !StringUtils.endsWithIgnoreCase(version, "-snapshot"))
                {
                    String baseVersionWithoutSnapshot = version.substring(0, snapshotIndex);
                    baseVersion = baseVersionWithoutSnapshot + "-SNAPSHOT";

                    String snapshotTimestamp = version.substring(snapshotIndex + 10);

                    if (!StringUtils.isBlank(snapshotTimestamp))
                    {
                        version = baseVersion + "-" + snapshotTimestamp;
                    }
                }
            }
        }

        Artifact artifact = new DetachedArtifact(groupId, artifactId, version, type, classifier);
        artifact.setBaseVersion(baseVersion);

        logger.debug("Coordinates: {} [groupId: {}; artifactId: {}, version: {}, baseVersion: {}, classifier: {}, type: {}]",
                     artifact, artifact.getGroupId(), artifact.getArtifactId(), artifact.getVersion(),
                     artifact.getBaseVersion(), artifact.getClassifier(), artifact.getType());

        return artifact;
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
        return StringUtils.containsIgnoreCase(version, "-snapshot") || SNAPSHOT_VERSION_PATTERN.matcher(version).matches();
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
        Matcher matcher = SNAPSHOT_VERSION_PATTERN.matcher(version);

        if (matcher.matches())
        {
            String baseVersion = matcher.group(1);

            return appendSnapshotSuffix ? baseVersion + "-SNAPSHOT" : baseVersion;
        }

        return version;
    }

    public static String getSnapshotTimestamp(String version)
    {
        String timestamp = null;

        if (isSnapshot(version))
        {
            Matcher snapshotMatcher = SNAPSHOT_VERSION_PATTERN.matcher(version);

            if (snapshotMatcher.matches())
            {
                timestamp = snapshotMatcher.group(3);
            }

        }

        return timestamp;
    }

    public static String getSnapshotBuildNumber(String version)
    {
        String buildNumber = null;

        if (isSnapshot(version))
        {
            Matcher snapshotMatcher = SNAPSHOT_VERSION_PATTERN.matcher(version);

            if (snapshotMatcher.matches())
            {
                buildNumber = snapshotMatcher.group(5);
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