package de.pt400c.defaultsettings.gui;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SplitterSegment extends Segment {
	
	private final LeftMenu menu;
	private final float origX;
	
	public SplitterSegment(GuiScreen gui, float posX, float posY, int height, LeftMenu menu) {
		super(gui, posX, posY, 1, height, false);
		this.menu = menu;
		this.origX = posX;
	}

	@Override
	public void render(float mouseX, float mouseY, float partialTicks) {
		
		this.posX = origX - this.menu.offs;
		GlStateManager.disableTexture2D();
		GlStateManager.enableBlend();
		GlStateManager.disableAlpha();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GL11.glShadeModel(GL11.GL_SMOOTH);

		Segment.drawGradientCircle((float) this.getPosX(), (float) this.getPosY() + 4, 6, 270, 75, 0xffaaaaaa, 0x00ffffff);

		Segment.drawGradientCircle((float) this.getPosX(), (float) this.getPosY() + this.getHeight() - 4, 6, 0, 75, 0xffaaaaaa, 0x00ffffff);

		Segment.drawGradient(this.getPosX(), this.getPosY() + 4, this.getPosX() + 6, this.getPosY() + this.getHeight() - 4, 0xffaaaaaa, 0x00ffffff);

		GL11.glShadeModel(GL11.GL_FLAT);
		GlStateManager.disableBlend();
		GlStateManager.enableAlpha();
		GlStateManager.enableTexture2D();
		Segment.drawRect(this.getPosX(), this.getPosY(), this.getPosX() + this.getWidth(), this.getPosY() + this.getHeight(), 0xffbebebe, true, null, false);
		
	}

}
