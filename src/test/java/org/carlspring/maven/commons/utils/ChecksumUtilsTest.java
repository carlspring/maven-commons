package org.carlspring.maven.commons.utils;

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

import org.carlspring.maven.commons.util.ChecksumUtils;

import org.junit.jupiter.api.Test;
import static junit.framework.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


/**
 * @author mtodorov
 */
public class ChecksumUtilsTest
{

    public static final String FILE_NAME = "target/test-classes/checksum-test-file";

    @Test
    public void testMD5Sum()
            throws Exception
    {
        final String md5Checksum = ChecksumUtils.getMD5Checksum(FILE_NAME);

        assertNotNull(md5Checksum);

        System.out.println("MD5:  " + md5Checksum);

        assertEquals("Incorrect MD5 sum!", "413fc0b59a067bf4620cc827c0027c5d", md5Checksum);
    }

    @Test
    public void testSHA1Sum()
            throws Exception
    {
        final String sha1Checksum = ChecksumUtils.getSHA1Checksum(FILE_NAME);

        assertNotNull(sha1Checksum);

        System.out.println("SHA1: " + sha1Checksum);

        assertEquals("Incorrect SHA1 sum!", "70e3646109a8a89e892fa07bbb2becf3780b1386", sha1Checksum);
    }

}
