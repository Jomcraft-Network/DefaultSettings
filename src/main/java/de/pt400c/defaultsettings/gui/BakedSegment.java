package de.pt400c.defaultsettings.gui;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;
import com.mojang.blaze3d.platform.GlStateManager;
import de.pt400c.defaultsettings.FramebufferPopup;
import de.pt400c.defaultsettings.GuiConfig;
import static de.pt400c.defaultsettings.FileUtil.MC;
import de.pt400c.defaultsettings.DefaultSettings;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class BakedSegment extends Segment {
	
	protected boolean updateForced = false;
	public boolean compiled = false;
	protected FramebufferPopup mapFrameBuffer;
	protected int bufferWidth;
	protected int bufferHeight;
	protected int id;
	protected int heightBuffer;
	protected int widthBuffer;
	protected final boolean stat;
	private final int red;
	private final int green;
	private final int blue;
	
	public BakedSegment(Screen gui, int id, float posX, float posY, float width, float height, int red, int green, int blue, boolean stat, boolean popupSegment) {
		super(gui, posX, posY, width, height, popupSegment);
		bufferWidth = (int) (this.width * scaledFactor);
		bufferHeight = (int) (this.height * scaledFactor);
		this.mapFrameBuffer = new FramebufferPopup(bufferWidth, bufferHeight);
		this.stat = stat;
		this.red = red;
		this.green = green;
		this.blue = blue;
	}
	
	public void setup() {
		if(updateForced) {
			this.compiled = false;
			this.updateForced = false;
		}

		if(resized != this.resized_mark) {	
			bufferWidth = (int) (this.width * scaledFactor);
			bufferHeight = (int) (this.height * scaledFactor);
			this.mapFrameBuffer.resize(bufferWidth, bufferHeight);

			this.resized_mark = resized;
			this.compiled = false;
		}
	}
	
	public void preRender() {
		this.heightBuffer = bufferHeight / (int) scaledFactor;
		this.widthBuffer = bufferWidth / (int) scaledFactor;
		GlStateManager.color4f(red / 255F, green / 255F, blue / 255F, (float) 1.0f);
		glClearColor(red / 255F, green / 255F, blue / 255F, 0);
		GlStateManager.bindFramebuffer(GL_FRAMEBUFFER, this.mapFrameBuffer.msFbo);
		glViewport(0, 0, bufferWidth, bufferHeight);
		GlStateManager.clear(16640, false);
		GlStateManager.enableTexture();
		RenderHelper.disableStandardItemLighting();
		glMatrixMode(GL_PROJECTION);
		glPushMatrix();
		glLoadIdentity();
		glOrtho(0, bufferWidth / (int) scaledFactor, bufferHeight / (int) scaledFactor, 0, 1000, 3000);
		glMatrixMode(GL_MODELVIEW);
		glPushMatrix();
		glLoadIdentity();
		GlStateManager.enableBlend();

		glTranslatef(0.0f, 0, -2000.0f);
	}
	
	public void postRender(float alpha, boolean popup) {
		GlStateManager.bindFramebuffer(GL_READ_FRAMEBUFFER, this.mapFrameBuffer.msFbo);
		GlStateManager.bindFramebuffer(GL_DRAW_FRAMEBUFFER, this.mapFrameBuffer.fbo);
		GlStateManager.clear(16640, false);
		glLoadIdentity();
		glMatrixMode(GL_PROJECTION);
		glPopMatrix();
		glMatrixMode(GL_MODELVIEW);
		glPopMatrix();
		
		glBlitFramebuffer(0, 0, bufferWidth, bufferHeight, 0, 0, bufferWidth, bufferHeight, GL_COLOR_BUFFER_BIT, GL_NEAREST);
		
		if(popup && ((GuiConfig) this.gui).popupField != null) {
			GlStateManager.bindFramebuffer(GL_FRAMEBUFFER, ((GuiConfig) this.gui).popupField.mapFrameBufferContents.msFbo);	
			GlStateManager.viewport((int) 0, (int) 0, (int) ((GuiConfig) this.gui).popupField.mapFrameBufferContents.width, (int) ((GuiConfig) this.gui).popupField.mapFrameBufferContents.height);
		}else if(this.gui instanceof GuiConfig){
			if(DefaultSettings.compatibilityMode) {
				if(DefaultSettings.antiAlias)
					GlStateManager.bindFramebuffer(GL_FRAMEBUFFER, 0);
				else
					MC.getFramebuffer().bindFramebuffer(true);
			} else
				GlStateManager.bindFramebuffer(GL_FRAMEBUFFER, ((GuiConfig) this.gui).framebufferMc.framebuffer);
			
			GlStateManager.viewport((int) 0, (int) 0, (int) MC.getFramebuffer().framebufferWidth, (int) MC.getFramebuffer().framebufferHeight);
		}else {
			if(DefaultSettings.antiAlias)
				GlStateManager.bindFramebuffer(GL_FRAMEBUFFER, 0);
			else
				MC.getFramebuffer().bindFramebuffer(true);
			GlStateManager.viewport((int) 0, (int) 0, (int) MC.getFramebuffer().framebufferWidth, (int) MC.getFramebuffer().framebufferHeight);
		}
	
		compiled = true;
	}
	
	@SuppressWarnings("unused")
	public void drawTexture(float alpha) {
		int currBound = glGetInteger(GL_TEXTURE_BINDING_2D);

		GlStateManager.bindTexture(this.mapFrameBuffer.texture);

		glPushMatrix();

		glTranslated(this.posX, this.posY, 0);
		
		GlStateManager.color4f(1, 1, 1, alpha);

		GlStateManager.texParameter(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		GlStateManager.texParameter(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		GlStateManager.texParameter(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP);
		GlStateManager.texParameter(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP);
	    
		GlStateManager.enableBlend();

		GlStateManager.disableAlphaTest();
		GlStateManager.blendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);

		glBegin(GL_QUADS);
	
		glTexCoord2f(0, 0); glVertex3d(0, heightBuffer, 0);
		glTexCoord2f(1, 0); glVertex3d(widthBuffer, heightBuffer, 0);
		glTexCoord2f(1, 1); glVertex3d(widthBuffer, 0, 0);
		glTexCoord2f(0, 1); glVertex3d(0, 0, 0);
		glEnd();
		
		GlStateManager.enableAlphaTest();
		GlStateManager.disableBlend();
		
		GlStateManager.bindTexture(currBound);
		glPopMatrix();
		glClearColor(0, 0, 0, 0);
			
	}
}