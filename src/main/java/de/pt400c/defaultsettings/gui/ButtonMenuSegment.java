package de.pt400c.defaultsettings.gui;

import java.util.function.Function;
import static de.pt400c.defaultsettings.FileUtil.MC;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import de.pt400c.defaultsettings.GuiConfig;

@SideOnly(Side.CLIENT)
public class ButtonMenuSegment extends ButtonSegment {
	
	public final int id;
	private boolean activated;
	private double offsetX = 0;
	private float offsetTick = 0;

	public ButtonMenuSegment(int id, GuiScreen gui, float posX, float posY, String title, Function<ButtonSegment, Boolean> function) {
		super(gui, posX, posY, title, function, 50, 20, 2);
		this.id = id;
	}

	@Override
	public void render(float mouseX, float mouseY, float partialTicks) {
		final double triple = Math.sin(0.25 * offsetTick);
		final double func = triple * triple * triple * 6;

		if((this.isSelected(mouseX, mouseY) || this.activated) && offsetTick < (2 * Math.PI))
			offsetTick += 0.4;
	
		else if(offsetTick > 0 && !(this.isSelected(mouseX, mouseY) || this.activated)){
			offsetTick -= 0.5;
		}
		offsetX = func;
		
		Segment.drawButton(this.getPosX() + this.offsetX, this.getPosY(), this.getPosX() + this.offsetX + this.getWidth(), this.getPosY() + this.getHeight(), this.getRenderColor((byte) (this.activated ? 2 : this.isSelected(mouseX, mouseY) ? 1 : 0)), 0xffdcdcdc, this.border);
		MC.fontRenderer.drawString(this.title, (float) (posX + this.offsetX + 3), (float) (posY + this.getHeight() / 2 - 4), 0xff3a3a3a, false);

	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {

		if (this.isSelected(mouseX, mouseY)) {
			this.grabbed = true;
			MenuScreen menu = ((GuiConfig) this.gui).menu;
			menu.getVariants().get(menu.index).selected = null;
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
				this.setActive(Boolean.logicalXor(this.activated, true), false);
				this.clickSound();
			}

		}
		return super.mouseReleased(mouseX, mouseY, button);
	}
	
	public ButtonMenuSegment setActive(boolean active, boolean silent) {
		if(!silent && ((GuiConfig) this.gui).selectedSegment == this)
			return this;
		
		this.activated = active;
		((GuiConfig) this.gui).changeSelected(this);
		return this;
	}

	@Override
	public boolean isSelected(double mouseX, double mouseY) {
		return ((GuiConfig) this.gui).popupField == null && mouseX >= (this.getPosX() + this.offsetX) && mouseY >= this.getPosY() && mouseX < (this.getPosX() + this.offsetX) + this.getWidth() && mouseY < this.getPosY() + this.getHeight();
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
