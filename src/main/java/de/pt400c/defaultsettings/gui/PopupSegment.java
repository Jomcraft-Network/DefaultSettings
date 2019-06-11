package de.pt400c.defaultsettings.gui;

import de.pt400c.defaultsettings.GuiConfig;
import net.minecraft.client.gui.GuiScreen;

public class PopupSegment extends Segment {

	private PopupWindow window;
	
	public boolean isVisible = false;
	public float backgroundTimer = 0;
	public float windowTimer = 0;
	public boolean open;

	public PopupSegment(GuiScreen gui, float posX, float posY, float width, float height) {
		super(gui, posX, posY, width, height, true);
	}
	
	@Override
	public void render(float mouseX, float mouseY, float partialTicks) {
		if (this.isVisible) {

			if (open) {
				if (this.backgroundTimer <= (Math.PI / 3))
					this.backgroundTimer += 0.05;

				if (this.windowTimer <= (Math.PI / 3))
					this.windowTimer += 0.05;

			} else {
				if (this.backgroundTimer > 0)
					this.backgroundTimer -= 0.05;
				else {
					this.isVisible = false;
					((GuiConfig) this.gui).popupField = null;
				}

				if (this.windowTimer > 0)
					this.windowTimer -= 0.05;

			}
			float alpha = 0;

			if (open)
				alpha = (float) ((Math.sin(3 * this.backgroundTimer - (Math.PI / 2)) + 1) / 2);
			else
				alpha = (float) ((Math.sin(3 * this.backgroundTimer - (Math.PI / 2)) + 1) / 2);

			Segment.drawRect(this.posX, this.posY, this.posX + width, this.posY + height, 0xc2000000, true, alpha, true);
			this.window.render(mouseX, mouseY, partialTicks);
			this.window.hoverCheck(mouseX, mouseY);
		}
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
		if(this.isVisible)
			return this.window.mouseClicked(mouseX, mouseY, mouseButton);
		return super.mouseClicked(mouseX, mouseY, mouseButton);
	}
	
	@Override
	public boolean mouseDragged(double p_mouseDragged_1_, double p_mouseDragged_3_, int p_mouseDragged_5_) {
		if(this.isVisible)
			return this.window.mouseDragged(p_mouseDragged_1_, p_mouseDragged_3_, p_mouseDragged_5_);
		return super.mouseDragged(p_mouseDragged_1_, p_mouseDragged_3_, p_mouseDragged_5_);
	}
	
	@Override
	public boolean mouseReleased(double p_mouseReleased_1_, double p_mouseReleased_3_, int p_mouseReleased_5_) {
		if(this.isVisible)
			return this.window.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_);
		return super.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_);
	}
	
	public PopupSegment setOpening(boolean open) {
		this.open = open;
		return this;
	}

	public PopupSegment setWindow(PopupWindow segment) {
		this.window = segment;
		return this;
	}

	public PopupWindow getWindow() {
		return this.window;
	}

}
