package net.jomcraft.defaultsettings.gui;

import net.jomcraft.neptunefx.NEX;
import net.jomcraft.neptunefx.gui.MathUtil.Vec2f;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import static org.lwjgl.opengl.GL14.glBlendFuncSeparate;
import static org.lwjgl.opengl.GL11.*;

@OnlyIn(Dist.CLIENT)
public class ScrollbarSegment extends ButtonSegment {
	
	protected boolean grabbed;
	
	private final ScrollableSegment superScrollable;
	
	private double distanceY = 0;

	public ScrollbarSegment(GuiScreen gui, float posX, float posY, int width, int height, ScrollableSegment segment) {
		super(gui, posX, posY, null, null, width, height, 0);
		this.superScrollable = segment;
	}
	
	@Override
	public boolean isSelected(int mouseX, int mouseY) {
		return mouseX >= this.getPosX() && mouseY >= this.getPosY() + 3 && mouseX < this.getPosX() + this.getWidth() && mouseY < this.getPosY() + this.getHeight() - 3;
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {

		if (this.grabbed) {
			final int factor = (int) (mouseY - distanceY);

			this.posY = factor;

			if (this.posY < this.superScrollable.posY)
				this.posY = this.superScrollable.posY;

			if (this.posY > this.superScrollable.posY + this.superScrollable.height - height)
				this.posY = this.superScrollable.posY + this.superScrollable.height - height;

			final float distance = Math.round(this.superScrollable.height - height);
			final float pos = Math.round(this.posY - this.superScrollable.posY);

			final float tempAdd = (int) (this.superScrollable.getPosY() + this.superScrollable.height - 1 - ScrollableSegment.row * (this.superScrollable.list.size() - 1) - this.superScrollable.getPosY() - ScrollableSegment.row - 0.5F);

			this.superScrollable.add = (int) (tempAdd * (pos / distance));

		} else {
			distanceY = 0;
		}

		glDisable(GL_TEXTURE_2D);
		glEnable(GL_BLEND);
		glBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);
		
		NEX.drawRectRoundedCorners(this.getPosX(), this.getPosY() + 3, this.getPosX() + this.getWidth(), this.getPosY() + this.getHeight() - 3, 0xffe0e0e0, 800);
		NEX.drawLine2D_2(0.2F, 0.2F, 0.2F, 1, (int) scaledFactor, 2, new Vec2f(this.getPosX() + this.width / 2 - 2F + 0.5F, this.getPosY() + this.height / 2 - 3), new Vec2f(this.getPosX() + this.width / 2 + 2F - 0.5F, this.getPosY() + this.height / 2 - 3));
		
		NEX.drawLine2D_2(0.2F, 0.2F, 0.2F, 1, (int) scaledFactor, 2, new Vec2f(this.getPosX() + this.width / 2 - 2F + 0.5F, this.getPosY() + this.height / 2), new Vec2f(this.getPosX() + this.width / 2 + 2F - 0.5F, this.getPosY() + this.height / 2));
		
		NEX.drawLine2D_2(0.2F, 0.2F, 0.2F, 1, (int) scaledFactor, 2, new Vec2f(this.getPosX() + this.width / 2 - 2F + 0.5F, this.getPosY() + this.height / 2 + 3), new Vec2f(this.getPosX() + this.width / 2 + 2F - 0.5F, this.getPosY() + this.height / 2 + 3));
		glDisable(GL_BLEND);
		glEnable(GL_TEXTURE_2D);
	
	}
	
	@Override
	public boolean mouseClicked(int mouseX, int mouseY, int button) {

		if (this.isSelected(mouseX, mouseY)) {
			this.grabbed = true;
			((DefaultSettingsGUI) this.gui).resetSelected();
			this.distanceY += (int) (mouseY - this.posY);
			
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean mouseDragged(int mouseX, int mouseY, int button) {
		return super.mouseDragged(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseReleased(int mouseX, int mouseY, int button) {
		if (this.grabbed) 
				this.grabbed = false;
				
		return super.mouseReleased(mouseX, mouseY, button);
	}
}