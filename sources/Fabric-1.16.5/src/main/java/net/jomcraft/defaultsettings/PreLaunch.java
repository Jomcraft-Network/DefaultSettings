package net.jomcraft.defaultsettings;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.util.UrlConversionException;
import net.jomcraft.jcplugin.FileUtilNoMC;
import org.apache.logging.log4j.Level;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

public class PreLaunch implements PreLaunchEntrypoint {

    public String getVersion() throws IOException, URISyntaxException, UrlConversionException {
        Manifest manifest = ManifestUtil.readManifest(this.getClass());
        Attributes attr = manifest.getMainAttributes();
        return attr.getValue("Implementation-Version");
    }

    @Override
    public void onPreLaunch() {
        try {
            DefaultSettings.VERSION = getVersion();

            File location = FabricLoader.getInstance().getGameDir().toFile();
            new File(location, "config").mkdir();

            try {
                Class.forName("net.jomcraft.jcplugin.JCLogger");
            } catch (ClassNotFoundException e) {
                DefaultSettings.log.log(Level.ERROR, "DefaultSettings can't find JCPlugin!", e);
                return;
            }

            FileUtilNoMC.mcDataDir = location;
            FileUtilNoMC.restoreContentsFirst();

        } catch (Exception e) {
            DefaultSettings.log.log(Level.ERROR, "An exception occurred while starting up the game:", e);
        }
    }
}