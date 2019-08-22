package de.pt400c.defaultsettings.gui;

import de.pt400c.defaultsettings.DefaultSettings;
import de.pt400c.defaultsettings.NEX;
import static de.pt400c.defaultsettings.FileUtil.MC;
import org.lwjgl.opengl.GL11;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL14.glBlendFuncSeparate;

@SideOnly(Side.CLIENT)
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
	
		glPushMatrix();
		glEnable(GL11.GL_BLEND);
		glBlendFuncSeparate(770, 771, 1, 0);
		MC.getTextureManager().bindTexture(icon);
		this.posX = origX + customX;
		NEX.drawScaledTex((float) posX, (float) posY, 19, 19);
		glDisable(GL11.GL_BLEND);
		glPopMatrix();
	}
	
	@Override
	public void render(float mouseX, float mouseY, float partialTicks) {
	
		glPushMatrix();
		glEnable(GL11.GL_BLEND);
		glBlendFuncSeparate(770, 771, 1, 0);
		MC.getTextureManager().bindTexture(icon);
		NEX.drawScaledTex((float) posX, (float) posY, 16, 16);
		glDisable(GL11.GL_BLEND);
		glPopMatrix();
	}
}