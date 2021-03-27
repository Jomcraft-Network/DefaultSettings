package de.pt400c.defaultsettings;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;

public class BakeryRegistry {
	
	public static final List<Integer> textures = new ArrayList<Integer>();
	
	public static final List<Integer> fbos = new ArrayList<Integer>();
	
	public static final List<Integer> renderbs = new ArrayList<Integer>();
	
	public static void clearAll() {
		for(int fbo : fbos)
			OpenGlHelper.glDeleteFramebuffers(fbo);
		
		fbos.clear();
		
		for(int texture : textures)
			GlStateManager.deleteTexture(texture);
		
		textures.clear();
		
		for(int renderb : renderbs)
			OpenGlHelper.glDeleteRenderbuffers(renderb);
		
		renderbs.clear();
	}
}