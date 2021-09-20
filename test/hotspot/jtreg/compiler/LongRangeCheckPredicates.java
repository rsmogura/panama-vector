/*
 * Copyright (c) 2014, 2021, Oracle and/or its affiliates. All rights reserved.
 * (c) Rado (& Ewa) Smogura, 2021
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

/**
 * @test
 * @summary Tests long range checks predicates
 * @run main/othervm/native -XX:+UnlockDiagnosticVMOptions -XX:+WhiteBoxAPI
 *  -XX:CompileThreshold=5000 -XX:-BackgroundCompilation -XX:-TieredCompilation -XX:+LongRCE
 *  -XX:CompileCommand=dontinline,compiler.LongRangeCheckPredicates::*
 *  -XX:+LogCompilation -XX:LogFile=LongRangeCheckPredicates.compile.log
 *  compiler.LongRangeCheckPredicates
 */
package compiler;

import java.util.Objects;

public class LongRangeCheckPredicates {

    public static void main(String[] args) {
        for (int i=0; i < 30_000; i++) {
            long result;
            if (4851 != (result = testRCE(0,99,100)))
                throw new AssertionError(result);
            // Effective index: 9223372036854775789 <= i < 9223372036854775799
            if (-155 != (result = testRCE_OverflowBoth(-20, -10, Long.MAX_VALUE)))
                throw new AssertionError(result);
            try {
                // Effective index 9223372036854775799, range 9223372036854775807
                testRCE_OverflowLow(-10, 10, Long.MAX_VALUE);
                throw new AssertionError("Should not be here");
            }catch (IndexOutOfBoundsException aoe) {
                System.out.printf(aoe.getMessage());
            }
        }

    }

    private static long testRCE(int start, int limit, long length) {
        long sum = 0;
        for (int i=start; i < limit; i++) {
            Objects.checkIndex(i, length);
            sum += i;
        }

        return sum;
    }

    private static long testRCE_OverflowBoth(int start, int limit, long length) {
        long sum = 0;
        for (int i=start; i < limit; i++) {
            Objects.checkIndex(i + -Long.MAX_VALUE, length);
            sum += i;
        }

        return sum;
    }

    private static long testRCE_OverflowLow(int start, int limit, long length) {
        long sum = 0;
        for (int i=start; i < limit; i++) {
            Objects.checkIndex(i + -Long.MAX_VALUE, length);
            sum += i;
        }

        return sum;
    }
}
