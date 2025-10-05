package net.jomcraft.defaultsettings;

import net.jomcraft.defaultsettings.commands.CommandDefaultSettings;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;

public class EventHandlers {

	@SubscribeEvent
	public static void serverStarting(ServerStartingEvent event) {
		CommandDefaultSettings.register(event.getServer().getCommands().getDispatcher());

	}
}