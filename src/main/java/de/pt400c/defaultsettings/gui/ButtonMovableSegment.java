package de.pt400c.defaultsettings.gui;

import java.util.function.Function;
import net.minecraft.client.gui.GuiScreen;

public class ButtonMovableSegment extends ButtonSegment {
	
	private boolean dragging;
	private double distanceX = 0;
	private double distanceY = 0;

	public ButtonMovableSegment(GuiScreen gui, float posX, float posY, Function<ButtonSegment, Boolean> function) {
		super(gui, posX, posY, "dummy", function, 50, 20, 2);
	}

	@Override
	public void render(float mouseX, float mouseY, float partialTicks) {
		Segment.drawButton(this.getPosX(), this.getPosY(), this.getPosX() + this.getWidth(), this.getPosY() + this.getHeight(), this.isSelected(mouseX, mouseY) ? 0xff7a7a7a : 0xffa4a4a4, 0xffdcdcdc, this.border);
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {

		if (this.isSelected(mouseX, mouseY)) {
			this.dragging = true;
			distanceX = (mouseX - this.posX);
			distanceY = (mouseY - this.posY);
			return true;
		} else {
			return false;
		}
	}
	
	@Override
    public boolean mouseDragged(double mouseX, double mouseY, int button) {
        if (this.dragging) {
        	
        	this.posX = mouseX - distanceX;
        	this.posY = mouseY - distanceY;
        }
        return this.dragging;
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
