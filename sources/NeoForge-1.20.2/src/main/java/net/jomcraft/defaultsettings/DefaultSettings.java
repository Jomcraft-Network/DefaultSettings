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
import net.jomcraft.defaultsettings.commands.ConfigArguments;
import net.jomcraft.defaultsettings.commands.OperationArguments;
import net.jomcraft.defaultsettings.commands.TypeArguments;
import net.jomcraft.jcplugin.JCLogger;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.IExtensionPoint;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.fml.common.Mod;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.jomcraft.jcplugin.FileUtilNoMC;

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

    private static final DeferredRegister<ArgumentTypeInfo<?, ?>> COMMAND_ARGUMENT_TYPES = DeferredRegister.create(Registries.COMMAND_ARGUMENT_TYPE, DefaultSettings.MODID);

    @SuppressWarnings({"deprecation"})
    public DefaultSettings(IEventBus modEventBus) {
        instance = this;
        NeoForgeCoreHook core = new NeoForgeCoreHook();
        Core.setInstance(core);

        if (FMLLoader.getDist().isClient()) {
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

            modEventBus.addListener(this::postInit);

            COMMAND_ARGUMENT_TYPES.register("ds_config", () -> ArgumentTypeInfos.registerByClass(ConfigArguments.class, new ConfigArguments.Info()));
            COMMAND_ARGUMENT_TYPES.register("ds_operation", () -> ArgumentTypeInfos.registerByClass(OperationArguments.class, new OperationArguments.Info()));
            COMMAND_ARGUMENT_TYPES.register("ds_type", () -> ArgumentTypeInfos.registerByClass(TypeArguments.class, new TypeArguments.Info()));

            COMMAND_ARGUMENT_TYPES.register(modEventBus);

            ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> "ANY", (remote, isServer) -> true));

            NeoForge.EVENT_BUS.register(new EventHandlers());

            if (shutDown) return;

            try {
                FileUtil.restoreContents();

            } catch (Exception e) {
                DefaultSettings.log.log(Level.ERROR, "An exception occurred while starting up the game:", e);
            }
            setUp = true;

        }

        if (FMLLoader.getDist().isDedicatedServer()) {
            DefaultSettings.log.log(Level.WARN, "DefaultSettings is a client-side mod only! It won't do anything on servers!");
        }
    }

    @SuppressWarnings("deprecation")
    public void postInit(FMLLoadCompleteEvent event) {
        if (FMLLoader.getDist().isClient()) {
            try {
                if (!shutDown) FileUtil.restoreKeys(true, FileUtilNoMC.privateJson.firstBootUp);
            } catch (IOException e) {
                DefaultSettings.log.log(Level.ERROR, "An exception occurred while starting up the game (Post):", e);
            } catch (NullPointerException e) {
                DefaultSettings.log.log(Level.ERROR, "An exception occurred while starting up the game (Post):", e);
            }
        }

        if (FMLLoader.getDist().isDedicatedServer()) {
            DefaultSettings.log.log(Level.WARN, "DefaultSettings is a client-side mod only! It won't do anything on servers!");
        }
    }

    public static DefaultSettings getInstance() {
        return instance;
    }
}