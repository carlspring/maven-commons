package org.carlspring.maven.commons.util;

import org.apache.maven.shared.dependency.tree.DependencyNode;

/**
 * @author mtodorov
 */
public class DependencyNodeUtils
{

    public static int getDepth(DependencyNode node)
    {
        int depth = 0;

        DependencyNode parent = node;
        while ((parent = parent.getParent()) != null)
        {
            depth += 2;
        }

        return depth;
    }

    public static String pad(int n, char c)
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++)
        {
            sb.append(c);
        }

        return sb.toString();
    }

    public static String getState(DependencyNode node)
    {
        switch (node.getState())
        {
            case DependencyNode.INCLUDED:
                return "included";

            case DependencyNode.OMITTED_FOR_DUPLICATE:
                return "omitted for duplicate";

            case DependencyNode.OMITTED_FOR_CONFLICT:
                return "omitted for conflict with " + node.getRelatedArtifact().getVersion();

            case DependencyNode.OMITTED_FOR_CYCLE:
                return "omitted for cycle";
        }

        throw new RuntimeException("Invalid dependency node state!");
    }

}
