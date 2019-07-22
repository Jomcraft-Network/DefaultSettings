package de.pt400c.defaultsettings.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.pt400c.defaultsettings.GuiConfig;
import net.minecraft.client.gui.GuiScreen;

@SideOnly(Side.CLIENT)
public class ButtonMovableSegment extends ButtonSegment {
	
	private boolean dragging;
	private double distanceX = 0;
	private double distanceY = 0;

	public ButtonMovableSegment(GuiScreen gui, float posX, float posY, Function<ButtonSegment, Boolean> function, boolean popupSegment) {
		super(gui, posX, posY, "dummy", function, 50, 20, 2, popupSegment);
	}

	@Override
	public void render(float mouseX, float mouseY, float partialTicks) {
		Segment.drawButton(this.getPosX(), this.getPosY(), this.getPosX() + this.getWidth(), this.getPosY() + this.getHeight(), this.isSelected(mouseX, mouseY) ? 0xff7a7a7a : 0xffa4a4a4, 0xffdcdcdc, this.border);
		if (this.dragging) {
			this.posX = mouseX - distanceX;
			this.posY = mouseY - distanceY;
		}
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {

		if (this.isSelected(mouseX, mouseY)) {
			this.dragging = true;
			MenuScreen menu = ((GuiConfig) this.gui).menu;
			menu.getVariants().get(menu.index).selected = null;

			distanceX = (mouseX - this.posX);
			distanceY = (mouseY - this.posY);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		if (this.dragging) {
			this.clickSound();
		}
		this.dragging = false;
		return false;
	}

}
