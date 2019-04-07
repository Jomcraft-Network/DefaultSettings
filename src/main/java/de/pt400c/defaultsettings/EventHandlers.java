package de.pt400c.defaultsettings;

import static org.lwjgl.glfw.GLFW.*;
import static de.pt400c.defaultsettings.FileUtil.MC;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.gui.GuiModList;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;

public class EventHandlers {

	@SubscribeEvent
	public void tickEvent(TickEvent.ClientTickEvent event) {
		if ((MC.currentScreen instanceof GuiModList || MC.currentScreen == null) && InputMappings.isKeyDown(GLFW_KEY_F7) && InputMappings.isKeyDown(GLFW_KEY_G))
			
			MC.displayGuiScreen(new GuiConfig(Minecraft.getInstance().currentScreen));
	}

	@SubscribeEvent
	public void serverStarting(FMLServerStartingEvent event) {
		CommandDefaultSettings.register(event);
		
	}

}
