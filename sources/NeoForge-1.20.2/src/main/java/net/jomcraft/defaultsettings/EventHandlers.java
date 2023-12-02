package net.jomcraft.defaultsettings;

import net.jomcraft.defaultsettings.commands.CommandDefaultSettings;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

public class EventHandlers {

    @SubscribeEvent
    public void serverStarting(ServerStartingEvent event) {
        CommandDefaultSettings.register(event);
    }
}