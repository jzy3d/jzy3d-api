To be built with JDK17, involving JavaFX 19 to be provided (Gluon distrib).


# Offscreen rendering with Javafx8

* DONE / Error 1  : one frame late
* Error 2 : downsize does not fit in width


# Offscreen rendering with Javafx17

## Error 1 :  
* Freezing because of a call to object.wait triggered in JAWTUtil l.468. 
* Which disappears if one comment this.

## Error 2  : can't use textures






# Onscreen image

Caused by: java.lang.RuntimeException: Exception in Application start method
	at javafx.graphics@19/com.sun.javafx.application.LauncherImpl.launchApplication1(LauncherImpl.java:901)
	at javafx.graphics@19/com.sun.javafx.application.LauncherImpl.lambda$launchApplication$2(LauncherImpl.java:196)
	at java.base/java.lang.Thread.run(Thread.java:833)
Caused by: java.lang.UnsatisfiedLinkError: Can't load library: /Users/martin/Dev/jzy3d/public/jzy3d-api/jzy3d-native-jogl-javafx/natives/macosx-universal/nativewindow_awt
	at java.base/java.lang.ClassLoader.loadLibrary(ClassLoader.java:2393)
	at java.base/java.lang.Runtime.load0(Runtime.java:755)
	at java.base/java.lang.System.load(System.java:1953)
	at com.jogamp.common.jvm.JNILibLoaderBase.loadLibraryInternal(JNILibLoaderBase.java:625)
	at com.jogamp.common.jvm.JNILibLoaderBase.access$000(JNILibLoaderBase.java:64)
	at com.jogamp.common.jvm.JNILibLoaderBase$DefaultAction.loadLibrary(JNILibLoaderBase.java:107)
	at com.jogamp.common.jvm.JNILibLoaderBase.loadLibrary(JNILibLoaderBase.java:488)
	at jogamp.nativewindow.NWJNILibLoader.access$000(NWJNILibLoader.java:39)
	at jogamp.nativewindow.NWJNILibLoader$1.run(NWJNILibLoader.java:49)
	at jogamp.nativewindow.NWJNILibLoader$1.run(NWJNILibLoader.java:41)
	at java.base/java.security.AccessController.doPrivileged(AccessController.java:318)
	at jogamp.nativewindow.NWJNILibLoader.loadNativeWindow(NWJNILibLoader.java:41)
	at jogamp.nativewindow.jawt.JAWTUtil.<clinit>(JAWTUtil.java:345)
	at java.base/java.lang.Class.forName0(Native Method)
	at java.base/java.lang.Class.forName(Class.java:467)
	at com.jogamp.nativewindow.NativeWindowFactory$2.run(NativeWindowFactory.java:405)
	at com.jogamp.nativewindow.NativeWindowFactory$2.run(NativeWindowFactory.java:401)
	at java.base/java.security.AccessController.doPrivileged(AccessController.java:318)
	at com.jogamp.nativewindow.NativeWindowFactory.initSingleton(NativeWindowFactory.java:401)
	at com.jogamp.newt.NewtFactory$1.run(NewtFactory.java:69)
	at java.base/java.security.AccessController.doPrivileged(AccessController.java:318)
	at com.jogamp.newt.NewtFactory.<clinit>(NewtFactory.java:66)
	at org.jzy3d.demos.javafx.App.start(App.java:36)
	at javafx.graphics@19/com.sun.javafx.application.LauncherImpl.lambda$launchApplication1$9(LauncherImpl.java:847)
	at javafx.graphics@19/com.sun.javafx.application.PlatformImpl.lambda$runAndWait$12(PlatformImpl.java:484)
	at javafx.graphics@19/com.sun.javafx.application.PlatformImpl.lambda$runLater$10(PlatformImpl.java:457)
	at java.base/java.security.AccessController.doPrivileged(AccessController.java:399)
	at javafx.graphics@19/com.sun.javafx.application.PlatformImpl.lambda$runLater$11(PlatformImpl.java:456)
	at javafx.graphics@19/com.sun.glass.ui.InvokeLaterDispatcher$Future.run(InvokeLaterDispatcher.java:96)
Exception running application org.jzy3d.demos.javafx.App


Cas KO
IOUtil.getTempRoot(): tempX1 </var/folders/d2/85g_sxm91rg71yyky4gg6l6h0000gn/T>, used true
IOUtil.getTempRoot(): tempX3 </var/folders/d2/85g_sxm91rg71yyky4gg6l6h0000gn/T>, used false
IOUtil.getTempRoot(): tempX4 </Users/martin>, used true
IOUtil.getTempRoot(): tempX2 <null>, used false
IOUtil.testDirImpl(tempX1): </var/folders/d2/85g_sxm91rg71yyky4gg6l6h0000gn/T>, create true, exec false: true
IOUtil.testDirImpl(tempX1): </var/folders/d2/85g_sxm91rg71yyky4gg6l6h0000gn/T/jogamp_0000>, create true, exec false: true
IOUtil.getTempRoot(): temp dirs: exec: /var/folders/d2/85g_sxm91rg71yyky4gg6l6h0000gn/T/jogamp_0000, noexec: /var/folders/d2/85g_sxm91rg71yyky4gg6l6h0000gn/T/jogamp_0000
IOUtil.testDirImpl(testDir): </var/folders/d2/85g_sxm91rg71yyky4gg6l6h0000gn/T/jogamp_0000/file_cache>, create true, exec false: true

ls -ahl /var/folders/d2/85g_sxm91rg71yyky4gg6l6h0000gn/T/jogamp_0000/file_cache/jln15031640269207408442/jln13351577871358034462/natives/macosx-universal/
total 168
drwxr-xr-x  3 martin  staff    96B 16 déc 18:00 .
drwxr-xr-x  3 martin  staff    96B 16 déc 18:00 ..
-rw-r--r--  1 martin  staff    84K 16 déc 18:00 libgluegen_rt.dylib


IOUtil.getTempRoot(): tempX1 </var/folders/d2/85g_sxm91rg71yyky4gg6l6h0000gn/T>, used true
IOUtil.getTempRoot(): tempX3 </var/folders/d2/85g_sxm91rg71yyky4gg6l6h0000gn/T>, used false
IOUtil.getTempRoot(): tempX4 </Users/martin>, used true
IOUtil.getTempRoot(): tempX2 <null>, used false
IOUtil.testDirImpl(tempX1): </var/folders/d2/85g_sxm91rg71yyky4gg6l6h0000gn/T>, create true, exec false: true
IOUtil.testDirImpl(tempX1): </var/folders/d2/85g_sxm91rg71yyky4gg6l6h0000gn/T/jogamp_0000>, create true, exec false: true
IOUtil.getTempRoot(): temp dirs: exec: /var/folders/d2/85g_sxm91rg71yyky4gg6l6h0000gn/T/jogamp_0000, noexec: /var/folders/d2/85g_sxm91rg71yyky4gg6l6h0000gn/T/jogamp_0000
IOUtil.testDirImpl(testDir): </var/folders/d2/85g_sxm91rg71yyky4gg6l6h0000gn/T/jogamp_0000/file_cache>, create true, exec false: true
FALLBACK (log once): Fallback to SW vertex for line stipple
FALLBACK (log once): Fallback to SW vertex processing, m_disable_code: 2000
FALLBACK (log once): Fallback to SW vertex processing in drawCore, m_disable_code: 2000

