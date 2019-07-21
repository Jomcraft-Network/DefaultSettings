package de.pt400c.defaultsettings.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SplitterSegment extends Segment {

	public SplitterSegment(GuiScreen gui, float posX, float posY, int height) {
		super(gui, posX, posY, 2, height, false);
	}

	@Override
	public void render(float mouseX, float mouseY, float partialTicks) {
		Segment.drawRect(this.getPosX(), this.getPosY(), this.getPosX() + this.getWidth(), this.getPosY() + this.getHeight(), 0xffe0e0e0, true, null, false);
	}

}
