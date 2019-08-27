package de.pt400c.defaultsettings.gui;

import de.pt400c.defaultsettings.DefaultSettings;
import de.pt400c.defaultsettings.FileUtil;
import static de.pt400c.neptunefx.DrawString.drawString;
import static de.pt400c.neptunefx.NEX.*;
import static de.pt400c.defaultsettings.FileUtil.MC;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import static org.lwjgl.opengl.GL14.glBlendFuncSeparate;
import java.util.ArrayList;
import java.util.Collections;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import static org.lwjgl.opengl.GL11.*;

@SideOnly(Side.CLIENT)
public class DeleteSegment extends Segment {
	
	private final ResourceLocation icon;
	final int id;
	private boolean grabbed;
	private boolean visible;
	private float timer;
	
	public DeleteSegment(GuiScreen gui, float posX, float posY, int width, int height, int id) {
		super(gui, posX, posY, width, height, false);
		this.icon = new ResourceLocation(DefaultSettings.MODID, "textures/gui/trash.png");
		this.id = id;
	}
	
	@Override
	public boolean isSelected(int mouseX, int mouseY) {
		
		return (((DefaultSettingsGUI) this.gui).popupField == null || this.getIsPopupSegment()) && distanceBetweenPoints(posX, posY, mouseX, mouseY) <= 11;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void hoverCheck(int mouseX, int mouseY) {
		if(this.isSelected(mouseX, mouseY) && this.visible) {
			final String message = "Delete this saved entry";
			final ArrayList<String> lines = new ArrayList<String>();
			
			int textWidth = (int) (mouseX + 12 + MC.fontRenderer.getStringWidth(message));
			if(textWidth > this.gui.width) {
				lines.addAll(MC.fontRenderer.listFormattedStringToWidth(message, (int) (this.gui.width - mouseX - 12)));
			}else {
				lines.add(message);
			}
			textWidth = 0;
			for(String line : lines) {
				
				if(MC.fontRenderer.getStringWidth(line) > textWidth)
					textWidth = MC.fontRenderer.getStringWidth(line);
			}
			
			drawButton(mouseX + 6, mouseY - 7 - 10 * lines.size(), mouseX + 12 + textWidth, mouseY - 3, 0xff3a3a3a, 0xffdcdcdc, 2);
			int offset = 0;
			
			Collections.reverse(lines);
			
			for(String line : lines) {
			
				drawString(line, (float)(mouseX + 9), (float)(mouseY - 14 - offset), 0xff3a3a3a, false);
				offset += 10;
			}
		}
	}
	
	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		
		this.visible = true;
		
		if(this.id == 0 && !FileUtil.keys_exists)
			this.visible = false;
		if(this.id == 1 && !FileUtil.options_exists)
			this.visible = false;
		if(this.id == 2 && !FileUtil.servers_exists)
			this.visible = false;

		if (this.visible) {
			
			if (this.timer <= (Math.PI / 3))
				this.timer += 0.05;

		} else {

			if (this.timer > 0)
				this.timer -= 0.05;

		}
		if(this.timer <= 0)
			return;

		float alpha = (float) ((Math.sin(3 * this.timer - 3 * (Math.PI / 2)) + 1) / 2);
		
		glPushMatrix();
		int color;
		
		if(this.isSelected(mouseX, mouseY))
			color = 0xffbe2e2c;
		else
			color = 0xffd85755;
		
		float f3 = (float) (color >> 24 & 255) / 255.0F;
		float f = (float) (color >> 16 & 255) / 255.0F;
		float f1 = (float) (color >> 8 & 255) / 255.0F;
		float f2 = (float) (color & 255) / 255.0F;

		glEnable(GL_BLEND);
		glDisable(GL_TEXTURE_2D);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glDisable(GL_ALPHA_TEST);
		
		glShadeModel(GL_SMOOTH);
		
		drawGradientCircle((float) posX + 2.5F, (float) posY + 2.5F, 13, 0, 0, calcAlpha(0xff000000, alpha).getRGB(), 0x00000000);
		
		glShadeModel(GL_FLAT);
		
		glColor4f(1, 1, 1, 1);
		
		drawCircle((float) posX, (float) posY, width / 2, 0, 0);
		
		glColor4f(f, f1, f2, f3 - alpha);

		drawCircle((float) posX, (float) posY, width / 2, 0, 0);

		glDisable(GL_BLEND);
		glEnable(GL_TEXTURE_2D);	
		
		glEnable(GL_BLEND);
		glBlendFuncSeparate(770, 771, 1, 0);
		glColor4f(1, 1, 1, 1 - alpha);
		MC.getTextureManager().bindTexture(icon);
		drawScaledTex((float) posX - 9.5F, (float) posY - 9.5F, (int) this.width, (int) this.height);
		glDisable(GL_BLEND);
		glPopMatrix();
	}
	
	@Override
	public boolean mouseClicked(int mouseX, int mouseY, int button) {

		if (this.isSelected(mouseX, mouseY) && this.visible) {
			this.grabbed = true;
			((DefaultSettingsGUI) this.gui).resetSelected();
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean mouseDragged(int mouseX, int mouseY, int button) {
		if (!this.isSelected(mouseX, mouseY) && this.visible)
			this.grabbed = false;
		return super.mouseDragged(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseReleased(int mouseX, int mouseY, int button) {
		if (this.grabbed) {
			if (this.isSelected(mouseX, mouseY) && this.visible)
				this.grabbed = false;
			if (this.id == 0) 
				FileUtil.deleteKeys();
			else if (this.id == 1)
				FileUtil.deleteOptions();
			else if (this.id == 2)
				FileUtil.deleteServers();

			this.clickSound();

		}
		return super.mouseReleased(mouseX, mouseY, button);
	}
}