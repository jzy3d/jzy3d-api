package org.jzy3d.plot3d.rendering.lights;

import org.jzy3d.chart.Chart;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.primitives.Geometry;
import org.jzy3d.plot3d.primitives.PolygonFill;
import org.jzy3d.plot3d.primitives.PolygonMode;

/**
 * Adds light to a scene which allows shading the {@link Geometry}'s colors according to the angle
 * between the light and the {@link Geometry}, which is required to perceive the volume of an
 * object.
 * 
 * You do not instantiate a light but rather invoke {@link Chart#addLight(Coord3d)} or
 * {@link Chart#addLightOnCamera()} which return a light instance. A chart can deal with up to 8
 * lights, which can be switched on or off with : <code>
 * <pre>
 * Light light = chart.getScene().getLightSet().get(0); // light ID in [0;7]
 * light.setEnabled(false);
 * </pre>
 * </code>
 * 
 * <p>
 * The important thing to do is to allow a <b>drawable to reflect light</b>, which requires to
 * compute its normals. This is achieved by {@link Geometry#setReflectLight(boolean)}, which is
 * false by default to avoid processing normals for the majority of chart that do not require
 * lighting.
 * </p>
 * 
 * <p>
 * Then you can tune both the light colors and the drawable reflection colors. The below
 * documentation are taken from the GL Red Book documentation and a great website for learning
 * OpenGL (see references below).
 * </p>
 * 
 * <h2>Light effect on coloring</h2>
 * 
 * <h3>Light settings</h3>
 * 
 * <img src="./doc-files/basic_lighting_phong.png"/>
 * 
 * <ul>
 * <li><b>Ambient</b> illumination is light that's been scattered so much by the environment that
 * its direction is impossible to determine - it seems to come from all directions. Backlighting in
 * a room has a large ambient component, since most of the light that reaches your eye has first
 * bounced off many surfaces. A spotlight outdoors has a tiny ambient component; most of the light
 * travels in the same direction, and since you're outdoors, very little of the light reaches your
 * eye after bouncing off other objects. When ambient light strikes a surface, it's scattered
 * equally in all directions.</li>
 * <li>The <b>diffuse</b> component is the light that comes from one direction, so it's brighter if
 * it comes squarely down on a surface than if it barely glances off the surface. Once it hits a
 * surface, however, it's scattered equally in all directions, so it appears equally bright, no
 * matter where the eye is located. Any light coming from a particular position or direction
 * probably has a diffuse component.</li>
 * <li>Finally, <b>specular</b> light comes from a particular direction, and it tends to bounce off
 * the surface in a preferred direction. A well-collimated laser beam bouncing off a high-quality
 * mirror produces almost 100 percent specular reflection. Shiny metal or plastic has a high
 * specular component, and chalk or carpet has almost none. You can think of specularity as
 * shininess.</li>
 * </ul>
 * 
 * Although a light source delivers a single distribution of frequencies, the ambient, diffuse, and
 * specular components might be different. For example, if you have a white light in a room with red
 * walls, the scattered light tends to be red, although the light directly striking objects is
 * white. OpenGL allows you to set the red, green, and blue values for each component of light
 * independently.
 * 
 * <h3>Object material settings</h3>
 * 
 * <p>
 * The OpenGL lighting model makes the approximation that a material's color depends on the
 * percentages of the incoming red, green, and blue light it reflects. For example, a perfectly red
 * ball reflects all the incoming red light and absorbs all the green and blue light that strikes
 * it. If you view such a ball in white light (composed of equal amounts of red, green, and blue
 * light), all the red is reflected, and you see a red ball. If the ball is viewed in pure red
 * light, it also appears to be red. If, however, the red ball is viewed in pure green light, it
 * appears black (all the green is absorbed, and there's no incoming red, so no light is reflected).
 * </p>
 * <p>
 * Like lights, materials have different ambient, diffuse, and specular colors, which determine the
 * ambient, diffuse, and specular reflectances of the material. A material's ambient reflectance is
 * combined with the ambient component of each incoming light source, the diffuse reflectance with
 * the light's diffuse component, and similarly for the specular reflectance and component. Ambient
 * and diffuse reflectances define the color of the material and are typically similar if not
 * identical. Specular reflectance is usually white or gray, so that specular highlights end up
 * being the color of the light source's specular intensity. If you think of a white light shining
 * on a shiny red plastic sphere, most of the sphere appears red, but the shiny highlight is white.
 * </p>
 * <p>
 * In addition to ambient, diffuse, and specular colors, materials have an emissive color, which
 * simulates light originating from an object. In the OpenGL lighting model, the emissive color of a
 * surface adds intensity to the object, but is unaffected by any light sources. Also, the emissive
 * color does not introduce any additional light into the overall scene.
 * </p>
 * 
 * <h3>RGB Values for Lights and Materials</h3>
 * 
 * <p>
 * The color components specified for lights mean something different than for materials. For a
 * light, the numbers correspond to a percentage of full intensity for each color. If the R, G, and
 * B values for a light's color are all 1.0, the light is the brightest possible white. If the
 * values are 0.5, the color is still white, but only at half intensity, so it appears gray. If
 * R=G=1 and B=0 (full red and green with no blue), the light appears yellow.
 * </p>
 * <p>
 * For materials, the numbers correspond to the reflected proportions of those colors. So if R=1,
 * G=0.5, and B=0 for a material, that material reflects all the incoming red light, half the
 * incoming green, and none of the incoming blue light. In other words, if an OpenGL light has
 * components (LR, LG, LB), and a material has corresponding components (MR, MG, MB), then, ignoring
 * all other reflectivity effects, the light that arrives at the eye is given by (LR*MR, LG*MG,
 * LB*MB).
 * </p>
 * <p>
 * Similarly, if you have two lights that send (R1, G1, B1) and (R2, G2, B2) to the eye, OpenGL adds
 * the components, giving (R1+R2, G1+G2, B1+B2). If any of the sums are greater than 1
 * (corresponding to a color brighter than the equipment can display), the component is clamped to
 * 1.
 * </p>
 * 
 * <h2>Light type</h2>
 *
 * As previously mentioned, you can choose whether to have a light source that's treated as though
 * it's located infinitely far away from the scene or one that's nearer to the scene. The first type
 * is referred to as a <i>directional</i> light source; the effect of an infinite location is that
 * the rays of light can be considered parallel by the time they reach an object. An example of a
 * real-world directional light source is the sun. The second type is called a <i>positional</i>
 * light source, since its exact position within the scene determines the effect it has on a scene
 * and, specifically, the direction from which the light rays come. A desk lamp is an example of a
 * positional light source.
 * 
 * To switch type, simply invoke :
 * 
 * <pre>
 * <code>
 * light.setType(Type.DIRECTIONAL);
 * </code>
 * </pre>
 * 
 * Note: Remember that the colors across the face of a smooth-shaded polygon are determined by the
 * colors calculated for the vertices. Because of this, you probably want to avoid using large
 * polygons with local lights. If you locate the light near the middle of the polygon, the vertices
 * might be too far away to receive much light, and the whole polygon will look darker than you
 * intended. To avoid this problem, break up the large polygon into smaller ones.
 *
 * <h2>Light attenuation</h2>
 * 
 * For real-world lights, the intensity of light decreases as distance from the light increases.
 * Since a directional light is infinitely far away, it doesn't make sense to attenuate its
 * intensity over distance, so attenuation is disabled for a directional light. However, you might
 * want to attenuate the light from a positional light. OpenGL attenuates a light source by
 * multiplying the contribution of that source by an attenuation factor:<br/>
 * 
 * <img src="./doc-files/attenuation.gif"/>
 * 
 * <br/>
 * where
 * <ul>
 * <li>d = distance between the light's position and the vertex
 * <li>kc = GL_CONSTANT_ATTENUATION
 * <li>kl = GL_LINEAR_ATTENUATION
 * <li>kq = GL_QUADRATIC_ATTENUATION
 * </ul>
 * 
 * By default in OpenGL, kc is 1.0 and both kl and kq are zero, but you can give these parameters
 * different values:
 * 
 * <pre>
 * <code>
 * glLightf(GL_LIGHT0, GL_CONSTANT_ATTENUATION, 2.0); 
 * glLightf(GL_LIGHT0, GL_LINEAR_ATTENUATION, 1.0); 
 * glLightf(GL_LIGHT0, GL_QUADRATIC_ATTENUATION, 0.5); 
 * </code>
 * </pre>
 * 
 * By default in Jzy3D, no attenuation is defined for a light. To specify an attenuation, simply
 * call
 * 
 * <pre>
 * <code>
 * Attenuation attenuation = new Attenuation();
 * attenuation.setConstant(1);
 * attenuation.setLinear(1);
 * attenuation.setQuadratic(1);
 * 
 * Light light = new Light();
 * light.setAttenuation(attenuation);
 * </code>
 * </pre>
 * 
 * Note that the ambient, diffuse, and specular contributions are all attenuated. Only the emission
 * and global ambient values aren't attenuated. Also note that since attenuation requires an
 * additional division (and possibly more math) for each calculated color, using attenuated lights
 * may slow down application performance.
 * 
 * 
 * <h2>Light Model</h2>
 * 
 * The OpenGL notion of a lighting model has three components:
 * <ul>
 * <li>The global ambient light intensity
 * <li>Whether the viewpoint position is local to the scene or whether it should be considered to be
 * an infinite distance away
 * <li>Whether lighting calculations should be performed differently for both the front and back
 * faces of objects
 * </ul>
 * 
 * 
 * <h3>Global Ambient Light</h3>
 * 
 * Each light source can contribute ambient light to a scene. In addition, there can be other
 * ambient light that's not from any particular source. To specify the RGBA intensity of such global
 * ambient light, use the GL_LIGHT_MODEL_AMBIENT parameter as follows:
 * 
 * <pre>
 * <code>
 * float[] ambiant = { 0.2f, 0.2f, 0.2f, 1.0f }; 
 * painter.glLightModel(LightModel.LIGHT_MODEL_AMBIENT, ambiant);
 * </code>
 * </pre>
 *  
 * In this example, the values used for lmodel_ambient are the default values for
 * GL_LIGHT_MODEL_AMBIENT. Since these numbers yield a small amount of white ambient light, even if
 * you don't add a specific light source to your scene, you can still see the objects in the scene.
 * "Plate 14" in Appendix I shows the effect of different amounts of global ambient light.
 * 
 * <h3>Local or Infinite Viewpoint</h3>
 * 
 * The location of the viewpoint affects the calculations for highlights produced by specular
 * reflectance. More specifically, the intensity of the highlight at a particular vertex depends on
 * the normal at that vertex, the direction from the vertex to the light source, and the direction
 * from the vertex to the viewpoint. Keep in mind that the viewpoint isn't actually being moved by
 * calls to lighting commands (you need to change the projection transformation, as described in
 * "Projection Transformations" in Chapter 3); instead, different assumptions are made for the
 * lighting calculations as if the viewpoint were moved.
 * 
 * With an infinite viewpoint, the direction between it and any vertex in the scene remains
 * constant. A local viewpoint tends to yield more realistic results, but since the direction has to
 * be calculated for each vertex, overall performance is decreased with a local viewpoint. By
 * default, an infinite viewpoint is assumed. Here's how to change to a local viewpoint:
 * 
 * <pre>
 * <code>
 * painter.glLightModel(LightModel.LIGHT_MODEL_LOCAL_VIEWER, true);
 * </code>
 * </pre>
 * 
 * This call places the viewpoint at (0, 0, 0) in eye coordinates. To switch back to an infinite
 * viewpoint, pass in <code>false</code> as the argument.
 * 
 * <h3>Two-sided Lighting</h3>
 * 
 * Lighting calculations are performed for all polygons, whether they're front-facing or
 * back-facing. Since you usually set up lighting conditions with the front-facing polygons in mind,
 * however, the back-facing ones typically aren't correctly illuminated. In Example 5-1 where the
 * object is a sphere, only the front faces are ever seen, since they're the ones on the outside of
 * the sphere. So, in this case, it doesn't matter what the back-facing polygons look like. If the
 * sphere is going to be cut away so that its inside surface will be visible, however, you might
 * want to have the inside surface be fully lit according to the lighting conditions you've defined;
 * you might also want to supply a different material description for the back faces. When you turn
 * on two-sided lighting with
 * 
 * <pre>
 * <code>
 * painter.glLightModel(LightModel.LIGHT_MODEL_TWO_SIDE, true);
 * </code>
 * </pre>
 * 
 * OpenGL reverses the surface normals for back-facing polygons; typically, this means that the
 * surface normals of visible back- and front-facing polygons face the viewer, rather than pointing
 * away. As a result, all polygons are illuminated correctly. However, these additional operations
 * usually make two-sided lighting perform more slowly than the default one-sided lighting.
 * 
 * To turn two-sided lighting off, pass in <code>false</code> as the argument in the preceding call.
 * (See "Defining Material Properties" for information about how to supply material properties for
 * both faces.) You can also control which faces OpenGL considers to be front-facing with the
 * command glFrontFace(). (See "Reversing and Culling Polygon Faces" in Chapter 2 for more
 * information.)
 * 
 * @see <a href="https://glprogramming.com/red/chapter05.html">OpenGL Red book chapter about
 *      lighting</a> which was copied to make this javadoc closest to the OpenGL documentation.
 * @see https://glprogramming.com/red/chapter05.html#name7 (mathematics of lighting)
 * @see https://learnopengl.com/Lighting/Basic-Lighting
 * @see https://www.sjbaker.org/steve/omniv/opengl_lighting.html
 * @see https://www.sjbaker.org/steve/omniv/ten_shading_problems.html
 * 
 * @author Martin Pernollet
 *
 */
