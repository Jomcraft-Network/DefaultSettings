package de.pt400c.defaultsettings.gui;

import java.util.function.Function;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.math.Vec2f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import static de.pt400c.neptunefx.NEX.*;
import static de.pt400c.defaultsettings.FileUtil.MC;
import static org.lwjgl.opengl.GL11.*;

@OnlyIn(Dist.CLIENT)
public class QuitButtonSegment extends ButtonSegment {

	private final float offs;
	
	public QuitButtonSegment(GuiScreen gui, float posX, float posY, int width, int height, Function<ButtonSegment, Boolean> function, float offs, boolean popup) {
		super(gui, posX, posY, null, function, width, height, 0, popup);
		this.offs = offs;
	}
	
	@Override
	public void render(float mouseX, float mouseY, float partialTicks) {
		drawRect(this.getPosX(), this.getPosY(), this.getPosX() + this.getWidth(), this.getPosY() + this.getHeight(), this.isSelected(mouseX, mouseY) ? 0xffbe2e2c : 0xffd85755, true, null, false);
		glPushMatrix();
		glEnable(GL_BLEND);
		glDisable(GL_TEXTURE_2D);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		final int scaleFactor = (int) MC.mainWindow.getGuiScaleFactor();
     	drawLine2D_2(1, 1, 1, 1, scaleFactor, this.isPopupSegment ? 3.0F : 5.0F, new Vec2f((float) posX + width / 2 - this.offs, (float) posY + height / 2 - this.offs), new Vec2f((float) posX + width / 2 + this.offs, (float) posY + height / 2 + this.offs));
     	drawLine2D_2(1, 1, 1, 1, scaleFactor, this.isPopupSegment ? 3.0F : 5.0F, new Vec2f((float) posX + width / 2 + this.offs, (float) posY + height / 2 - this.offs), new Vec2f((float) posX + width / 2 - this.offs, (float) posY + height / 2 + this.offs));
		glDisable(GL_BLEND);
		glEnable(GL_TEXTURE_2D);
		glPopMatrix();
	}
}