ls -ahl /var/folders/d2/85g_sxm91rg71yyky4gg6l6h0000gn/T/jogamp_0000/file_cache/jln8689435615232869924/jln1125319555183817591/natives/macosx-universal/
total 5920
drwxr-xr-x  8 martin  staff   256B 16 déc 18:03 .
drwxr-xr-x  3 martin  staff    96B 16 déc 18:03 ..
-rw-r--r--  1 martin  staff    84K 16 déc 18:03 libgluegen_rt.dylib
-rw-r--r--  1 martin  staff   1,6M 16 déc 18:03 libjogl_desktop.dylib
-rw-r--r--  1 martin  staff   823K 16 déc 18:03 libjogl_mobile.dylib
-rw-r--r--  1 martin  staff    66K 16 déc 18:03 libnativewindow_awt.dylib
-rw-r--r--  1 martin  staff   120K 16 déc 18:03 libnativewindow_macosx.dylib
-rw-r--r--  1 martin  staff   189K 16 déc 18:03 libnewt_head.dylib



getJarUri Default jar:file:/Users/martin/.m2/repository/org/jogamp/gluegen/gluegen-rt/v2.4.0-rc4/gluegen-rt-v2.4.0-rc4.jar!/com/jogamp/common/os/Platform.class
	-> jar:file:/Users/martin/.m2/repository/org/jogamp/gluegen/gluegen-rt/v2.4.0-rc4/gluegen-rt-v2.4.0-rc4.jar!/com/jogamp/common/os/Platform.class
getJarUri res: com.jogamp.common.os.Platform -> jar:file:/Users/martin/.m2/repository/org/jogamp/gluegen/gluegen-rt/v2.4.0-rc4/gluegen-rt-v2.4.0-rc4.jar!/com/jogamp/common/os/Platform.class -> jar:file:/Users/martin/.m2/repository/org/jogamp/gluegen/gluegen-rt/v2.4.0-rc4/gluegen-rt-v2.4.0-rc4.jar!/com/jogamp/common/os/Platform.class
JNILibLoaderBase: addNativeJarLibs(
  classesFromJavaJars   = [class jogamp.common.Debug]
  singleJarMarker       = null
)
getJarUri Default jar:file:/Users/martin/.m2/repository/org/jogamp/gluegen/gluegen-rt/v2.4.0-rc4/gluegen-rt-v2.4.0-rc4.jar!/jogamp/common/Debug.class
	-> jar:file:/Users/martin/.m2/repository/org/jogamp/gluegen/gluegen-rt/v2.4.0-rc4/gluegen-rt-v2.4.0-rc4.jar!/jogamp/common/Debug.class
getJarUri res: jogamp.common.Debug -> jar:file:/Users/martin/.m2/repository/org/jogamp/gluegen/gluegen-rt/v2.4.0-rc4/gluegen-rt-v2.4.0-rc4.jar!/jogamp/common/Debug.class -> jar:file:/Users/martin/.m2/repository/org/jogamp/gluegen/gluegen-rt/v2.4.0-rc4/gluegen-rt-v2.4.0-rc4.jar!/jogamp/common/Debug.class
getJarName res: gluegen-rt-v2.4.0-rc4.jar
JNILibLoaderBase: jarBasename: gluegen-rt-v2.4.0-rc4
JNILibLoaderBase: addNativeJarLibsImpl(
  classFromJavaJar  = class jogamp.common.Debug
  classJarURI       = jar:file:/Users/martin/.m2/repository/org/jogamp/gluegen/gluegen-rt/v2.4.0-rc4/gluegen-rt-v2.4.0-rc4.jar!/jogamp/common/Debug.class
  jarBasename       = gluegen-rt-v2.4.0-rc4.jar
  os.and.arch       = macosx-universal
  nativeJarBasename = gluegen-rt-v2.4.0-rc4-natives-macosx-universal.jar
)
JNILibLoaderBase: addNativeJarLibsImpl: initial: file:/Users/martin/.m2/repository/org/jogamp/gluegen/gluegen-rt/v2.4.0-rc4/gluegen-rt-v2.4.0-rc4.jar -> file:/Users/martin/.m2/repository/org/jogamp/gluegen/gluegen-rt/v2.4.0-rc4/
JNILibLoaderBase: addNativeJarLibsImpl: nativeLibraryPath: natives/macosx-universal/
JNILibLoaderBase: addNativeJarLibsImpl: module: gluegen-rt-v2.4.0-rc4-natives-macosx-universal.jar -> jar:file:/Users/martin/.m2/repository/org/jogamp/gluegen/gluegen-rt/v2.4.0-rc4/gluegen-rt-v2.4.0-rc4-natives-macosx-universal.jar!/
getJarFile.0: jar:file:/Users/martin/.m2/repository/org/jogamp/gluegen/gluegen-rt/v2.4.0-rc4/gluegen-rt-v2.4.0-rc4-natives-macosx-universal.jar!/
getJarFile.1: jar:file:/Users/martin/.m2/repository/org/jogamp/gluegen/gluegen-rt/v2.4.0-rc4/gluegen-rt-v2.4.0-rc4-natives-macosx-universal.jar!/
JNILibLoaderBase: addNativeJarLibsImpl: Caught /Users/martin/.m2/repository/org/jogamp/gluegen/gluegen-rt/v2.4.0-rc4/gluegen-rt-v2.4.0-rc4-natives-macosx-universal.jar (No such file or directory)
java.io.FileNotFoundException: /Users/martin/.m2/repository/org/jogamp/gluegen/gluegen-rt/v2.4.0-rc4/gluegen-rt-v2.4.0-rc4-natives-macosx-universal.jar (No such file or directory)
	at java.util.zip.ZipFile.open(Native Method)
	at java.util.zip.ZipFile.<init>(ZipFile.java:225)
	at java.util.zip.ZipFile.<init>(ZipFile.java:155)
	at java.util.jar.JarFile.<init>(JarFile.java:166)
	at java.util.jar.JarFile.<init>(JarFile.java:103)
	at sun.net.www.protocol.jar.URLJarFile.<init>(URLJarFile.java:93)
	at sun.net.www.protocol.jar.URLJarFile.getJarFile(URLJarFile.java:69)
	at sun.net.www.protocol.jar.JarFileFactory.get(JarFileFactory.java:84)
	at sun.net.www.protocol.jar.JarURLConnection.connect(JarURLConnection.java:122)
	at sun.net.www.protocol.jar.JarURLConnection.getJarFile(JarURLConnection.java:89)
	at com.jogamp.common.util.JarUtil.getJarFile(JarUtil.java:403)
	at com.jogamp.common.util.cache.TempJarCache.addNativeLibs(TempJarCache.java:270)
	at com.jogamp.common.jvm.JNILibLoaderBase.addNativeJarLibsImpl(JNILibLoaderBase.java:221)
	at com.jogamp.common.jvm.JNILibLoaderBase.addNativeJarLibsWithTempJarCache(JNILibLoaderBase.java:457)
	at com.jogamp.common.jvm.JNILibLoaderBase.addNativeJarLibs(JNILibLoaderBase.java:409)
	at com.jogamp.common.os.Platform$1.run(Platform.java:315)
	at java.security.AccessController.doPrivileged(Native Method)
	at com.jogamp.common.os.Platform.<clinit>(Platform.java:290)
	at com.jogamp.opengl.GLProfile.<clinit>(GLProfile.java:154)
	at org.jzy3d.chart.factories.NativePainterFactory.detectGLProfile(NativePainterFactory.java:121)
	at org.jzy3d.chart.factories.NativePainterFactory.<init>(NativePainterFactory.java:41)
	at org.jzy3d.chart.factories.AWTPainterFactory.<init>(AWTPainterFactory.java:40)
	at org.jzy3d.javafx.AbstractJavaFXPainterFactory.<init>(AbstractJavaFXPainterFactory.java:24)
	at org.jzy3d.javafx.offscreen.JavaFXOffscreenPainterFactory.<init>(JavaFXOffscreenPainterFactory.java:7)
	at org.jzy3d.javafx.offscreen.JavaFXOffscreenChartFactory.<init>(JavaFXOffscreenChartFactory.java:39)
	at org.jzy3d.demos.javafx.DemoJzy3dFX_OpenJDK8.start(DemoJzy3dFX_OpenJDK8.java:47)
	at com.sun.javafx.application.LauncherImpl.lambda$launchApplication1$166(LauncherImpl.java:863)
	at com.sun.javafx.application.PlatformImpl.lambda$runAndWait$179(PlatformImpl.java:326)
	at com.sun.javafx.application.PlatformImpl.lambda$null$177(PlatformImpl.java:295)
	at java.security.AccessController.doPrivileged(Native Method)
	at com.sun.javafx.application.PlatformImpl.lambda$runLater$178(PlatformImpl.java:294)
	at com.sun.glass.ui.InvokeLaterDispatcher$Future.run(InvokeLaterDispatcher.java:95)
