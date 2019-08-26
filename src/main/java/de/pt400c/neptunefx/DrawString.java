package de.pt400c.neptunefx;

import static de.pt400c.defaultsettings.FileUtil.MC;

public class DrawString {

	public static void drawString(String text, float x, float y, int color) {
		MC.fontRenderer.drawString(text, x, y, color);
	}
}