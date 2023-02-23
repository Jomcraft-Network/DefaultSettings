package net.jomcraft.defaultsettings;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import net.jomcraft.defaultsettings.commands.CommandDefaultSettings_18;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.jomcraft.jcplugin.FileUtilNoMC;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

@Mod(modid = DefaultSettings_18.MODID, acceptedMinecraftVersions = "[1.8,1.12.2]", name = DefaultSettings_18.NAME, version = DefaultSettings_18.VERSION, clientSideOnly = true)
public class DefaultSettings_18 {

    public static final String MODID = "defaultsettings";
    public static final String NAME = "DefaultSettings";
    public static final Logger log = LogManager.getLogger(DefaultSettings_18.MODID);
    public static final String VERSION = "2.8.7";
    public static Map<String, KeyContainer_18> keyRebinds = new HashMap<String, KeyContainer_18>();
    public static boolean setUp = false;
    public static RegistryEvent_18 newEvent;
    public static boolean init = false;
    public static boolean shutDown = false;

    @Mod.Instance
    public static DefaultSettings_18 instance;

    @Mod.EventHandler
    public static void construction(FMLConstructionEvent event) {
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
            if (setUp) return;

            try {
                Field pluginClass = Class.forName("net.jomcraft.jcplugin.JCPlugin").getDeclaredField("checksSuccessful");

                if (!pluginClass.getBoolean(null)) {
                    shutDown = true;
                    DefaultSettings_18.log.log(Level.ERROR, "DefaultSettings can't start up! Something is hella broken! Shutting down...");
                }
            } catch (ClassNotFoundException | NoSuchFieldException | SecurityException | IllegalArgumentException |
                     IllegalAccessException e) {
                shutDown = true;
                DefaultSettings_18.log.log(Level.ERROR, "DefaultSettings is missing the JCPlugin mod! Shutting down...");
            }

            if (shutDown) return;

            try {
                FileUtil_18.restoreContents();
            } catch (Exception e) {
                DefaultSettings_18.log.log(Level.ERROR, "An exception occurred while starting up the game:", e);
            }
            setUp = true;
        } else {
            DefaultSettings_18.log.log(Level.WARN, "DefaultSettings is a client-side mod only! It won't do anything on servers!");
        }
    }

    public DefaultSettings_18() {
        instance = this;
    }

    @Mod.EventHandler
    public static void preInit(FMLPreInitializationEvent event) {
        ClientCommandHandler.instance.registerCommand(new CommandDefaultSettings_18());

        MinecraftForge.EVENT_BUS.register(DefaultSettings_18.class);
    }

    @Mod.EventHandler
    public static void keysEvent(FMLLoadCompleteEvent event) {
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
            try {
                if (!shutDown) FileUtil_18.restoreKeys(true, FileUtilNoMC.firstBootUp);
            } catch (IOException e) {
                DefaultSettings_18.log.log(Level.ERROR, "An exception occurred while starting up the game (Post):", e);
            } catch (NullPointerException e) {
                DefaultSettings_18.log.log(Level.ERROR, "An exception occurred while starting up the game (Post):", e);
            }
        } else {
            DefaultSettings_18.log.log(Level.WARN, "DefaultSettings is a client-side mod only! It won't do anything on servers!");
        }
    }

    public static DefaultSettings_18 getInstance() {
        return instance;
    }
}