JNILibLoaderBase: addNativeJarLibsImpl: ClassLoader/TAG: Locating module common, os.and.arch macosx.universal: jogamp.nativetag.common.macosx.universal.TAG
getJarUri Default jar:file:/Users/martin/.m2/repository/org/jogamp/gluegen/gluegen-rt-natives-macosx-universal/v2.4.0-rc4/gluegen-rt-natives-macosx-universal-v2.4.0-rc4.jar!/jogamp/nativetag/common/macosx/universal/TAG.class
	-> jar:file:/Users/martin/.m2/repository/org/jogamp/gluegen/gluegen-rt-natives-macosx-universal/v2.4.0-rc4/gluegen-rt-natives-macosx-universal-v2.4.0-rc4.jar!/jogamp/nativetag/common/macosx/universal/TAG.class
getJarUri res: jogamp.nativetag.common.macosx.universal.TAG -> jar:file:/Users/martin/.m2/repository/org/jogamp/gluegen/gluegen-rt-natives-macosx-universal/v2.4.0-rc4/gluegen-rt-natives-macosx-universal-v2.4.0-rc4.jar!/jogamp/nativetag/common/macosx/universal/TAG.class -> jar:file:/Users/martin/.m2/repository/org/jogamp/gluegen/gluegen-rt-natives-macosx-universal/v2.4.0-rc4/gluegen-rt-natives-macosx-universal-v2.4.0-rc4.jar!/jogamp/nativetag/common/macosx/universal/TAG.class
JNILibLoaderBase: addNativeJarLibsImpl: ClassLoader/TAG: jogamp.nativetag.common.macosx.universal.TAG -> jar:file:/Users/martin/.m2/repository/org/jogamp/gluegen/gluegen-rt-natives-macosx-universal/v2.4.0-rc4/gluegen-rt-natives-macosx-universal-v2.4.0-rc4.jar!/jogamp/nativetag/common/macosx/universal/TAG.class
getJarFile.0: jar:file:/Users/martin/.m2/repository/org/jogamp/gluegen/gluegen-rt-natives-macosx-universal/v2.4.0-rc4/gluegen-rt-natives-macosx-universal-v2.4.0-rc4.jar!/jogamp/nativetag/common/macosx/universal/TAG.class
getJarFile.1: jar:file:/Users/martin/.m2/repository/org/jogamp/gluegen/gluegen-rt-natives-macosx-universal/v2.4.0-rc4/gluegen-rt-natives-macosx-universal-v2.4.0-rc4.jar!/jogamp/nativetag/common/macosx/universal/TAG.class
getJarFile res: /Users/martin/.m2/repository/org/jogamp/gluegen/gluegen-rt-natives-macosx-universal/v2.4.0-rc4/gluegen-rt-natives-macosx-universal-v2.4.0-rc4.jar
JarUtil: extract: /Users/martin/.m2/repository/org/jogamp/gluegen/gluegen-rt-natives-macosx-universal/v2.4.0-rc4/gluegen-rt-natives-macosx-universal-v2.4.0-rc4.jar -> /var/folders/d2/85g_sxm91rg71yyky4gg6l6h0000gn/T/jogamp_0000/file_cache/jln2160290438920784955/jln1278812486691372267, extractNativeLibraries true (natives/macosx-universal/), extractClassFiles false, extractOtherFiles false
JarUtil: JarEntry : META-INF/MANIFEST.MF other-file skipped
JarUtil: JarEntry : jogamp/nativetag/common/macosx/universal/TAG.class class-file skipped
JarUtil: JarEntry : isNativeLib true, isClassFile false, isDir false, isRootEntry false
JarUtil: MKDIR (parent): natives/macosx-universal/libgluegen_rt.dylib -> /var/folders/d2/85g_sxm91rg71yyky4gg6l6h0000gn/T/jogamp_0000/file_cache/jln2160290438920784955/jln1278812486691372267/natives/macosx-universal
JarUtil.fixNativeLibAttribs: /var/folders/d2/85g_sxm91rg71yyky4gg6l6h0000gn/T/jogamp_0000/file_cache/jln2160290438920784955/jln1278812486691372267/natives/macosx-universal/libgluegen_rt.dylib - UnsatisfiedLinkError: com.jogamp.common.util.JarUtil.fixNativeLibAttribs(Ljava/lang/String;)Z
JarUtil: EXTRACT[1]: [gluegen_rt -> ] natives/macosx-universal/libgluegen_rt.dylib -> /var/folders/d2/85g_sxm91rg71yyky4gg6l6h0000gn/T/jogamp_0000/file_cache/jln2160290438920784955/jln1278812486691372267/natives/macosx-universal/libgluegen_rt.dylib: 85840 bytes, addedAsNativeLib: true
JNILibLoaderBase: addNativeJarLibsImpl.X: gluegen-rt-v2.4.0-rc4.jar / gluegen-rt-v2.4.0-rc4-natives-macosx-universal.jar -> ok: true; duration: now 10842 ms, total 10842 ms (count 1, avrg 10842,000 ms)
JNILibLoaderBase: addNativeJarLibsWhenInitialized: count 1, ok true
JNILibLoaderBase: loadLibraryInternal(gluegen_rt), TempJarCache: /var/folders/d2/85g_sxm91rg71yyky4gg6l6h0000gn/T/jogamp_0000/file_cache/jln2160290438920784955/jln1278812486691372267/natives/macosx-universal/libgluegen_rt.dylib
JNILibLoaderBase: System.load(/var/folders/d2/85g_sxm91rg71yyky4gg6l6h0000gn/T/jogamp_0000/file_cache/jln2160290438920784955/jln1278812486691372267/natives/macosx-universal/libgluegen_rt.dylib) - mode 2
JNILibLoaderBase: loadLibraryInternal(gluegen_rt): OK - mode 2
JNILibLoaderBase: Loaded Native Library: gluegen_rt
JNILibLoaderBase: loaded gluegen_rt
JNILibLoaderBase: addNativeJarLibs(
  classesFromJavaJars   = [class jogamp.nativewindow.Debug, class jogamp.opengl.Debug, class jogamp.newt.Debug]
  singleJarMarker       = -all
)
getJarUri Default jar:file:/Users/martin/.m2/repository/org/jogamp/jogl/jogl-all/v2.4.0-rc4/jogl-all-v2.4.0-rc4.jar!/jogamp/nativewindow/Debug.class
	-> jar:file:/Users/martin/.m2/repository/org/jogamp/jogl/jogl-all/v2.4.0-rc4/jogl-all-v2.4.0-rc4.jar!/jogamp/nativewindow/Debug.class
