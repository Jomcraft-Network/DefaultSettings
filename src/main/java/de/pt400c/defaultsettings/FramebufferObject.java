package de.pt400c.defaultsettings;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL11.*;

@SideOnly(Side.CLIENT)
public class FramebufferObject
{
    public int framebufferTextureWidth;
    public int framebufferTextureHeight;
    public int framebufferWidth;
    public int framebufferHeight;
    public int framebufferObject;
    public int colorBuffer;

    public FramebufferObject(int width, int height) {
        this.framebufferObject = -1;
        this.createBindFramebuffer(width, height);
    }

	public void createBindFramebuffer(int width, int height) {

		if (this.framebufferObject >= 0) 
			this.deleteFramebuffer();

		this.createFramebuffer(width, height);
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
	}

	public void deleteFramebuffer() {
		this.unbindFramebuffer();
		if (this.framebufferObject > -1) {
			glBindFramebuffer(GL_FRAMEBUFFER, 0);
			glDeleteFramebuffers(this.framebufferObject);
			this.framebufferObject = -1;
		}
	}

	public void createFramebuffer(int width, int height) {
		this.framebufferWidth = width;
		this.framebufferHeight = height;
		this.framebufferTextureWidth = width;
		this.framebufferTextureHeight = height;
		this.createFrameBuffer();
		this.createColorAttachment();
		this.framebufferClear();
	}
    
    private void createFrameBuffer() {
    	this.framebufferObject = glGenFramebuffers();
    	glBindFramebuffer(GL_FRAMEBUFFER, this.framebufferObject);
    	glDrawBuffer(GL_COLOR_ATTACHMENT0);
    }
    
    private void createColorAttachment() {
		this.colorBuffer = glGenRenderbuffers();
		glBindRenderbuffer(GL_RENDERBUFFER, this.colorBuffer);
		glRenderbufferStorageMultisample(GL_RENDERBUFFER, 9, GL_RGBA8, this.framebufferWidth, this.framebufferHeight);

		glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_RENDERBUFFER, this.colorBuffer);
	}

	public void bindFramebuffer(final boolean vp) {
		glBindFramebuffer(GL_FRAMEBUFFER, this.framebufferObject);

		if (vp) 
			glViewport(0, 0, this.framebufferWidth, this.framebufferHeight);

	}

    public void unbindFramebuffer() {
    	glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public void framebufferClear() {
        this.bindFramebuffer(true);
        glClear(GL_COLOR_BUFFER_BIT);
        this.unbindFramebuffer();
    }
}