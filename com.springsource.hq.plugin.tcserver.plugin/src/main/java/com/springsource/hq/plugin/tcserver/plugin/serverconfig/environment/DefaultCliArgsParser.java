
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

public class DefaultCliArgsParser implements CliArgsParser {

    String parameters;

    public DefaultCliArgsParser(String parameters) {
        this.parameters = parameters;
    }

    public List<String> getArgumentList() {
        return separatePropertyValues(parameters);
    }

    private List<String> separatePropertyValues(String fullValue) {
        List<String> values = new ArrayList<String>();
        int quoteCount = 0;
        String separatedValue = "";
        String trimmedFullValue = fullValue.trim();
        for (int i = 0; i < trimmedFullValue.length(); i++) {
            char character = trimmedFullValue.charAt(i);
            if (character == '\'' || character == '\"') {
                quoteCount++;
                separatedValue += character;
            } else {
                separatedValue += character;
            }
            // If there is a space and it is not within quotes it is a new value,
            // or if it is the last character we also want to add the value
            if ((character == ' ' && quoteCount % 2 == 0) || trimmedFullValue.length() == i + 1) {
                values.add(separatedValue.trim());
                quoteCount = 0;
                separatedValue = "";
            }
        }
        return values;
    }
}
