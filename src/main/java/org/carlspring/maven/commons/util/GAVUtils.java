package org.carlspring.maven.commons.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class handles GAV string containing
 * groupId:artifactId:version:type:classifier parameters.
 *
 * @author mtodorov
 */
public class GAVUtils
{

    public static boolean matches(String gav,
                                  String gavPattern)
    {
        final String[] elements = gavPattern.split(":");
        final int gavElementsLength = gav.split(":").length;

        if (elements.length > gavElementsLength)
        {
            throw new IllegalArgumentException("Incorrect GAV components for: " + gav +"!");
        }

        if (elements.length < gavElementsLength)
        {
            // Make sure both sides have the same number of elements to compare
            for (int i = elements.length; i < gavElementsLength; i++)
            {
                gavPattern += ":*";
            }
        }

        gavPattern = gavPattern.replaceAll("\\*", "\\.\\*");

        Pattern p = Pattern.compile(gavPattern);
        Matcher m = p.matcher(gav);

        return m.matches();
    }

}
