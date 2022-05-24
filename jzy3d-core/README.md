Jzy3d - Core
================================

Provides the base definition of charts without the rendering backend that may be implemented by JOGL (in all module named `jzy3d-native-jogl-*`) or EmulGL (in `jzy3d-emul-gl-*`).




## Software Architecture

Creating a chart implies building and wiring the below high-level components.

<img src="doc/Components.png"/>


### Customize chart with factories

The ```IChartFactory``` builds all objects that will define how the chart will look (```Axis```, ```View```, ```Camera```, ```Chart```).

The ```IPainterFactory``` builds every objects that allow compatibility across windowing toolkits and GPU/CPU. The chart factories and drawable have no knowledge of concrete AWT, SWT, Swing, etc. This is all powered by the painter factory introduced in Jzy3d 2.0.

The ```Drawable``` class hierarchy defines geometries able to use a ```IPainter``` to draw something.

<img src="doc/Factories.png"/>


### Native and emulated elements

<img src="doc/Interop.png"/>


## Javadoc schemas reference

### Camera

#### Model
<img src="src/main/java/org/jzy3d/plot3d/rendering/view/doc-files/camera.png"/>

#### Orthogonal projection
<img src="src/main/java/org/jzy3d/plot3d/rendering/view/doc-files/orthogonal.png"/>

#### Perspective projection
<img src="src/main/java/org/jzy3d/plot3d/rendering/view/doc-files/perspective.png"/>

### Axis

#### AxisLabelRotator

##### Cube Axis
<img src="src/main/java/org/jzy3d/plot3d/primitives/axis/doc-files/AxisBox-Label.png"/>

##### Ternary Axis
<img src="src/main/java/org/jzy3d/plot3d/primitives/axis/doc-files/AxisBox-LabelRotate.png"/>
