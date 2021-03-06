/*
 * Copyright (c) 2014, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package execution;

import hierarchies.TypeHierarchy;
import scenarios.Scenario;

/**
 *  Type profiling conflict execution scenario. The main goal is
 *  to make compiler profile and compile methods with different types.
 *  Scenario tests guards by passing conflicting types (incompatible
 *  for the profiled data).
 */
public class TypeConflict<T extends TypeHierarchy.I, R> implements Execution<T, R> {
    /** Test methods execution number to make profile  */
    private final static int POLLUTION_THRESHOLD = 5000;
    /** Test methods execution number to make it profiled and compiled*/
    private final static int PROFILE_THRESHOLD = 20000;

    @Override
    public void execute(Scenario<T, R> scenario) {
        T base = scenario.getProfiled();
        T incompatible = scenario.getConflict();

        // pollute profile by passing different types
        R baseResult = null;
        R incResult = null;
        for (int i = 0; i < POLLUTION_THRESHOLD; i++) {
            baseResult = methodNotToCompile(scenario, base);
            incResult = methodNotToCompile(scenario, incompatible);
        }
        scenario.check(baseResult, base);
        scenario.check(incResult, incompatible);

        // profile and compile
        R result = null;
        for (int i = 0; i < PROFILE_THRESHOLD; i++) {
            result = methodNotToCompile(scenario, base);
        }
        scenario.check(result, base);

        // pass another type to make guard work and recompile
        for (int i = 0; i < PROFILE_THRESHOLD; i++) {
            result = methodNotToCompile(scenario, incompatible);
        }
        scenario.check(result, incompatible);
    }

    private R methodNotToCompile(Scenario<T, R> scenario, T t) {
        return scenario.run(t);
    }
}

