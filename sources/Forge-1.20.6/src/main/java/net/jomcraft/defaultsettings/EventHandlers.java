package net.jomcraft.defaultsettings;

import net.jomcraft.defaultsettings.commands.CommandDefaultSettings;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.lang.reflect.Method;

public class EventHandlers {

	@SubscribeEvent
	public void serverStarting(ServerStartingEvent event) {
		Method[] l = event.getServer().getClass().getDeclaredMethods();
		for(Method i : l){
			System.out.println("NAME: " + i.getName());
		}
		CommandDefaultSettings.register(event.getServer().getCommands().getDispatcher());

	}
}