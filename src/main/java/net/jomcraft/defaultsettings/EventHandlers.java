//? if neoforge || forge {
package net.jomcraft.defaultsettings;

import net.jomcraft.defaultsettings.commands.CommandDefaultSettings;
//? if neoforge {
/*import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
*///?}
//? if forge {
import net.minecraftforge.eventbus.api.SubscribeEvent;
//? if <1.18
import net.minecraftforge.fmlserverevents.FMLServerStartingEvent;
//? if >=1.18
/*import net.minecraftforge.event.server.ServerStartingEvent;*/
//?}

public class EventHandlers {

    @SubscribeEvent
    //? if (neoforge) || ((forge) && (>=1.18))
    /*public void serverStarting(ServerStartingEvent event) {*/
    //? if (forge) && (<1.18)
    public void serverStarting(FMLServerStartingEvent event) {
        CommandDefaultSettings.register(event.getServer().getCommands().getDispatcher());
    }
}
//?}