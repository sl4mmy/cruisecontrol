/********************************************************************************
 * CruiseControl, a Continuous Integration Toolkit
 * Copyright (c) 2001, ThoughtWorks, Inc.
 * 651 W Washington Ave. Suite 600
 * Chicago, IL 60661 USA
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *     + Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *
 *     + Redistributions in binary form must reproduce the above
 *       copyright notice, this list of conditions and the following
 *       disclaimer in the documentation and/or other materials provided
 *       with the distribution.
 *
 *     + Neither the name of ThoughtWorks, Inc., CruiseControl, nor the
 *       names of its contributors may be used to endorse or promote
 *       products derived from this software without specific prior
 *       written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ********************************************************************************/
package net.sourceforge.cruisecontrol.sourcecontrols;

import java.io.File;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;
import net.sourceforge.cruisecontrol.CruiseControlException;

public class HttpFileTest extends TestCase {

    public void testValidate() {
        HttpFile httpFile = new HttpFile();

        try {
            httpFile.validate();
            fail("HttpFile should throw when url not set.");
        } catch (CruiseControlException e) {
            assertEquals("'url' is a required attribute for HttpFile", e.getMessage());
        }

        try {
            httpFile.setProperty("test");
            fail("HttpFile should throw when property attribute set.");
        } catch (UnsupportedOperationException e) {
            assertEquals("attribute 'property' is not supported", e.getMessage());
        }

        try {
            httpFile.setPropertyOnDelete("test");
            fail("HttpFile should throw when setPropertyOnDelete attribute set.");
        } catch (UnsupportedOperationException e) {
            assertEquals("attribute 'propertyOnDelete' is not supported", e.getMessage());
        }

        httpFile.setURL("Invalid URL");
        try {
            httpFile.validate();
            fail("HttpFile should throw when an invalid URL is provided");
        } catch (CruiseControlException e) {
            assertEquals(
                "'url' is not a valid connection string : no protocol: Invalid URL",
                e.getMessage());
        }

    }

    //I'm not sure if something that hits a live web server is good for unit tests
    public void disabled_testGetModifications() throws Exception {
        HttpFile httpFile = new HttpFile();
        httpFile.setURL("http://cruisecontrol.sourceforge.net/");
        List modifications = httpFile.getModifications(new Date(1), new Date());
        assertEquals(1, modifications.size());
    }

    public void testGetModificationsInvalidURL() throws Exception {
        HttpFile httpFile = new HttpFile();
        File tempFile = File.createTempFile("HttpFileTest", null);
        httpFile.setURL(tempFile.toURL().toExternalForm());
        tempFile.delete();
        // should not throw with a URL that cannot be connected to
        httpFile.getModifications(new Date(), new Date());
    }
}
