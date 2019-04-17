package de.pt400c.defaultsettings.gui;

import java.awt.Color;
import java.util.ArrayList;
import static de.pt400c.defaultsettings.FileUtil.MC;
import java.util.Collections;
import java.util.function.Function;
import net.minecraft.client.gui.GuiScreen;

public class ButtonSegment extends Segment {
	
	protected final Function<ButtonSegment, Boolean> function;
	protected static final int RED_MASK = 255 << 16;
	protected static final int GREEN_MASK = 255 << 8;
	protected static final int BLUE_MASK = 255;
	private static final float BRIGHT_SCALE = 0.85f;
	public String title;
	public String hoverMessage = null;
	protected boolean grabbed;
	protected final int border;
	public int color = 0xffa4a4a4;

	public ButtonSegment(GuiScreen gui, float posX, float posY, String title, Function<ButtonSegment, Boolean> function, int width, int height, int border, String hoverMessage) {
		super(gui, posX, posY, width, height);
		this.title = title;
		this.function = function;
		this.border = border;
		this.hoverMessage = hoverMessage;
	}
	
	public ButtonSegment(GuiScreen gui, float posX, float posY, String title, Function<ButtonSegment, Boolean> function, int width, int height, int border) {
		this(gui, posX, posY, title, function, width, height, border, null);
	}

	@Override
	public void render(float mouseX, float mouseY, float partialTicks) {
		Segment.drawButton(this.getPosX(), this.getPosY(), this.getPosX() + this.getWidth(), this.getPosY() + this.getHeight(), this.isSelected(mouseX, mouseY) ? darkenColor(this.color).getRGB() : this.color, 0xffdcdcdc, this.border);
		MC.fontRendererObj.drawString(this.title, (float)((posX + this.getWidth() / 2) - MC.fontRendererObj.getStringWidth(this.title) / 2), (float) (posY + this.getHeight() / 2 - 4), 0xff3a3a3a, false);
	}
	
	@Override
	public void hoverCheck(float mouseX, float mouseY) {
		if(this.isSelected(mouseX, mouseY) && this.hoverMessage != null) {
			
			ArrayList<String> lines = new ArrayList<String>();
			
			int textWidth = (int) (mouseX + 12 + MC.fontRendererObj.getStringWidth(this.hoverMessage));
			if(textWidth > this.gui.width) {
				lines.addAll(MC.fontRendererObj.listFormattedStringToWidth(this.hoverMessage, (int) (this.gui.width - mouseX - 12)));
			}else {
				lines.add(this.hoverMessage);
			}
			textWidth = 0;
			for(String line : lines) {
				
				if(MC.fontRendererObj.getStringWidth(line) > textWidth)
					textWidth = MC.fontRendererObj.getStringWidth(line);
			}
			
			Segment.drawButton(mouseX + 6, mouseY - 7 - 10 * lines.size(), mouseX + 12 + textWidth, mouseY - 3, 0xff3a3a3a, 0xffdcdcdc, 2);
			int offset = 0;
			
			Collections.reverse(lines);
			
			for(String line : lines) {
			
				MC.fontRendererObj.drawString(line, (float)(mouseX + 9), (float)(mouseY - 14 - offset), 0xff3a3a3a, false);
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
