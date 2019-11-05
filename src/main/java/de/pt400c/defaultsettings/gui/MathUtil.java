package de.pt400c.defaultsettings.gui;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MathUtil {

	public static float clamp(float num, float min, float max) {
        if (num < min)
            return min;

        else
            return num > max ? max : num;
    }
	
	public static int clamp(int num, int min, int max) {
        if (num < min)
            return min;
        
        else
            return num > max ? max : num;  
    }
	
	@SideOnly(Side.CLIENT)
	public static class Vec2f
	{
	    public final float x;
	    public final float y;

	    public Vec2f(float xIn, float yIn)
	    {
	        this.x = xIn;
	        this.y = yIn;
	    }
	}
}