package org.carlspring.maven.commons;

import org.carlspring.maven.commons.util.ArtifactUtils;

import java.io.File;
import java.util.Collection;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.artifact.metadata.ArtifactMetadata;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.OverConstrainedVersionException;
import org.apache.maven.artifact.versioning.VersionRange;

/**
 * @author mtodorov
 */
public class DetachedArtifact implements Artifact
{

    private String groupId;

    private String artifactId;

    private VersionRange version;

    private String type = "jar";

    private String classifier;

    private String scope;

    private File file;

    /**
     * This should be either the actual release version or X.Y.Z-SNAPSHOT
     */
    private String baseVersion = null;

    public DetachedArtifact(String groupId, String artifactId, VersionRange version, String type, String classifier)
    {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.type = type;
        this.classifier = classifier;
    }

    public DetachedArtifact(String groupId, String artifactId, String version, String type, String classifier)
    {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version != null ? VersionRange.createFromVersion(version) : null;
        this.type = type;
        this.classifier = classifier;
    }

    public DetachedArtifact(String groupId, String artifactId, String version, String type)
    {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version != null ? VersionRange.createFromVersion(version) : null;
        this.type = type;
    }

    public DetachedArtifact(String groupId, String artifactId, String version)
    {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version != null ? VersionRange.createFromVersion(version) : null;
    }

    public DetachedArtifact(String groupId, String artifactId)
    {
        this.groupId = groupId;
        this.artifactId = artifactId;
    }

    @Override
    public String getGroupId()
    {
        return groupId;
    }

    @Override
    public void setGroupId(String groupId)
    {
        this.groupId = groupId;
    }

    @Override
    public String getArtifactId()
    {
        return artifactId;
    }

    @Override
    public void setArtifactId(String artifactId)
    {
        this.artifactId = artifactId;
    }

    @Override
    public String getVersion()
    {
        return version != null ? version.toString() : null;
    }

    @Override
    public void setVersion(String version)
    {
        this.version = VersionRange.createFromVersion(version);
    }

    @Override
    public String getScope()
    {
        return scope;
    }

    @Override
    public void setScope(String scope)
    {
        this.scope = scope;
    }

    @Override
    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    @Override
    public String getClassifier()
    {
        return classifier;
    }

    @Override
    public boolean hasClassifier()
    {
        return classifier != null;
    }

    @Override
    public File getFile()
    {
        return file;
    }

    @Override
    public void setFile(File file)
    {
        this.file = file;
    }

    @Override
    public String getBaseVersion()
    {
        return this.baseVersion;
    }

    @Override
    public void setBaseVersion(String s)
    {
        this.baseVersion = s;
    }

    @Override
    public String getId()
    {
        return null;
    }

    @Override
    public String getDependencyConflictId()
    {
        return null;
    }

    @Override
    public void addMetadata(ArtifactMetadata artifactMetadata)
    {

    }

    @Override
    public Collection<ArtifactMetadata> getMetadataList()
    {
        return null;
    }

    @Override
    public void setRepository(ArtifactRepository artifactRepository)
    {

    }

    @Override
    public ArtifactRepository getRepository()
    {
        return null;
    }

    @Override
    public void updateVersion(String s, ArtifactRepository artifactRepository)
    {

    }

    @Override
    public String getDownloadUrl()
    {
        return null;
    }

    @Override
    public void setDownloadUrl(String s)
    {

    }

    @Override
    public ArtifactFilter getDependencyFilter()
    {
        return null;
    }

    @Override
    public void setDependencyFilter(ArtifactFilter artifactFilter)
    {

    }

    @Override
    public ArtifactHandler getArtifactHandler()
    {
        return null;
    }

    @Override
    public List<String> getDependencyTrail()
    {
        return null;
    }

    @Override
    public void setDependencyTrail(List<String> list)
    {

    }

    @Override
    public VersionRange getVersionRange()
    {
        return null;
    }

    @Override
    public void setVersionRange(VersionRange versionRange)
    {
        this.version = versionRange;
    }

    @Override
    public void selectVersion(String s)
    {

    }

    @Override
    public boolean isSnapshot()
    {
        // TODO: Support VersionRange-s
        return getBaseVersion() != null
               && ( getBaseVersion().endsWith( SNAPSHOT_VERSION ) || getBaseVersion().equals( LATEST_VERSION ) );
    }

    @Override
    public void setResolved(boolean b)
    {
    }

    @Override
    public boolean isResolved()
    {
        return false;
    }

    @Override
    public void setResolvedVersion(String s)
    {

    }

    @Override
    public void setArtifactHandler(ArtifactHandler artifactHandler)
    {

    }

    @Override
    public boolean isRelease()
    {
        // TODO: Support VersionRange-s
        return !isSnapshot();
    }

    @Override
    public void setRelease(boolean b)
    {

    }

    @Override
    public List<ArtifactVersion> getAvailableVersions()
    {
        return null;
    }

    @Override
    public void setAvailableVersions(List<ArtifactVersion> list)
    {

    }

    @Override
    public boolean isOptional()
    {
        return false;
    }

    @Override
    public void setOptional(boolean b)
    {

    }

    @Override
    public ArtifactVersion getSelectedVersion()
            throws OverConstrainedVersionException
    {
        return null;
    }

    @Override
    public boolean isSelectedVersionKnown()
            throws OverConstrainedVersionException
    {
        return false;
    }

    @Override
    public int compareTo(Artifact o)
    {
        return 0;
    }

    @Override
    public String toString()
    {
        return groupId + ':' +
               (artifactId != null ? artifactId : "") + ':' +
               (version != null ? version : "") + ':' +
               (type != null ? type : "") +
               (classifier != null ? ':' + classifier : "");
    }

}