getJarUri res: jogamp.nativewindow.Debug -> jar:file:/Users/martin/.m2/repository/org/jogamp/jogl/jogl-all/v2.4.0-rc4/jogl-all-v2.4.0-rc4.jar!/jogamp/nativewindow/Debug.class -> jar:file:/Users/martin/.m2/repository/org/jogamp/jogl/jogl-all/v2.4.0-rc4/jogl-all-v2.4.0-rc4.jar!/jogamp/nativewindow/Debug.class
getJarName res: jogl-all-v2.4.0-rc4.jar
JNILibLoaderBase: jarBasename: jogl-all-v2.4.0-rc4
JNILibLoaderBase: addNativeJarLibsImpl(
  classFromJavaJar  = class jogamp.nativewindow.Debug
  classJarURI       = jar:file:/Users/martin/.m2/repository/org/jogamp/jogl/jogl-all/v2.4.0-rc4/jogl-all-v2.4.0-rc4.jar!/jogamp/nativewindow/Debug.class
  jarBasename       = jogl-all-v2.4.0-rc4.jar
  os.and.arch       = macosx-universal
  nativeJarBasename = jogl-all-v2.4.0-rc4-natives-macosx-universal.jar
)
JNILibLoaderBase: addNativeJarLibsImpl: initial: file:/Users/martin/.m2/repository/org/jogamp/jogl/jogl-all/v2.4.0-rc4/jogl-all-v2.4.0-rc4.jar -> file:/Users/martin/.m2/repository/org/jogamp/jogl/jogl-all/v2.4.0-rc4/
JNILibLoaderBase: addNativeJarLibsImpl: nativeLibraryPath: natives/macosx-universal/
JNILibLoaderBase: addNativeJarLibsImpl: module: jogl-all-v2.4.0-rc4-natives-macosx-universal.jar -> jar:file:/Users/martin/.m2/repository/org/jogamp/jogl/jogl-all/v2.4.0-rc4/jogl-all-v2.4.0-rc4-natives-macosx-universal.jar!/
getJarFile.0: jar:file:/Users/martin/.m2/repository/org/jogamp/jogl/jogl-all/v2.4.0-rc4/jogl-all-v2.4.0-rc4-natives-macosx-universal.jar!/
getJarFile.1: jar:file:/Users/martin/.m2/repository/org/jogamp/jogl/jogl-all/v2.4.0-rc4/jogl-all-v2.4.0-rc4-natives-macosx-universal.jar!/
JNILibLoaderBase: addNativeJarLibsImpl: Caught /Users/martin/.m2/repository/org/jogamp/jogl/jogl-all/v2.4.0-rc4/jogl-all-v2.4.0-rc4-natives-macosx-universal.jar (No such file or directory)
java.io.FileNotFoundException: /Users/martin/.m2/repository/org/jogamp/jogl/jogl-all/v2.4.0-rc4/jogl-all-v2.4.0-rc4-natives-macosx-universal.jar (No such file or directory)
	at java.util.zip.ZipFile.open(Native Method)
	at java.util.zip.ZipFile.<init>(ZipFile.java:225)
	at java.util.zip.ZipFile.<init>(ZipFile.java:155)
	at java.util.jar.JarFile.<init>(JarFile.java:166)
	at java.util.jar.JarFile.<init>(JarFile.java:103)
	at sun.net.www.protocol.jar.URLJarFile.<init>(URLJarFile.java:93)
	at sun.net.www.protocol.jar.URLJarFile.getJarFile(URLJarFile.java:69)
	at sun.net.www.protocol.jar.JarFileFactory.get(JarFileFactory.java:84)
	at sun.net.www.protocol.jar.JarURLConnection.connect(JarURLConnection.java:122)
	at sun.net.www.protocol.jar.JarURLConnection.getJarFile(JarURLConnection.java:89)
	at com.jogamp.common.util.JarUtil.getJarFile(JarUtil.java:403)
	at com.jogamp.common.util.cache.TempJarCache.addNativeLibs(TempJarCache.java:270)
	at com.jogamp.common.jvm.JNILibLoaderBase.addNativeJarLibsImpl(JNILibLoaderBase.java:221)
	at com.jogamp.common.jvm.JNILibLoaderBase.addNativeJarLibsWithTempJarCache(JNILibLoaderBase.java:457)
	at com.jogamp.common.jvm.JNILibLoaderBase.addNativeJarLibs(JNILibLoaderBase.java:409)
	at com.jogamp.common.jvm.JNILibLoaderBase.addNativeJarLibsJoglCfg(JNILibLoaderBase.java:324)
	at com.jogamp.opengl.GLProfile$1.run(GLProfile.java:237)
	at java.security.AccessController.doPrivileged(Native Method)
	at com.jogamp.opengl.GLProfile.initSingleton(GLProfile.java:225)
	at com.jogamp.opengl.GLProfile.isAvailable(GLProfile.java:309)
	at com.jogamp.opengl.GLProfile.isAvailable(GLProfile.java:324)
	at org.jzy3d.chart.factories.NativePainterFactory.detectGLProfile(NativePainterFactory.java:121)
	at org.jzy3d.chart.factories.NativePainterFactory.<init>(NativePainterFactory.java:41)
	at org.jzy3d.chart.factories.AWTPainterFactory.<init>(AWTPainterFactory.java:40)
	at org.jzy3d.javafx.AbstractJavaFXPainterFactory.<init>(AbstractJavaFXPainterFactory.java:24)
	at org.jzy3d.javafx.offscreen.JavaFXOffscreenPainterFactory.<init>(JavaFXOffscreenPainterFactory.java:7)
	at org.jzy3d.javafx.offscreen.JavaFXOffscreenChartFactory.<init>(JavaFXOffscreenChartFactory.java:39)
	at org.jzy3d.demos.javafx.DemoJzy3dFX_OpenJDK8.start(DemoJzy3dFX_OpenJDK8.java:47)
	at com.sun.javafx.application.LauncherImpl.lambda$launchApplication1$166(LauncherImpl.java:863)
	at com.sun.javafx.application.PlatformImpl.lambda$runAndWait$179(PlatformImpl.java:326)
	at com.sun.javafx.application.PlatformImpl.lambda$null$177(PlatformImpl.java:295)
	at java.security.AccessController.doPrivileged(Native Method)
	at com.sun.javafx.application.PlatformImpl.lambda$runLater$178(PlatformImpl.java:294)
	at com.sun.glass.ui.InvokeLaterDispatcher$Future.run(InvokeLaterDispatcher.java:95)
