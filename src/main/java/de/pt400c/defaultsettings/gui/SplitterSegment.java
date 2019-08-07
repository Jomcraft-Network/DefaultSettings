package de.pt400c.defaultsettings.gui;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.OpenGlHelper;

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

		  GL11.glDisable(GL11.GL_TEXTURE_2D);

	        GL11.glEnable(GL11.GL_BLEND);
	        GL11.glDisable(GL11.GL_ALPHA_TEST);
	        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
	        GL11.glShadeModel(GL11.GL_SMOOTH);
	        
	    	Segment.drawGradientCircle((float) (this.getPosX() + func), (float) this.getPosY() + 4, 6, 270, 75, 0xffaaaaaa, 0x00ffffff);
	    	
	    	Segment.drawGradientCircle((float) (this.getPosX() + func), (float) this.getPosY() + this.getHeight() - 4, 6, 0, 75, 0xffaaaaaa, 0x00ffffff);
	        
	        Segment.drawGradient((this.getPosX() + func), this.getPosY() + 4, (this.getPosX() + 6 + func), this.getPosY() + this.getHeight() - 4, 0xffaaaaaa, 0x00ffffff);
		
	
		
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
		Segment.drawRect(this.getPosX() + func, this.getPosY(), this.getPosX() + this.getWidth() + func, this.getPosY() + this.getHeight(), 0xffbebebe, true, null, false);

	}

}
