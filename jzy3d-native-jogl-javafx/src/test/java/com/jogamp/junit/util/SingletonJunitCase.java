/**
 * Copyright 2010 JogAmp Community. All rights reserved.
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

import com.jogamp.common.util.locks.SingletonInstance;

import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public abstract class SingletonJunitCase extends JunitTracer {
    public static final String SINGLE_INSTANCE_LOCK_FILE = "SingletonTestCase.lock";
    public static final int SINGLE_INSTANCE_LOCK_PORT = 59999;

    public static final long SINGLE_INSTANCE_LOCK_TO   = 15*60*1000; // wait up to 15 mins
    public static final long SINGLE_INSTANCE_LOCK_POLL =        500; // poll every 500 ms

    private static SingletonInstance singletonInstance = null; // system wide lock via port locking
    private static final Object singletonSync = new Object();  // classloader wide lock
    private static boolean enabled = true;

    /**
     * Default is {@code true}.
     */
    public static final void enableSingletonLock(final boolean v) {
        enabled = v;
    }

    @BeforeClass
    public static final void oneTimeSetUpSingleton() {
        // one-time initialization code
        synchronized( singletonSync ) {
            if( enabled ) {
                if( null == singletonInstance )  {
                    System.err.println("++++ Test Singleton.ctor()");
                    // singletonInstance = SingletonInstance.createFileLock(SINGLE_INSTANCE_LOCK_POLL, SINGLE_INSTANCE_LOCK_FILE);
                    singletonInstance = SingletonInstance.createServerSocket(SINGLE_INSTANCE_LOCK_POLL, SINGLE_INSTANCE_LOCK_PORT);
                }
                System.err.println("++++ Test Singleton.lock()");
                if(!singletonInstance.tryLock(SINGLE_INSTANCE_LOCK_TO)) {
                    throw new RuntimeException("Fatal: Could not lock single instance: "+singletonInstance.getName());
                }
            }
        }
    }

    @AfterClass
    public static final void oneTimeTearDownSingleton() {
        // one-time cleanup code
        synchronized( singletonSync ) {
            System.gc(); // force cleanup
            if( enabled ) {
                System.err.println("++++ Test Singleton.unlock()");
                singletonInstance.unlock();
                try {
                    // allowing other JVM instances to pick-up socket
                    Thread.sleep( SINGLE_INSTANCE_LOCK_POLL );
                } catch (final InterruptedException e) { }
            }
        }
    }
}

