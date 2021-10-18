package org.jzy3d.plot3d.primitives;

import java.util.ArrayList;
import java.util.List;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.IMultiColorable;
import org.jzy3d.colors.ISingleColorable;
import org.jzy3d.events.DrawableChangedEvent;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Utils;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.rendering.scene.Decomposition;
import org.jzy3d.plot3d.transform.Transform;
import org.jzy3d.plot3d.transform.space.SpaceTransformer;

/**
 * A {@link Composite} gathers several {@link Drawable} and provides default methods managing them
 * all in one call : drawing, getting bounds, setting face or wireframe colors, etc.
 * 
 * {@link Composite}s have the nice property of being decomposable (see {@link Decomposition}),
 * meaning a scene {@link Graph} can take all items independently to sort them for optimized
 * translucency rendering.
 *
 * @author Martin Pernollet
 *
 */
public class Composite extends Wireframeable implements ISingleColorable, IMultiColorable {
  public Composite() {
    super();
    components = new ArrayList<Drawable>();
  }
  
  /**
   * This indicate to a scene graph if the Composite consider it is worth being decomposed or not.
   * 
   * @return will return true only if the Composite has face displayed (simply because decomposing wireframe-only drawable is useless)
   */
  public boolean canDecompose() {
    return isFaceDisplayed();
  }

  /****************************************************************/

  /** Append a list of Drawables to this composite. */
  public void add(List<? extends Drawable> drawables) {
    synchronized (components) {
      components.addAll(drawables);
    }
  }

  /** Clear the list of Drawables from this composite. */
  public void clear() {
    synchronized (components) {
      components.clear();
    }
  }

  /** Add a Drawable to this composite. */
  public void add(Drawable drawable) {
    synchronized (components) {
      components.add(drawable);
    }
  }

  /** Remove a Drawable from this composite. */
  public void remove(Drawable drawable) {
    synchronized (components) {
      components.remove(drawable);
    }
  }

  /** Get a Drawable stored by this composite. */
  public Drawable get(int p) {
    synchronized (components) {
      return components.get(p);
    }
  }

  public List<Drawable> getDrawables() {
    return components;
  }

  /** Return the number of Drawable stored by this composite. */
  public int size() {
    return components.size();
  }

  /* */

  /** Delegate rendering iteratively to all Drawable of this composite. */
  @Override
  public void draw(IPainter painter) {
    if (mapper != null)
      mapper.preDraw(this);

    synchronized (components) {
      for (Drawable c : components) {
        if (c != null)
          c.draw(painter);
      }
    }

    if (mapper != null)
      mapper.postDraw(this);

    doDrawBoundsIfDisplayed(painter);
  }

  /**
   * Delegate transforming iteratively to all Drawable of this composite and stores the given
   * transform for keeping the ability of retrieving it.
   */
  @Override
  public void setTransform(Transform transform) {
    this.transform = transform;

    if(!canDecompose())
      return;
    
    
    synchronized (components) {
      for (Drawable c : components) {
        if (c != null)
          c.setTransform(transform);
      }
    }
  }

  @Override
  public void setTransformBefore(Transform transform) {
    this.transformBefore = transform;
    
    if(!canDecompose())
      return;

    synchronized (components) {
      for (Drawable c : components) {
        if (c != null)
          c.setTransformBefore(transform);
      }
    }
  }

  @Override
  public void setSpaceTransformer(SpaceTransformer spaceTransformer) {
    this.spaceTransformer = spaceTransformer;

    synchronized (components) {
      for (Drawable c : components) {
        if (c != null)
          c.setSpaceTransformer(spaceTransformer);
      }
    }
  }

  /** Return the transform that was affected to this composite. */
  @Override
  public Transform getTransform() {
    return transform;
  }

  /**
   * Creates and return a BoundingBox3d that embed all available Drawable bounds.
   */
  @Override
  public BoundingBox3d getBounds() {
    updateBounds();
    return bbox;
  }

  @Override
  public void updateBounds() {
    BoundingBox3d box = new BoundingBox3d();

    synchronized (components) {
      for (Drawable c : components) {
        if (c != null && c.getBounds() != null)
          box.add(c.getBounds());
      }
    }
    bbox = box;
  }

  @Override
  public void applyGeometryTransform(Transform transform) {
    synchronized (components) {
      for (Drawable c : components) {
        c.applyGeometryTransform(transform);
      }
    }
    // updateBounds(); no need, as computed by getBounds()
  }

