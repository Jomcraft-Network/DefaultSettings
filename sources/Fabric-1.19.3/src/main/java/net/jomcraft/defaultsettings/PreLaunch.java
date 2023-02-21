package net.jomcraft.defaultsettings;

import java.io.File;

import net.fabricmc.loader.api.FabricLoader;
import net.jomcraft.jcplugin.FileUtilNoMC;
import org.apache.logging.log4j.Level;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

public class PreLaunch implements PreLaunchEntrypoint {

    @Override
    public void onPreLaunch() {
        try {
            File location = FabricLoader.getInstance().getGameDir().toFile();
            new File(location, "config").mkdir();
            FileUtilNoMC.mcDataDir = location;
            FileUtilNoMC.restoreContentsFirst();

        } catch (Exception e) {
            DefaultSettings.log.log(Level.ERROR, "An exception occurred while starting up the game:", e);
        }
    }
}