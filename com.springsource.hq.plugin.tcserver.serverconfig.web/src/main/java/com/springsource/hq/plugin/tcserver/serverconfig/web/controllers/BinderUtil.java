/*
 * Copyright (C) 2010-2015  Pivotal Software, Inc
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

package com.springsource.hq.plugin.tcserver.serverconfig.web.controllers;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.springsource.hq.plugin.tcserver.serverconfig.BindableAttributes;

/**
 * Contains a utility method used to scan for nested attributes in order to
 * white-list Spring MVC controller attributes.
 */
public class BinderUtil {

    /**
     * This method will start with a top level object, and scan it for a nested
     * listing of properties. It handles multiple inputs, and will only print
     * out a distinct list of properties between all the provided ones.
     * @param Class<?>... classes
     * @return
     */
    public static String[] getBindableAttributes(Class<?>... classes) {

        List<Class<?>> annotatedClassesAndSuperClasses = getAnnotatedClassesAndSuperClasses(classes);

        Set<String> attributes = new HashSet<String>();

        for (Class<?> clazz : annotatedClassesAndSuperClasses) {
            for (Method method : clazz.getDeclaredMethods()) {
                String beanName = convertMethodNameToBeanName(method.getName());
                if (beanName != null) {
                    attributes.add(beanName);

                    Class<?> beanReturnType = method.getReturnType();
                    if (beanReturnType.getAnnotation(BindableAttributes.class) != null) {
                        if (beanReturnType == clazz) {
                            throw new RuntimeException(
                                    clazz
                                            + " has recursive definition. Unable to descend.");
                        }
                        for (String str : getBindableAttributes(beanReturnType)) {
                            attributes.add(beanName + "." + str);
                        }
                    }
                }
            }
        }

        return attributes.toArray(new String[attributes.size()]);
    }

    private static List<Class<?>> getAnnotatedClassesAndSuperClasses(
            Class<?>... classes) {
        List<Class<?>> annotatedClasses = new ArrayList<Class<?>>();

        for (Class<?> clazz : classes) {

            Class<?> currentClass = clazz;

            while (currentClass != null
                    && currentClass
                            .isAnnotationPresent(BindableAttributes.class)) {
                annotatedClasses.add(currentClass);
                currentClass = currentClass.getSuperclass();
            }
        }

        return annotatedClasses;
    }

    /**
     * This support method is used to convert a "getter" into a bean name.
     * @param methodName
     * @return String bean name after stripping off the get prefix.
     */
    private static String convertMethodNameToBeanName(String methodName) {
        String bean = null;
        for (String prefix : new String[] { "get", "is" }) {
            if (methodName.startsWith(prefix)) {
                bean = methodName.substring(prefix.length());
                break;
            }
        }
        if (bean != null) {
            String beanName = bean.substring(0, 1).toLowerCase()
                    + bean.substring(1);
            return beanName;
        }
        else {
            return null;
        }
    }
}