  /****************************************************************/

  @Override
  public void setWireframeColor(Color color) {
    super.setWireframeColor(color);

    if (components != null) {
      synchronized (components) {
        for (Drawable c : components) {
          if (c instanceof Wireframeable)
            ((Wireframeable) c).setWireframeColor(color);
        }
      }
    }
  }
  
  @Override
  public void setWireframeColorFromPolygonPoints(boolean status) {
    super.setWireframeColorFromPolygonPoints(status);
    
    if (components != null) {
      synchronized (components) {
        for (Drawable c : components) {
          if (c instanceof Wireframeable)
            ((Wireframeable) c).setWireframeColorFromPolygonPoints(status);
          //System.out.println(status);
        }
      }
    }
  }


  @Override
  public void setWireframeDisplayed(boolean status) {
    super.setWireframeDisplayed(status);

    if (components != null) {
      synchronized (components) {
        for (Drawable c : components) {
          if (c instanceof Wireframeable)
            ((Wireframeable) c).setWireframeDisplayed(status);
        }
      }
    }
  }

  @Override
  public void setWireframeWidth(float width) {
    super.setWireframeWidth(width);

    if (components != null) {
      synchronized (components) {
        for (Drawable c : components) {
          if (c != null && c instanceof Wireframeable)
            ((Wireframeable) c).setWireframeWidth(width);
        }
      }
    }
  }

  @Override
  public void setFaceDisplayed(boolean status) {
    super.setFaceDisplayed(status);

    if (components != null) {
      synchronized (components) {
        for (Drawable c : components) {
          if (c != null && c instanceof Wireframeable)
            ((Wireframeable) c).setFaceDisplayed(status);
        }
      }
    }
  }

  @Override
  public void setDisplayed(boolean status) {
    super.setDisplayed(status);

    if (components != null) {
      synchronized (components) {
        for (Drawable c : components) {
          if (c != null && c instanceof Wireframeable)
            ((Wireframeable) c).setDisplayed(status);
        }
      }
    }
  }

  /**
   * A utility to change polygon offset fill status of a {@link Composite} containing
   * {@link Geometry}s.
   * 
   * @param composite
   * @param polygonOffsetFillEnable status
   */
  @Override
  public void setPolygonOffsetFillEnable(boolean polygonOffsetFillEnable) {
    //super.setPolygonOffsetFillEnable(polygonOffsetFillEnable);
    
    if (components != null) {
      synchronized (components) {

        for (Drawable d : components) {
          if (d instanceof Wireframeable) {
            ((Wireframeable) d).setPolygonOffsetFillEnable(polygonOffsetFillEnable);
          } else if (d instanceof Composite) {
            ((Composite) d).setPolygonOffsetFillEnable(polygonOffsetFillEnable);
          }
        }
      }
    }
  }

  @Override
  public void setPolygonWireframeDepthTrick(boolean polygonOffsetFillEnable) {
    //super.setPolygonWireframeDepthTrick(polygonOffsetFillEnable);
    
    if (components != null) {
      synchronized (components) {

        for (Drawable d : components) {
          if (d instanceof Wireframeable) {
            ((Wireframeable) d).setPolygonWireframeDepthTrick(polygonOffsetFillEnable);
          } else if (d instanceof Composite) {
            ((Composite) d).setPolygonWireframeDepthTrick(polygonOffsetFillEnable);
          }
        }
      }
    }
  }
  
  @Override
  public void setReflectLight(boolean reflectLight) {
    super.setReflectLight(reflectLight);
    
    if (components != null) {
      synchronized (components) {
        for (Drawable d : components) {
          if (d instanceof Wireframeable) {
            ((Wireframeable) d).setReflectLight(reflectLight);
          } else if (d instanceof Composite) {
            ((Composite) d).setReflectLight(reflectLight);
          }
        }
      }
    }
  }

  public void setMaterialAmbiantReflection(Color materialAmbiantReflection) {
    this.materialAmbiantReflection = materialAmbiantReflection;
    
    if (components != null) {
      synchronized (components) {
        for (Drawable d : components) {
          if (d instanceof Wireframeable) {
            ((Wireframeable) d).setMaterialAmbiantReflection(materialAmbiantReflection);
          } else if (d instanceof Composite) {
            ((Composite) d).setMaterialAmbiantReflection(materialAmbiantReflection);
          }
        }
      }
    }
  }

