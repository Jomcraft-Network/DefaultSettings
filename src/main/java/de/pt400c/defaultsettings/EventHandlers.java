package de.pt400c.defaultsettings;

import net.minecraft.client.util.InputMappings;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.gui.GuiModList;
import net.minecraftforge.fml.client.gui.screen.ModListScreen;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import static org.lwjgl.glfw.GLFW.*;
import static de.pt400c.defaultsettings.FileUtil.MC;

public class EventHandlers {

	@SubscribeEvent
	public void tickEvent(TickEvent.ClientTickEvent event) {
		
		if(DefaultSettings.is_1_15) {
			if ((MC.currentScreen instanceof ModListScreen && MC.world == null) && InputMappings.isKeyDown(MC.mainWindow.getHandle(), GLFW_KEY_F7) && InputMappings.isKeyDown(MC.mainWindow.getHandle(), GLFW_KEY_G))
				MC.displayGuiScreen(new GuiConfig(MC, MC.currentScreen));
		
		} else {
			if ((MC.currentScreen instanceof GuiModList && MC.world == null) && InputMappings.isKeyDown(MC.mainWindow.getHandle(), GLFW_KEY_F7) && InputMappings.isKeyDown(MC.mainWindow.getHandle(), GLFW_KEY_G))
				MC.displayGuiScreen(new GuiConfig(MC, MC.currentScreen));
		}
	}

	@SubscribeEvent
	public void serverStarting(FMLServerStartingEvent event) {
		CommandSwitchProfile.register(event);
		CommandDefaultSettings.register(event);
	}
}