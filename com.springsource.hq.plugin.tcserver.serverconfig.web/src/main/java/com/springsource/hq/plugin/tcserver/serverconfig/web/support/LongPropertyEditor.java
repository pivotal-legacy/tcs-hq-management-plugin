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

package com.springsource.hq.plugin.tcserver.serverconfig.web.support;

import java.beans.PropertyEditorSupport;
import java.text.NumberFormat;
import java.text.ParseException;

/**
 * Support for converting String to Long where "NaN" is null
 * 
 * @since 2.0
 */
public class LongPropertyEditor extends PropertyEditorSupport {

    @Override
    public String getAsText() {
        return (getValue() != null) ? NumberFormat.getInstance().format(
                getValue()) : "";
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        if ("NaN".equals(text) || "".equals(text)) {
            setValue(null);
        }
        else {
            try {
                setValue(NumberFormat.getInstance().parse(text).longValue());
            }
            catch (ParseException e) {
                setValue(null);
            }
        }
    }

}
