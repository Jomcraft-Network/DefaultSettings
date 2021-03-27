package de.pt400c.defaultsettings;

import java.util.ArrayList;
import java.util.List;
import com.mojang.blaze3d.platform.GlStateManager;
import static org.lwjgl.opengl.GL30.*;

public class BakeryRegistry {
	
	public static final List<Integer> textures = new ArrayList<Integer>();
	
	public static final List<Integer> fbos = new ArrayList<Integer>();
	
	public static final List<Integer> renderbs = new ArrayList<Integer>();
	
	public static void clearAll() {
		for(int fbo : fbos)
			GlStateManager.deleteFramebuffers(fbo);
		
		fbos.clear();
		
		for(int texture : textures)
			GlStateManager.deleteTexture(texture);
		
		textures.clear();
		
		for(int renderb : renderbs)
			glDeleteRenderbuffers(renderb);
		
		renderbs.clear();
	}
}