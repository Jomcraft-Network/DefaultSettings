package de.pt400c.defaultsettings.gui;

import de.pt400c.defaultsettings.DefaultSettings;
import static de.pt400c.neptunefx.NEX.*;
import static de.pt400c.defaultsettings.FileUtil.MC;
import static org.lwjgl.opengl.GL14.glBlendFuncSeparate;
import net.minecraft.client.gui.GuiScreen;
import static org.lwjgl.opengl.GL11.*;
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
	public void customRender(int mouseX, int mouseY, float customX, float customY, float partialTicks) {
		glPushMatrix();
		glEnable(GL_BLEND);
     	glBlendFuncSeparate(770, 771, 1, 0);
		MC.getTextureManager().bindTexture(icon);
		this.posX = origX + customX;
		drawScaledTex((float) posX, (float) posY, 19, 19);
		glDisable(GL_BLEND);
		glPopMatrix();
	}
	
	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		glPushMatrix();
		glEnable(GL_BLEND);
     	glBlendFuncSeparate(770, 771, 1, 0);
		MC.getTextureManager().bindTexture(icon);
		drawScaledTex((float) posX, (float) posY, 19, 19);
		glDisable(GL_BLEND);
		glPopMatrix();
	}
}