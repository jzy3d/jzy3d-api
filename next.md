


# Tests integration

Pourquoi panama que sur les 2 premiers? 
Pourquoi l'image panama apparait plus grande?
Pourquoi y a pas swing et panama sur les 2D?

Est ce qu'on veut encore tester HiDPI / pas HiDPI dans ces tests?

Peut on réduire les différents test de marge pour avoir la même combinatoire mais moins de RUN (exec plus vite)

Voir readme de integration tests -> y a des choses à faire.

Eviter la grande QTT de warning 
Caught AppContextInfo(Bug 1004) IllegalAccessException: class com.jogamp.nativewindow.awt.AppContextInfo cannot access class sun.awt.AppContext (in module java.desktop) because module java.desktop does not export sun.awt to unnamed module @3d121db3 on thread main

#### Pour plus tard
- tester javafx et SWT en non reg
- faudra t-il des sous projets par WT ou mieux d'avoir une vue globale?


# Implem globale

- Peut on avoir un HiDPI EmulGL calqué sur JOGL/Panama. Avec adaptation des font size quand HiDPI Off + test vérifiant tout.




# Structure

Grouper 
- jzy3d-native - jogl + panama
- jzy3d-emulgl - awt/swt/jGL
- ou render-gpu / render-cpu

- depthpeeling naming : core + jogl + panamagl ? ou bien intégrer le depth peeling dans les core de chacun 



Dégager XLS, reports?
Dégager SVM mapper et libsvm ?


Tutorials jogl-javafx éviter les 3 versions

# Clean code

### Dead code prune

Check with Guide to ensure not loosing usefull code
Cyclomatic complexity review (e.g. PanamaGLViewOverlay still relevant ? )


# Extensions privées

Intégrer symbols ? 

# PanamaGL

Plan pour bosser single binding multi proc

- vérifier que les libs CGL, GLX, WGL, GLEXT soient également toutes 


# Documentation

Donner les matrices de compat sur le readme

- OS / Proc / JVM
- OpenGL version 

- Pyramide des tests as a graphic



# MARKET 3.0.0

Emailing ?
Notify all commiters ?




# Future

Vulkan to replace 