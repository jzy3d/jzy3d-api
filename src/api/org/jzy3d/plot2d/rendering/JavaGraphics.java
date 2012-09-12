package org.jzy3d.plot2d.rendering;

import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class JavaGraphics {
	public static void printGraphicParameters(Graphics2D g2){
		System.out.println("KEY_ALPHA_INTERPOLATION="+ g2.getRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION));
		System.out.println("KEY_ANTIALIASING="       + g2.getRenderingHint(RenderingHints.KEY_ANTIALIASING));
		System.out.println("KEY_COLOR_RENDERING="    + g2.getRenderingHint(RenderingHints.KEY_COLOR_RENDERING));
		System.out.println("KEY_DITHERING="          + g2.getRenderingHint(RenderingHints.KEY_DITHERING));
		System.out.println("KEY_FRACTIONALMETRICS="  + g2.getRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS));
		System.out.println("KEY_INTERPOLATION="      + g2.getRenderingHint(RenderingHints.KEY_INTERPOLATION));
		System.out.println("KEY_RENDERING="          + g2.getRenderingHint(RenderingHints.KEY_RENDERING));
		System.out.println("KEY_STROKE_CONTROL="     + g2.getRenderingHint(RenderingHints.KEY_STROKE_CONTROL));
		System.out.println("KEY_TEXT_ANTIALIASING="  + g2.getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING));
		//System.out.println("KEY_TEXT_LCD_CONTRAST="  + g2.getRenderingHint(RenderingHints.KEY_TEXT_LCD_CONTRAST));
		System.out.println("KEY_STROKE_CONTROL="     + g2.getRenderingHint(RenderingHints.KEY_STROKE_CONTROL));
	}
}
