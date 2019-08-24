package de.pt400c.defaultsettings;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author Jomcraft Network (PT400C)
 * @category NeptuneFX
 */
public class NEX {
	
	public static void drawScaledTex(float x, float y, int width, int height) {
		glBegin(GL_QUADS);
		glTexCoord2f(0, 1); glVertex3d(x, 19 + y, 0);
		glTexCoord2f(1, 1); glVertex3d(19 + x, 19 + y, 0);
		glTexCoord2f(1, 0); glVertex3d(19 + x, y, 0);
		glTexCoord2f(0, 0); glVertex3d(x, y, 0);
		glEnd();
    }
}
