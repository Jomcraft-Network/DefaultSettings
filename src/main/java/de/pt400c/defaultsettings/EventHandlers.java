package de.pt400c.defaultsettings;

import static de.pt400c.defaultsettings.FileUtil.MC;
import org.lwjgl.input.Keyboard;
import cpw.mods.fml.client.GuiModList;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.client.Minecraft;

public class EventHandlers {

	@SubscribeEvent
	public void tickEvent(TickEvent.ClientTickEvent event) {
		if((MC.currentScreen instanceof GuiModList || MC.currentScreen == null) && Keyboard.isKeyDown(Keyboard.KEY_F7) && Keyboard.isKeyDown(Keyboard.KEY_G))
			MC.displayGuiScreen(new GuiConfig(Minecraft.getMinecraft().currentScreen));
		
	}
	
	public static int getLimitFramerate() {
		return MC.currentScreen instanceof GuiConfig ? 60 : 30;
	}
	
}
