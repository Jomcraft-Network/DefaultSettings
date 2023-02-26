package net.jomcraft.defaultsettings;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.jomcraft.defaultsettings.commands.CommandDefaultSettings;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.jomcraft.jcplugin.FileUtilNoMC;

public class DefaultSettings implements ModInitializer {

    public static final String MODID = "defaultsettings";
    public static final Logger log = LogManager.getLogger(DefaultSettings.MODID);
    public static final String VERSION = DefaultSettings.class.getPackage().getImplementationVersion();
    public static Map<String, KeyContainer> keyRebinds = new HashMap<String, KeyContainer>();
    public static boolean setUp = false;
    public static DefaultSettings instance;
    public static boolean init = false;
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
                FileUtil.restoreKeys(true, FileUtilNoMC.firstBootUp);
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