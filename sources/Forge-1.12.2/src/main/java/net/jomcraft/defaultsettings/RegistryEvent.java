package net.jomcraft.defaultsettings;

import net.minecraft.client.settings.GameSettings;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.Level;
import net.minecraft.client.Minecraft;

public class RegistryEvent {

	@SuppressWarnings({ "deprecation", "resource" })
	public void regInitNew(net.minecraftforge.event.RegistryEvent.NewRegistry event) {
		if (!DefaultSettings.init && !DefaultSettings.shutDown) {
			if(FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
				try {
					GameSettings gameSettings = Minecraft.getMinecraft().gameSettings;
					gameSettings.loadOptions();
					Minecraft.getMinecraft().gameSettings.saveOptions();

				} catch (NullPointerException e) {
					DefaultSettings.log.log(Level.ERROR, "Something went wrong while starting up: ", e);
				}
			} else {
				DefaultSettings.log.log(Level.WARN, "DefaultSettings is a client-side mod only! It won't do anything on servers!");
			}

			DefaultSettings.init = true;
		}
	}

}