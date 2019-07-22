package de.pt400c.defaultsettings.gui;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.pt400c.defaultsettings.GuiConfig;
import net.minecraft.client.gui.GuiScreen;

@SideOnly(Side.CLIENT)
public class QuitButtonSegment extends ButtonSegment {

	public QuitButtonSegment(GuiScreen gui, float posX, float posY, int width, int height, Function<ButtonSegment, Boolean> function, boolean popup) {
		super(gui, posX, posY, "X", function, width, height, 0, popup);
	}
	
	@Override
	public void render(float mouseX, float mouseY, float partialTicks) {
		
		float alpha = !this.isPopupSegment ? 0 : ((GuiConfig) this.gui).popupField == null ? 1 : ((GuiConfig) this.gui).popupField.getWindow().alphaRate;
		Segment.drawRect(this.getPosX(), this.getPosY(), this.getPosX() + this.getWidth(), this.getPosY() + this.getHeight(), this.isSelected(mouseX, mouseY) ? 0xffbe2e2c : 0xffd85755, true, alpha, false);
		GL11.glPushMatrix();
     	GL11.glEnable(GL11.GL_BLEND);
     	GL14.glBlendFuncSeparate(770, 771, 1, 0);
		this.drawString(this.title, (float) (posX + this.getWidth() / 2 - 2), (float) (posY + this.getHeight() / 2 - 4), calcAlpha(0xffffffff, alpha).getRGB(), false);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glPopMatrix();
		
	}
}