public class Light {
  public static final Color DEFAULT_COLOR = Color.GRAY.clone();

  public enum Type {
    DIRECTIONAL, POSITIONAL;
  }

  protected static final int POSITIONAL_TYPE = 1;
  protected static final int DIRECTIONAL_TYPE = 0;

  protected Type type = Type.POSITIONAL;

  protected int lightId;
  protected boolean enabled;
  protected Color ambiantColor;
  protected Color diffuseColor;
  protected Color specularColor;
  protected Coord3d position = new Coord3d();
  protected float[] glPositionAndType;

  protected Attenuation attenuation = new Attenuation();

  protected boolean representationDisplayed;
  protected float representationRadius = 10;
  protected Color representationColor = new Color(0.0f, 1.0f, 1.0f);

  protected static int lightCount;

  public static void resetCounter() {
    lightCount = 0;
  }

  public Light() {
    this(lightCount++, false);
  }

  public Light(int id) {
    this(id, false);
  }

  public Light(int id, boolean representationDisplayed) {
    this(id, true, representationDisplayed);
  }

  public Light(int id, boolean enabled, boolean representationDisplayed) {
    this.lightId = id;
    this.enabled = enabled;
    this.representationDisplayed = representationDisplayed;

    ambiantColor = DEFAULT_COLOR.clone();
    diffuseColor = DEFAULT_COLOR.clone();
    specularColor = DEFAULT_COLOR.clone();

    setType(Type.POSITIONAL);
    setAttenuation(null);

  }

