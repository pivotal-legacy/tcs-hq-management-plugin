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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.springsource.hq.plugin.tcserver.serverconfig.BindableAttributes;

public class BinderUtilTests {

    @Test
    public void bindableObjectWithOneAttribute() {
        Assert.assertNotNull(Foobar.class
                .getAnnotation(BindableAttributes.class));
        assertAttributes(Foobar.class, "attr");
    }

    @Test
    public void bindableObjectWithNestedBindable() {
        assertNotNull(Foobar2.class.getAnnotation(BindableAttributes.class));
        assertAttributes(Foobar2.class, "id", "foobar", "foobar.attr");
    }

    @Test(expected = RuntimeException.class)
    public void bindableObjectWithRecursivePojo() {
        assertNotNull(Foobar3.class.getAnnotation(BindableAttributes.class));
        BinderUtil.getBindableAttributes(Foobar3.class);
    }

    @Test
    public void bindableObjectDeeplyNested() {
        assertNotNull(Foobar4.class.getAnnotation(BindableAttributes.class));
        assertAttributes(Foobar4.class, "foobar", "foobar.attr", "foobar2",
                "foobar2.id", "foobar2.foobar", "foobar2.foobar.attr");
    }

    @Test
    public void nonBindableObject() {
        assertNull(ClassWithNoAnnotation.class
                .getAnnotation(BindableAttributes.class));
        assertAttributes(ClassWithNoAnnotation.class);
    }

    @Test
    public void nonBindableObjectMultipleTimes() {
        assertAttributes(new Class<?>[] { ClassWithNoAnnotation.class,
                ClassWithNoAnnotation.class });
    }

    @Test
    public void multipleClassesWithReplicationAndNoNesting() {
        assertAttributes(new Class<?>[] { Foobar.class, Foobar.class }, "attr");
    }

    @Test
    public void multipleClassesWithReplicationAndNesting() {
        assertAttributes(new Class<?>[] { Foobar2.class, Foobar2.class }, "id",
                "foobar", "foobar.attr");
    }

    @Test
    public void multipleClassesWithNoReplication() {
        assertAttributes(new Class<?>[] { Foobar.class, Foobar2.class },
                "attr", "id", "foobar", "foobar.attr");
    }

    @Test
    public void multipleClassesWithReplication() {
        assertAttributes(new Class<?>[] { Foobar2.class, Foobar4.class }, "id",
                "foobar", "foobar.attr", "foobar2", "foobar2.id",
                "foobar2.foobar", "foobar2.foobar.attr");
    }

    @Test
    public void mixingBindableWithNonBindable() {
        assertAttributes(new Class<?>[] { Foobar2.class, Foobar4.class,
                ClassWithNoAnnotation.class }, "id", "foobar", "foobar.attr",
                "foobar2", "foobar2.id", "foobar2.foobar",
                "foobar2.foobar.attr");
    }

    @Test
    public void bindableClassWithBindableSuperclass() {
        assertAttributes(new Class<?>[] { BindableSubclass.class }, "alpha",
                "id", "foobar", "foobar.attr");
    }

    private void assertAttributes(Class<?> clazz, String... expectedAttributes) {
        assertAttributes(new Class<?>[] { clazz }, expectedAttributes);
    }

    private void assertAttributes(Class<?>[] classes,
            String... expectedAttributes) {
        List<String> actualAttributes = Arrays.asList(BinderUtil
                .getBindableAttributes(classes));
        assertEquals(expectedAttributes.length, actualAttributes.size());
        for (String expectedAttribute : expectedAttributes) {
            assertTrue(actualAttributes
                    + " does not contain the expected attribute '"
                    + expectedAttribute + "'", actualAttributes
                    .contains(expectedAttribute));
        }
    }
}
