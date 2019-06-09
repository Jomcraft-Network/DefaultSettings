package de.pt400c.defaultsettings.gui;

import net.minecraft.client.gui.screen.Screen;

public class SplitterSegment extends Segment {

	public SplitterSegment(Screen gui, float posX, float posY, int height) {
		super(gui, posX, posY, 2, height, false);
	}

	@Override
	public void render(float mouseX, float mouseY, float partialTicks) {
		Segment.drawRect(this.getPosX(), this.getPosY(), this.getPosX() + this.getWidth(), this.getPosY() + this.getHeight(), 0xffe0e0e0);
	}

}
