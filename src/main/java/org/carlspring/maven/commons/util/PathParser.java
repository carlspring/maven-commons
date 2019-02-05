package org.carlspring.maven.commons.util;

import org.carlspring.maven.commons.DetachedArtifact;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.apache.maven.artifact.Artifact.LATEST_VERSION;
import static org.apache.maven.artifact.Artifact.SNAPSHOT_VERSION;

public class PathParser
{

    private final static Pattern VERSION_PATTERN = Pattern.compile(".*(\\d+\\.\\d+).*");

    private final static Logger logger = LoggerFactory.getLogger(PathParser.class);

    /**
     * This field is meant to be used for secondary level file extansions.
     * By convention the file extensions are supposed to be the part where the last dot occurred,
     * however there are exceptions to this rule such as `tar.gz`. We will not be supporting
     * all of these cases - only the most used ones.
     */
    private static final List<String> secondaryExtensions = Arrays.asList("tar");

    private List<String> segments = new ArrayList<>();

    private DetachedArtifact artifact;

    public PathParser(String path)
    {
        this.parsePath(path);
    }

    public DetachedArtifact getArtifact()
    {
        return artifact;
    }

    public List<String> getSegments()
    {
        return segments;
    }

    /**
     * Attempts to parse the provided path and then create a DetachedArtifact.
     *
     * @param path
     */
    private void parsePath(String path)
    {
        logger.debug("Parsing path: {}", path);

        segments = getSegments(path);

        String groupId = null;
        String artifactId = null;
        String classifier = null;
        String version = null; // This should be the actual version (with the timestamp when this is a snapshot)
        String baseVersion = null; // This should be either the release version or X.Y.Z-SNAPSHOT without a timestamp.
        String type = null;

        if (segments.size() < 3)
        {
            // We assume this is a "g:a".
            if (segments.size() == 2)
            {
                groupId = segments.get(0);
                artifactId = segments.get(1);
            }
            else if (segments.size() == 1)
            {
                groupId = segments.get(0);
            }
            else
            {
                // TODO: Return an exception one day - for now we're returning an error in the logs.
            }
        }
        else
        {
            String filename = segments.get(segments.size() - 1);

            // We're attempting to take the version from the path.
            // In a release, this would normally be something like 1.2.3(-alpha)
            // In a snapshot, this would normally be something like 1.2.3-SNAPSHOT
            version = segments.get(segments.size() - 2);
            baseVersion = version; // we use the path version for `baseVersion`
            String versionStripped = StringUtils.replaceIgnoreCase(version, "-snapshot", "");

            // corner cases - testConvertPathToArtifact#artifact16 and testConvertPathToArtifact#artifact30
            if (StringUtils.endsWithIgnoreCase(version, "-snapshot") ||
                (StringUtils.containsIgnoreCase(filename, versionStripped) &&
                 VERSION_PATTERN.matcher(version).matches()) ||
                version.matches("\\d+")
            )
            {
                groupId = segments.get(0);
                artifactId = segments.get(1);
                type = getFileExtension(filename);

                // snapshot version
                if (!version.equals(versionStripped))
                {
                    String extractedVersion = extractSnapshotVersion(filename, baseVersion);

                    // We need this so that if we can't extract the version from the filename (i.e. maven-metadata.xml)
                    // we still end up with a version.
                    if (extractedVersion != null)
                    {
                        version = extractedVersion;
                    }
                }

                if (!version.equals(""))
                {
                    classifier = extractClassifier(filename, version, type);
                }
            }
            else
            {
                // Maybe the path ends with the version?
                version = segments.get(segments.size() - 1);
                baseVersion = version;

                // corner case - testConvertPathToArtifact#artifact31
                if (VERSION_PATTERN.matcher(version).matches() || version.matches("\\d+"))
                {
                    groupId = segments.stream()
                                      .limit(segments.size() - 2)
                                      .map(Object::toString)
                                      .collect(Collectors.joining("."));
                    artifactId = segments.get(segments.size() - 2);
                }
                // Fallback, this is a G:A
                else
                {
                    groupId = segments.stream()
                                      .limit(segments.size() - 1)
                                      .map(Object::toString)
                                      .collect(Collectors.joining("."));
                    artifactId = segments.get(segments.size() - 1);
                    version = null;
                    baseVersion = null;
                }
            }


            if (StringUtils.equalsIgnoreCase(filename, "maven-metadata.xml"))
            {
                artifactId = filename;
            }
        }

        if (groupId == null || groupId.length() == 0)
        {
            logger.error("Insufficient path segments - DetachedArtifact will be empty! {}", path);
        }

        artifact = new DetachedArtifact(groupId, artifactId, version, type, classifier);
        artifact.setBaseVersion(baseVersion);

        logger.debug("Coordinates: {} [groupId: {}; artifactId: {}, version: {}, baseVersion: {}, classifier: {}, type: {}]",
                     artifact, artifact.getGroupId(), artifact.getArtifactId(), artifact.getVersion(),
                     artifact.getBaseVersion(), artifact.getClassifier(), artifact.getType());
    }

