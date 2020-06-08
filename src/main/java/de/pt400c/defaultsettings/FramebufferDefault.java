package de.pt400c.defaultsettings;

import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL32.*;
import static org.lwjgl.opengl.GL11.*;
import java.nio.ByteBuffer;
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
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
	}

	public void deleteFramebuffer() {
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
		glDeleteFramebuffers(this.framebuffer);
		glDeleteFramebuffers(this.interFramebuffer);
	}

	public void createFramebuffer(int width, int height) {
		this.framebufferWidth = width;
		this.framebufferHeight = height;
		this.createFrameBuffer();
		this.createMSColorAttachment();	
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
		this.createMSFrameBuffer();
		this.createColorAttachment();
		this.framebufferClear();
	}
    
	private void createColorAttachment() {
		this.screenTexture = glGenTextures();
	    glBindTexture(GL_TEXTURE_2D, this.screenTexture);
	    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, this.framebufferWidth, this.framebufferHeight, 0, GL_RGB, GL_UNSIGNED_BYTE, (ByteBuffer) null);
	    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
	    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
	    glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, this.screenTexture, 0);
	}
	
    private void createFrameBuffer() {
    	this.framebuffer = glGenFramebuffers();
    	glBindFramebuffer(GL_FRAMEBUFFER, this.framebuffer);
    }
    
    private void createMSFrameBuffer() {
    	this.interFramebuffer = glGenFramebuffers();
    	glBindFramebuffer(GL_FRAMEBUFFER, this.interFramebuffer);
    }
    
    private void createMSColorAttachment() {
		this.multisampledTexture = glGenTextures();
	    glBindTexture(GL_TEXTURE_2D_MULTISAMPLE, this.multisampledTexture);
	    glTexImage2DMultisample(GL_TEXTURE_2D_MULTISAMPLE, DefaultSettings.targetMS, GL_RGB, this.framebufferWidth, this.framebufferHeight, true);
	    glBindTexture(GL_TEXTURE_2D_MULTISAMPLE, 0);
	    glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D_MULTISAMPLE, this.multisampledTexture, 0);
	}

    public void framebufferClear() {
        glClear(GL_COLOR_BUFFER_BIT);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

	public void resize(int width, int height) {
		glDeleteFramebuffers(this.framebuffer);
		glDeleteFramebuffers(this.interFramebuffer);
		glDeleteTextures(this.screenTexture);
		glDeleteTextures(this.multisampledTexture);
        this.createBindFramebuffer(width, height);
	}
}