  public void setMaterialDiffuseReflection(Color materialDiffuseReflection) {
    this.materialDiffuseReflection = materialDiffuseReflection;
    
    if (components != null) {
      synchronized (components) {
        for (Drawable d : components) {
          if (d instanceof Wireframeable) {
            ((Wireframeable) d).setMaterialDiffuseReflection(materialDiffuseReflection);
          } else if (d instanceof Composite) {
            ((Composite) d).setMaterialDiffuseReflection(materialDiffuseReflection);
          }
        }
      }
    }
  }

  public void setMaterialSpecularReflection(Color materialSpecularReflection) {
    this.materialSpecularReflection = materialSpecularReflection;
    
    if (components != null) {
      synchronized (components) {
        for (Drawable d : components) {
          if (d instanceof Wireframeable) {
            ((Wireframeable) d).setMaterialSpecularReflection(materialSpecularReflection);
          } else if (d instanceof Composite) {
            ((Composite) d).setMaterialSpecularReflection(materialSpecularReflection);
          }
        }
      }
    }
  }

  public void setMaterialEmission(Color materialEmission) {
    this.materialEmission = materialEmission;
    
    if (components != null) {
      synchronized (components) {
        for (Drawable d : components) {
          if (d instanceof Wireframeable) {
            ((Wireframeable) d).setMaterialEmission(materialEmission);
          } else if (d instanceof Composite) {
            ((Composite) d).setMaterialEmission(materialEmission);
          }
        }
      }
    }
  }

  public void setMaterialShininess(float shininess) {
    materialShininess[0] = shininess;
    
    if (components != null) {
      synchronized (components) {
        for (Drawable d : components) {
          if (d instanceof Wireframeable) {
            ((Wireframeable) d).setMaterialShininess(shininess);
          } else if (d instanceof Composite) {
            ((Composite) d).setMaterialShininess(shininess);
          }
        }
      }
    }
  }
  
  /****************************************************************/

  @Override
  public void setColorMapper(ColorMapper mapper) {
    this.mapper = mapper;

    if (components != null) {

      synchronized (components) {
        for (Drawable d : components) {
          if (d instanceof IMultiColorable)
            ((IMultiColorable) d).setColorMapper(mapper);
          else if (d instanceof ISingleColorable)
            ((ISingleColorable) d).setColor(mapper.getColor(d.getBarycentre()));
        }
      }

      fireDrawableChanged(new DrawableChangedEvent(this, DrawableChangedEvent.FIELD_COLOR));
    }

  }

  @Override
  public ColorMapper getColorMapper() {
    return mapper;
  }

  @Override
  public void setColor(Color color) {
    this.color = color;

    if (components != null) {
      synchronized (components) {
        for (Drawable d : components)
          if (d instanceof ISingleColorable)
            ((ISingleColorable) d).setColor(color);
      }
      fireDrawableChanged(new DrawableChangedEvent(this, DrawableChangedEvent.FIELD_COLOR));
    }
  }

  @Override
  public Color getColor() {
    return color;
  }

  /****************************************************************/

  /** Print out information concerning all Drawable of this composite. */
  @Override
  public String toString() {
    return toString(0);
  }

  @Override
  public String toString(int depth) {
    String output = Utils.blanks(depth) + "(Composite3d) #elements:" + components.size()
        + " | isDisplayed=" + isDisplayed();

    if (detailedToString) {
      int k = 0;
      for (Drawable c : components) {
        if (c != null) {
          if (c instanceof Composite)
            output += "\n" + ((Composite) c).toString(depth + 1);
          else
            output += "\n" + Utils.blanks(depth + 1) + " Composite element[" + (k++) + "]:"
                + c.toString();
        } else
          output += Utils.blanks(depth + 1) + "(null)\n";
      }
    }

    return output;
  }

  public boolean isDetailedToString() {
    return detailedToString;
  }

  /**
   * When to true, the {@link toString()} method will give the detail of each element of this
   * composite object in a tree like layout.
   */
  public void setDetailedToString(boolean detailedToString) {
    this.detailedToString = detailedToString;
  }

  /****************************************************************/

  protected List<Drawable> components = null;
  protected Transform transform;

  protected ColorMapper mapper;
  protected Color color;
  protected boolean detailedToString = false;
}