JNILibLoaderBase: addNativeJarLibsImpl: ClassLoader/TAG: Locating module nativewindow, os.and.arch macosx.universal: jogamp.nativetag.nativewindow.macosx.universal.TAG
JNILibLoaderBase: addNativeJarLibsImpl: Caught Cannot not find: jogamp.nativetag.nativewindow.macosx.universal.TAG
java.io.IOException: Cannot not find: jogamp.nativetag.nativewindow.macosx.universal.TAG
	at com.jogamp.common.util.IOUtil.getClassURL(IOUtil.java:451)
	at com.jogamp.common.util.JarUtil.getJarUri(JarUtil.java:146)
	at com.jogamp.common.jvm.JNILibLoaderBase.addNativeJarLibsImpl(JNILibLoaderBase.java:273)
	at com.jogamp.common.jvm.JNILibLoaderBase.addNativeJarLibsWithTempJarCache(JNILibLoaderBase.java:457)
	at com.jogamp.common.jvm.JNILibLoaderBase.addNativeJarLibs(JNILibLoaderBase.java:409)
	at com.jogamp.common.jvm.JNILibLoaderBase.addNativeJarLibsJoglCfg(JNILibLoaderBase.java:324)
	at com.jogamp.opengl.GLProfile$1.run(GLProfile.java:237)
	at java.security.AccessController.doPrivileged(Native Method)
	at com.jogamp.opengl.GLProfile.initSingleton(GLProfile.java:225)
	at com.jogamp.opengl.GLProfile.isAvailable(GLProfile.java:309)
	at com.jogamp.opengl.GLProfile.isAvailable(GLProfile.java:324)
	at org.jzy3d.chart.factories.NativePainterFactory.detectGLProfile(NativePainterFactory.java:121)
	at org.jzy3d.chart.factories.NativePainterFactory.<init>(NativePainterFactory.java:41)
	at org.jzy3d.chart.factories.AWTPainterFactory.<init>(AWTPainterFactory.java:40)
	at org.jzy3d.javafx.AbstractJavaFXPainterFactory.<init>(AbstractJavaFXPainterFactory.java:24)
	at org.jzy3d.javafx.offscreen.JavaFXOffscreenPainterFactory.<init>(JavaFXOffscreenPainterFactory.java:7)
	at org.jzy3d.javafx.offscreen.JavaFXOffscreenChartFactory.<init>(JavaFXOffscreenChartFactory.java:39)
	at org.jzy3d.demos.javafx.DemoJzy3dFX_OpenJDK8.start(DemoJzy3dFX_OpenJDK8.java:47)
	at com.sun.javafx.application.LauncherImpl.lambda$launchApplication1$166(LauncherImpl.java:863)
	at com.sun.javafx.application.PlatformImpl.lambda$runAndWait$179(PlatformImpl.java:326)
	at com.sun.javafx.application.PlatformImpl.lambda$null$177(PlatformImpl.java:295)
	at java.security.AccessController.doPrivileged(Native Method)
	at com.sun.javafx.application.PlatformImpl.lambda$runLater$178(PlatformImpl.java:294)
	at com.sun.glass.ui.InvokeLaterDispatcher$Future.run(InvokeLaterDispatcher.java:95)
JNILibLoaderBase: addNativeJarLibsImpl.X: jogl-all-v2.4.0-rc4.jar / jogl-all-v2.4.0-rc4-natives-macosx-universal.jar -> ok: false; duration: now 6 ms, total 10848 ms (count 2, avrg 5424,000 ms)
JNILibLoaderBase: addNativeJarLibs0: done: jogl-all-v2.4.0-rc4
getJarUri Default jar:file:/Users/martin/.m2/repository/org/jogamp/jogl/jogl-all/v2.4.0-rc4/jogl-all-v2.4.0-rc4.jar!/jogamp/opengl/Debug.class
	-> jar:file:/Users/martin/.m2/repository/org/jogamp/jogl/jogl-all/v2.4.0-rc4/jogl-all-v2.4.0-rc4.jar!/jogamp/opengl/Debug.class
getJarUri res: jogamp.opengl.Debug -> jar:file:/Users/martin/.m2/repository/org/jogamp/jogl/jogl-all/v2.4.0-rc4/jogl-all-v2.4.0-rc4.jar!/jogamp/opengl/Debug.class -> jar:file:/Users/martin/.m2/repository/org/jogamp/jogl/jogl-all/v2.4.0-rc4/jogl-all-v2.4.0-rc4.jar!/jogamp/opengl/Debug.class
getJarName res: jogl-all-v2.4.0-rc4.jar
JNILibLoaderBase: jarBasename: jogl-all-v2.4.0-rc4
JNILibLoaderBase: addNativeJarLibsImpl(
  classFromJavaJar  = class jogamp.opengl.Debug
  classJarURI       = jar:file:/Users/martin/.m2/repository/org/jogamp/jogl/jogl-all/v2.4.0-rc4/jogl-all-v2.4.0-rc4.jar!/jogamp/opengl/Debug.class
  jarBasename       = jogl-all-v2.4.0-rc4.jar
  os.and.arch       = macosx-universal
  nativeJarBasename = jogl-all-v2.4.0-rc4-natives-macosx-universal.jar
)
JNILibLoaderBase: addNativeJarLibsImpl: initial: file:/Users/martin/.m2/repository/org/jogamp/jogl/jogl-all/v2.4.0-rc4/jogl-all-v2.4.0-rc4.jar -> file:/Users/martin/.m2/repository/org/jogamp/jogl/jogl-all/v2.4.0-rc4/
JNILibLoaderBase: addNativeJarLibsImpl: nativeLibraryPath: natives/macosx-universal/
JNILibLoaderBase: addNativeJarLibsImpl: module: jogl-all-v2.4.0-rc4-natives-macosx-universal.jar -> jar:file:/Users/martin/.m2/repository/org/jogamp/jogl/jogl-all/v2.4.0-rc4/jogl-all-v2.4.0-rc4-natives-macosx-universal.jar!/
JNILibLoaderBase: addNativeJarLibsImpl: Caught TempJarCache: addNativeLibs: jar:file:/Users/martin/.m2/repository/org/jogamp/jogl/jogl-all/v2.4.0-rc4/jogl-all-v2.4.0-rc4-natives-macosx-universal.jar!/, previous load attempt failed
java.io.IOException: TempJarCache: addNativeLibs: jar:file:/Users/martin/.m2/repository/org/jogamp/jogl/jogl-all/v2.4.0-rc4/jogl-all-v2.4.0-rc4-natives-macosx-universal.jar!/, previous load attempt failed
	at com.jogamp.common.util.cache.TempJarCache.addNativeLibs(TempJarCache.java:284)
	at com.jogamp.common.jvm.JNILibLoaderBase.addNativeJarLibsImpl(JNILibLoaderBase.java:221)
	at com.jogamp.common.jvm.JNILibLoaderBase.addNativeJarLibsWithTempJarCache(JNILibLoaderBase.java:457)
	at com.jogamp.common.jvm.JNILibLoaderBase.addNativeJarLibs(JNILibLoaderBase.java:409)
	at com.jogamp.common.jvm.JNILibLoaderBase.addNativeJarLibsJoglCfg(JNILibLoaderBase.java:324)
	at com.jogamp.opengl.GLProfile$1.run(GLProfile.java:237)
	at java.security.AccessController.doPrivileged(Native Method)
	at com.jogamp.opengl.GLProfile.initSingleton(GLProfile.java:225)
	at com.jogamp.opengl.GLProfile.isAvailable(GLProfile.java:309)
	at com.jogamp.opengl.GLProfile.isAvailable(GLProfile.java:324)
	at org.jzy3d.chart.factories.NativePainterFactory.detectGLProfile(NativePainterFactory.java:121)
	at org.jzy3d.chart.factories.NativePainterFactory.<init>(NativePainterFactory.java:41)
	at org.jzy3d.chart.factories.AWTPainterFactory.<init>(AWTPainterFactory.java:40)
	at org.jzy3d.javafx.AbstractJavaFXPainterFactory.<init>(AbstractJavaFXPainterFactory.java:24)
	at org.jzy3d.javafx.offscreen.JavaFXOffscreenPainterFactory.<init>(JavaFXOffscreenPainterFactory.java:7)
	at org.jzy3d.javafx.offscreen.JavaFXOffscreenChartFactory.<init>(JavaFXOffscreenChartFactory.java:39)
	at org.jzy3d.demos.javafx.DemoJzy3dFX_OpenJDK8.start(DemoJzy3dFX_OpenJDK8.java:47)
	at com.sun.javafx.application.LauncherImpl.lambda$launchApplication1$166(LauncherImpl.java:863)
	at com.sun.javafx.application.PlatformImpl.lambda$runAndWait$179(PlatformImpl.java:326)
	at com.sun.javafx.application.PlatformImpl.lambda$null$177(PlatformImpl.java:295)
	at java.security.AccessController.doPrivileged(Native Method)
	at com.sun.javafx.application.PlatformImpl.lambda$runLater$178(PlatformImpl.java:294)
	at com.sun.glass.ui.InvokeLaterDispatcher$Future.run(InvokeLaterDispatcher.java:95)
