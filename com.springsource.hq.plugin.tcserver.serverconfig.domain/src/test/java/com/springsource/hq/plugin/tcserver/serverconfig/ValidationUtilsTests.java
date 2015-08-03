
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

package com.springsource.hq.plugin.tcserver.serverconfig;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public final class ValidationUtilsTests {

    @Test
    public void validationOfCollection() {
        List<StubValidator> validators = new ArrayList<StubValidator>();
        validators.add(new StubValidator());
        validators.add(new StubValidator());
        validators.add(new StubValidator());

        ValidationUtils.validateCollection(validators, "theValidators", new BeanPropertyBindingResult(null, null));

        for (int i = 0; i < validators.size(); i++) {
            StubValidator validator = validators.get(i);
            Assert.assertSame(validator, validator.target);
            Assert.assertEquals("theValidators[" + i + "].", validator.nestedPath);
        }
    }

    private static final class StubValidator implements Validator {

        private volatile Object target;

        private volatile String nestedPath;

        @SuppressWarnings("rawtypes")
        public boolean supports(Class clazz) {
            return true;
        }

        public void validate(Object target, Errors errors) {
            this.target = target;
            this.nestedPath = errors.getNestedPath();
        }
    }
}
