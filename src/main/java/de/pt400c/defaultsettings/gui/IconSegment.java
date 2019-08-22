package de.pt400c.defaultsettings.gui;

import de.pt400c.defaultsettings.DefaultSettings;
import de.pt400c.defaultsettings.NEX;
import static de.pt400c.defaultsettings.FileUtil.MC;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
	
		NEX.pushMX();
		NEX.en(GL11.GL_BLEND);
		NEX.blendSep(770, 771, 1, 0);
		MC.getTextureManager().bindTexture(icon);
		this.posX = origX + customX;
		Segment.drawScaledCustomSizeModalRect((float) posX, (float) posY, 19, 19);
		NEX.dis(GL11.GL_BLEND);
		NEX.popMX();
	}
	
	@Override
	public void render(float mouseX, float mouseY, float partialTicks) {
	
		NEX.pushMX();
		NEX.en(GL11.GL_BLEND);
		NEX.blendSep(770, 771, 1, 0);
		MC.getTextureManager().bindTexture(icon);
		Segment.drawScaledCustomSizeModalRect((float) posX, (float) posY, 16, 16);
		NEX.dis(GL11.GL_BLEND);
		NEX.popMX();
	}
}