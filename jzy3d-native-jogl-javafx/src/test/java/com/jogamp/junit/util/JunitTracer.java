/**
 * Copyright 2011 JogAmp Community. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 *
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY JogAmp Community ``AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL JogAmp Community OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those of the
 * authors and should not be interpreted as representing official policies, either expressed
 * or implied, of JogAmp Community.
 */

package com.jogamp.junit.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.junit.runners.MethodSorters;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public abstract class JunitTracer {
    @Rule public final TestName _unitTestName = new TestName();

    static volatile boolean testSupported = true;

    public static final boolean isTestSupported() {
        return testSupported;
    }

    public static final void setTestSupported(final boolean v) {
        System.err.println("setTestSupported: "+v);
        testSupported = v;
    }

    public final String getTestMethodName() {
        return _unitTestName.getMethodName();
    }

    public final String getSimpleTestName(final String separator) {
        return getClass().getSimpleName()+separator+getTestMethodName();
    }

    public final String getFullTestName(final String separator) {
        return getClass().getName()+separator+getTestMethodName();
    }

    @BeforeClass
    public static final void oneTimeSetUpBase() {
        // one-time initialization code
    }

    @AfterClass
    public static final void oneTimeTearDownBase() {
        // one-time cleanup code
        System.gc(); // force cleanup
    }

    @Before
    public final void setUpBase() {
        System.err.print("++++ TestCase.setUp: "+getFullTestName(" - "));
        if(!testSupported) {
            System.err.println(" - "+unsupportedTestMsg);
            Assume.assumeTrue(testSupported);
        }
        System.err.println();
    }

    @After
    public final void tearDownBase() {
        System.err.println("++++ TestCase.tearDown: "+getFullTestName(" - "));
    }

    static final String unsupportedTestMsg = "Test not supported on this platform.";

    public static void waitForKey(final String preMessage) {
        final BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
        System.err.println(preMessage+"> Press enter to continue");
        try {
            System.err.println(stdin.readLine());
        } catch (final IOException e) { e.printStackTrace(); }
    }
}