JNILibLoaderBase: addNativeJarLibsImpl: ClassLoader/TAG: Locating module opengl, os.and.arch macosx.universal: jogamp.nativetag.opengl.macosx.universal.TAG
getJarUri Default jar:file:/Users/martin/.m2/repository/org/jogamp/jogl/jogl-all-natives-macosx-universal/v2.4.0-rc4/jogl-all-natives-macosx-universal-v2.4.0-rc4.jar!/jogamp/nativetag/opengl/macosx/universal/TAG.class
	-> jar:file:/Users/martin/.m2/repository/org/jogamp/jogl/jogl-all-natives-macosx-universal/v2.4.0-rc4/jogl-all-natives-macosx-universal-v2.4.0-rc4.jar!/jogamp/nativetag/opengl/macosx/universal/TAG.class
getJarUri res: jogamp.nativetag.opengl.macosx.universal.TAG -> jar:file:/Users/martin/.m2/repository/org/jogamp/jogl/jogl-all-natives-macosx-universal/v2.4.0-rc4/jogl-all-natives-macosx-universal-v2.4.0-rc4.jar!/jogamp/nativetag/opengl/macosx/universal/TAG.class -> jar:file:/Users/martin/.m2/repository/org/jogamp/jogl/jogl-all-natives-macosx-universal/v2.4.0-rc4/jogl-all-natives-macosx-universal-v2.4.0-rc4.jar!/jogamp/nativetag/opengl/macosx/universal/TAG.class
JNILibLoaderBase: addNativeJarLibsImpl: ClassLoader/TAG: jogamp.nativetag.opengl.macosx.universal.TAG -> jar:file:/Users/martin/.m2/repository/org/jogamp/jogl/jogl-all-natives-macosx-universal/v2.4.0-rc4/jogl-all-natives-macosx-universal-v2.4.0-rc4.jar!/jogamp/nativetag/opengl/macosx/universal/TAG.class
getJarFile.0: jar:file:/Users/martin/.m2/repository/org/jogamp/jogl/jogl-all-natives-macosx-universal/v2.4.0-rc4/jogl-all-natives-macosx-universal-v2.4.0-rc4.jar!/jogamp/nativetag/opengl/macosx/universal/TAG.class
getJarFile.1: jar:file:/Users/martin/.m2/repository/org/jogamp/jogl/jogl-all-natives-macosx-universal/v2.4.0-rc4/jogl-all-natives-macosx-universal-v2.4.0-rc4.jar!/jogamp/nativetag/opengl/macosx/universal/TAG.class
getJarFile res: /Users/martin/.m2/repository/org/jogamp/jogl/jogl-all-natives-macosx-universal/v2.4.0-rc4/jogl-all-natives-macosx-universal-v2.4.0-rc4.jar
JarUtil: extract: /Users/martin/.m2/repository/org/jogamp/jogl/jogl-all-natives-macosx-universal/v2.4.0-rc4/jogl-all-natives-macosx-universal-v2.4.0-rc4.jar -> /var/folders/d2/85g_sxm91rg71yyky4gg6l6h0000gn/T/jogamp_0000/file_cache/jln2160290438920784955/jln1278812486691372267, extractNativeLibraries true (natives/macosx-universal/), extractClassFiles false, extractOtherFiles false
JarUtil: JarEntry : META-INF/MANIFEST.MF other-file skipped
JarUtil: JarEntry : jogamp/nativetag/opengl/macosx/universal/TAG.class class-file skipped
JarUtil: JarEntry : isNativeLib true, isClassFile false, isDir false, isRootEntry false
JarUtil.fixNativeLibAttribs: /var/folders/d2/85g_sxm91rg71yyky4gg6l6h0000gn/T/jogamp_0000/file_cache/jln2160290438920784955/jln1278812486691372267/natives/macosx-universal/libjogl_desktop.dylib - OK
JarUtil: EXTRACT[1]: [jogl_desktop -> ] natives/macosx-universal/libjogl_desktop.dylib -> /var/folders/d2/85g_sxm91rg71yyky4gg6l6h0000gn/T/jogamp_0000/file_cache/jln2160290438920784955/jln1278812486691372267/natives/macosx-universal/libjogl_desktop.dylib: 1705368 bytes, addedAsNativeLib: true
JarUtil: JarEntry : isNativeLib true, isClassFile false, isDir false, isRootEntry false
JarUtil.fixNativeLibAttribs: /var/folders/d2/85g_sxm91rg71yyky4gg6l6h0000gn/T/jogamp_0000/file_cache/jln2160290438920784955/jln1278812486691372267/natives/macosx-universal/libjogl_mobile.dylib - OK
JarUtil: EXTRACT[2]: [jogl_mobile -> ] natives/macosx-universal/libjogl_mobile.dylib -> /var/folders/d2/85g_sxm91rg71yyky4gg6l6h0000gn/T/jogamp_0000/file_cache/jln2160290438920784955/jln1278812486691372267/natives/macosx-universal/libjogl_mobile.dylib: 842984 bytes, addedAsNativeLib: true
JarUtil: JarEntry : isNativeLib true, isClassFile false, isDir false, isRootEntry false
JarUtil.fixNativeLibAttribs: /var/folders/d2/85g_sxm91rg71yyky4gg6l6h0000gn/T/jogamp_0000/file_cache/jln2160290438920784955/jln1278812486691372267/natives/macosx-universal/libnativewindow_awt.dylib - OK
JarUtil: EXTRACT[3]: [nativewindow_awt -> ] natives/macosx-universal/libnativewindow_awt.dylib -> /var/folders/d2/85g_sxm91rg71yyky4gg6l6h0000gn/T/jogamp_0000/file_cache/jln2160290438920784955/jln1278812486691372267/natives/macosx-universal/libnativewindow_awt.dylib: 67896 bytes, addedAsNativeLib: true
JarUtil: JarEntry : isNativeLib true, isClassFile false, isDir false, isRootEntry false
JarUtil.fixNativeLibAttribs: /var/folders/d2/85g_sxm91rg71yyky4gg6l6h0000gn/T/jogamp_0000/file_cache/jln2160290438920784955/jln1278812486691372267/natives/macosx-universal/libnativewindow_macosx.dylib - OK
JarUtil: EXTRACT[4]: [nativewindow_macosx -> ] natives/macosx-universal/libnativewindow_macosx.dylib -> /var/folders/d2/85g_sxm91rg71yyky4gg6l6h0000gn/T/jogamp_0000/file_cache/jln2160290438920784955/jln1278812486691372267/natives/macosx-universal/libnativewindow_macosx.dylib: 122912 bytes, addedAsNativeLib: true
JarUtil: JarEntry : isNativeLib true, isClassFile false, isDir false, isRootEntry false
JarUtil.fixNativeLibAttribs: /var/folders/d2/85g_sxm91rg71yyky4gg6l6h0000gn/T/jogamp_0000/file_cache/jln2160290438920784955/jln1278812486691372267/natives/macosx-universal/libnewt_head.dylib - OK
JarUtil: EXTRACT[5]: [newt_head -> ] natives/macosx-universal/libnewt_head.dylib -> /var/folders/d2/85g_sxm91rg71yyky4gg6l6h0000gn/T/jogamp_0000/file_cache/jln2160290438920784955/jln1278812486691372267/natives/macosx-universal/libnewt_head.dylib: 193424 bytes, addedAsNativeLib: true
JNILibLoaderBase: addNativeJarLibsImpl.X: jogl-all-v2.4.0-rc4.jar / jogl-all-v2.4.0-rc4-natives-macosx-universal.jar -> ok: true; duration: now 1769 ms, total 12617 ms (count 3, avrg 4205,667 ms)
JNILibLoaderBase: addNativeJarLibs0: done: jogl-all-v2.4.0-rc4
getJarUri Default jar:file:/Users/martin/.m2/repository/org/jogamp/jogl/jogl-all/v2.4.0-rc4/jogl-all-v2.4.0-rc4.jar!/jogamp/newt/Debug.class
	-> jar:file:/Users/martin/.m2/repository/org/jogamp/jogl/jogl-all/v2.4.0-rc4/jogl-all-v2.4.0-rc4.jar!/jogamp/newt/Debug.class
