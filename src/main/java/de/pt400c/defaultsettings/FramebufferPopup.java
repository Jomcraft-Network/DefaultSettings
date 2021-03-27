package de.pt400c.defaultsettings;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL11.*;
import static de.pt400c.defaultsettings.FileUtil.MC;

@OnlyIn(Dist.CLIENT)
public class FramebufferPopup {
	
	public int width;
	public int height;
	public int msColorRenderBuffer;
	public int msFbo;
    public int texture;
    public int fbo;
	   
	public FramebufferPopup(int width, int height) {
		this.width = width;
		this.height = height;
		setupFBO();
	}
	
	public void resize(int width, int height) {
		
		BakeryRegistry.fbos.remove(new Integer(msFbo));
        BakeryRegistry.fbos.remove(new Integer(fbo));
        BakeryRegistry.renderbs.remove(new Integer(msColorRenderBuffer));
        BakeryRegistry.textures.remove(new Integer(texture));
		
		OpenGlHelper.glDeleteFramebuffers(this.fbo);
		OpenGlHelper.glDeleteRenderbuffers(this.msFbo);
		OpenGlHelper.glDeleteRenderbuffers(this.msColorRenderBuffer);
		GlStateManager.deleteTexture(this.texture);

		this.width = width;
		this.height = height;
		setupFBO();
	}
	
	public void setupFBO() {
        msColorRenderBuffer = OpenGlHelper.glGenRenderbuffers();
        msFbo = OpenGlHelper.glGenFramebuffers();
        OpenGlHelper.glBindFramebuffer(GL_FRAMEBUFFER, msFbo);
        glBindRenderbuffer(GL_RENDERBUFFER, msColorRenderBuffer);
        glRenderbufferStorageMultisample(GL_RENDERBUFFER, Math.min(glGetInteger(GL_MAX_SAMPLES), DefaultSettings.targetMS), GL_RGBA8, width, height);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_RENDERBUFFER, msColorRenderBuffer);

        glBindRenderbuffer(GL_RENDERBUFFER, 0);
        if(DefaultSettings.antiAlias)
			OpenGlHelper.glBindFramebuffer(GL_FRAMEBUFFER, 0);
		else
			MC.getFramebuffer().bindFramebuffer(true);

        texture = GlStateManager.generateTexture();
        fbo = OpenGlHelper.glGenFramebuffers();
        OpenGlHelper.glBindFramebuffer(GL_FRAMEBUFFER, fbo);
        GlStateManager.bindTexture(texture);
        GlStateManager.texParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        GlStateManager.texParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        GlStateManager.texImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, (IntBuffer) null);
        OpenGlHelper.glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, texture, 0);

        glBindRenderbuffer(GL_RENDERBUFFER, 0);
        if(DefaultSettings.antiAlias)
			OpenGlHelper.glBindFramebuffer(GL_FRAMEBUFFER, 0);
		else
			MC.getFramebuffer().bindFramebuffer(true);
        
        BakeryRegistry.fbos.add(new Integer(msFbo));
        BakeryRegistry.fbos.add(new Integer(fbo));
        BakeryRegistry.renderbs.add(new Integer(msColorRenderBuffer));
        BakeryRegistry.textures.add(new Integer(texture));
	}
}