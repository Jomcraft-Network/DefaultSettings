package de.pt400c.defaultsettings;

import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL32.*;
import static org.lwjgl.opengl.GL11.*;
import java.nio.ByteBuffer;
import org.lwjgl.opengl.GL11;
import static de.pt400c.defaultsettings.FileUtil.MC;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
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

		DefaultSettings.antiAlias = !OpenGlHelper.isFramebufferEnabled();

		if(DefaultSettings.antiAlias)
			OpenGlHelper.glBindFramebuffer(GL_FRAMEBUFFER, 0);
		else
			MC.getFramebuffer().bindFramebuffer(true);
	}

	public void deleteFramebuffer() {
		if(DefaultSettings.antiAlias)
			OpenGlHelper.glBindFramebuffer(GL_FRAMEBUFFER, 0);
		else
			MC.getFramebuffer().bindFramebuffer(true);
		OpenGlHelper.glDeleteFramebuffers(this.framebuffer);
		OpenGlHelper.glDeleteFramebuffers(this.interFramebuffer);
	}

	public void createFramebuffer(int width, int height) {
		this.framebufferWidth = width;
		this.framebufferHeight = height;
		this.createFrameBuffer();
		this.createMSColorAttachment();	
		if(DefaultSettings.antiAlias)
			OpenGlHelper.glBindFramebuffer(GL_FRAMEBUFFER, 0);
		else
			MC.getFramebuffer().bindFramebuffer(true);
		this.createMSFrameBuffer();
		this.createColorAttachment();
		this.framebufferClear();
	}
    
	private void createColorAttachment() {
		this.screenTexture = GlStateManager.generateTexture();
	    GlStateManager.bindTexture(this.screenTexture);
	    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, this.framebufferWidth, this.framebufferHeight, 0, GL_RGB, GL_UNSIGNED_BYTE, (ByteBuffer) null);
	    GL11.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
	    GL11.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
	    OpenGlHelper.glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, this.screenTexture, 0);
	}
	
    private void createFrameBuffer() {
    	this.framebuffer = OpenGlHelper.glGenFramebuffers();
    	OpenGlHelper.glBindFramebuffer(GL_FRAMEBUFFER, this.framebuffer);
    }
    
    private void createMSFrameBuffer() {
    	this.interFramebuffer = OpenGlHelper.glGenFramebuffers();
    	OpenGlHelper.glBindFramebuffer(GL_FRAMEBUFFER, this.interFramebuffer);
    }
    
    private void createMSColorAttachment() {
    	this.multisampledTexture = GlStateManager.generateTexture();
		glBindTexture(GL_TEXTURE_2D_MULTISAMPLE, this.multisampledTexture);
		if(glGetError() != 0) {
			DefaultSettings.compatibilityMode = true;
			return;
		}
	    glTexImage2DMultisample(GL_TEXTURE_2D_MULTISAMPLE, Math.min(glGetInteger(GL_MAX_SAMPLES), DefaultSettings.targetMS), GL_RGBA8, this.framebufferWidth, this.framebufferHeight, true);
	    glBindTexture(GL_TEXTURE_2D, 0);
	    OpenGlHelper.glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D_MULTISAMPLE, this.multisampledTexture, 0);
	}

    public void framebufferClear() {
    	GlStateManager.clear(GL11.GL_COLOR_BUFFER_BIT);
    	if(DefaultSettings.antiAlias)
			OpenGlHelper.glBindFramebuffer(GL_FRAMEBUFFER, 0);
		else
			MC.getFramebuffer().bindFramebuffer(true);
    }

	public void resize(int width, int height) {
		OpenGlHelper.glDeleteFramebuffers(this.framebuffer);
		OpenGlHelper.glDeleteFramebuffers(this.interFramebuffer);
		GlStateManager.deleteTexture(this.screenTexture);
		GlStateManager.deleteTexture(this.multisampledTexture);
        this.createBindFramebuffer(width, height);
	}
}