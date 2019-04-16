package de.pt400c.defaultsettings.gui;

import net.minecraft.client.gui.GuiScreen;

public class QuitButtonSegment extends ButtonSegment {

	public QuitButtonSegment(GuiScreen gui, float posX, float posY, Function<ButtonSegment, Boolean> function) {
		super(gui, posX, posY, "X", function, 20, 20, 0);
	}
	
	@Override
	public void render(float mouseX, float mouseY, float partialTicks) {
		Segment.drawRect(this.getPosX(), this.getPosY(), this.getPosX() + this.getWidth(), this.getPosY() + this.getHeight(), this.isSelected(mouseX, mouseY) ? 0xffbe2e2c : 0xffd85755);
		this.drawString(this.title, (float) (posX + this.getWidth() / 2 - 2), (float) (posY + this.getHeight() / 2 - 4), 0xffffffff, false);
	
	}


}
