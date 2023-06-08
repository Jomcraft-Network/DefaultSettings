package net.jomcraft.defaultsettings;

import net.jomcraft.defaultsettings.commands.CommandDefaultSettings;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EventHandlers {

	@SubscribeEvent
	public void serverStarting(ServerStartingEvent event) {
		CommandDefaultSettings.register(event);
	}
}