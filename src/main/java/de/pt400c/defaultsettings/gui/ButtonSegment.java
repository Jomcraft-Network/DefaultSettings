package de.pt400c.defaultsettings.gui;

import java.awt.Color;
import java.util.ArrayList;
import static de.pt400c.defaultsettings.FileUtil.MC;
import java.util.Collections;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GLAllocation;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL14.glBlendFuncSeparate;
import static de.pt400c.neptunefx.NEX.*;
import static de.pt400c.neptunefx.DrawString.*;

@SideOnly(Side.CLIENT)
public class ButtonSegment extends Segment {
	
	protected final Function<ButtonSegment, Boolean> function;
	private static final float BRIGHT_SCALE = 0.85f;
	public String title;
	public String hoverMessage = null;
	protected boolean grabbed;
	protected final int border;
	public int color = 0xffa4a4a4;
	private int bgDPLList = -1;
    private boolean compiled;

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
	public void render(int mouseX, int mouseY, float partialTicks) {

		if (compiled)
			glCallList(this.bgDPLList);
		else {
			this.bgDPLList = GLAllocation.generateDisplayLists(1);
			glNewList(this.bgDPLList, GL_COMPILE);
			glPushMatrix();

			glEnable(GL_BLEND);
			glBlendFunc(GL_DST_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
			glShadeModel(GL_SMOOTH);
			glDisable(GL_TEXTURE_2D);

			drawGradient(this.getPosX() + this.width - 2, this.getPosY() + 2, this.getPosX() + this.width + 5, this.getPosY() + this.height - 2, 0xff000000, 0x00404040, 0);

			drawGradient(this.getPosX() - 5, this.getPosY() + 2, this.getPosX() + 2, this.getPosY() + this.height - 2, 0xff000000, 0x00404040, 2);

			drawGradient(this.getPosX() + 2, this.getPosY() - 5, this.getPosX() + this.width - 2, this.getPosY() + 2, 0xff000000, 0x00404040, 3);

			drawGradient(this.getPosX() + 2, this.getPosY() + this.height - 2, this.getPosX() + this.width - 2, this.getPosY() + this.height + 5, 0xff000000, 0x00404040, 1);

			drawGradientCircle((float) this.getPosX() + 2, (float) this.getPosY() + 2, 7, 180, 75, 0xff000000, 0x00404040);

			drawGradientCircle((float) this.getPosX() + this.width - 2, (float) this.getPosY() + 2, 7, 270, 75, 0xff000000, 0x00404040);

			drawGradientCircle((float) this.getPosX() + this.width - 2, (float) this.getPosY() + this.height - 2, 7, 0, 75, 0xff000000, 0x00404040);

			drawGradientCircle((float) this.getPosX() + 2, (float) this.getPosY() + this.height - 2, 7, 90, 75, 0xff000000, 0x00404040);

			glEnable(GL_TEXTURE_2D);
			glShadeModel(GL_FLAT);
			glDisable(GL_BLEND);

			glPopMatrix();

			glEndList();
			compiled = true;
			glCallList(this.bgDPLList);
		}

		drawButton(this.getPosX(), this.getPosY(), this.getPosX() + this.getWidth(), this.getPosY() + this.getHeight(), this.isSelected(mouseX, mouseY) ? darkenColor(this.color).getRGB() : this.color, 0xffdcdcdc, this.border);
		glPushMatrix();
     	glEnable(GL_BLEND);
     	glBlendFuncSeparate(770, 771, 1, 0);
		drawString(this.title, (float)((posX + this.getWidth() / 2) - MC.fontRenderer.getStringWidth(this.title) / 2), (float) (posY + this.getHeight() / 2 - 4), 0xff3a3a3a, false);
		glDisable(GL_BLEND);
		glPopMatrix();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean hoverCheck(int mouseX, int mouseY) {
		if(this.isSelected(mouseX, mouseY) && this.hoverMessage != null) {
			
			final ArrayList<String> lines = new ArrayList<String>();
			
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
			
			drawButton(mouseX + 6, mouseY - 7 - 10 * lines.size(), mouseX + 12 + textWidth, mouseY - 3, 0xff3a3a3a, 0xffdcdcdc, 2);
			int offset = 0;
			
			Collections.reverse(lines);
			
			for(String line : lines) {
			
				drawString(line, (float)(mouseX + 9), (float)(mouseY - 14 - offset), 0xff3a3a3a, false);
				offset += 10;
			}
			return true;
		}
		return false;
	}
	
	protected static Color darkenColor(int color) {
		return new Color((int) (((color & RED_MASK) >> 16) * BRIGHT_SCALE), (int) (((color & GREEN_MASK) >> 8) * BRIGHT_SCALE), (int) ((color & BLUE_MASK) * BRIGHT_SCALE), 255);
	}
	
	@Override
	public boolean mouseClicked(int mouseX, int mouseY, int button) {

		if (this.isSelected(mouseX, mouseY)) {
			this.grabbed = true;
			((DefaultSettingsGUI) this.gui).resetSelected();
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean mouseDragged(int mouseX, int mouseY, int button) {
		if (!this.isSelected(mouseX, mouseY))
			this.grabbed = false;
		return super.mouseDragged(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseReleased(int mouseX, int mouseY, int button) {
		if (this.grabbed) {
			if (this.isSelected(mouseX, mouseY))
				this.grabbed = false;

			if (this.function.apply(this)) 
				this.clickSound();

		}
		return super.mouseReleased(mouseX, mouseY, button);
	}
	
	@Override
	public Segment setPos(float x, float y) {
		compiled = false;
		return super.setPos(x, y);
	}

}