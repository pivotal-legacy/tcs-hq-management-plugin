
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

import java.util.Collection;
import java.util.Iterator;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * Utility methods for performing validation of domain objects
 * <p />
 * 
 * <strong>Concurrent Semantics</strong><br />
 * 
 * Thread-safe
 * 
 */
public final class ValidationUtils {

    private ValidationUtils() {

    }

    /**
     * Performs validation of the given <code>Collection</code> of <code>Validator</code> instances. Each
     * <code>Validator</code> is treated as a self-validating domain object, i.e. the following is called upon each
     * <code>Validator</code>: <code>validator.validate(validator, errors)</code>. The given <code>identifier</code> is
     * used when pushing the path on the given <code>errors</code>, e.g. an identifier of 'foo' will result in
     * <code>foo[i]</code> being pushed, where i is the index of the validator in the given collection. The given
     * <code>errors</code> will be used to record any errors that are found.
     * 
     * @see Validator#validate(Object, Errors)
     * 
     * @param selfValidatingItems The self-validating items to validate
     * @param identifier The identifier
     * @param errors Passed to each validator and to be used to record any errors
     */
    public static void validateCollection(Collection<? extends Validator> selfValidatingItems, String identifier, Errors errors) {
        Iterator<? extends Validator> iterator = selfValidatingItems.iterator();
        int index = 0;
        while (iterator.hasNext()) {
            errors.pushNestedPath(String.format("%s[%d]", identifier, index++));
            Validator selfValidating = iterator.next();
            selfValidating.validate(selfValidating, errors);
            errors.popNestedPath();
        }
    }
}
