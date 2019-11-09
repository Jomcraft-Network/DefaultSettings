package de.pt400c.defaultsettings.gui;

import java.util.function.Function;

import net.minecraft.client.gui.screen.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import static de.pt400c.neptunefx.NEX.*;
import static de.pt400c.defaultsettings.FileUtil.MC;
import de.pt400c.defaultsettings.GuiConfig;
import static de.pt400c.defaultsettings.DefaultSettings.fontRenderer;
import static org.lwjgl.opengl.GL14.glBlendFuncSeparate;
import static org.lwjgl.opengl.GL11.*;

@OnlyIn(Dist.CLIENT)
public class ButtonMenuSegment extends ButtonSegment {
	
	public final int id;
	public boolean activated;
	private float offsetX = 0;
	private float offsetTick = 0;
	private float timer;
	private final LeftMenu menu;
	private final float origLength;
	private final IconSegment icon;

	public ButtonMenuSegment(int id, Screen gui, float posX, float posY, String title, Function<ButtonSegment, Boolean> function, LeftMenu menu, String icon) {
		super(gui, posX, posY, title, function, 50, 20, 2);
		this.id = id;
		this.menu = menu;
		this.origLength = 50;
		this.icon = new IconSegment(gui, posX + 27, posY + 27 - 2, 13, 13, icon, this.menu);
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		final float triple = (float) Math.sin(0.25 * offsetTick);
		final float func = triple * triple * triple * 6;
		this.width = this.origLength - this.menu.offs * 1.6F;

		final float percent = MathUtil.clamp(menu.offsetTick / menu.maxOffTick, 0, 1);

		if (this.activated) {
			
			if (this.timer <= (Math.PI / 3)) 
				this.timer += 0.05;

		} else {

			if (this.timer > 0) 
				this.timer -= 0.05;
		}
		
		float alpha = (float) ((Math.sin(3 * this.timer - 3 * (Math.PI / 2)) + 1) / 2);
		
		if(!(width < 3.5F)) {
		
			if((this.isSelected(mouseX, mouseY) || this.activated) && offsetTick < (2 * Math.PI))
				offsetTick += 0.4;
	
			else if(offsetTick > 0 && !(this.isSelected(mouseX, mouseY) || this.activated))
				offsetTick -= 0.5;
			
			this.offsetX = func;
			glPushMatrix();
			glEnable(GL_SCISSOR_TEST);
			glEnable(GL_BLEND);
			glBlendFuncSeparate(770, 771, 1, 0);

			final int scaleFactor = (int) scaledFactor;
			glScissor((int) ((this.getPosX() + 2 + this.offsetX) * scaleFactor), (int) ((MC.mainWindow.getScaledWidth() - this.getPosY() - this.getHeight()) * scaleFactor), (int) ((this.getWidth() - 4) * scaleFactor), (int) (this.getHeight() * scaleFactor));

			glDisable(GL_BLEND);
			glDisable(GL_SCISSOR_TEST);
	
			glPopMatrix();
		}

		int color = 0xffe6e6e6;
		
		if(this.activated)
			color = 0xffff8518;
		
		float f = (float) (color >> 16 & 255) / 255.0F;
		float f1 = (float) (color >> 8 & 255) / 255.0F;
		float f2 = (float) (color & 255) / 255.0F;

		float stringWidth = fontRenderer.getStringWidth(this.title, 0.9F, true);
		
		float width = (17F + stringWidth) / 2;
		
		float posi = 74 / 2 - width;

		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glDisable(GL_ALPHA_TEST);
		glDisable(GL_TEXTURE_2D);
		
		int outer = calcAlpha(0xffff8518, percent * 1.7F).getRGB();
		
		int inner = calcAlpha(0xff505050, percent * 1.7F).getRGB();
		
		int red = getRed(outer);
		
		int green = getGreen(outer);
			
		int blue = getBlue(outer);
		
		int alphaTest = MathUtil.clamp((int) ((getAlpha(outer)) - 255 * (alpha)), 0, 255);
		
		outer = ((alphaTest & 0x0ff) << 24) | ((red & 0x0ff) << 16) | ((green & 0x0ff) << 8) | (blue & 0x0ff);
		
		red = getRed(inner);
		
		green = getGreen(inner);
			
		blue = getBlue(inner);

		alphaTest = MathUtil.clamp((int) ((getAlpha(inner)) - 255 * (alpha)), 0, 255);
		
		inner = ((alphaTest & 0x0ff) << 24) | ((red & 0x0ff) << 16) | ((green & 0x0ff) << 8) | (blue & 0x0ff);

		drawRectRoundedCorners(posi - 5 + 10 * percent, posY + this.getHeight() / 2 - 14, posi + width * 2 + 3.5F - 20 * percent, posY + this.getHeight() / 2 + 7, outer, 800);
		
		drawRectRoundedCorners(posi - 5 + 1F + 10 * percent, posY + this.getHeight() / 2 - 14 + 1F, posi + width * 2 + 3.5F - 1F - 20 * percent, posY + this.getHeight() / 2 + 7 - 1F, inner, 800);
		
		if(this.activated)
			drawRectRoundedCorners(-10, posY + this.getHeight() / 2 - 14, 40 * (percent), posY + this.getHeight() / 2 + 7, 0xa9505050, 800);

		glDisable(GL_BLEND);
		glEnable(GL_ALPHA_TEST);

		glEnable(GL_TEXTURE_2D);
		glColor4f(f, f1, f2, 1);
		
		float animStuff = posi - 36 + 23;
		
		this.icon.customRender(mouseX, mouseY, posi - 36 - animStuff * percent, 0, partialTicks);
	
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

		glEnable(GL_TEXTURE_2D);
		
		glEnable(GL_SCISSOR_TEST);

		glScissor(0, (int) ((gui.height - (posY + this.getHeight() / 2 - 14 + 21)) * (int) scaledFactor), (int)(74 * (1 - percent) * (int) scaledFactor), 21 * (int) scaledFactor);
		
		fontRenderer.drawString(this.title, (float) (posi + 17), (float) (posY + this.getHeight() / 2 - 8) + 1.2F * percent, this.activated ? 0xffff8518 : 0xffe6e6e6, 0.9F - 0.3F * percent, true);
		glDisable(GL_SCISSOR_TEST);

		glDisable(GL_BLEND);
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
			if (this.isSelected(mouseX, mouseY)) {
				this.grabbed = false;

			if (this.function.apply(this)) {
				((GuiConfig) this.gui).headerPart.compiled = false;
				this.setActive(Boolean.logicalXor(this.activated, true), false);
				this.clickSound();
			}
			return true;
			}
		}
		return false;
	}
	
	public ButtonMenuSegment setActive(boolean active, boolean silent) {
		if(!silent && ((GuiConfig) this.gui).selectedSegment == this)
			return this;
		
		this.activated = active;
		((GuiConfig) this.gui).changeSelected(this);
		return this;
	}

	@Override
	public boolean isSelected(int mouseX, int mouseY) {
		return ((GuiConfig) this.gui).popupField == null && mouseX >= this.getPosX() + this.hitX && mouseY >= this.getPosY() + this.hitY && mouseX < (this.getPosX() + this.hitX + this.offsetX) + this.getWidth() && mouseY < this.getPosY() + this.hitY + this.getHeight();
	}
	
	protected int getRenderColor(byte state) {
		switch (state) {
		
		// 1 = hovered, 2 = activated
		case 1: 
			return 0xff7a7a7a;
		case 2: 
			return 0xff5d5d5d;
		default: 
			return 0xffa4a4a4;
		}
	}
}