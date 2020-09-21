package de.pt400c.defaultsettings.gui;

import javax.annotation.Nonnull;
import de.pt400c.defaultsettings.GuiConfig;
import de.pt400c.defaultsettings.FramebufferPopup;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.RenderHelper;
import de.pt400c.defaultsettings.DefaultSettings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import static de.pt400c.neptunefx.NEX.*;
import static org.lwjgl.opengl.GL11.*;
import static de.pt400c.defaultsettings.FileUtil.MC;
import static org.lwjgl.opengl.GL14.glBlendFuncSeparate;
import static org.lwjgl.opengl.GL30.*;

@OnlyIn(Dist.CLIENT)
public class PopupSegment extends Segment {

	@Nonnull
	private PopupWindow window;
	boolean isVisible = false;
	public float backgroundTimer = 0;
	public float windowTimer = 0;
	public boolean open;
	public FramebufferPopup mapFrameBuffer;
	public FramebufferPopup mapFrameBufferContents;
	private int bufferWidth, testWidth = 220;
	private int bufferHeight, testHeight = 110;
	private float distanceX = 0;
	private float distanceY = 0;
	public float popX = 0;
	public float popY = 0;
	public boolean compiled = false;
	public boolean dragging;
	private float widthBuffer;
	private float heightBuffer;

	public PopupSegment(Screen gui, float posX, float posY, float width, float height) {
		super(gui, posX, posY, width, height, true);
		
		bufferWidth = bufferWidth * (int) scaledFactor;
		bufferHeight = bufferHeight * (int) scaledFactor;
		this.popX = this.gui.width / 2 - (testWidth - 10) / 2;
		this.popY = this.gui.height / 2 - (testHeight - 10) / 2;
		this.mapFrameBufferContents = new FramebufferPopup(bufferWidth, bufferHeight);
		this.mapFrameBuffer = new FramebufferPopup(bufferWidth, bufferHeight);
	}
	
	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		
		if(resized != this.resized_mark) {
			width = this.gui.width;
			height = this.gui.height;
			bufferWidth = testWidth * (int) scaledFactor;
			bufferHeight = testHeight * (int) scaledFactor;
			this.popX = this.gui.width / 2 - (testWidth - 10) / 2;
			this.popY = this.gui.height / 2 - (testHeight - 10) / 2;
			this.mapFrameBufferContents.resize(bufferWidth, bufferHeight);
			this.mapFrameBuffer.resize(bufferWidth, bufferHeight);
			this.resized_mark = resized;
		}
		
