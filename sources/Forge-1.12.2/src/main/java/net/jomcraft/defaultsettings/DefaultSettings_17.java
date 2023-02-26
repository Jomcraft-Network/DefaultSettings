package net.jomcraft.defaultsettings;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLConstructionEvent;
import cpw.mods.fml.common.event.FMLLoadCompleteEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import net.jomcraft.defaultsettings.commands.CommandDefaultSettings_17;
import net.minecraftforge.client.ClientCommandHandler;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.jomcraft.jcplugin.FileUtilNoMC;
import net.minecraftforge.common.MinecraftForge;

@Mod(modid = DefaultSettings_17.MODID, acceptedMinecraftVersions = "[1.7.10]", name = DefaultSettings_17.NAME)
public class DefaultSettings_17 {

    public static final String MODID = "defaultsettings";
    public static final String NAME = "DefaultSettings";
    public static final Logger log = LogManager.getLogger(DefaultSettings_17.MODID);
    public static final String VERSION = DefaultSettings_17.class.getPackage().getImplementationVersion();
    public static Map<String, Integer> keyRebinds = new HashMap<String, Integer>();
    public static boolean setUp = false;
    public static boolean init = false;
    public static boolean shutDown = false;

    @Mod.Instance
    public static DefaultSettings_17 instance;

    @Mod.EventHandler
    public static void construction(FMLConstructionEvent event) {
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
            if (setUp) return;

            try {
                Field pluginClass = Class.forName("net.jomcraft.jcplugin.JCPlugin").getDeclaredField("checksSuccessful");

                if (!pluginClass.getBoolean(null)) {
                    shutDown = true;
                    DefaultSettings_17.log.log(Level.ERROR, "DefaultSettings can't start up! Something is hella broken! Shutting down...");
                }
            } catch (ClassNotFoundException | NoSuchFieldException | SecurityException | IllegalArgumentException |
                     IllegalAccessException e) {
                shutDown = true;
                DefaultSettings_17.log.log(Level.ERROR, "DefaultSettings is missing the JCPlugin mod! Shutting down...");
            }

            if (shutDown) return;

            try {
                FileUtil_17.restoreContents();
            } catch (Exception e) {
                DefaultSettings_17.log.log(Level.ERROR, "An exception occurred while starting up the game:", e);
            }
            setUp = true;
        } else {
            DefaultSettings_17.log.log(Level.WARN, "DefaultSettings is a client-side mod only! It won't do anything on servers!");
        }
    }

    public DefaultSettings_17() {
        instance = this;
    }

    @Mod.EventHandler
    public static void preInit(FMLPreInitializationEvent event) {
        ClientCommandHandler.instance.registerCommand(new CommandDefaultSettings_17());

        try {
            Field eventBusField = MinecraftForge.class.getDeclaredField("EVENT_BUS");
            Object eventBusObject = eventBusField.get(null);
            Method m = eventBusObject.getClass().getDeclaredMethod("register", Object.class);
            m.invoke(eventBusObject, DefaultSettings_17.class);

        } catch (NoSuchFieldException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            DefaultSettings_17.log.log(Level.ERROR, "Mod pre-initialization failed with error:", e);
        }
    }

    @Mod.EventHandler
    public static void keysEvent(FMLLoadCompleteEvent event) {
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
            try {
                if (!shutDown) FileUtil_17.restoreKeys(true, FileUtilNoMC.firstBootUp);
            } catch (IOException e) {
                DefaultSettings_17.log.log(Level.ERROR, "An exception occurred while starting up the game (Post):", e);
            } catch (NullPointerException e) {
                DefaultSettings_17.log.log(Level.ERROR, "An exception occurred while starting up the game (Post):", e);
            }
        } else {
            DefaultSettings_17.log.log(Level.WARN, "DefaultSettings is a client-side mod only! It won't do anything on servers!");
        }
    }

    public static DefaultSettings_17 getInstance() {
        return instance;
    }
}