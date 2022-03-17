package net.jomcraft.defaultsettings;

import org.apache.logging.log4j.Level;

import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.registries.NewRegistryEvent;

public class EventNew {

	@SuppressWarnings({ "deprecation", "resource" })
	public void regInitNew(NewRegistryEvent event) {
		if (!DefaultSettings.init) {
			DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
				try {
					Options gameSettings = Minecraft.getInstance().options;
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
