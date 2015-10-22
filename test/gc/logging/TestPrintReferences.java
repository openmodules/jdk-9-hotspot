/*
 * Copyright (c) 2015, Oracle and/or its affiliates. All rights reserved.
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

/*
 * @test TestPrintReferences
 * @bug 8136991
 * @summary Validate the reference processing logging
 * @key gc
 * @library /testlibrary
 * @modules java.base/sun.misc
 *          java.management
 */

import jdk.test.lib.ProcessTools;
import jdk.test.lib.OutputAnalyzer;

public class TestPrintReferences {
  public static void main(String[] args) throws Exception {
    ProcessBuilder pb_enabled =
      ProcessTools.createJavaProcessBuilder("-XX:+PrintGCDetails", "-XX:+PrintReferenceGC", "-Xmx10M", GCTest.class.getName());
    OutputAnalyzer output = new OutputAnalyzer(pb_enabled.start());

    String countRegex = "[0-9]+ refs";
    String timeRegex = "[0-9]+[.,][0-9]+ secs";

    output.shouldMatch(
      "#[0-9]+: \\[SoftReference, " + countRegex + ", " + timeRegex + "\\]" +
      "#[0-9]+: \\[WeakReference, " + countRegex + ", " + timeRegex + "\\]" +
      "#[0-9]+: \\[FinalReference, " + countRegex + ", " + timeRegex + "\\]" +
      "#[0-9]+: \\[PhantomReference, " + countRegex + ", " + timeRegex + "\\]" +
      "#[0-9]+: \\[JNI Weak Reference, (" + countRegex + ", )?" + timeRegex + "\\]");

    output.shouldHaveExitValue(0);
  }

  static class GCTest {
    public static void main(String [] args) {
      System.gc();
    }
  }
}