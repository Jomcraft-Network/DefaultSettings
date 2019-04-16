package de.pt400c.defaultsettings.gui;

import net.minecraft.client.gui.GuiScreen;

public class SplitterSegment extends Segment {

	public SplitterSegment(GuiScreen gui, float posX, float posY, int height) {
		super(gui, posX, posY, 2, height);
	}

	@Override
	public void render(float mouseX, float mouseY, float partialTicks) {
		Segment.drawRect(this.getPosX(), this.getPosY(), this.getPosX() + this.getWidth(), this.getPosY() + this.getHeight(), 0xffe0e0e0);
	}

}
