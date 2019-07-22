package de.pt400c.defaultsettings.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class Vec2f {
    public final float x;
    public final float y;

    public Vec2f(float xIn, float yIn)
    {
        this.x = xIn;
        this.y = yIn;
    }
}