  Coord3d lastScale = Coord3d.IDENTITY.clone();
  
  public void apply(IPainter painter, Coord3d scale) {
    if (enabled) {
      painter.glMatrixMode_ModelView();
      painter.glLoadIdentity();
      lastScale = scale;
      painter.glTranslatef(position.x * scale.x, position.y * scale.y, position.z * scale.z);

      // Light position representation (cube)
      if (representationDisplayed) {
        painter.glDisable_Lighting();
        painter.color(representationColor);
        painter.glPolygonMode(PolygonMode.FRONT_AND_BACK, PolygonFill.LINE);
        painter.glutSolidCube(representationRadius);
        painter.glEnable_Lighting();
      }

      configureLight(painter, lightId);
      
    } else {
      painter.glDisable_Lighting();
    }
  }

  protected void configureLight(IPainter painter, int lightId) {
    painter.glEnable_Light(lightId);
    painter.glLight_Position(lightId, glPositionAndType);
    painter.glLight_Ambiant(lightId, ambiantColor);
    painter.glLight_Diffuse(lightId, diffuseColor);
    painter.glLight_Specular(lightId, specularColor);
    
    if (attenuation != null) {
      painter.glLightf(lightId, Attenuation.Type.CONSTANT, attenuation.constant);
      painter.glLightf(lightId, Attenuation.Type.LINEAR, attenuation.linear);
      painter.glLightf(lightId, Attenuation.Type.QUADRATIC, attenuation.quadratic);
    }
  }

