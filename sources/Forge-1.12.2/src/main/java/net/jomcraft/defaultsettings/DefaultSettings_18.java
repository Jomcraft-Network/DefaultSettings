package net.jomcraft.defaultsettings;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import net.jomcraft.defaultsettings.commands.CommandDefaultSettings_18;
import net.jomcraft.jcplugin.JCLogger;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.common.*;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.jomcraft.jcplugin.FileUtilNoMC;
import net.minecraftforge.common.MinecraftForge;

@Mod(modid = DefaultSettings_18.MODID, acceptedMinecraftVersions = "[1.8,1.12.2]", name = DefaultSettings_18.NAME, clientSideOnly = true)
public class DefaultSettings_18 {

    public static final String MODID = "defaultsettings";
    public static final String NAME = "DefaultSettings";
    public static final Logger log = LogManager.getLogger(DefaultSettings_18.MODID);
    public static final String VERSION = DefaultSettings_18.class.getPackage().getImplementationVersion();
    public static Map<String, KeyContainer_18> keyRebinds = new HashMap<String, KeyContainer_18>();
    public static boolean setUp = false;
    public static boolean shutDown = false;
    public static String shutdownReason = null;

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
                    shutdownReason = "The JCPlugin mod couldn't be found! Please make sure that the correct version (probably " + VERSION + ") is installed!";
                    DefaultSettings_18.log.log(Level.ERROR, "DefaultSettings can't start up! Something is hella broken! Shutting down...");
                } else {

                    Map<String, String> launchArgsList = (Map<String, String>) Launch.blackboard.get("launchArgs");
                    String gameDir = launchArgsList.get("--gameDir");

                    File mods = new File(gameDir, "mods");

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
                                    DefaultSettings_18.log.log(Level.INFO, "DefaultSettings found correct version of JCPlugin, starting up...");
                                    break;
                                } else {
                                    shutDown = true;
                                    shutdownReason = "The correct JCPlugin mod version couldn't be found! Please install version " + wantedVersion;
                                    DefaultSettings_18.log.log(Level.ERROR, "DefaultSettings can't start up! JCPlugin version must be " + wantedVersion + "!");
                                }

                            }
                        }
                    }

                    if (!foundDefaultSettings || wantedVersion == null) {
                        shutDown = true;
                        shutdownReason = "Strange! We can't find the DefaultSettings mod, eventhough you're currently using it!";
                        DefaultSettings_18.log.log(Level.ERROR, "DefaultSettings can't start up! Couldn't get requested version of JCPlugin!");
                    }
                }
            } catch (ClassNotFoundException | NoSuchFieldException | SecurityException | IllegalArgumentException |
                     IllegalAccessException | IOException e) {
                shutDown = true;
                shutdownReason = "The JCPlugin mod couldn't be found! Please make sure that the correct version (probably " + VERSION + ") is installed!";
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
                if (!shutDown) FileUtil_18.restoreKeys(true, FileUtilNoMC.privateJson.firstBootUp);
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