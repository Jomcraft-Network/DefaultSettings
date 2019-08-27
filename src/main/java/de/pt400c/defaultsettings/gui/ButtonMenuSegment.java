package de.pt400c.defaultsettings.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import static de.pt400c.defaultsettings.FileUtil.MC;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import de.pt400c.defaultsettings.GuiConfig;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL14.glBlendFuncSeparate;
import static de.pt400c.neptunefx.NEX.*;
import static de.pt400c.neptunefx.DrawString.*;

@SideOnly(Side.CLIENT)
public class ButtonMenuSegment extends ButtonSegment {
	
	public final int id;
	private boolean activated;
	private float offsetX = 0;
	private float offsetTick = 0;
	private final LeftMenu menu;
	private final float origLength;
	private final IconSegment icon;

	public ButtonMenuSegment(int id, GuiScreen gui, float posX, float posY, String title, Function<ButtonSegment, Boolean> function, LeftMenu menu, String icon) {
		super(gui, posX, posY, title, function, 50, 20, 2);
		this.id = id;
		this.menu = menu;
		this.origLength = 50;
		this.icon = new IconSegment(gui, posX + 27, posY + 27 - 2, 16, 16, icon, this.menu);
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		final float triple = (float) Math.sin(0.25 * offsetTick);
		final float func = triple * triple * triple * 6;
		this.width = this.origLength - this.menu.offs * 1.6F;

		final float percent = GuiConfig.clamp(menu.offsetTick / menu.maxOffTick, 0, 1);
		
		if(!(width < 3.5F)) {
		
			if((this.isSelected(mouseX, mouseY) || this.activated) && offsetTick < (2 * Math.PI))
				offsetTick += 0.4;
	
			else if(offsetTick > 0 && !(this.isSelected(mouseX, mouseY) || this.activated))
				offsetTick -= 0.5;
			
			this.offsetX = func;
			glPushMatrix();
			glEnable(GL_BLEND);
			glBlendFuncSeparate(770, 771, 1, 0);
			drawButton(this.getPosX() + this.offsetX, this.getPosY(), this.getPosX() + this.offsetX + this.getWidth(), this.getPosY() + this.getHeight(), calcAlpha(this.getRenderColor((byte) (this.activated ? 2 : this.isSelected(mouseX, mouseY) ? 1 : 0)), percent).getRGB(), calcAlpha(0xffdcdcdc, percent).getRGB(), this.border);
			glDisable(GL_BLEND);
			glEnable(GL_SCISSOR_TEST);
			glEnable(GL_BLEND);
			glBlendFuncSeparate(770, 771, 1, 0);
			ScaledResolution scaledResolution = new ScaledResolution(MC.gameSettings, MC.displayWidth, MC.displayHeight);
			int scaleFactor = scaledResolution.getScaleFactor();
			glScissor((int) ((this.getPosX() + 2 + this.offsetX) * scaleFactor), (int) ((scaledResolution.getScaledHeight() - this.getPosY() - this.getHeight()) * scaleFactor), (int) ((this.getWidth() - 4) * scaleFactor), (int) (this.getHeight() * scaleFactor));
			drawString(this.title, (float) (posX + this.offsetX + 3), (float) (posY + this.getHeight() / 2 - 4), calcAlpha(0xff3a3a3a, percent).getRGB(), false);
			glDisable(GL_BLEND);
			glDisable(GL_SCISSOR_TEST);
	
			glPopMatrix();
		}
		
		final int plus = this.activated ? 9 : 0;
		glColor4f(1, 1, 1, 1);
		if(this.activated) 
			drawRect(posX + 29 + (-25) * percent, posY, posX + 29 + 3 + (-25) * percent, posY + 19, calcAlpha(0xffff8518, 1 - percent).getRGB(), true, null, false);
		
		glColor4f(1, 1, 1, percent);
		this.icon.customRender(mouseX, mouseY, (-25 + plus) * percent, 0, partialTicks);
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
				this.setActive(this.activated ^ true, false);
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
		return ((GuiConfig) this.gui).popupField == null && mouseX >= (this.getPosX()) && mouseY >= this.getPosY() && mouseX < (this.getPosX() + this.offsetX) + this.getWidth() && mouseY < this.getPosY() + this.getHeight();
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
