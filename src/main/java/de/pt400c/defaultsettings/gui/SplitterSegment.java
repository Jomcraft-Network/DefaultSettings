package de.pt400c.defaultsettings.gui;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SplitterSegment extends Segment {

	private float offsetTick = 0;
	
	public SplitterSegment(GuiScreen gui, float posX, float posY, int height) {
		super(gui, posX, posY, 1, height, false);
	}

	@Override
	public void render(float mouseX, float mouseY, float partialTicks) {
		
		final double triple = Math.sin(offsetTick);
		offsetTick += 0.05;
		final double func = triple * 20;
		  GlStateManager.disableTexture2D();
	        GlStateManager.enableBlend();
	        GlStateManager.disableAlpha();
	        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
	        GL11.glShadeModel(GL11.GL_SMOOTH);
	        
	    	Segment.drawGradientCircle((float) (this.getPosX() + func), (float) this.getPosY() + 4, 6, 270, 75, 0xffaaaaaa, 0x00ffffff);
	    	
	    	Segment.drawGradientCircle((float) (this.getPosX() + func), (float) this.getPosY() + this.getHeight() - 4, 6, 0, 75, 0xffaaaaaa, 0x00ffffff);
	        
	        Segment.drawGradient((this.getPosX() + func), this.getPosY() + 4, (this.getPosX() + 6 + func), this.getPosY() + this.getHeight() - 4, 0xffaaaaaa, 0x00ffffff);
		
	
		
        GL11.glShadeModel(GL11.GL_FLAT);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
		Segment.drawRect(this.getPosX() + func, this.getPosY(), this.getPosX() + this.getWidth() + func, this.getPosY() + this.getHeight(), 0xffbebebe, true, null, false);
		
	}

}
