package net.jomcraft.defaultsettings;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import cpw.mods.modlauncher.Launcher;
import cpw.mods.modlauncher.api.IEnvironment;
import net.jomcraft.jcplugin.JCLogger;
import net.minecraftforge.fml.ExtensionPoint;
import org.apache.commons.lang3.tuple.Pair;
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
    public static boolean shutDown = false;
    public static String shutdownReason = null;

    @SuppressWarnings({"deprecation"})
    public DefaultSettings() {
        instance = this;
        ForgeCoreHook core = new ForgeCoreHook();
        Core.setInstance(core);

        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
            if (setUp) return;

            try {
                Field pluginClass = Class.forName("net.jomcraft.jcplugin.JCPlugin").getDeclaredField("checksSuccessful");

                if (!pluginClass.getBoolean(null)) {
                    shutDown = true;
                    shutdownReason = "The JCPlugin mod couldn't be found! Please make sure that the correct version (probably " + VERSION + ") is installed!";
                    DefaultSettings.log.log(Level.ERROR, "DefaultSettings can't start up! Something is hella broken! Shutting down...");
                } else {

                    final Path location = Launcher.INSTANCE.environment().getProperty(IEnvironment.Keys.GAMEDIR.get()).get();

                    File mods = new File(location.toFile(), "mods");

                    boolean foundDefaultSettings = false;
                    String wantedVersion = null;

                    for (File mod : mods.listFiles()) {
                        if (mod.getName().toLowerCase().contains("defaultsettings")) {

                            JarFile jar = new JarFile(mod);

                            ZipEntry toml = jar.getEntry("META-INF/MANIFEST.MF");
                            if (toml != null) {

                                BufferedReader result = new BufferedReader(new InputStreamReader(jar.getInputStream(toml)));

                                String readerLine;

                                while ((readerLine = result.readLine()) != null) {
                                    if (readerLine.contains("Implementation-Title: DefaultSettings")) {
                                        foundDefaultSettings = true;
                                    } else if (readerLine.startsWith("JCPluginVersion")) {
                                        wantedVersion = readerLine.split(": ")[1];
                                    }
                                }

                                result.close();
                            }

                            jar.close();

                            if (foundDefaultSettings && wantedVersion != null) {

                                if (wantedVersion.equals(JCLogger.class.getPackage().getImplementationVersion())) {
                                    DefaultSettings.log.log(Level.INFO, "DefaultSettings found correct version of JCPlugin, starting up...");
                                    break;
                                } else {
                                    shutDown = true;
                                    shutdownReason = "The correct JCPlugin mod version couldn't be found! Please install version " + wantedVersion;
                                    DefaultSettings.log.log(Level.ERROR, "DefaultSettings can't start up! JCPlugin version must be " + wantedVersion + "!");
                                }

                            }
                        }
                    }

                    String launchTarget = Launcher.INSTANCE.environment().getProperty(IEnvironment.Keys.LAUNCHTARGET.get()).get();

                    if (!launchTarget.contains("dev") && (!foundDefaultSettings || wantedVersion == null)) {
                        shutDown = true;
                        shutdownReason = "Strange! We can't find the DefaultSettings mod, eventhough you're currently using it!";
                        DefaultSettings.log.log(Level.ERROR, "DefaultSettings can't start up! Couldn't get requested version of JCPlugin!");
                    }
                }
            } catch (ClassNotFoundException | NoSuchFieldException | SecurityException | IllegalArgumentException |
                     IllegalAccessException | IOException e) {
                shutDown = true;
                shutdownReason = "The JCPlugin mod couldn't be found! Please make sure that the correct version (probably " + VERSION + ") is installed!";
                DefaultSettings.log.log(Level.ERROR, "DefaultSettings is missing the JCPlugin mod! Shutting down...");
            }

            FMLJavaModLoadingContext.get().getModEventBus().addListener(this::postInit);

            ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> Pair.of(() -> "ANY", (remote, isServer) -> true));

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
                if (!shutDown) FileUtil.restoreKeys(true, FileUtilNoMC.privateJson.firstBootUp);
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