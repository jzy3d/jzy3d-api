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
 * <h2>Light settings</h2>
 * 
 * <img src="./doc-files/basic_lighting_phong.png"/>
 * 
 * <ul>
 * <li><b>Ambient</b> illumination is light that's been scattered so much by the environment that its
 * direction is impossible to determine - it seems to come from all directions. Backlighting in a
 * room has a large ambient component, since most of the light that reaches your eye has first
 * bounced off many surfaces. A spotlight outdoors has a tiny ambient component; most of the light
 * travels in the same direction, and since you're outdoors, very little of the light reaches your
 * eye after bouncing off other objects. When ambient light strikes a surface, it's scattered
 * equally in all directions.</li>
 * <li>The <b>diffuse</b> component is the light that comes from one direction, so it's brighter if it
 * comes squarely down on a surface than if it barely glances off the surface. Once it hits a
 * surface, however, it's scattered equally in all directions, so it appears equally bright, no
 * matter where the eye is located. Any light coming from a particular position or direction
 * probably has a diffuse component.</li>
 * <li>Finally, <b>specular</b> light comes from a particular direction, and it tends to bounce off the
 * surface in a preferred direction. A well-collimated laser beam bouncing off a high-quality mirror
 * produces almost 100 percent specular reflection. Shiny metal or plastic has a high specular
 * component, and chalk or carpet has almost none. You can think of specularity as shininess.</li>
 * </ul>
 * 
 * Although a light source delivers a single distribution of frequencies, the ambient, diffuse, and
 * specular components might be different. For example, if you have a white light in a room with red
 * walls, the scattered light tends to be red, although the light directly striking objects is
 * white. OpenGL allows you to set the red, green, and blue values for each component of light
 * independently.
 * 
 * <h2>Object material settings</h2>
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
 * <h2>RGB Values for Lights and Materials</h2>
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
 * @see https://glprogramming.com/red/chapter05.html
 * @see https://learnopengl.com/Lighting/Basic-Lighting
 * 
 * @author Martin Pernollet
 *
 */
public class Light {
  public static final Color DEFAULT_COLOR = new Color(1f,1f,1f,1f);
  
  protected int lightId;
  protected boolean enabled;
  protected Color ambiantColor;
  protected Color diffuseColor;
  protected Color specularColor;
  protected Coord3d position;
  protected float positionZero[] = {0.0f, 0.0f, 0.0f, 1.0f};

  protected boolean representationDisplayed;
  protected float representationRadius = 10;
  protected Color representationColor = new Color(0.0f, 1.0f, 1.0f);

  protected static int lightCount;

  public static void resetCounter() {
    lightCount = 0;
  }

  public Light() {
    this(lightCount++, true);
  }

  public Light(int id) {
    this(id, true);
  }

  public Light(int id, boolean representationDisplayed) {
    this(id, true, false);
  }

  public Light(int id, boolean enabled, boolean representationDisplayed) {
    this.lightId = id;
    this.enabled = enabled;
    this.representationDisplayed = representationDisplayed;

    ambiantColor = Color.WHITE;
    diffuseColor = Color.WHITE;
    specularColor = Color.WHITE;
  }

  public void apply(IPainter painter, Coord3d scale) {
    if (enabled) {
      painter.glMatrixMode_ModelView();
      painter.glLoadIdentity();
      painter.glTranslatef(position.x * scale.x, position.y * scale.y, position.z * scale.z);

      // Light position representation (cube)
      if (representationDisplayed) {
        painter.glDisable_Lighting();
        painter.color(representationColor);
        painter.glPolygonMode(PolygonMode.FRONT_AND_BACK, PolygonFill.LINE);
        painter.glutSolidCube(representationRadius);
        painter.glEnable_Lighting();
      }

      configureLight(painter);
    } else {
      painter.glDisable_Lighting();
    }
  }

  protected void configureLight(IPainter painter) {
    // Actual light source setting TODO: check we really need to
    // define @ each rendering
    LightSwitch.enable(painter, lightId);

    configureLight(painter, lightId);
  }

  protected void configureLight(IPainter painter, int lightId) {
    painter.glLight_Position(lightId, positionZero);
    painter.glLight_Ambiant(lightId, ambiantColor);
    painter.glLight_Diffuse(lightId, diffuseColor);
    painter.glLight_Specular(lightId, specularColor);
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
}
