package de.pt400c.defaultsettings.gui;

import de.pt400c.defaultsettings.GuiConfig;
import static de.pt400c.defaultsettings.FileUtil.MC;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL14.glBlendFuncSeparate;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.SharedConstants;
import static de.pt400c.neptunefx.NEX.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import static org.lwjgl.glfw.GLFW.*;
import static de.pt400c.neptunefx.DrawString.*;

@OnlyIn(Dist.CLIENT)
public class SearchbarSegment extends Segment {
	
	protected boolean grabbed;
	public String query = "";
	protected boolean focused = false;
	private int cursorTimer = 0;
	private boolean activated;
	private float flashingTimer = 0;
	protected final ScrollableSegment parent;

	public SearchbarSegment(Screen gui, float posX, float posY, int width, int height, boolean popupSegment, ScrollableSegment parent) {
		super(gui, posX, posY, width, height, popupSegment);
		this.parent = parent;
	}
	
	@Override
	public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
		if (p_keyPressed_1_ == GLFW_KEY_BACKSPACE) {
			if (this.query.length() > 0)
				this.query = this.query.substring(0, this.query.length() - 1);
			this.activated = false;
			return true;
		} else if (p_keyPressed_1_ == GLFW_KEY_ENTER || p_keyPressed_1_ == GLFW_KEY_KP_ENTER) {
			if (!this.query.isEmpty()) {
				this.activated = true;

			}
			this.sendQuery();

			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public boolean charTyped(char p_charTyped_1_, int p_charTyped_2_) {
		if (SharedConstants.isAllowedCharacter(p_charTyped_1_)) {
			final String s1 = SharedConstants.filterAllowedCharacters(Character.toString(p_charTyped_1_));
			if (this.query.isEmpty() && s1.equals(" "))
				return true;

			this.query += s1;
			this.activated = false;
			return true;

		} else {
			return false;
		}
	}

	private void sendQuery() {
		parent.add = 0;
		parent.guiContentUpdate(this.query);
	}
	
	public static int clamp(int num, int min, int max)
    {
        if (num < min)
        {
            return min;
        }
        else
        {
            return num > max ? max : num;
        }
    }

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		glEnable(GL_BLEND);
		glDisable(GL_TEXTURE_2D);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

		flashingTimer += 0.07;
		final float darken = (float) ((Math.sin(flashingTimer - Math.PI / 2) + 1) / 4 + 0.5);

		int color = 0;

		this.width = clamp(MC.fontRenderer.getStringWidth(this.query) + 15, 45, this.gui.width - 180);

		String text = this.query;
		int dots = MC.fontRenderer.getStringWidth("...");

		int widthString = MC.fontRenderer.getStringWidth(text);

		if (widthString >= this.gui.width - 190) 
			text = MC.fontRenderer.trimStringToWidth(text, (int) (this.gui.width - 190 - 1 - dots)) + "...";
		

		MenuScreen menu = ((GuiConfig) this.gui).menu;

		if (menu.getVariants().get(menu.index).selected == this)
			this.focused = true;
		else
			this.focused = false;

		if (this.focused)
			color = 0xffcc7100;
		else
			color = 0xffa0a0a0;

		float f3 = (float) (color >> 24 & 255) / 255.0F;
		float f = (float) (color >> 16 & 255) / 255.0F;
		float f1 = (float) (color >> 8 & 255) / 255.0F;
		float f2 = (float) (color & 255) / 255.0F;

		glColor4f(f, f1, f2, f3);

		drawRect(this.getPosX(), this.getPosY(), this.getPosX() + this.getWidth(), this.getPosY() + this.getHeight(), null, false, null, false);

		color = 0xff272727;

		f3 = (float) (color >> 24 & 255) / 255.0F;
		f = (float) (color >> 16 & 255) / 255.0F;
		f1 = (float) (color >> 8 & 255) / 255.0F;
		f2 = (float) (color & 255) / 255.0F;

		glColor4f(f, f1, f2, f3);

		drawRect(this.getPosX() - 10, this.getPosY() - 1, this.getPosX() - 8, this.getPosY() + this.getHeight() + 1, null, false, null, false);

		if (this.focused)
			color = 0xffa0a0a0;
		else
			color = 0xffcc7100;

		f3 = (float) (color >> 24 & 255) / 255.0F;
		f = (float) (color >> 16 & 255) / 255.0F;
		f1 = (float) (color >> 8 & 255) / 255.0F;
		f2 = (float) (color & 255) / 255.0F;

		glColor4f(f, f1, f2, f3);

		drawRect(this.getPosX() + 1, this.getPosY() + 1, this.getPosX() + this.getWidth() - 1, this.getPosY() + this.getHeight() - 1, null, false, null, false);

		color = 0xffffffff;

		f3 = (float) (color >> 24 & 255) / 255.0F;
		f = (float) (color >> 16 & 255) / 255.0F;
		f1 = (float) (color >> 8 & 255) / 255.0F;
		f2 = (float) (color & 255) / 255.0F;

		glColor4f(f, f1, f2, f3);

		drawRect(this.getPosX() + 2, this.getPosY() + 2, this.getPosX() + this.getWidth() - 2, this.getPosY() + this.getHeight() - 2, null, false, null, false);

		this.cursorTimer++;
		if (this.cursorTimer > 80)
			this.cursorTimer = 0;

		if (this.cursorTimer <= 40 && this.focused) {

			color = 0xffa0a0a0;

			f3 = (float) (color >> 24 & 255) / 255.0F;
			f = (float) (color >> 16 & 255) / 255.0F;
			f1 = (float) (color >> 8 & 255) / 255.0F;
			f2 = (float) (color & 255) / 255.0F;

			glColor4f(f, f1, f2, f3);

			drawRect(this.getPosX() + 5 + MC.fontRenderer.getStringWidth(text), this.getPosY() + 4, this.getPosX() + 5.5F + MC.fontRenderer.getStringWidth(text), this.getPosY() + this.getHeight() - 4, null, false, null, false);
		}

		glDisable(GL_BLEND);
		glEnable(GL_TEXTURE_2D);

		glPushMatrix();
		glEnable(GL_BLEND);
		glBlendFuncSeparate(770, 771, 1, 0);

		if (this.query.isEmpty())
			drawString("Query", (float) (this.getPosX() + 5), (float) (this.getPosY() + 5), this.focused && !this.activated ? darkenColor(0xffb8b8b8, darken).getRGB() : 0xff7a7a7a);
		else
			drawString(text, (float) (this.getPosX() + 5), (float) (this.getPosY() + 5), this.focused && !this.activated ? darkenColor(0xff7a7a7a, darken).getRGB() : 0x0);

		glDisable(GL_BLEND);
		glPopMatrix();

	}

	@Override
	public boolean mouseClicked(int mouseX, int mouseY, int button) {

		if (this.isSelected(mouseX, mouseY)) {
			MenuScreen menu = ((GuiConfig) this.gui).menu;
			menu.getVariants().get(menu.index).selected = this;
			this.grabbed = true;

			return true;
		} else 
			return false;
		
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

		}
		return super.mouseReleased(mouseX, mouseY, button);
	}
}