package de.pt400c.defaultsettings;

import static de.pt400c.defaultsettings.FileUtil.MC;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.IntBuffer;

import org.apache.logging.log4j.Level;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FramebufferDefault {
    public int framebufferWidth;
    public int framebufferHeight;
    public int framebuffer;
    public int interFramebuffer;
    public int colorBuffer;
    public int multisampledTexture;
    public int screenTexture;

    public FramebufferDefault(int width, int height) {
        this.interFramebuffer = -1;
        this.framebuffer = -1;
        this.createBindFramebuffer(width, height);
    }

	public void createBindFramebuffer(int width, int height) {
		if (this.framebuffer >= 0) 
			this.deleteFramebuffer();

		this.createFramebuffer(width, height);
		DefaultSettings.antiAlias = false;
		try {
			Method method = GLX.class.getMethod("isUsingFBOs");
			Boolean returnValue = (Boolean) method.invoke(null);
			DefaultSettings.antiAlias = !returnValue;
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			DefaultSettings.log.log(Level.INFO, "Optifine is not present, disable AA");
		}

		if(DefaultSettings.antiAlias)
			GlStateManager.bindFramebuffer(GL_FRAMEBUFFER, 0);
		else
			MC.getFramebuffer().bindFramebuffer(true);
	}

	public void deleteFramebuffer() {
		if(DefaultSettings.antiAlias)
			GlStateManager.bindFramebuffer(GL_FRAMEBUFFER, 0);
		else
			MC.getFramebuffer().bindFramebuffer(true);
		GlStateManager.deleteFramebuffers(this.framebuffer);
		GlStateManager.deleteFramebuffers(this.interFramebuffer);
	}

	public void createFramebuffer(int width, int height) {
		this.framebufferWidth = width;
		this.framebufferHeight = height;
		this.createFrameBuffer();
		this.createMSColorAttachment();	
		if(DefaultSettings.antiAlias)
			GlStateManager.bindFramebuffer(GL_FRAMEBUFFER, 0);
		else
			MC.getFramebuffer().bindFramebuffer(true);
		this.createMSFrameBuffer();
		this.createColorAttachment();
		this.framebufferClear();
	}
    
	private void createColorAttachment() {
		this.screenTexture = GlStateManager.genTexture();
		GlStateManager.bindTexture(this.screenTexture);
		GlStateManager.texImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, this.framebufferWidth, this.framebufferHeight, 0, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, (IntBuffer) null);
		GlStateManager.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
	    GlStateManager.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GlStateManager.framebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, this.screenTexture, 0);
	}
	
    private void createFrameBuffer() {
    	this.framebuffer = GlStateManager.genFramebuffers();
    	GlStateManager.bindFramebuffer(GL30.GL_FRAMEBUFFER, this.framebuffer);
    }
    
    private void createMSFrameBuffer() {
    	this.interFramebuffer = GlStateManager.genFramebuffers();
    	GlStateManager.bindFramebuffer(GL30.GL_FRAMEBUFFER, this.interFramebuffer);
    }
    
    private void createMSColorAttachment() {
		this.multisampledTexture = GlStateManager.genTexture();
		GL30.glBindTexture(GL32.GL_TEXTURE_2D_MULTISAMPLE, this.multisampledTexture);
		if(GL11.glGetError() != 0) {
			DefaultSettings.compatibilityMode = true;
			return;
		}
		GL32.glTexImage2DMultisample(GL32.GL_TEXTURE_2D_MULTISAMPLE, Math.min(GL30.glGetInteger(GL30.GL_MAX_SAMPLES), DefaultSettings.targetMS), GL30.GL_RGBA8, this.framebufferWidth, this.framebufferHeight, true);
	    GL30.glBindTexture(GL30.GL_TEXTURE_2D, 0);
	    GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL32.GL_TEXTURE_2D_MULTISAMPLE, this.multisampledTexture, 0);
	}

    public void framebufferClear() {
    	GlStateManager.clear(GL11.GL_COLOR_BUFFER_BIT, false);
    	if(DefaultSettings.antiAlias)
			GlStateManager.bindFramebuffer(GL_FRAMEBUFFER, 0);
		else
			MC.getFramebuffer().bindFramebuffer(true);
    }

	public void resize(int width, int height) {
		GlStateManager.deleteFramebuffers(this.framebuffer);
		GlStateManager.deleteFramebuffers(this.interFramebuffer);
		GlStateManager.deleteTexture(this.screenTexture);
		GlStateManager.deleteTexture(this.multisampledTexture);
        this.createBindFramebuffer(width, height);
	}
}