    private List<String> getSegments(String path)
    {
        // Windows style paths
        if (path.contains("\\\\") || path.contains("\\"))
        {
            path = StringUtils.replaceEach(path, new String[] {"\\\\", "\\"}, new String[]{"/", "/"});
        }

        // remove beginning and trailing slashes.
        path = StringUtils.removeEnd(path, "/");
        path = StringUtils.removeStart(path, "/");

        int start;
        int end = path.length();
        int foundSeparators = 0;

        List<String> segments = new ArrayList<>();

        // Start processing the string backwards
        for (int i = path.length() - 1, n = 0; i > n && foundSeparators < 3; i--)
        {
            char c = path.charAt(i);

            if (c == '/')
            {
                foundSeparators++;
                start = i + 1;
                segments.add(path.substring(start, end));
                end = i;
            }
        }

        // add remaining part
        if (end > 0)
        {
            segments.add(path.substring(0, end).replaceAll("/", "."));
        }

        // Reverse back to normal order.
        Collections.reverse(segments);

        return segments;
    }

    /**
     * @param filename         the full filename
     * @param baseVersion      the base version - this will be whatever the version in the path has
     * @return
     */
    private String extractSnapshotVersion(String filename,
                                          String baseVersion)
    {
        int baseVersionStartPosition;
        int baseVersionEndPosition;

        // corner case: testArtifactToPathWithClassifierAndTimestampedSnapshot
        if(StringUtils.containsIgnoreCase(filename, baseVersion))
        {
            baseVersionStartPosition = filename.indexOf(baseVersion);
            baseVersionEndPosition = baseVersionStartPosition + baseVersion.length();
        }
        else
        {
            String versionStripped = StringUtils.replaceIgnoreCase(baseVersion, "-snapshot", "");
            baseVersionStartPosition = filename.indexOf(versionStripped);
            baseVersionEndPosition = baseVersionStartPosition + versionStripped.length();
        }

        StringBuilder versionBuilder = new StringBuilder();

        // filename could be `maven-metadata.xml` in which case there will be no version in the filename to match against.
        if (baseVersionStartPosition > -1)
        {
            versionBuilder.append(filename, baseVersionStartPosition, baseVersionEndPosition);

            for (int i = baseVersionEndPosition; i < filename.length(); i++)
            {
                char c = filename.charAt(i);

                if (Character.isDigit(c))
                {
                    if (i > 0)
                    {
                        char p = filename.charAt(i - 1);
                        if (p == '.' || p == '-' || p == '_')
                        {
                            versionBuilder.append(p);
                        }
                    }
                    versionBuilder.append(c);
                }
                else if (c == '.' || c == '-' || c == '_')
                {
                    continue;
                }
                else
                {
                    break;
                }
            }
        }

        return versionBuilder.length() > 0 ? versionBuilder.toString() : null;
    }

    /**
     * @param filename the filename
     * @param version  the version as it would be in the filename.
     * @param type     the file type (jar, zip, etc)
     * @return
     */
    private String extractClassifier(String filename,
                                     String version,
                                     String type)
    {
        String classifier = null;

        // filename could be `maven-metadata.xml` in which case there will be classifier
        if (!filename.equalsIgnoreCase("maven-metadata.xml"))
        {
            int classifierStartPosition = filename.indexOf(version) + version.length() + 1;
            int classifierEndPosition = type != null ? filename.length() - type.length() - 1 : 0;

            // when there is no classifier, the starting position might exceed the end position.
            if (classifierStartPosition < classifierEndPosition)
            {
                classifier = filename.substring(classifierStartPosition, classifierEndPosition);

                if (!classifier.equals(""))
                {
                    for (String secondaryExtension : secondaryExtensions)
                    {
                        if (StringUtils.endsWithIgnoreCase(classifier, "." + secondaryExtension))
                        {
                            classifier = classifier.substring(0,
                                                              classifier.length() -
                                                              secondaryExtension.length() - 1);
                            break;
                        }
                    }
                }
                else
                {
                    classifier = null;
                }
            }
        }

        return classifier;
    }

    /**
     * Attempts to guess the artifact extension.
     *
     * @param filename
     * @return
     */
    public static String getFileExtension(String filename)
    {
        String fileExtension = StringUtils.substringAfterLast(filename, ".");
        String filenameWithoutExtension = filename.substring(0, filename.length() - fileExtension.length() - 1);

        StringJoiner extension = new StringJoiner(".");

        for (String secondaryExtension : secondaryExtensions)
        {
            if (filenameWithoutExtension.endsWith("." + secondaryExtension))
            {
                extension.add(secondaryExtension);
                break;
            }
        }

        // Append the actual extension (i.e. jar/gz/bz2/etc)
        extension.add(fileExtension);

        return extension.length() > 0 ? extension.toString() : null;
    }
}
