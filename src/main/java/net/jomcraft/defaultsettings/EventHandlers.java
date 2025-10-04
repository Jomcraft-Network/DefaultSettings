package net.jomcraft.defaultsettings;

import net.jomcraft.defaultsettings.commands.CommandDefaultSettings;
import net.minecraftforge.event.server.ServerStartingEvent;

public class EventHandlers {

	public static void serverStarting(ServerStartingEvent event) {
		CommandDefaultSettings.register(event.getServer().getCommands().getDispatcher());
	}
}