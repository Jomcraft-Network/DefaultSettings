package de.pt400c.defaultsettings.gui;

import de.pt400c.defaultsettings.DefaultSettings;
import static de.pt400c.defaultsettings.FileUtil.MC;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class IconSegment extends Segment {
	
	private final ResourceLocation icon;
	private final float origX;
	
	public IconSegment(GuiScreen gui, float posX, float posY, int width, int height, String res, LeftMenu menu) {
		super(gui, posX, posY, width, height, false);
		this.icon = new ResourceLocation(DefaultSettings.MODID, res);
		this.origX = posX;
	}

	@Override
	public void customRender(float mouseX, float mouseY, float customX, float customY, float partialTicks) {
	
		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_BLEND);
     	OpenGlHelper.glBlendFuncSeparate(770, 771, 1, 0);
		MC.getTextureManager().bindTexture(icon);
		this.posX = origX + customX;
		final float actual = 128;

		Gui.drawScaledCustomSizeModalRect((int) posX, (int) posY, 0, 0, (int) actual, (int) actual, 19, 19, actual, actual);

		GL11.glDisable(GL11.GL_BLEND);
		GL11.glPopMatrix();
		
	}
	
	@Override
	public void render(float mouseX, float mouseY, float partialTicks) {
	
		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_BLEND);
     	OpenGlHelper.glBlendFuncSeparate(770, 771, 1, 0);
		MC.getTextureManager().bindTexture(icon);
		
		final float actual = 128;
		Gui.drawScaledCustomSizeModalRect((int) posX, (int) posY, 0, 0, (int) actual, (int) actual, 16, 16, actual, actual);

		GL11.glDisable(GL11.GL_BLEND);
		GL11.glPopMatrix();
		
	}
}