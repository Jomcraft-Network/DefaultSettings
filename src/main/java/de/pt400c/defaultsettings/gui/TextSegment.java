package de.pt400c.defaultsettings.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import static de.pt400c.defaultsettings.DefaultSettings.fontRenderer;
import static org.lwjgl.opengl.GL14.glBlendFuncSeparate;
import static org.lwjgl.opengl.GL11.*;

@SideOnly(Side.CLIENT)
public class TextSegment extends Segment {

	public final int color;
	public final String text;
	private final int offset;
	private final float size;
	
	public TextSegment(GuiScreen gui, float posX, float posY, int width, int height, String text, int color, boolean popup, float size) {
		this(gui, posX, posY, width, height, text, color, 9, popup, size);
	}
	
	public TextSegment(GuiScreen gui, float posX, float posY, int width, int height, String text, int color, boolean popup) {
		this(gui, posX, posY, width, height, text, color, 9, popup, 0.9F);
	}
	
	public TextSegment(GuiScreen gui, float posX, float posY, int width, int height, String text, int color, int offset, boolean popup, float size) {
		super(gui, posX, posY, width, height, popup);
		
		this.color = color;
		this.text = text;
		this.offset = offset;
		this.size = size;
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		glPushMatrix();
     	GlStateManager.enableBlend();
     	GlStateManager.tryBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);
     	int offsetY = 0;
     	for(String line : this.text.split("\n")) {
     		fontRenderer.drawString(line, (float) this.getPosX(), (float) this.getPosY() + offsetY, this.color, this.size, true);
     		offsetY += this.offset;
     	}
     	
		GlStateManager.disableBlend();
		glPopMatrix();
	}
}