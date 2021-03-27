package de.pt400c.defaultsettings;

import java.nio.IntBuffer;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import static de.pt400c.defaultsettings.FileUtil.MC;
import static org.lwjgl.opengl.GL30.*;

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
		
        GlStateManager.deleteFramebuffers(this.fbo);
		glDeleteRenderbuffers(this.msFbo);
		glDeleteRenderbuffers(this.msColorRenderBuffer);
		GlStateManager.deleteTexture(this.texture);

		this.width = width;
		this.height = height;
		setupFBO();
	}
	
	public void setupFBO() {
        msColorRenderBuffer = glGenRenderbuffers();
        msFbo = GlStateManager.genFramebuffers();
        GlStateManager.bindFramebuffer(GL_FRAMEBUFFER, msFbo);
        glBindRenderbuffer(GL_RENDERBUFFER, msColorRenderBuffer);
        glRenderbufferStorageMultisample(GL_RENDERBUFFER, Math.min(glGetInteger(GL_MAX_SAMPLES), DefaultSettings.targetMS), GL_RGBA8, width, height);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_RENDERBUFFER, msColorRenderBuffer);

        glBindRenderbuffer(GL_RENDERBUFFER, 0);
        if(DefaultSettings.antiAlias)
			GlStateManager.bindFramebuffer(GL_FRAMEBUFFER, 0);
		else
			MC.getFramebuffer().bindFramebuffer(true);
        texture = GlStateManager.genTexture();
        fbo = GlStateManager.genFramebuffers();
        GlStateManager.bindFramebuffer(GL_FRAMEBUFFER, fbo);
        GlStateManager.bindTexture(texture);
        GlStateManager.texParameter(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        GlStateManager.texParameter(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        GlStateManager.texImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, (IntBuffer) null);

        GlStateManager.framebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, texture, 0);
        glBindRenderbuffer(GL_RENDERBUFFER, 0);
        if(DefaultSettings.antiAlias)
			GlStateManager.bindFramebuffer(GL_FRAMEBUFFER, 0);
		else
			MC.getFramebuffer().bindFramebuffer(true);
        
        BakeryRegistry.fbos.add(new Integer(msFbo));
        BakeryRegistry.fbos.add(new Integer(fbo));
        BakeryRegistry.renderbs.add(new Integer(msColorRenderBuffer));
        BakeryRegistry.textures.add(new Integer(texture));
	}
}