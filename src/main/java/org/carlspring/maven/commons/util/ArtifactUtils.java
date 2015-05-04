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

import org.apache.commons.io.FilenameUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.handler.DefaultArtifactHandler;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.model.Dependency;
import org.carlspring.maven.commons.DetachedArtifact;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author mtodorov
 */
public class ArtifactUtils
{

    private static final Pattern versionPattern = Pattern.compile("^(?i)(\\d+((\\.\\d+)+)?)(((?!-\\d{8})-\\w+)*)?(-(\\d{8})((.|-)(\\d+)((.|-)(\\d+))?)?)?$");
    private static final Pattern BASE_VERSION_PATTERN = Pattern.compile("^(\\d+((\\.\\d+)+)?)(((?!-\\d{8})-\\w+)*)?");
    private static final Pattern TIMESTAMP_BUILDNUMBER_PATTERN = Pattern.compile("((\\d{8})(.|-)(\\d+))((.|-)(\\d+))?$");

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
        String artifactId = null;
        String version = null;
        StringBuilder classifier = new StringBuilder();
        String type = null;

        String[] pathElements = path.split("/");

        if (pathIsCompleteGAV(path))
        {
            for (int i = 0; i < (pathElements.length - 3); i++)
            {
                groupId += groupId.length() == 0 ? pathElements[i] : "." + pathElements[i];
            }

            artifactId = pathElements[pathElements.length - 3];

            String fileName = FilenameUtils.getBaseName(path);

            String versionPath = Paths.get(path).toFile().getParentFile().getName();
            if (versionPath.toLowerCase().contains("snapshot"))
            {
                versionPath = versionPath.replaceAll("(?i)-snapshot", "");
            }

            // Store version part 1
            version = versionPath;

            String split = fileName.substring((artifactId.length() + versionPath.length() + 1), fileName.length());

            if (!split.isEmpty())
            {
                // remove leading dash
                split = split.substring(1, split.length());

                String[] tokens = split.split("-");

                for (String token : tokens)
                {
                    String lowerToken = token.toLowerCase();
                    if (lowerToken.contains("snapshot"))
                    {
                        version += "-SNAPSHOT";
                    }
                    else if (Character.isDigit(lowerToken.charAt(0)) && Character.isDigit(lowerToken.charAt(lowerToken.length() - 1)))
                    {
                        version += "-" + token;
                    }
                    else
                    {
                        if (classifier.length() > 0)
                        {
                            classifier.append("-");
                        }

                        classifier.append(token);
                    }
                }
            }

            type = path.substring(path.lastIndexOf(".") + 1, path.length());
        }
        else
        {
            if (pathElements.length >= 3)
            {
                String aId = pathElements[pathElements.length - 3];
                String v = pathElements[pathElements.length - 2];
                String fileName  = pathElements[pathElements.length - 1];

                if (fileName.contains(aId) && fileName.contains(v))
                {
                    // There seems to be sufficient information from which to extract the artifactId
                    artifactId = aId;
                    version = v;
                }
                else
                {
                    // This is apparently just g:a
                    groupId = "";
                    for (int i = 0; i <= (pathElements.length - 2); i++)
                    {
                        groupId += groupId.length() == 0 ? pathElements[i] : "." + pathElements[i];
                    }

                    artifactId = pathElements[pathElements.length - 1];
                }
            }
            else
            {
                groupId = pathElements[0];
                artifactId = pathElements.length == 2 ?               // This is apparently just g:a
                             pathElements[1] :
                             null;                                    // There is insufficient information from which to extract the artifactId
            }
        }

        return new DetachedArtifact(groupId,
                                    artifactId,
                                    version,
                                    type,
                                    (classifier.length() > 0 ? classifier.toString() : null));
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

            if (fileName.startsWith(artifactId) && fileName.contains(version))
            {
                return true;
            }
        }

        return false;
    }

    public static String convertArtifactToPath(Artifact artifact)
    {
        String path = "";

        path += artifact.getGroupId().replaceAll("\\.", "/") + "/";
        path += artifact.getArtifactId();

        if (artifact.getVersion() != null)
        {
            path += "/";

            if (isReleaseVersion(artifact.getVersion()))
            {
                path += artifact.getVersion() + "/";
            }
            else
            {
                path += getSnapshotBaseVersion(artifact.getVersion()) + "/";
            }

            path += artifact.getArtifactId() + "-";
            path += artifact.getVersion();
            path += artifact.getClassifier() != null &&
                    !artifact.getClassifier().equals("") &&
                    !artifact.getClassifier().equals("null") ?
                    "-" + artifact.getClassifier() : "";
            path += artifact.getType() != null ? "." + artifact.getType() : "";
        }
        else
        {

        }


        return path;
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
        String type = gavComponents.length < 4 ? "jar": gavComponents[3];
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

}