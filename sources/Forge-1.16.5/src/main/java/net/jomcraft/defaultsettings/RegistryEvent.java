package net.jomcraft.defaultsettings;

import net.minecraft.client.GameSettings;
import org.apache.logging.log4j.Level;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

public class RegistryEvent {

	@SuppressWarnings({ "deprecation", "resource" })
	public void regInitNew(net.minecraftforge.event.RegistryEvent.NewRegistry event) {
		if (!DefaultSettings.init && !DefaultSettings.shutDown) {
			DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
				try {
					GameSettings gameSettings = Minecraft.getInstance().options;
					gameSettings.load();
					Minecraft.getInstance().options.save();

				} catch (NullPointerException e) {
					DefaultSettings.log.log(Level.ERROR, "Something went wrong while starting up: ", e);
				}

			});
			DistExecutor.runWhenOn(Dist.DEDICATED_SERVER, () -> () -> {
				DefaultSettings.log.log(Level.WARN, "DefaultSettings is a client-side mod only! It won't do anything on servers!");
			});
			DefaultSettings.init = true;
		}
	}

}