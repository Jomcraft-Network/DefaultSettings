package net.jomcraft.defaultsettings;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import net.minecraftforge.fml.IExtensionPoint;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.jomcraft.jcplugin.FileUtilNoMC;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(value = DefaultSettings.MODID)
public class DefaultSettings {

    public static final String MODID = "defaultsettings";
    public static final Logger log = LogManager.getLogger(DefaultSettings.MODID);
    public static final String VERSION = DefaultSettings.class.getPackage().getImplementationVersion();
    public static Map<String, KeyContainer> keyRebinds = new HashMap<String, KeyContainer>();
    public static boolean setUp = false;
    public static DefaultSettings instance;
    public static RegistryEvent newEvent;
    public static boolean init = false;
    public static boolean shutDown = false;

    @SuppressWarnings({"deprecation"})
    public DefaultSettings() {
        instance = this;

        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
            if (setUp) return;

            try {
                Field pluginClass = Class.forName("net.jomcraft.jcplugin.JCPlugin").getDeclaredField("checksSuccessful");

                if (!pluginClass.getBoolean(null)) {
                    shutDown = true;
                    DefaultSettings.log.log(Level.ERROR, "DefaultSettings can't start up! Something is hella broken! Shutting down...");
                }
            } catch (ClassNotFoundException | NoSuchFieldException | SecurityException | IllegalArgumentException |
                     IllegalAccessException e) {
                shutDown = true;
                DefaultSettings.log.log(Level.ERROR, "DefaultSettings is missing the JCPlugin mod! Shutting down...");
            }

            FMLJavaModLoadingContext.get().getModEventBus().addListener(this::postInit);

            newEvent = new RegistryEvent();
            FMLJavaModLoadingContext.get().getModEventBus().addListener(newEvent::regInitNew);

            ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> "ANY", (remote, isServer) -> true));

            MinecraftForge.EVENT_BUS.register(DefaultSettings.class);

            MinecraftForge.EVENT_BUS.register(new EventHandlers());

            if (shutDown) return;

            try {
                FileUtil.restoreContents();

            } catch (Exception e) {
                DefaultSettings.log.log(Level.ERROR, "An exception occurred while starting up the game:", e);
            }
            setUp = true;

        });

        DistExecutor.runWhenOn(Dist.DEDICATED_SERVER, () -> () -> {
            DefaultSettings.log.log(Level.WARN, "DefaultSettings is a client-side mod only! It won't do anything on servers!");
        });
    }

    @SuppressWarnings("deprecation")
    public void postInit(FMLLoadCompleteEvent event) {
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {

            try {
                if (!shutDown) FileUtil.restoreKeys(true, FileUtilNoMC.firstBootUp);
            } catch (IOException e) {
                DefaultSettings.log.log(Level.ERROR, "An exception occurred while starting up the game (Post):", e);
            } catch (NullPointerException e) {
                DefaultSettings.log.log(Level.ERROR, "An exception occurred while starting up the game (Post):", e);
            }

        });

        DistExecutor.runWhenOn(Dist.DEDICATED_SERVER, () -> () -> {
            DefaultSettings.log.log(Level.WARN, "DefaultSettings is a client-side mod only! It won't do anything on servers!");
        });

    }

    public static DefaultSettings getInstance() {
        return instance;
    }
}