getJarUri res: jogamp.newt.Debug -> jar:file:/Users/martin/.m2/repository/org/jogamp/jogl/jogl-all/v2.4.0-rc4/jogl-all-v2.4.0-rc4.jar!/jogamp/newt/Debug.class -> jar:file:/Users/martin/.m2/repository/org/jogamp/jogl/jogl-all/v2.4.0-rc4/jogl-all-v2.4.0-rc4.jar!/jogamp/newt/Debug.class
getJarName res: jogl-all-v2.4.0-rc4.jar
JNILibLoaderBase: jarBasename: jogl-all-v2.4.0-rc4
JNILibLoaderBase: addNativeJarLibsImpl(
  classFromJavaJar  = class jogamp.newt.Debug
  classJarURI       = jar:file:/Users/martin/.m2/repository/org/jogamp/jogl/jogl-all/v2.4.0-rc4/jogl-all-v2.4.0-rc4.jar!/jogamp/newt/Debug.class
  jarBasename       = jogl-all-v2.4.0-rc4.jar
  os.and.arch       = macosx-universal
  nativeJarBasename = jogl-all-v2.4.0-rc4-natives-macosx-universal.jar
)
JNILibLoaderBase: addNativeJarLibsImpl: initial: file:/Users/martin/.m2/repository/org/jogamp/jogl/jogl-all/v2.4.0-rc4/jogl-all-v2.4.0-rc4.jar -> file:/Users/martin/.m2/repository/org/jogamp/jogl/jogl-all/v2.4.0-rc4/
JNILibLoaderBase: addNativeJarLibsImpl: nativeLibraryPath: natives/macosx-universal/
JNILibLoaderBase: addNativeJarLibsImpl: module: jogl-all-v2.4.0-rc4-natives-macosx-universal.jar -> jar:file:/Users/martin/.m2/repository/org/jogamp/jogl/jogl-all/v2.4.0-rc4/jogl-all-v2.4.0-rc4-natives-macosx-universal.jar!/
JNILibLoaderBase: addNativeJarLibsImpl: Caught TempJarCache: addNativeLibs: jar:file:/Users/martin/.m2/repository/org/jogamp/jogl/jogl-all/v2.4.0-rc4/jogl-all-v2.4.0-rc4-natives-macosx-universal.jar!/, previous load attempt failed
java.io.IOException: TempJarCache: addNativeLibs: jar:file:/Users/martin/.m2/repository/org/jogamp/jogl/jogl-all/v2.4.0-rc4/jogl-all-v2.4.0-rc4-natives-macosx-universal.jar!/, previous load attempt failed
	at com.jogamp.common.util.cache.TempJarCache.addNativeLibs(TempJarCache.java:284)
	at com.jogamp.common.jvm.JNILibLoaderBase.addNativeJarLibsImpl(JNILibLoaderBase.java:221)
	at com.jogamp.common.jvm.JNILibLoaderBase.addNativeJarLibsWithTempJarCache(JNILibLoaderBase.java:457)
	at com.jogamp.common.jvm.JNILibLoaderBase.addNativeJarLibs(JNILibLoaderBase.java:409)
	at com.jogamp.common.jvm.JNILibLoaderBase.addNativeJarLibsJoglCfg(JNILibLoaderBase.java:324)
	at com.jogamp.opengl.GLProfile$1.run(GLProfile.java:237)
	at java.security.AccessController.doPrivileged(Native Method)
	at com.jogamp.opengl.GLProfile.initSingleton(GLProfile.java:225)
	at com.jogamp.opengl.GLProfile.isAvailable(GLProfile.java:309)
	at com.jogamp.opengl.GLProfile.isAvailable(GLProfile.java:324)
	at org.jzy3d.chart.factories.NativePainterFactory.detectGLProfile(NativePainterFactory.java:121)
	at org.jzy3d.chart.factories.NativePainterFactory.<init>(NativePainterFactory.java:41)
	at org.jzy3d.chart.factories.AWTPainterFactory.<init>(AWTPainterFactory.java:40)
	at org.jzy3d.javafx.AbstractJavaFXPainterFactory.<init>(AbstractJavaFXPainterFactory.java:24)
	at org.jzy3d.javafx.offscreen.JavaFXOffscreenPainterFactory.<init>(JavaFXOffscreenPainterFactory.java:7)
	at org.jzy3d.javafx.offscreen.JavaFXOffscreenChartFactory.<init>(JavaFXOffscreenChartFactory.java:39)
	at org.jzy3d.demos.javafx.DemoJzy3dFX_OpenJDK8.start(DemoJzy3dFX_OpenJDK8.java:47)
	at com.sun.javafx.application.LauncherImpl.lambda$launchApplication1$166(LauncherImpl.java:863)
	at com.sun.javafx.application.PlatformImpl.lambda$runAndWait$179(PlatformImpl.java:326)
	at com.sun.javafx.application.PlatformImpl.lambda$null$177(PlatformImpl.java:295)
	at java.security.AccessController.doPrivileged(Native Method)
	at com.sun.javafx.application.PlatformImpl.lambda$runLater$178(PlatformImpl.java:294)
	at com.sun.glass.ui.InvokeLaterDispatcher$Future.run(InvokeLaterDispatcher.java:95)
