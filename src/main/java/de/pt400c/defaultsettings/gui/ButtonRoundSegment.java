package de.pt400c.defaultsettings.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import java.util.ArrayList;
import java.util.Collections;
import java.util.function.Function;
import static net.jomcraft.neptunefx.NeptuneFX.*;
import static net.jomcraft.neptunefx.NEX.*;
import net.jomcraft.neptunefx.gui.MathUtil;
import de.pt400c.defaultsettings.GuiConfig;
import static org.lwjgl.opengl.GL11.*;

@SideOnly(Side.CLIENT)
public class ButtonRoundSegment extends BakedSegment {
	
	protected boolean grabbed;
	protected boolean grabbed_prev;
	public float animTimer = 0;
	public float animTimerRight = 0;
    private float processFactor;
    private final String hover;
    private final Function<ButtonRoundSegment, Boolean> function;
	public int color;
	private boolean doIt;
	public final String title;
	private boolean selected_prev;
	public int color_prev;
	public final float size;
	
	public ButtonRoundSegment(GuiScreen gui, float posX, float posY, float width, float height, String title, String hover, Function<ButtonRoundSegment, Boolean> function, float size, boolean popupSegment) {
		
		super(gui, 52, posX, posY, width, height, 44, 44, 44, true, popupSegment);
		this.title = title;
		this.hover = hover;
		this.function = function;
		this.size = size; 
	}
	
	@Override
	public boolean hoverCheck(int mouseX, int mouseY) {
		if(this.isSelected(mouseX, mouseY) && this.hover != null) {
			final ArrayList<String> lines = new ArrayList<String>();
			
			float textWidth = (int) (mouseX + 12 + fontRenderer.getStringWidth(this.hover, 0.8F, true));
			if(textWidth > this.gui.width) {
				lines.addAll(fontRenderer.listFormattedStringToWidth(this.hover, (int) (this.gui.width - mouseX - 12), true));
			}else {
				lines.add(this.hover);
			}
			textWidth = 0;
			for(String line : lines) {
				
				if(fontRenderer.getStringWidth(line, 0.8F, true) > textWidth)
					textWidth = fontRenderer.getStringWidth(line, 0.8F, true);
			}
			
			drawButton(mouseX + 5, mouseY - 7 - 10 * lines.size(), mouseX + 15 + textWidth, mouseY - 3, 0xff3a3a3a, 0xffdcdcdc, 2);
			int offset = 0;
			
			Collections.reverse(lines);
			
			for(String line : lines) {
			
				fontRenderer.drawString(line, (float)(mouseX + 9), (float)(mouseY - 14 - offset), 0xff3a3a3a, 0.8F, true);
				offset += 10;
			}
			return true;
		}
		return false;
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		if(this.gui instanceof GuiConfig && ((GuiConfig) this.gui).popupField == null)
			return;
		setup();
		
		final boolean selected = this.isSelected(mouseX, mouseY);
		
		if(selected) {
			
			if(this.animTimer <= MathUtil.PI / 2) {
				this.animTimer += 0.15;
				doIt = true;
			}
			
		}else{
			if(this.animTimer > 0) {
				this.animTimer -= 0.15;
				doIt = true;
			}
		}
		
		if(this.selected_prev != selected || this.grabbed != this.grabbed_prev || this.color != this.color_prev || doIt) {
			this.compiled = false;
			doIt = false;
		}

		if (!compiled) {
			preRender();
			glPushMatrix();
			glEnable(GL_BLEND);
			glDisable(GL_ALPHA_TEST);
			glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
			glDisable(GL_TEXTURE_2D);

			drawRectRoundedCorners(0, 0, width, height, 0xffffffff, Integer.MAX_VALUE);

			float diff = this.grabbed ? 1.7F : 1.3F;

			final int off = 0xff3c3c3c;

			this.processFactor = (float) Math.sin(this.animTimer * 2 - (MathUtil.PI / 2)) / 2 + 0.5F;

			int red = (int) ((getRed(off)) + 35 * (processFactor));

			int green = (int) ((getGreen(off)) + 35 * (processFactor));

			int blue = (int) ((getBlue(off)) + 35 * (processFactor));

			drawRectRoundedCorners(diff, diff, width - diff, height - diff, ((255 & 0x0ff) << 24) | ((red & 0x0ff) << 16) | ((green & 0x0ff) << 8) | (blue & 0x0ff), Integer.MAX_VALUE);

			glEnable(GL_TEXTURE_2D);
			glEnable(GL_ALPHA_TEST);
			glDisable(GL_BLEND);

			fontRenderer.drawString(this.title, 1 + (this.getWidth()) / 2 - fontRenderer.getStringWidth(this.title, size, true) / 2, 6.5F + (size > 0.8F ? 1.2F : 0), 0xffffffff, size, true);

			glPopMatrix();
			postRender(1, this.isPopupSegment);

		}
		glPushMatrix();
		drawTexture(1);
		glPopMatrix();
	    	
		this.grabbed_prev = this.grabbed;
		this.selected_prev = selected;
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

			if(this.gui instanceof GuiConfig && !((GuiConfig) this.gui).popupField.open)
				return false;
			
			if (this.function.apply(this)) 
				this.clickSound();

		}
		return super.mouseReleased(mouseX, mouseY, button);
	}
	
	@Override
	public Segment setPos(float x, float y) {
		return super.setPos(x, y);
	}
}