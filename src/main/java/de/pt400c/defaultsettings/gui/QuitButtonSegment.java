package de.pt400c.defaultsettings.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiScreen;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL14.glBlendFuncSeparate;
import static de.pt400c.neptunefx.NEX.*;
import static de.pt400c.neptunefx.DrawString.*;

@SideOnly(Side.CLIENT)
public class QuitButtonSegment extends ButtonSegment {

	public QuitButtonSegment(GuiScreen gui, float posX, float posY, int width, int height, Function<ButtonSegment, Boolean> function, boolean popup) {
		super(gui, posX, posY, "X", function, width, height, 0, popup);
	}
	
	@Override
	public void render(float mouseX, float mouseY, float partialTicks) {
		drawRect(this.getPosX(), this.getPosY(), this.getPosX() + this.getWidth(), this.getPosY() + this.getHeight(), this.isSelected(mouseX, mouseY) ? 0xffbe2e2c : 0xffd85755, true, null, false);
		glPushMatrix();
     	glEnable(GL_BLEND);
     	glBlendFuncSeparate(770, 771, 1, 0);
		drawString(this.title, (float) (posX + this.getWidth() / 2 - 2), (float) (posY + this.getHeight() / 2 - 4), 0xffffffff, false);
		glDisable(GL_BLEND);
		glPopMatrix();
	}
}