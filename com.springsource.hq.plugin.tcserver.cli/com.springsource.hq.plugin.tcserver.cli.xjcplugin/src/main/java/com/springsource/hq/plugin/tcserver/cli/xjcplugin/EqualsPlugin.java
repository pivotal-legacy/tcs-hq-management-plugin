
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

package com.springsource.hq.plugin.tcserver.cli.xjcplugin;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.xml.sax.ErrorHandler;

import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JVar;
import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.Plugin;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.Outline;

public final class EqualsPlugin extends Plugin {

    private static final String OPTION_NAME = "Xequals";

    private static final String USAGE = "-Xequals";

    @Override
    public String getOptionName() {
        return OPTION_NAME;
    }

    @Override
    public String getUsage() {
        return USAGE;
    }

    @Override
    public boolean run(Outline outline, Options opt, ErrorHandler errorHandler) {
        for (ClassOutline classOutline : outline.getClasses()) {
            generateEqualsMethod(classOutline.ref);
        }
        return true;
    }

    private void generateEqualsMethod(JDefinedClass implClass) {
        JCodeModel codeModel = implClass.owner();

        JMethod equalsMethod = implClass.method(JMod.PUBLIC, codeModel.BOOLEAN, "equals");

        equalsMethod.annotate(Override.class);

        JVar that = equalsMethod.param(Object.class, "that");
        equalsMethod.body()._return(codeModel.ref(EqualsBuilder.class).staticInvoke("reflectionEquals").arg(JExpr._this()).arg(that));

    }

}
