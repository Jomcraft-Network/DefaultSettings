package de.pt400c.defaultsettings.gui;

import static de.pt400c.defaultsettings.FileUtil.MC;
import org.lwjgl.opengl.GL11;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.screen.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TextSegment extends Segment {

	public final int color;
	public final String text;
	private final int offset;
	
	public TextSegment(Screen gui, float posX, float posY, int width, int height, String text, int color, boolean popup) {
		this(gui, posX, posY, width, height, text, color, 9, popup);
	}
	
	public TextSegment(Screen gui, float posX, float posY, int width, int height, String text, int color, int offset, boolean popup) {
		super(gui, posX, posY, width, height, popup);
		
		this.color = color;
		this.text = text;
		this.offset = offset;
	}

	@Override
	public void render(float mouseX, float mouseY, float partialTicks) {

		GL11.glPushMatrix();
     	GL11.glEnable(GL11.GL_BLEND);
     	GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
     	int offsetY = 0;
     	for(String line : this.text.split("\n")) {
     		MC.fontRenderer.drawString(line, (float) this.getPosX(), (float) this.getPosY() + offsetY, this.color);
     		offsetY += this.offset;
     	}
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glPopMatrix();
		
	}
}