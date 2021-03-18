package de.pt400c.defaultsettings;

import java.util.ArrayList;
import java.util.List;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

public class BakeryRegistry {
	
	public static final List<Integer> textures = new ArrayList<Integer>();
	
	public static final List<Integer> fbos = new ArrayList<Integer>();
	
	public static final List<Integer> renderbs = new ArrayList<Integer>();
	
	public static void clearAll() {
		for(int fbo : fbos)
			glDeleteFramebuffers(fbo);
		
		fbos.clear();
		
		for(int texture : textures)
			glDeleteTextures(texture);
		
		textures.clear();
		
		for(int renderb : renderbs)
			glDeleteRenderbuffers(renderb);
		
		renderbs.clear();
	}
}