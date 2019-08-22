package de.pt400c.defaultsettings.gui;

import static de.pt400c.defaultsettings.FileUtil.MC;
import javax.annotation.Nonnull;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import de.pt400c.defaultsettings.GuiConfig;
import de.pt400c.defaultsettings.FramebufferPopup;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL14.glBlendFuncSeparate;

@SideOnly(Side.CLIENT)
public class PopupSegment extends Segment {

	@Nonnull
	private PopupWindow window;
	public boolean isVisible = false;
	public float backgroundTimer = 0;
	public float windowTimer = 0;
	public boolean open;
	public FramebufferPopup framebufferMc;

	public PopupSegment(GuiScreen gui, float posX, float posY, float width, float height) {
		super(gui, posX, posY, width, height, true);
		this.framebufferMc = new FramebufferPopup(MC.displayWidth, MC.displayHeight);
	}
	
	@Override
	public void render(float mouseX, float mouseY, float partialTicks) {
		if (this.isVisible) {

			if (this.open) {
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

			if (this.open)
				alpha = (float) ((Math.sin(3 * this.backgroundTimer - (Math.PI / 2)) + 1) / 2);
			else
				alpha = (float) ((Math.sin(3 * this.backgroundTimer - (Math.PI / 2)) + 1) / 2);
			glDisable(GL11.GL_ALPHA_TEST);
			Segment.drawRect(this.posX, this.posY, this.posX + width, this.posY + height, 0xc2000000, true, alpha, true);
			glEnable(GL11.GL_ALPHA_TEST);
			((GuiConfig) this.gui).framebufferMc.unbindFramebuffer();

			glClear(16640);

			glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebufferMc.msFbo);

			glClear(GL11.GL_COLOR_BUFFER_BIT);

			glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
			
			this.window.render(mouseX, mouseY, partialTicks);
			
			glDisable(GL11.GL_ALPHA_TEST);
			glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, framebufferMc.msFbo);
			glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, framebufferMc.fbo);
			glBlitFramebuffer(0, 0, MC.displayWidth, MC.displayHeight, 0, 0, MC.displayWidth, MC.displayHeight, GL11.GL_COLOR_BUFFER_BIT, GL11.GL_NEAREST);
			glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);

			((GuiConfig) this.gui).framebufferMc.bindFramebuffer(true);

			glEnable(GL11.GL_TEXTURE_2D);

			glEnable(GL11.GL_BLEND);

			glBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
			glBindTexture(GL11.GL_TEXTURE_2D, framebufferMc.texture);

			glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
			glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
			glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
			glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
			
			float alphaRate = ((GuiConfig) this.gui).popupField == null ? 1 : (float) ((Math.sin(3 * ((GuiConfig) this.gui).popupField.windowTimer - 3 * (Math.PI / 2)) + 1) / 2);

			glColor4f(1, 1, 1, 1 - alphaRate);
			glBegin(GL11.GL_QUADS);
			glTexCoord2f(0, 0); glVertex3d(0, height, 0);
			glTexCoord2f(1, 0); glVertex3d(width, height, 0);
			glTexCoord2f(1, 1); glVertex3d(width, 0, 0);
			glTexCoord2f(0, 1); glVertex3d(0, 0, 0);
			glEnd();

			glBindTexture(GL11.GL_TEXTURE_2D, 0);
			glEnable(GL11.GL_ALPHA_TEST);
			glDisable(GL11.GL_BLEND);

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