/********************************************************************************
 * CruiseControl, a Continuous Integration Toolkit                              *
 * Copyright (C) 2001  ThoughtWorks, Inc.                                       *
 * 651 W Washington Ave. Suite 500                                              *
 * Chicago, IL 60661 USA                                                        *
 *                                                                              *
 * This program is free software; you can redistribute it and/or                *
 * modify it under the terms of the GNU General Public License                  *
 * as published by the Free Software Foundation; either version 2               *
 * of the License, or (at your option) any later version.                       *
 *                                                                              *
 * This program is distributed in the hope that it will be useful,              *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of               *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                *
 * GNU General Public License for more details.                                 *
 *                                                                              *
 * You should have received a copy of the GNU General Public License            *
 * along with this program; if not, write to the Free Software                  *
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  *
 ********************************************************************************/

package net.sourceforge.cruisecontrol;

import java.util.*;
import junit.framework.*;

import net.sourceforge.cruisecontrol.testutil.MockTask;

/**
 * @author  robertdw, jchyip
 */
public class CVSElementTest extends TestCase {
    
    public CVSElementTest(java.lang.String testName) {
        super(testName);
    }

    public void testLogCommandNullLocal() {
        Date lastBuildTime = new Date();
        Date currTime = new Date();
        
        CVSElement element = new CVSElement();
        element.setCvsroot("cvsroot");
        element.setLocalWorkingCopy(null);
        
        assertEquals("cvs -d cvsroot log -d \"" 
         + CVSElement.CVSDATE.format(lastBuildTime) 
         + "<" + CVSElement.CVSDATE.format(currTime) + "\"", 
         element.prepareCommandForDisplay(element.buildLogCommand(lastBuildTime, 
          currTime)).trim());
    }
    
    public void testLogCommandNullCVSROOT() {
        Date lastBuildTime = new Date();
        Date currTime = new Date();
        CVSElement element = new CVSElement();
        element.setCvsroot(null);
        element.setLocalWorkingCopy("local");

        assertEquals("cvs log -d \"" + CVSElement.CVSDATE.format(lastBuildTime) 
         + "<" + CVSElement.CVSDATE.format(currTime) + "\" local", 
         element.prepareCommandForDisplay(element.buildLogCommand(lastBuildTime, 
          currTime)).trim());
    }
    
    public void testLogPrepend() {
        CVSElement element = new CVSElement();
        MockTask task = new MockTask();
        element.setAntTask(task);

        String logMessage = "log message";
        element.log(logMessage);
        
        assertEquals("[cvselement]" + " " + logMessage, task.getSentLog());
    }
    
    public void testFormatLogDate() {
        Date may18_2001_6pm = 
         new GregorianCalendar(2001, 4, 18, 18, 0, 0).getTime();
        assertEquals("2001/05/18 18:00:00 " 
         + TimeZone.getDefault().getDisplayName(true, TimeZone.SHORT), 
         CVSElement.LOGDATE.format(may18_2001_6pm));
    }
    
    public void testFormatCVSDateGMTPlusZero() {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+0:00"));
        Date may18_2001_6pm = 
         new GregorianCalendar(2001, 4, 18, 18, 0, 0).getTime();
        assertEquals("2001-05-18 18:00:00 GMT", 
         CVSElement.CVSDATE.format(may18_2001_6pm));
    }
    
    public void testFormatCVSDateGMTPlusTen() {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+10:00"));
        Date may18_2001_6pm = new GregorianCalendar(2001, 4, 18, 18, 0, 0).getTime();
        assertEquals("2001-05-18 08:00:00 GMT", 
         CVSElement.CVSDATE.format(may18_2001_6pm));
        Date may8_2001_6pm = new GregorianCalendar(2001, 4, 18, 8, 0, 0).getTime();
        assertEquals("2001-05-17 22:00:00 GMT", 
         CVSElement.CVSDATE.format(may8_2001_6pm));
    }

    public void testFormatCVSDateGMTMinusTen() {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT-10:00"));
        Date may18_2001_6pm = new GregorianCalendar(2001, 4, 18, 18, 0, 0).getTime();
        assertEquals("2001-05-19 04:00:00 GMT", 
         CVSElement.CVSDATE.format(may18_2001_6pm));
        Date may8_2001_6pm = new GregorianCalendar(2001, 4, 18, 8, 0, 0).getTime();
        assertEquals("2001-05-18 18:00:00 GMT", 
         CVSElement.CVSDATE.format(may8_2001_6pm));
    }

    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(CVSElementTest.class);
    }
    
    
}
