/*******************************************************************************
 * CruiseControl, a Continuous Integration Toolkit Copyright (c) 2001,
 * ThoughtWorks, Inc. 651 W Washington Ave. Suite 600 Chicago, IL 60661 USA All
 * rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *  + Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *  + Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *  + Neither the name of ThoughtWorks, Inc., CruiseControl, nor the names of
 * its contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
package net.sourceforge.cruisecontrol.builders;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;
import net.sourceforge.cruisecontrol.CruiseControlException;
import net.sourceforge.cruisecontrol.util.Util;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.PatternLayout;
import org.jdom.Attribute;
import org.jdom.DataConversionException;
import org.jdom.Element;

public class NantBuilderTest extends TestCase {
    
    private final List filesToClear = new ArrayList();
    private NantBuilder builder;
    private String classpath;
    private Hashtable properties;
    private String nantCmd = "NAnt.exe";
    private File rootTempDir = null;
    private String rootTempDirPath = null;

    protected void setUp() throws Exception {
        builder = new NantBuilder();
        builder.setTarget("target");
        builder.setBuildFile("buildfile");

        // Must be a cleaner way to do this...
//        builder.setNantWorkingDir(new File(
//                new URI(ClassLoader.getSystemResource("test.build").toString())).getParent());
        properties = new Hashtable();
        properties.put("label", "200.1.23");

        BasicConfigurator.configure(new ConsoleAppender(new PatternLayout("%m%n")));

        rootTempDir = new File(new File(File.createTempFile("temp", "txt").getParent()), "testRoot");
        rootTempDirPath = rootTempDir.getCanonicalPath();
        rootTempDir.mkdir();
    }

    public void tearDown() {
        for (Iterator iterator = filesToClear.iterator(); iterator.hasNext();) {
            File file = (File) iterator.next();
            if (file.exists()) {
                file.delete();
            }
        }

        builder = null;
        classpath = null;
        properties = null;

        Util.deleteFile(rootTempDir);
    }

    public void testValidate() {
        builder = new NantBuilder();

        try {
            builder.validate();
        } catch (CruiseControlException e) {
            fail("nantbuilder is missing required attributes");
        }

        builder.setTime("0100");
        builder.setBuildFile("buildfile");
        builder.setTarget("target");

        try {
            builder.validate();
        } catch (CruiseControlException e) {
            fail("validate should not throw exceptions when options are set.");
        }

        builder.setSaveLogDir("I/hope/this/dir/does/not/exist/");
        try {
            builder.validate();
            fail("validate should throw exceptions when saveLogDir doesn't exist");
        } catch (CruiseControlException e) {
        }

        builder.setSaveLogDir(null);
        builder.setMultiple(2);

        try {
            builder.validate();
            fail("validate should throw exceptions when multiple and time are both set.");
        } catch (CruiseControlException e) {
        }
    }
    
    public void testTranslateNantErrorElementsWithBuildResultsErrorAttribute() 
        throws CruiseControlException, DataConversionException {
        Element buildLogElement = new Element("buildresults");
        Attribute errorAttribute = new Attribute("error", "true");
        buildLogElement.setAttribute(errorAttribute);
        buildLogElement = builder.translateNantErrorElements(buildLogElement);
        assertEquals("build", buildLogElement.getName());
        assertTrue(buildLogElement.getAttribute("error").getBooleanValue());       
        
    }
    
    public void testTranslateNantErrorElementsWithFailureElements() 
        throws CruiseControlException, DataConversionException {
        Element buildLogElement = new Element("buildresults");
        Element failureElement = new Element("failure");
        buildLogElement.addContent(failureElement);
        
        buildLogElement = builder.translateNantErrorElements(buildLogElement);
        assertEquals("build", buildLogElement.getName());
        assertTrue(buildLogElement.getAttribute("error").getBooleanValue());       
        
    }    

    public void testGetCommandLineArgs() throws CruiseControlException {
        String[] resultInfo = { nantCmd, 
                "-listener:NAnt.Core.XmlLogger", 
                "-D:XmlLogger.file=log.xml",
                "-D:label=200.1.23", 
                "-buildfile:buildfile", 
                "target" };
        assertEquals(getCommandLineFromArgsArray(resultInfo), getCommandLineFromArgsArray(builder
                .getCommandLineArgs(properties)));

        String[] resultLogger = { nantCmd, 
                "-logger:NAnt.Core.XmlLogger", 
                "-logfile:log.xml", 
                "-D:label=200.1.23",
                "-buildfile:buildfile", 
                "target" };
        builder.setUseLogger(true);
        assertEquals(getCommandLineFromArgsArray(resultLogger), getCommandLineFromArgsArray(builder
                .getCommandLineArgs(properties)));
    }

    public void testGetCommandLineArgs_EmptyLogger() throws CruiseControlException {
        String[] resultInfo = { nantCmd, 
                "-listener:NAnt.Core.XmlLogger", 
                "-D:XmlLogger.file=log.xml",
                "-buildfile:buildfile", 
                "target" };
        properties.put("label", "");
        assertEquals(getCommandLineFromArgsArray(resultInfo), getCommandLineFromArgsArray(builder
                .getCommandLineArgs(properties)));

        String[] resultLogger = { nantCmd, 
                "-logger:NAnt.Core.XmlLogger", 
                "-logfile:log.xml", 
                "-buildfile:buildfile",
                "target" };
        builder.setUseLogger(true);
        assertEquals(getCommandLineFromArgsArray(resultLogger), getCommandLineFromArgsArray(builder
                .getCommandLineArgs(properties)));
    }

    public void testGetCommandLineArgs_Debug() throws CruiseControlException {
        String[] resultDebug = { nantCmd, 
                "-logger:NAnt.Core.XmlLogger", 
                "-logfile:log.xml", 
                "-debug+",
                "-D:label=200.1.23", 
                "-buildfile:buildfile", 
                "target" };
        builder.setUseDebug(true);
        builder.setUseLogger(true);
        assertEquals(getCommandLineFromArgsArray(resultDebug), getCommandLineFromArgsArray(builder
                .getCommandLineArgs(properties)));
    }

    public void testGetCommandLineArgs_Quiet() throws CruiseControlException {
        String[] resultQuiet = { nantCmd, 
                "-logger:NAnt.Core.XmlLogger", 
                "-logfile:log.xml", 
                "-quiet+",
                "-D:label=200.1.23", 
                "-buildfile:buildfile", 
                "target" };
        builder.setUseQuiet(true);
        builder.setUseLogger(true);
        assertEquals(getCommandLineFromArgsArray(resultQuiet), getCommandLineFromArgsArray(builder
                .getCommandLineArgs(properties)));
    }

    public void testGetCommandLineArgs_DebugAndQuiet() throws CruiseControlException {
        builder.setUseDebug(true);
        builder.setUseQuiet(true);
        try {
            builder.validate();
            fail("validate() should throw CruiseControlException when both useDebug and useQuiet are true");
        } catch (CruiseControlException expected) {
        }
    }

    public void testGetNantLogAsElement() throws IOException, CruiseControlException {
        Element buildLogElement = new Element("build");
        File logFile = new File("_tempNantLog.xml");
        filesToClear.add(logFile);
        BufferedWriter bw2 = new BufferedWriter(new FileWriter(logFile));
        bw2.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n<?xml-stylesheet "
                + "type=\"text/xsl\" href=\"log.xsl\"?>\n<build></build>");
        bw2.flush();
        bw2.close();

        assertEquals(buildLogElement.toString(), NantBuilder.getNantLogAsElement(logFile).toString());
    }

    public void testGetNantLogAsElement_NoLogFile() throws IOException {
        File doesNotExist = new File("blah blah blah does not exist");
        try {
            NantBuilder.getNantLogAsElement(doesNotExist);
            fail();
        } catch (CruiseControlException expected) {
            assertEquals("NAnt logfile " + doesNotExist.getAbsolutePath() + " does not exist.", expected.getMessage());
        }
    }

    public void testBuild() throws Exception {
        builder.setBuildFile("test.build");
        builder.setTempFile("notLog.xml");
        builder.setTarget("init");
        HashMap buildProperties = new HashMap();
        Element buildElement = builder.build(buildProperties);
        int initCount = getInitCount(buildElement);
        assertEquals(1, initCount);

        // TODO: Don't know if this is a valid test with NAnt's file format. Need to verify or convert to Ant format.
//        builder.setTarget("init init");
//        buildElement = builder.build(buildProperties);
//        initCount = getInitCount(buildElement);
//        assertEquals(2, initCount);
    }

    public void testIsWindows() {
        builder = new NantBuilder() {
            protected String getOsName() {
                return "Windows 2000";
            }
        };
        assertTrue(builder.isWindows());
    }

    public int getInitCount(Element buildElement) {
        int initFoundCount = 0;
        Iterator targetIterator = buildElement.getChildren("target").iterator();
        String name;
        while (targetIterator.hasNext()) {
            name = ((Element) targetIterator.next()).getAttributeValue("name");
            if (name.equals("init")) {
                initFoundCount++;
            }
        }
        return initFoundCount;
    }

    public void testBuildTimeout() throws Exception {
        builder.setBuildFile("test.build");
        builder.setTarget("timeout-test-target");
        builder.setTimeout(5);
        builder.setUseDebug(true);
        builder.setUseLogger(true);

        HashMap buildProperties = new HashMap();
        long startTime = System.currentTimeMillis();
        Element buildElement = builder.build(buildProperties);
        assertTrue((System.currentTimeMillis() - startTime) < 9 * 1000L);
        assertTrue(buildElement.getAttributeValue("error").indexOf("timeout") >= 0);

        // test we don't fail when there is no NAnt log file
        builder.setTimeout(1);
        builder.setUseDebug(false);
        builder.setUseLogger(false);
        builder.setTempFile("shouldNot.xml");
        buildElement = builder.build(buildProperties);
        assertTrue(buildElement.getAttributeValue("error").indexOf("timeout") >= 0);
    }

    public void testSaveNantLog() throws IOException {
        String originalDirName = "target";
        String logName = "log.xml";
        String saveDirName = "target/reports/nant";

        builder.setSaveLogDir(saveDirName);
        builder.setTempFile(logName);

        File originalDir = new File(originalDirName);
        File originalLog = new File(originalDir, logName);
        originalDir.mkdirs();
        originalLog.createNewFile();

        File saveDir = new File(saveDirName);
        File savedLog = new File(saveDir, logName);
        saveDir.mkdirs();
        savedLog.delete();

        builder.saveNantLog(originalLog);
        assertTrue(savedLog.exists());

        savedLog.delete();

        builder.setSaveLogDir("");
        builder.saveNantLog(originalLog);
        assertFalse(savedLog.exists());

        builder.setSaveLogDir(null);
        builder.saveNantLog(originalLog);
        assertFalse(savedLog.exists());
    }

    private static String getCommandLineFromArgsArray(String[] args) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < args.length; i++) {
            sb.append(args[i]).append(" ");
        }
        return sb.toString();
    }
}