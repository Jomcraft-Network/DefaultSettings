package de.pt400c.defaultsettings.gui;

import de.pt400c.defaultsettings.DefaultSettings;
import static de.pt400c.neptunefx.NEX.*;
import static de.pt400c.defaultsettings.FileUtil.MC;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import com.mojang.blaze3d.platform.GlStateManager;
import static org.lwjgl.opengl.GL11.*;

@OnlyIn(Dist.CLIENT)
public class IconSegment extends Segment {
	
	private final ResourceLocation icon;
	private final float origX;
	
	public IconSegment(Screen gui, float posX, float posY, float width, float height, String res, LeftMenu menu) {
		super(gui, posX, posY, width, height, false);
		this.icon = new ResourceLocation(DefaultSettings.MODID, res);
		this.origX = posX;
	}

	@Override
	public void customRender(int mouseX, int mouseY, float customX, float customY, float partialTicks) {
	
		glPushMatrix();
		GlStateManager.enableBlend();
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		MC.getTextureManager().bindTexture(icon);
		GlStateManager.texParameter(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		GlStateManager.texParameter(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		GlStateManager.texParameter(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP);
		GlStateManager.texParameter(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP);
		this.posX = origX + customX;
		drawScaledTex((float) posX, (float) posY, (int) this.width, (int) this.height);
		GlStateManager.disableBlend();
		glPopMatrix();
		
	}
	
	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
	
		glPushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.glBlendFuncSeparate(770, 771, 1, 0);
		MC.getTextureManager().bindTexture(icon);
		GlStateManager.texParameter(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		GlStateManager.texParameter(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		GlStateManager.texParameter(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP);
		GlStateManager.texParameter(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP);
		drawScaledTex((float) posX, (float) posY, 16, 16);
		GlStateManager.disableBlend();
		glPopMatrix();
		
	}
}