  /** Indicates if a square is drawn to show the light position. */
  public void setRepresentationDisplayed(boolean status) {
    representationDisplayed = status;
  }

  public boolean getRepresentationDisplayed() {
    return representationDisplayed;
  }

  public float getRepresentationRadius() {
    return representationRadius;
  }

  public void setRepresentationRadius(float representationRadius) {
    this.representationRadius = representationRadius;
  }

  public int getId() {
    return lightId;
  }

  public void setPosition(Coord3d position) {
    this.position = position;
  }

  public Coord3d getPosition() {
    return position;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public Color getAmbiantColor() {
    return ambiantColor;
  }

  public void setAmbiantColor(Color ambiantColor) {
    this.ambiantColor = ambiantColor;
  }

  public Color getDiffuseColor() {
    return diffuseColor;
  }

  public void setDiffuseColor(Color diffuseColor) {
    this.diffuseColor = diffuseColor;
  }

  public Color getSpecularColor() {
    return specularColor;
  }

  public void setSpecularColor(Color specularColor) {
    this.specularColor = specularColor;
  }

  public Type getType() {
    return type;
  }

  public void setType(Type type) {
    this.type = type;

    glPositionAndType = new float[4];
    
    //position.mul(lastScale).toArray(glPositionAndType, 0);
    
    if (Type.POSITIONAL.equals(type)) {
      glPositionAndType[3] = POSITIONAL_TYPE;
    } else if (Type.DIRECTIONAL.equals(type)) {
      glPositionAndType[3] = DIRECTIONAL_TYPE;
    }
  }

  public Attenuation getAttenuation() {
    return attenuation;
  }

  public void setAttenuation(Attenuation attenuation) {
    this.attenuation = attenuation;
  }
}
