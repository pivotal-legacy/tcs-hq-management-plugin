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

package com.springsource.hq.plugin.tcserver.cli.client;

import java.util.Arrays;
import java.util.Map;

import org.easymock.IArgumentMatcher;

/**
 * Implementation of {@link IArgumentMatcher} that compares a Map containing String[] values that is used for the
 * parameters that are passed to every Connection call
 */
public class ParamsEquals implements IArgumentMatcher {

    private Map<String, String[]> expected;

    /**
     * 
     * @param expected The expected params
     */
    public ParamsEquals(Map<String, String[]> expected) {
        this.expected = expected;
    }

    public void appendTo(StringBuffer buffer) {
        buffer.append("eqParams(");
        buffer.append(expected.getClass().getName());
        buffer.append(" with params \"{");
        for (Map.Entry<String, String[]> expectedEntry : expected.entrySet()) {
            buffer.append(expectedEntry.getKey()).append("=");
            for (String val : expectedEntry.getValue()) {
                buffer.append(val).append(", ");
            }
            buffer.append("; ");
        }
        buffer.append("}\"");

    }

    public boolean matches(Object actual) {
        if (!(actual instanceof Map<?, ?>)) {
            return false;
        }
        @SuppressWarnings("unchecked")
        Map<String, String[]> actualMap = (Map<String, String[]>) actual;
        return ((actualMap.size() == expected.size()) && ((actualMap.keySet().equals(expected.keySet()))) && matchValues(actualMap));
    }

    private boolean matchValues(Map<String, String[]> actual) {
        for (Map.Entry<String, String[]> expectedEntry : expected.entrySet()) {
            if (!(Arrays.equals(expectedEntry.getValue(), (String[]) actual.get(expectedEntry.getKey())))) {
                return false;
            }
        }
        return true;
    }

}
