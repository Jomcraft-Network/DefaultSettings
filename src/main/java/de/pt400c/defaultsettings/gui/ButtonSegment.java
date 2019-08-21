package de.pt400c.defaultsettings.gui;

import java.awt.Color;
import java.util.ArrayList;
import static de.pt400c.defaultsettings.FileUtil.MC;
import java.util.Collections;
import java.util.function.Function;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.pt400c.defaultsettings.GuiConfig;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.OpenGlHelper;

@SideOnly(Side.CLIENT)
public class ButtonSegment extends Segment {
	
	protected final Function<ButtonSegment, Boolean> function;
	private static final float BRIGHT_SCALE = 0.85f;
	public String title;
	public String hoverMessage = null;
	protected boolean grabbed;
	protected final int border;
	public int color = 0xffa4a4a4;

	public ButtonSegment(GuiScreen gui, float posX, float posY, String title, Function<ButtonSegment, Boolean> function, int width, int height, int border, String hoverMessage, LeftMenu menu, boolean popupSegment) {
		super(gui, posX, posY, width, height, popupSegment);
		this.title = title;
		this.function = function;
		this.border = border;
		this.hoverMessage = hoverMessage;
	}
	
	public ButtonSegment(GuiScreen gui, float posX, float posY, String title, Function<ButtonSegment, Boolean> function, int width, int height, int border, String hoverMessage, boolean popupSegment) {
		this(gui, posX, posY, title, function, width, height, border, hoverMessage, null, popupSegment);
	}
	
	public ButtonSegment(GuiScreen gui, float posX, float posY, String title, Function<ButtonSegment, Boolean> function, int width, int height, int border, String hoverMessage) {
		this(gui, posX, posY, title, function, width, height, border, hoverMessage, null, false);
	}
	
	public ButtonSegment(GuiScreen gui, float posX, float posY, String title, Function<ButtonSegment, Boolean> function, int width, int height, int border) {
		this(gui, posX, posY, title, function, width, height, border, null);
	}
	
	public ButtonSegment(GuiScreen gui, float posX, float posY, String title, Function<ButtonSegment, Boolean> function, int width, int height, int border, boolean popupSegment) {
		this(gui, posX, posY, title, function, width, height, border, null, popupSegment);
	}

	@Override
	public void render(float mouseX, float mouseY, float partialTicks) {
		float alpha = !this.isPopupSegment ? 0 : ((GuiConfig) this.gui).popupField == null ? 1 : ((GuiConfig) this.gui).popupField.getWindow().alphaRate;
		
		Segment.drawButton(this.getPosX(), this.getPosY(), this.getPosX() + this.getWidth(), this.getPosY() + this.getHeight(), calcAlpha((this.isSelected(mouseX, mouseY) ? darkenColor(this.color).getRGB() : this.color), alpha).getRGB(), calcAlpha(0xffdcdcdc, alpha).getRGB(), this.border);
		
		GL11.glPushMatrix();
     	GL11.glEnable(GL11.GL_BLEND);
     	OpenGlHelper.glBlendFunc(770, 771, 1, 0);
     	this.drawString(this.title, (float)((posX + this.getWidth() / 2) - MC.fontRenderer.getStringWidth(this.title) / 2), (float) (posY + this.getHeight() / 2 - 4), calcAlpha(0xff3a3a3a, alpha).getRGB(), false);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glPopMatrix();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void hoverCheck(float mouseX, float mouseY) {
		if(this.isSelected(mouseX, mouseY) && this.hoverMessage != null) {
			
			ArrayList<String> lines = new ArrayList<String>();
			
			int textWidth = (int) (mouseX + 12 + MC.fontRenderer.getStringWidth(this.hoverMessage));
			if(textWidth > this.gui.width) {
				lines.addAll(MC.fontRenderer.listFormattedStringToWidth(this.hoverMessage, (int) (this.gui.width - mouseX - 12)));
			}else {
				lines.add(this.hoverMessage);
			}
			textWidth = 0;
			for(String line : lines) {
				
				if(MC.fontRenderer.getStringWidth(line) > textWidth)
					textWidth = MC.fontRenderer.getStringWidth(line);
			}
			
			Segment.drawButton(mouseX + 6, mouseY - 7 - 10 * lines.size(), mouseX + 12 + textWidth, mouseY - 3, 0xff3a3a3a, 0xffdcdcdc, 2);
			int offset = 0;
			
			Collections.reverse(lines);
			
			for(String line : lines) {
			
				this.drawString(line, (float)(mouseX + 9), (float)(mouseY - 14 - offset), 0xff3a3a3a, false);
				offset += 10;
			}
		}
	}
	
	protected static Color darkenColor(int color) {
		return new Color((int) (((color & RED_MASK) >> 16) * BRIGHT_SCALE), (int) (((color & GREEN_MASK) >> 8) * BRIGHT_SCALE),
		(int) ((color & BLUE_MASK) * BRIGHT_SCALE), 255);
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {

		if (this.isSelected(mouseX, mouseY)) {
			this.grabbed = true;
			((DefaultSettingsGUI) this.gui).resetSelected();

			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button) {
		if (!this.isSelected(mouseX, mouseY))
			this.grabbed = false;
		return super.mouseDragged(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		if (this.grabbed) {
			if (this.isSelected(mouseX, mouseY))
				this.grabbed = false;

			if (this.function.apply(this)) {
				this.clickSound();
			}

		}
		return super.mouseReleased(mouseX, mouseY, button);
	}

}