		if (this.isVisible) {

			if (this.open) {
				if (this.backgroundTimer <= (MathUtil.PI / 3))
					this.backgroundTimer += 0.05;

				if (this.windowTimer <= (MathUtil.PI / 3))
					this.windowTimer += 0.05;

			} else {
				if (this.backgroundTimer > 0)
					this.backgroundTimer -= 0.05;
				else {
					setVisible(false);
					((GuiConfig) this.gui).popupField = null;
				}

				if (this.windowTimer > 0)
					this.windowTimer -= 0.05;

			}
			
			float alpha = (float) ((Math.sin(3 * this.backgroundTimer - (MathUtil.PI / 2)) + 1) / 2);
			glDisable(GL_ALPHA_TEST);
			drawRect(this.posX, this.posY, this.posX + width, this.posY + height, 0xc2000000, true, alpha, true);
			glEnable(GL_ALPHA_TEST);

			if (!compiled) {
				this.heightBuffer = bufferHeight / (int) scaledFactor;
				this.widthBuffer = bufferWidth / (int) scaledFactor;
				glColor4f((float) 1.0f, (float) 1.0f, (float) 1.0f, (float) 1.0f);
				glBindFramebuffer(GL_FRAMEBUFFER, this.mapFrameBuffer.msFbo);
				glViewport(0, 0, bufferWidth, bufferHeight);
				glClear(16640);
				glEnable(GL_TEXTURE_2D);
				RenderHelper.disableStandardItemLighting();
				glClear(256);
				glMatrixMode(GL_PROJECTION);
				glPushMatrix();
				glLoadIdentity();
				glOrtho(0, bufferWidth / (int) scaledFactor, bufferHeight / (int) scaledFactor, 0, 1000, 3000);
				glMatrixMode(GL_MODELVIEW);
				glPushMatrix();
				glLoadIdentity();
				glEnable(GL_BLEND);
				glTranslatef(0.0f, 0, -2000.0f);

				glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

				glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

				this.window.render(mouseX, mouseY, partialTicks);

				glBindFramebuffer(GL_READ_FRAMEBUFFER, this.mapFrameBuffer.msFbo);
				glBindFramebuffer(GL_DRAW_FRAMEBUFFER, this.mapFrameBuffer.fbo);
				glBlitFramebuffer(0, 0, bufferWidth, bufferHeight, 0, 0, bufferWidth, bufferHeight, GL_COLOR_BUFFER_BIT, GL_NEAREST);
				MC.getFramebuffer().bindFramebuffer(true);

				glClear(16640);

				glLoadIdentity();

				glColor4f((float) 1.0f, (float) 1.0f, (float) 1.0f, (float) 1.0f);
				glMatrixMode(GL_PROJECTION);
				glPopMatrix();
				glMatrixMode(GL_MODELVIEW);
				glPopMatrix();

				if(DefaultSettings.compatibilityMode)
					MC.getFramebuffer().bindFramebuffer(true);
				else
					glBindFramebuffer(GL_FRAMEBUFFER, ((GuiConfig) this.gui).framebufferMc.framebuffer);
				
				glViewport((int) 0, (int) 0, (int) MC.getFramebuffer().framebufferWidth, (int) MC.getFramebuffer().framebufferHeight);
				compiled = true;
			}
			
			int currBound = glGetInteger(GL_TEXTURE_BINDING_2D);

			glBindTexture(GL_TEXTURE_2D, this.mapFrameBuffer.texture);
			
			if (this.dragging) {
				
				final float origX = this.popX;
				final float origY = this.popY;

				this.popX = mouseX - distanceX;
				this.popY = mouseY - distanceY;

				if(!((this.popX - origX) == 0 && (this.popY - origY) == 0))
					this.window.children.forEach(segment -> segment.setPosHit(segment.posX, segment.posY, this.popX, this.popY));

			}

			glPushMatrix();
			glTranslated(this.popX, this.popY, 0);
			
			float alphaRate = ((GuiConfig) this.gui).popupField == null ? 1 : (float) ((Math.sin(3 * ((GuiConfig) this.gui).popupField.windowTimer - 3 * (MathUtil.PI / 2)) + 1) / 2);
			
			glColor4f(1, 1, 1, 1 - alphaRate);

			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP);
		    
