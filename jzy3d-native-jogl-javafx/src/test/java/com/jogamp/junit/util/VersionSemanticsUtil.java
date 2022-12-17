/**
 * Copyright 2014 JogAmp Community. All rights reserved.
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

public class VersionSemanticsUtil {

    /*public static void testVersion(final DiffCriteria diffCriteria,
                                   final Delta.CompatibilityType expectedCompatibilityType,
                                   final File previousJar, final VersionNumberString preVersionNumber,
                                   final Class<?> currentJarClazz, final ClassLoader currentJarCL, final VersionNumberString curVersionNumber,
                                   final Set<String> excludesRegExp)
                                       throws IllegalArgumentException, IOException, URISyntaxException
    {
        // Get containing JAR file "TestJarsInJar.jar" and add it to the TempJarCache
        final Uri currentJarUri = JarUtil.getJarUri(currentJarClazz.getName(), currentJarCL).getContainedUri();
        testVersion(diffCriteria, expectedCompatibilityType,
                    previousJar, preVersionNumber,
                    currentJarUri.toFile(), curVersionNumber,
                    excludesRegExp);
    }

    public static void testVersion(final DiffCriteria diffCriteria,
                                   final Delta.CompatibilityType expectedCompatibilityType,
                                   final File previousJar, final VersionNumberString preVersionNumber,
                                   final File currentJar, final VersionNumberString curVersionNumber,
                                   final Set<String> excludesRegExp)
                                       throws IllegalArgumentException, IOException, URISyntaxException
    {
        final Set<String> includesRegExp = new HashSet<String>();

        final Comparer comparer = new Comparer(diffCriteria, previousJar, currentJar, includesRegExp, true, excludesRegExp, true);
        final Delta delta = comparer.diff();

        //Validates that computed and provided compatibility type are compatible.
        final Delta.CompatibilityType detectedCompatibilityType = delta.computeCompatibilityType();
        final int comp = detectedCompatibilityType.compareTo(expectedCompatibilityType);
        final boolean compOK = 0 >= comp;
        final String compS;
        if( 0 > comp ) {
            compS = "< ";
        } else if ( 0 == comp ) {
            compS = "==";
        } else {
            compS = "> ";
        }

        System.err.println("Semantic Version Test");
        System.err.println(" criteria: "+diffCriteria);
        System.err.println(" Previous version: "+preVersionNumber+" - "+previousJar.toString());
        System.err.println(" Current  version: "+curVersionNumber+" - "+currentJar.toString());
        System.err.println(" Field values changed: "+delta.fieldCompatChanged());
        System.err.println(" Compat. expected: "+expectedCompatibilityType);
        System.err.println(" Compat. detected: "+detectedCompatibilityType);
        System.err.println(" Compat. result:   detected "+compS+" expected -> "+(compOK ? "OK" : "ERROR"));
        final String resS;
        if( compOK ) {
            resS = " Current version "+curVersionNumber+" is "+expectedCompatibilityType+" to previous version "+preVersionNumber+", actually "+detectedCompatibilityType;
        } else {
            resS = " Current version "+curVersionNumber+" is not "+expectedCompatibilityType+" to previous version "+preVersionNumber+", but "+detectedCompatibilityType;
        }
        System.err.println(resS);
        System.err.printf("%n%n");

        Dumper.dumpFullStats(delta, 4, System.err);

        // Also dump sorted by class name
        System.err.printf("%n%nClass Order%n%n");
        Dumper.dump(delta, System.err);

        Assert.assertTrue(resS, compOK);

    }*/
}
