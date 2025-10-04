//? if neoforge {
/*package net.jomcraft.defaultsettings.loaders;

import net.jomcraft.defaultsettings.DefaultSettings;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

@Mod("defaultsettings")
public class NeoforgeEntrypoint {

    public NeoforgeEntrypoint(IEventBus modEventBus) {
        new DefaultSettings().initialize(modEventBus);
        DefaultSettings.VERSION = DefaultSettings.class.getPackage().getImplementationVersion();
    }
}
*///?}
