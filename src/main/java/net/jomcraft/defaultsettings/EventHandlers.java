package net.jomcraft.defaultsettings;

import net.jomcraft.defaultsettings.commands.CommandDefaultSettings;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fmlserverevents.FMLServerStartingEvent;

public class EventHandlers {

	@SubscribeEvent
	public void serverStarting(FMLServerStartingEvent event) {
		CommandDefaultSettings.register(event);
	}
}