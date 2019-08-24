package de.pt400c.defaultsettings.gui;

import static de.pt400c.defaultsettings.FileUtil.MC;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import static org.lwjgl.opengl.GL14.glBlendFuncSeparate;
import static org.lwjgl.opengl.GL11.*;

@SideOnly(Side.CLIENT)
public class TextSegment extends Segment {

	public final int color;
	public final String text;
	private final int offset;
	
	public TextSegment(GuiScreen gui, float posX, float posY, int width, int height, String text, int color, boolean popup) {
		this(gui, posX, posY, width, height, text, color, 9, popup);
	}
	
	public TextSegment(GuiScreen gui, float posX, float posY, int width, int height, String text, int color, int offset, boolean popup) {
		super(gui, posX, posY, width, height, popup);
		
		this.color = color;
		this.text = text;
		this.offset = offset;
	}

	@Override
	public void render(float mouseX, float mouseY, float partialTicks) {
		glPushMatrix();
     	glEnable(GL_BLEND);
     	glBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);
     	int offsetY = 0;
     	for(String line : this.text.split("\n")) {
     		MC.fontRenderer.drawString(line, (float) this.getPosX(), (float) this.getPosY() + offsetY, this.color, false);
     		offsetY += this.offset;
     	}
		glDisable(GL_BLEND);
		glPopMatrix();
	}
}