//? if forge {
package net.jomcraft.defaultsettings.loaders;

import net.jomcraft.defaultsettings.DefaultSettings;
import net.minecraftforge.fml.common.Mod;

@Mod("defaultsettings")
public class ForgeEntrypoint {

    public ForgeEntrypoint() {
        new DefaultSettings().initialize();
        DefaultSettings.log.error("VERS: " + DefaultSettings.class.getPackage().getImplementationVersion());
        DefaultSettings.VERSION = DefaultSettings.class.getPackage().getImplementationVersion();
    }
}
//?}
