package net.jomcraft.defaultsettings.gui;

import net.jomcraft.neptunefx.gui.MathUtil;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.jomcraft.defaultsettings.DefaultSettings;
import net.jomcraft.neptunefx.NEX;
import static net.jomcraft.neptunefx.NeptuneFX.fontRenderer;
import static net.jomcraft.neptunefx.NeptuneFX.MC;
import static org.lwjgl.opengl.GL14.glBlendFuncSeparate;
import java.util.ArrayList;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import static org.lwjgl.opengl.GL11.*;

@SideOnly(Side.CLIENT)
public class AboutSegment extends Segment {
	
	private final ResourceLocation icon;
	
	public AboutSegment(GuiScreen gui, float posX, float posY, int width, int height, boolean popup) {
		super(gui, posX, posY, width, height, popup);
		this.icon = new ResourceLocation(DefaultSettings.MODID, "textures/gui/icon.png");
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		glPushMatrix();
		
		glEnable(GL_BLEND);
     	glBlendFuncSeparate(770, 771, 1, 0);
		MC.getTextureManager().bindTexture(icon);
		NEX.drawScaledTex((float) posX, (float) posY - 5, 80, 80);
		glDisable(GL_BLEND);
		
		fontRenderer.drawString("DefaultSettings", (float) this.getPosX() + 90, (float) this.getPosY() - 5, 0xfff0f0f0, 1.8F, true);
		final String text = "This client mod offers the ability of creating and using modpacks which ship custom settings, entries in the server list or even custom keybindings without having them overwriting your local settings every time the modpack updates";

		final ArrayList<String> lines = new ArrayList<String>();
		float textWidth = this.getPosX() + 90 + fontRenderer.getStringWidth(text, 1, false);
		
		int maxSize = MathUtil.clamp((int) (this.gui.width - this.getPosX() - 90), 200, Integer.MAX_VALUE);

		if(textWidth > this.gui.width) {
			lines.addAll(fontRenderer.listFormattedStringToWidth(text, maxSize, false));
		}else {
			lines.add(text);
		}
		textWidth = 0;
		for(String line : lines) {
			
			if(fontRenderer.getStringWidth(line, 1, false) > textWidth)
				textWidth = fontRenderer.getStringWidth(line, 1, false);
		}

		int offset = 0;
		
		for(String line : lines) {

			fontRenderer.drawString(line, (float) this.getPosX() + 90, (float) this.getPosY() + 20 + offset, 0xfff0f0f0, 1F, false);
			offset += 10;
		}
		
     	fontRenderer.drawString("DefaultSettings:", (float) this.getPosX(), (float) this.getPosY() + 90, 0xfff0f0f0, 1.2F, true);
     	
     	fontRenderer.drawString("Build - ID:", (float) this.getPosX(), (float) this.getPosY() + 90 + 20, 0xfff0f0f0, 1.2F, true);
     	
     	fontRenderer.drawString("Build - Time:", (float) this.getPosX(), (float) this.getPosY() + 90 + 20 * 2, 0xfff0f0f0, 1.2F, true);
     	
     	fontRenderer.drawString("Created by Jomcraft Network, 2020", (float) this.getPosX(), (float) this.getPosY() + 90 + 70, 0xfff0f0f0, 1.2F, true);
     	
     	fontRenderer.drawString(MinecraftForge.MC_VERSION + "-" + DefaultSettings.VERSION, (float) this.getPosX() + 125, (float) this.getPosY() + 90, 0xff9e9e9e, 1.2F, false);
     	
     	fontRenderer.drawString(DefaultSettings.BUILD_ID, (float) this.getPosX() + 125, (float) this.getPosY() + 90 + 20, 0xff9e9e9e, 1.2F, false);
     	
     	fontRenderer.drawString(DefaultSettings.BUILD_TIME, (float) this.getPosX() + 125, (float) this.getPosY() + 90 + 20 * 2, 0xff9e9e9e, 1.2F, false);
		glPopMatrix();
	}
}