JNILibLoaderBase: addNativeJarLibsImpl: ClassLoader/TAG: Locating module newt, os.and.arch macosx.universal: jogamp.nativetag.newt.macosx.universal.TAG
JNILibLoaderBase: addNativeJarLibsImpl: Caught Cannot not find: jogamp.nativetag.newt.macosx.universal.TAG
java.io.IOException: Cannot not find: jogamp.nativetag.newt.macosx.universal.TAG
	at com.jogamp.common.util.IOUtil.getClassURL(IOUtil.java:451)
	at com.jogamp.common.util.JarUtil.getJarUri(JarUtil.java:146)
	at com.jogamp.common.jvm.JNILibLoaderBase.addNativeJarLibsImpl(JNILibLoaderBase.java:273)
	at com.jogamp.common.jvm.JNILibLoaderBase.addNativeJarLibsWithTempJarCache(JNILibLoaderBase.java:457)
	at com.jogamp.common.jvm.JNILibLoaderBase.addNativeJarLibs(JNILibLoaderBase.java:409)
	at com.jogamp.common.jvm.JNILibLoaderBase.addNativeJarLibsJoglCfg(JNILibLoaderBase.java:324)
	at com.jogamp.opengl.GLProfile$1.run(GLProfile.java:237)
	at java.security.AccessController.doPrivileged(Native Method)
	at com.jogamp.opengl.GLProfile.initSingleton(GLProfile.java:225)
	at com.jogamp.opengl.GLProfile.isAvailable(GLProfile.java:309)
	at com.jogamp.opengl.GLProfile.isAvailable(GLProfile.java:324)
	at org.jzy3d.chart.factories.NativePainterFactory.detectGLProfile(NativePainterFactory.java:121)
	at org.jzy3d.chart.factories.NativePainterFactory.<init>(NativePainterFactory.java:41)
	at org.jzy3d.chart.factories.AWTPainterFactory.<init>(AWTPainterFactory.java:40)
	at org.jzy3d.javafx.AbstractJavaFXPainterFactory.<init>(AbstractJavaFXPainterFactory.java:24)
	at org.jzy3d.javafx.offscreen.JavaFXOffscreenPainterFactory.<init>(JavaFXOffscreenPainterFactory.java:7)
	at org.jzy3d.javafx.offscreen.JavaFXOffscreenChartFactory.<init>(JavaFXOffscreenChartFactory.java:39)
	at org.jzy3d.demos.javafx.DemoJzy3dFX_OpenJDK8.start(DemoJzy3dFX_OpenJDK8.java:47)
	at com.sun.javafx.application.LauncherImpl.lambda$launchApplication1$166(LauncherImpl.java:863)
	at com.sun.javafx.application.PlatformImpl.lambda$runAndWait$179(PlatformImpl.java:326)
	at com.sun.javafx.application.PlatformImpl.lambda$null$177(PlatformImpl.java:295)
	at java.security.AccessController.doPrivileged(Native Method)
	at com.sun.javafx.application.PlatformImpl.lambda$runLater$178(PlatformImpl.java:294)
	at com.sun.glass.ui.InvokeLaterDispatcher$Future.run(InvokeLaterDispatcher.java:95)
JNILibLoaderBase: addNativeJarLibsImpl.X: jogl-all-v2.4.0-rc4.jar / jogl-all-v2.4.0-rc4-natives-macosx-universal.jar -> ok: false; duration: now 4 ms, total 12621 ms (count 4, avrg 3155,250 ms)
JNILibLoaderBase: addNativeJarLibs0: done: jogl-all-v2.4.0-rc4
JNILibLoaderBase: addNativeJarLibsWhenInitialized: count 1, ok false
JNILibLoaderBase: loadLibraryInternal(nativewindow_awt), TempJarCache: /var/folders/d2/85g_sxm91rg71yyky4gg6l6h0000gn/T/jogamp_0000/file_cache/jln2160290438920784955/jln1278812486691372267/natives/macosx-universal/libnativewindow_awt.dylib
JNILibLoaderBase: System.load(/var/folders/d2/85g_sxm91rg71yyky4gg6l6h0000gn/T/jogamp_0000/file_cache/jln2160290438920784955/jln1278812486691372267/natives/macosx-universal/libnativewindow_awt.dylib) - mode 2
JNILibLoaderBase: loadLibraryInternal(nativewindow_awt): OK - mode 2
JNILibLoaderBase: Loaded Native Library: nativewindow_awt
JNILibLoaderBase: loaded nativewindow_awt
JNILibLoaderBase: loadLibraryInternal(nativewindow_macosx), TempJarCache: /var/folders/d2/85g_sxm91rg71yyky4gg6l6h0000gn/T/jogamp_0000/file_cache/jln2160290438920784955/jln1278812486691372267/natives/macosx-universal/libnativewindow_macosx.dylib
JNILibLoaderBase: System.load(/var/folders/d2/85g_sxm91rg71yyky4gg6l6h0000gn/T/jogamp_0000/file_cache/jln2160290438920784955/jln1278812486691372267/natives/macosx-universal/libnativewindow_macosx.dylib) - mode 2
JNILibLoaderBase: loadLibraryInternal(nativewindow_macosx): OK - mode 2
JNILibLoaderBase: Loaded Native Library: nativewindow_macosx
JNILibLoaderBase: loaded nativewindow_macosx
JNILibLoaderBase: loadLibraryInternal(jogl_desktop), TempJarCache: /var/folders/d2/85g_sxm91rg71yyky4gg6l6h0000gn/T/jogamp_0000/file_cache/jln2160290438920784955/jln1278812486691372267/natives/macosx-universal/libjogl_desktop.dylib
JNILibLoaderBase: System.load(/var/folders/d2/85g_sxm91rg71yyky4gg6l6h0000gn/T/jogamp_0000/file_cache/jln2160290438920784955/jln1278812486691372267/natives/macosx-universal/libjogl_desktop.dylib) - mode 2
JNILibLoaderBase: loadLibraryInternal(jogl_desktop): OK - mode 2
JNILibLoaderBase: Loaded Native Library: jogl_desktop
JNILibLoaderBase: loaded jogl_desktop
JNILibLoaderBase: loadLibraryInternal(jogl_mobile), TempJarCache: /var/folders/d2/85g_sxm91rg71yyky4gg6l6h0000gn/T/jogamp_0000/file_cache/jln2160290438920784955/jln1278812486691372267/natives/macosx-universal/libjogl_mobile.dylib
JNILibLoaderBase: System.load(/var/folders/d2/85g_sxm91rg71yyky4gg6l6h0000gn/T/jogamp_0000/file_cache/jln2160290438920784955/jln1278812486691372267/natives/macosx-universal/libjogl_mobile.dylib) - mode 2
JNILibLoaderBase: loadLibraryInternal(jogl_mobile): OK - mode 2
JNILibLoaderBase: Loaded Native Library: jogl_mobile
JNILibLoaderBase: loaded jogl_mobile
FALLBACK (log once): Fallback to SW vertex for line stipple
FALLBACK (log once): Fallback to SW vertex processing, m_disable_code: 2000
FALLBACK (log once): Fallback to SW vertex processing in drawCore, m_disable_code: 2000
JavaFXOffscreenChartFactory.resetTo 500.0 x 172.0
JavaFXOffscreenChartFactory.bind receives an image of size 500.0 x 172.0
JavaFXOffscreenChartFactory.bind receives an image of size 500.0 x 172.0
JavaFXOffscreenChartFactory.resetTo 500.0 x 472.0
JavaFXOffscreenChartFactory.bind receives an image of size 500.0 x 472.0
JavaFXOffscreenChartFactory.bind receives an image of size 500.0 x 472.0

NWJNILibLoader
NEWTJNILibLoader
