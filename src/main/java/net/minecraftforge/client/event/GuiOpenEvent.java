package net.minecraftforge.client.event;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraft.client.gui.GuiScreen;

@Cancelable
public class GuiOpenEvent extends Event
{
	public GuiScreen gui;
    public GuiOpenEvent(GuiScreen gui)
    {
        this.setGui(gui);
    }

    public GuiScreen getGui()
    {
        return gui;
    }

    public void setGui(GuiScreen gui)
    {
        this.gui = gui;
    }
}