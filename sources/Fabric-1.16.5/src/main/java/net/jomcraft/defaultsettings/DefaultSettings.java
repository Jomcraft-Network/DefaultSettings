package net.jomcraft.defaultsettings;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.jomcraft.defaultsettings.commands.CommandDefaultSettings;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.jomcraft.jcplugin.FileUtilNoMC;

public class DefaultSettings implements ModInitializer {

    public static final String MODID = "defaultsettings";
    public static final Logger log = LogManager.getLogger(DefaultSettings.MODID);
    public static String VERSION = "none";
    public static Map<String, KeyContainer> keyRebinds = new HashMap<String, KeyContainer>();
    public static DefaultSettings instance;
    public static boolean shutDown = false;

    @Override
    public void onInitialize() {
        instance = this;
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            if (!dedicated) {
                CommandDefaultSettings.register(dispatcher);
            }
        });

        ClientLifecycleEvents.CLIENT_STARTED.register((test) -> {
            try {
                FileUtil.restoreContents();
            } catch (Exception e) {
                DefaultSettings.log.log(Level.ERROR, "An exception occurred while starting up the game:", e);
            }

            try {
                FileUtil.restoreKeys(true, FileUtilNoMC.privateJson.firstBootUp);
            } catch (IOException e) {
                DefaultSettings.log.log(Level.ERROR, "An exception occurred while starting up the game (Post):", e);
            } catch (NullPointerException e) {
                DefaultSettings.log.log(Level.ERROR, "An exception occurred while starting up the game (Post):", e);
            }
        });
    }

    public static DefaultSettings getInstance() {
        return instance;
    }
}