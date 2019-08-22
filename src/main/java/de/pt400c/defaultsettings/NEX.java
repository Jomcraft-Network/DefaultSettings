package de.pt400c.defaultsettings;

import org.lwjgl.opengl.GL11;

/**
 * @author Jomcraft Network (PT400C)
 * @category NeptuneFX
 */
public class NEX {
	
	public static void drawScaledTex(float x, float y, int width, int height) {
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2f(0, 1); GL11.glVertex3d(x, 19 + y, 0);
		GL11.glTexCoord2f(1, 1); GL11.glVertex3d(19 + x, 19 + y, 0);
		GL11.glTexCoord2f(1, 0); GL11.glVertex3d(19 + x, y, 0);
		GL11.glTexCoord2f(0, 0); GL11.glVertex3d(x, y, 0);
		GL11.glEnd();
    }
}
