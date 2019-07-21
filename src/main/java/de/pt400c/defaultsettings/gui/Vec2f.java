package de.pt400c.defaultsettings.gui;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class Vec2f
{
    public final float x;
    public final float y;

    public Vec2f(float xIn, float yIn)
    {
        this.x = xIn;
        this.y = yIn;
    }
}