			glEnable(GL_BLEND);
			glDisable(GL_ALPHA_TEST);
			glBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);

			glBegin(GL_QUADS);
		
			glTexCoord2f(0, 0); glVertex3d(0, heightBuffer, 0);
			glTexCoord2f(1, 0); glVertex3d(widthBuffer, heightBuffer, 0);
			glTexCoord2f(1, 1); glVertex3d(widthBuffer, 0, 0);
			glTexCoord2f(0, 1); glVertex3d(0, 0, 0);
			glEnd();
			
			glEnable(GL_ALPHA_TEST);
			glDisable(GL_BLEND);
			
			glBindTexture(GL_TEXTURE_2D, currBound);
			glPopMatrix();

			renderContents(mouseX, mouseY, partialTicks);
			float c = 58F / 255F;
			glColor4f(c, c, c, 1);
			
			this.window.hoverChecks(mouseX, mouseY);

		}
	}
	
	@Override
	public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
		return ((DefaultSettingsGUI) this.gui).menu.getVariants().get(((DefaultSettingsGUI) this.gui).menu.index).selected != null ? ((DefaultSettingsGUI) this.gui).menu.getVariants().get(((DefaultSettingsGUI) this.gui).menu.index).selected.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_) : false;
	}
	
	@Override
	public boolean charTyped(char p_charTyped_1_, int p_charTyped_2_) {
		return ((DefaultSettingsGUI) this.gui).menu.getVariants().get(((DefaultSettingsGUI) this.gui).menu.index).selected != null ? ((DefaultSettingsGUI) this.gui).menu.getVariants().get(((DefaultSettingsGUI) this.gui).menu.index).selected.charTyped(p_charTyped_1_, p_charTyped_2_) : false;
	}
	
	public boolean isSelectedUpper(int mouseX, int mouseY) {
		return (mouseX >= popX + 5 && mouseY >= popY + 15 && mouseX <= popX + (testWidth - 10) + 5 && mouseY <= popY + 29) || (mouseX >= popX + 15 && mouseY >= popY + 5 && mouseX < popX + (testWidth - 20) + 5 && mouseY < popY + 15) || (distanceBetweenPoints(popX + 15F, popY + 15F, mouseX, mouseY) <= 10) || (distanceBetweenPoints(popX + (testWidth - 10) - 5F, popY + 15F, mouseX, mouseY) <= 10);
	}
	
	public void renderContents(int mouseX, int mouseY, float partialTicks) {
		this.heightBuffer = bufferHeight / (int) scaledFactor;
		this.widthBuffer = bufferWidth / (int) scaledFactor;
		glColor4f((float) 1.0f, (float) 1.0f, (float) 1.0f, (float) 1.0f);
		glBindFramebuffer(GL_FRAMEBUFFER, this.mapFrameBufferContents.msFbo);
		glViewport(0, 0, bufferWidth, bufferHeight);
		glClear(16640);
		glEnable(GL_TEXTURE_2D);
		RenderHelper.disableStandardItemLighting();
		glClear(256);
		glMatrixMode(GL_PROJECTION);
		glPushMatrix();
		glLoadIdentity();
		glOrtho(0, bufferWidth / (int) scaledFactor, bufferHeight / (int) scaledFactor, 0, 1000, 3000);
		glMatrixMode(GL_MODELVIEW);
		glPushMatrix();
		glLoadIdentity();
		glEnable(GL_BLEND);
		glTranslatef(0.0f, 0, -2000.0f);

		glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

		glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

		this.window.renderContents(mouseX, mouseY, partialTicks);

		glBindFramebuffer(GL_READ_FRAMEBUFFER, this.mapFrameBufferContents.msFbo);
		glBindFramebuffer(GL_DRAW_FRAMEBUFFER, this.mapFrameBufferContents.fbo);
		glBlitFramebuffer(0, 0, bufferWidth, bufferHeight, 0, 0, bufferWidth, bufferHeight, GL_COLOR_BUFFER_BIT, GL_NEAREST);
		MC.getFramebuffer().bindFramebuffer(true);

		glClear(16640);

		glLoadIdentity();

		glColor4f((float) 1.0f, (float) 1.0f, (float) 1.0f, (float) 1.0f);
		glMatrixMode(GL_PROJECTION);
		glPopMatrix();
		glMatrixMode(GL_MODELVIEW);
		glPopMatrix();

		if(DefaultSettings.compatibilityMode)
			MC.getFramebuffer().bindFramebuffer(true);
		else
			glBindFramebuffer(GL_FRAMEBUFFER, ((GuiConfig) this.gui).framebufferMc.framebuffer);
		
		glViewport((int) 0, (int) 0, (int) MC.getFramebuffer().framebufferWidth, (int) MC.getFramebuffer().framebufferHeight);
		
		int currBound = glGetInteger(GL_TEXTURE_BINDING_2D);

		glBindTexture(GL_TEXTURE_2D, this.mapFrameBufferContents.texture);

		glPushMatrix();
		glTranslated(this.popX, this.popY, 0);
		
		float alphaRate = ((GuiConfig) this.gui).popupField == null ? 1 : (float) ((Math.sin(3 * ((GuiConfig) this.gui).popupField.windowTimer - 3 * (MathUtil.PI / 2)) + 1) / 2);
		
		glColor4f(1, 1, 1, 1 - alphaRate);

		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP);
	    
		glEnable(GL_BLEND);
		glDisable(GL_ALPHA_TEST);
		glBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);

		glBegin(GL_QUADS);
	
		glTexCoord2f(0, 0); glVertex3d(0, heightBuffer, 0);
		glTexCoord2f(1, 0); glVertex3d(widthBuffer, heightBuffer, 0);
		glTexCoord2f(1, 1); glVertex3d(widthBuffer, 0, 0);
		glTexCoord2f(0, 1); glVertex3d(0, 0, 0);
		glEnd();
		
		glEnable(GL_ALPHA_TEST);
		glDisable(GL_BLEND);
		
		glBindTexture(GL_TEXTURE_2D, currBound);
		glPopMatrix();

	}
	
	public boolean isSelectedLower(int mouseX, int mouseY) {
		return (mouseX >= popX + 5 && mouseY >= popY + 29 && mouseX <= popX + (testWidth - 10) + 5 && mouseY <= popY + (testHeight - 10) + 5);
	}
	
	@Override
	public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
		if(this.isVisible) {

			if(!this.isSelectedUpper(mouseX, mouseY) && !this.isSelectedLower(mouseX, mouseY))
				((GuiConfig) this.gui).popupField.setOpening(false);
			
			if (this.isSelectedUpper(mouseX, mouseY)) {
				this.dragging = true;
				this.distanceX = (mouseX - this.popX);
				this.distanceY = (mouseY - this.popY);
			}
			
			return this.window.mouseClicked(mouseX, mouseY, mouseButton);
		}
		return super.mouseClicked(mouseX, mouseY, mouseButton);
	}
	
	@Override
	public boolean mouseDragged(int p_mouseDragged_1_, int p_mouseDragged_3_, int p_mouseDragged_5_) {
		if(this.isVisible)
			return this.window.mouseDragged(p_mouseDragged_1_, p_mouseDragged_3_, p_mouseDragged_5_);
		return super.mouseDragged(p_mouseDragged_1_, p_mouseDragged_3_, p_mouseDragged_5_);
	}
	
	public void reset() {
		this.popX = this.gui.width / 2 - (testWidth - 10) / 2;
		this.popY = this.gui.height / 2 - (testHeight - 10) / 2;
		this.compiled = false;
	}
	
	public void setVisible(boolean visible) {
		this.isVisible = visible;
	}
	
	@Override
	public boolean mouseReleased(int p_mouseReleased_1_, int p_mouseReleased_3_, int p_mouseReleased_5_) {
		this.dragging = false;
		
		if(this.isVisible)
			return this.window.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_);
	
		return super.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_);
	}
	
	public PopupSegment setOpening(boolean open, int width, int height) {
		this.open = open;
		this.bufferWidth = width;
		this.testWidth = width;
		this.bufferHeight = height;
		this.testHeight = height;
		bufferWidth = testWidth * (int) scaledFactor;
		bufferHeight = testHeight * (int) scaledFactor;
		this.popX = this.gui.width / 2 - (testWidth - 10) / 2;
		this.popY = this.gui.height / 2 - (testHeight - 10) / 2;
		this.mapFrameBufferContents.resize(bufferWidth, bufferHeight);
		this.mapFrameBuffer.resize(bufferWidth, bufferHeight);
		this.compiled = false;
		window.width = width - 10;
		window.height = height - 10;
		return this;
	}
	
	public PopupSegment setOpening(boolean open) {
		this.open = open;
		if(open) {
			this.bufferWidth = 220;
			this.testWidth = 220;
			this.bufferHeight = 110;
			this.testHeight = 110;
			bufferWidth = testWidth * (int) scaledFactor;
			bufferHeight = testHeight * (int) scaledFactor;
			this.popX = this.gui.width / 2 - (testWidth - 10) / 2;
			this.popY = this.gui.height / 2 - (testHeight - 10) / 2;
			this.mapFrameBufferContents.resize(bufferWidth, bufferHeight);
			this.mapFrameBuffer.resize(bufferWidth, bufferHeight);
			this.compiled = false;
			window.width = 220 - 10;
			window.height = 110 - 10;
		}
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