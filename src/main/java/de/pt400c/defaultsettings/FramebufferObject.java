package de.pt400c.defaultsettings;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
	}

	public void deleteFramebuffer() {
		this.unbindFramebuffer();
		if (this.framebufferObject > -1) {
			GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
			GL30.glDeleteFramebuffers(this.framebufferObject);
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
    	this.framebufferObject = GL30.glGenFramebuffers();
    	GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, this.framebufferObject);
    	GL11.glDrawBuffer(GL30.GL_COLOR_ATTACHMENT0);
    }
    
    private void createColorAttachment() {
		this.colorBuffer = GL30.glGenRenderbuffers();
		GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, this.colorBuffer);
		GL30.glRenderbufferStorageMultisample(GL30.GL_RENDERBUFFER, 9 /*9 samples*/, GL11.GL_RGBA8, this.framebufferWidth, this.framebufferHeight);

		GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL30.GL_RENDERBUFFER, this.colorBuffer);
	}

	public void bindFramebuffer(final boolean vp) {
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, this.framebufferObject);

		if (vp) 
			GL11.glViewport(0, 0, this.framebufferWidth, this.framebufferHeight);

	}

    public void unbindFramebuffer() {
    	GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
    }

    public void framebufferClear() {
        this.bindFramebuffer(true);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
        this.unbindFramebuffer();
    }

}