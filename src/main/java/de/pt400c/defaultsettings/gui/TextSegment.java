package de.pt400c.defaultsettings.gui;

import org.lwjgl.opengl.GL11;
import de.pt400c.defaultsettings.GuiConfig;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.OpenGlHelper;

public class TextSegment extends Segment {

	public final int color;
	public final String text;
	
	public TextSegment(GuiScreen gui, float posX, float posY, int width, int height, String text, int color, boolean popup) {
		super(gui, posX, posY, width, height, popup);
		
		this.color = color;
		this.text = text;
	}

	@Override
	public void render(float mouseX, float mouseY, float partialTicks) {
		
		float alpha = !this.isPopupSegment ? 0 : ((GuiConfig) this.gui).popupField == null ? 1 : ((GuiConfig) this.gui).popupField.getWindow().alphaRate;
		GL11.glPushMatrix();
     	GL11.glEnable(GL11.GL_BLEND);
     	OpenGlHelper.glBlendFunc(770, 771, 1, 0);
     	int offsetY = 0;
     	for(String line : this.text.split("\n")) {
     		this.drawString(line, (float) this.getPosX(), (float) this.getPosY() + offsetY, calcAlpha(this.color, alpha).getRGB(), false);
     		offsetY += 9;
     	}
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glPopMatrix();
		
	}
}
