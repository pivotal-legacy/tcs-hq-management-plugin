/*
 * Copyright (C) 2009-2015  Pivotal Software, Inc
 *
 * This program is is free software; you can redistribute it and/or modify
 * it under the terms version 2 of the GNU General Public License as
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package com.springsource.hq.plugin.tcserver.plugin.serverconfig.environment;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

public class WindowsSpecificNonJvmOptsTests extends TestCase {

    private WindowsSpecificNonJvmOptsUtil windowsOptsUtil;

    @Override
    protected void setUp() throws Exception {
        windowsOptsUtil = new WindowsSpecificNonJvmOptsUtil();
    }

    public void testMergeOpts() {
        List<String> requestedOpts = new ArrayList<String>();
        requestedOpts.add("-Dmy.java.opts=foo");
        requestedOpts.add("-Dmy.yet.another.java.opts=foo");
        requestedOpts.add("-Dcatalina.base=bar");

        List<String> existingOpts = new ArrayList<String>();
        existingOpts.add("-Dmy.java.opts=bar");
        existingOpts.add("-Dmy.other.java.opts=bar");
        existingOpts.add("-Dcatalina.base=biz");
        existingOpts.add("-Dcatalina.home=boop");

        List<String> resultingOpts = windowsOptsUtil.mergeOpts(existingOpts, requestedOpts);

        assertEquals(4, resultingOpts.size());
        assertTrue(resultingOpts.contains("-Dmy.java.opts=foo"));
        assertTrue(resultingOpts.contains("-Dmy.yet.another.java.opts=foo"));
        assertTrue(resultingOpts.contains("-Dcatalina.base=bar"));
        assertTrue(resultingOpts.contains("-Dcatalina.home=boop"));

        assertFalse(resultingOpts.contains("-Dmy.java.opts=bar"));
        assertFalse(resultingOpts.contains("-Dmy.other.java.opts=bar"));
        assertFalse(resultingOpts.contains("-Dcatalina.base=biz"));
    }

    public void testRemoveProtectedOpts() {
        List<String> requestedOpts = new ArrayList<String>();
        requestedOpts.add("-Dmy.java.opts=foo");
        requestedOpts.add("-Dcatalina.base=bar");

        List<String> resultingOpts = windowsOptsUtil.removeProtectedOpts(requestedOpts);

        assertEquals(1, resultingOpts.size());
        assertTrue(resultingOpts.contains("-Dmy.java.opts=foo"));
        assertFalse(resultingOpts.contains("-Dcatalina.base=bar"));
    }

    public void testAddQuotesIfNecessary() {
        assertEquals("-Dmy.java.opts=\"foo bar\"", windowsOptsUtil.addQuotesIfNeeded("-Dmy.java.opts=\"foo bar\""));
        assertEquals("-Dmy.other.java.opts=\"c:\\Program Files\\directory\\location\"",
            windowsOptsUtil.addQuotesIfNeeded("-Dmy.other.java.opts=\"c:\\Program Files\\directory\\location\""));
        assertEquals("-Dcatalina.base=\"new one\"", windowsOptsUtil.addQuotesIfNeeded("-Dcatalina.base=new one"));
        assertEquals("-Dcatalina.home=\"boop\"", windowsOptsUtil.addQuotesIfNeeded("-Dcatalina.home=\"boop\""));
    }

}
