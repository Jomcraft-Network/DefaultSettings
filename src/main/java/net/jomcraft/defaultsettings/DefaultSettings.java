package net.jomcraft.defaultsettings;

import com.mojang.brigadier.arguments.ArgumentType;
//? if fabric {
/*import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
//? if >= 1.20
/^import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;^/
*///?}
import net.jomcraft.defaultsettings.commands.CommandDefaultSettings;
import net.jomcraft.defaultsettings.commands.ConfigArguments;
import net.jomcraft.defaultsettings.commands.OperationArguments;
import net.jomcraft.defaultsettings.commands.TypeArguments;
import net.jomcraft.jcplugin.FileUtilNoMC;
import net.jomcraft.jcplugin.JCLogger;
//? if >= 1.20 {
/*import net.jomcraft.defaultsettings.mixin.ArgumentTypeInfosMixin;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.core.registries.BuiltInRegistries;
*///?}
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
//? if neoforge {
/*import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.Registries;
//? if <1.21.9 {
import cpw.mods.modlauncher.Launcher;
import cpw.mods.modlauncher.api.IEnvironment;
//?}
*///?}
//? if forge {
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import cpw.mods.modlauncher.Launcher;
import cpw.mods.modlauncher.api.IEnvironment;
//? if >=1.19 {
/*import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
*///?}
//?}
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

public class DefaultSettings {

    public static final String MODID = "defaultsettings";
    public static final Logger log = LogManager.getLogger(DefaultSettings.MODID);
    public static String VERSION = "none";
    public static Map<String, KeyContainer> keyRebinds = new HashMap<String, KeyContainer>();
    public static boolean setUp = false;
    public static DefaultSettings instance;
    public static boolean shutDown = false;
    //TODO:
    public static String shutdownReason = null;
    //? if neoforge
    /*private static final DeferredRegister<ArgumentTypeInfo<?, ?>> COMMAND_ARGUMENT_TYPES = DeferredRegister.create(Registries.COMMAND_ARGUMENT_TYPE, DefaultSettings.MODID);*/
    //? if (forge) && (>=1.19)
    /*private static final DeferredRegister<ArgumentTypeInfo<?, ?>> COMMAND_ARGUMENT_TYPES = DeferredRegister.create(ForgeRegistries.Keys.COMMAND_ARGUMENT_TYPES, DefaultSettings.MODID);*/

    //? if (fabric) && >= 1.20 {
    /*public static synchronized <A extends ArgumentType<?>, T extends ArgumentTypeInfo.Template<A>, I extends ArgumentTypeInfo<A, T>> I registerByClass(Class<A> infoClass, I argumentTypeInfo) {
        ArgumentTypeInfosMixin.getBY_CLASS().put(infoClass, argumentTypeInfo);
        return argumentTypeInfo;
    }
    *///?}

    public void initialize (/*? if neoforge {*//*IEventBus modEventBus*//*?}*/) {
        instance = this;
        //? if fabric {
        /*FabricCoreHook core = new FabricCoreHook();
        Core.setInstance(core);
        *///?}

        //? if forge {
        ForgeCoreHook core = new ForgeCoreHook();
        Core.setInstance(core);
        //?}

        //? if neoforge {
        /*NeoForgeCoreHook core = new NeoForgeCoreHook();
        Core.setInstance(core);
        *///?}

        //? if neoforge {

        /*//? if <1.21.9
        if (FMLLoader.getDist().isClient()) {
        //? if >=1.21.9
        /^if (FMLLoader.getCurrent().getDist().isClient()) {^/

            if (setUp) return;

            try {
                Field pluginClass = Class.forName("net.jomcraft.jcplugin.JCPlugin").getDeclaredField("checksSuccessful");

                if (!pluginClass.getBoolean(null)) {
                    shutDown = true;
                    shutdownReason = "The JCPlugin mod couldn't be found! Please make sure that the correct version (probably " + VERSION + ") is installed!";
                    DefaultSettings.log.log(Level.ERROR, "DefaultSettings can't start up! Something is hella broken! Shutting down...");
                } else {
                    //? if <1.21.9
                    final Path location = Launcher.INSTANCE.environment().getProperty(IEnvironment.Keys.GAMEDIR.get()).get();
                    //? if >=1.21.9
                    /^final Path location = FMLLoader.getCurrent().getGameDir().toAbsolutePath();^/

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
                    //? if <1.21.9
                    String launchTarget = Launcher.INSTANCE.environment().getProperty(IEnvironment.Keys.LAUNCHTARGET.get()).get();

                    boolean isProduction = false;
                    //? if <1.21.9
                    if(!launchTarget.contains("dev"))
                    //? if >=1.21.9
                    /^if(FMLLoader.getCurrent().isProduction())^/
                        isProduction = true;

                    if (isProduction && (!foundDefaultSettings || wantedVersion == null)) {
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

            //ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> "ANY", (remote, isServer) -> true));

            NeoForge.EVENT_BUS.register(new EventHandlers());

            if (shutDown) return;

            try {
                FileUtil.restoreContents();

            } catch (Exception e) {
                DefaultSettings.log.log(Level.ERROR, "An exception occurred while starting up the game:", e);
            }

        }
        *///?}
        //? if forge {
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
            ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> "ANY", (remote, isServer) -> true));

            //? if >=1.19 {
            /*COMMAND_ARGUMENT_TYPES.register("ds_config", () -> ArgumentTypeInfos.registerByClass(ConfigArguments.class, new ConfigArguments.Info()));
            COMMAND_ARGUMENT_TYPES.register("ds_operation", () -> ArgumentTypeInfos.registerByClass(OperationArguments.class, new OperationArguments.Info()));
            COMMAND_ARGUMENT_TYPES.register("ds_type", () -> ArgumentTypeInfos.registerByClass(TypeArguments.class, new TypeArguments.Info()));

            COMMAND_ARGUMENT_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
            *///?}
            MinecraftForge.EVENT_BUS.register(DefaultSettings.class);

            MinecraftForge.EVENT_BUS.register(new EventHandlers());

            if (shutDown) return;

            try {
                FileUtil.restoreContents();

            } catch (Exception e) {
                DefaultSettings.log.log(Level.ERROR, "An exception occurred while starting up the game:", e);
            }

        });
        //?}

        //? if (fabric) && >= 1.20 {
        /*ConfigArguments.Info config = new ConfigArguments.Info();
        OperationArguments.Info operation = new OperationArguments.Info();
        TypeArguments.Info type = new TypeArguments.Info();
        Registry.register(BuiltInRegistries.COMMAND_ARGUMENT_TYPE, createResourceLocation(MODID, "config"), config);
        Registry.register(BuiltInRegistries.COMMAND_ARGUMENT_TYPE, createResourceLocation(MODID, "operation"), operation);
        Registry.register(BuiltInRegistries.COMMAND_ARGUMENT_TYPE, createResourceLocation(MODID, "type"), type);
        registerByClass(ConfigArguments.class, config);
        registerByClass(OperationArguments.class, operation);
        registerByClass(TypeArguments.class, type);
        *///?}

        //? if (fabric) && >= 1.20 {
        /*CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            if (!environment.includeDedicated) {
                CommandDefaultSettings.register(dispatcher);
            }
        });
        *///?}

        //? if (fabric) && < 1.20 {
        /*net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
             if (!dedicated) {
                CommandDefaultSettings.register(dispatcher);
            }
        });
        *///?}

        //? if fabric {
        /*ClientLifecycleEvents.CLIENT_STARTED.register((test) -> {
            restoreStart();
        });
        *///?}
    }

    private void restoreStart(){
        try {
            if (!shutDown)
                FileUtil.restoreContents();
        } catch (Exception e) {
            DefaultSettings.log.log(Level.ERROR, "An exception occurred while starting up the game:", e);
        }

        try {
            if (!shutDown)
                FileUtil.restoreKeys(true, FileUtilNoMC.privateJson.firstBootUp);
        } catch (IOException | NullPointerException e) {
            DefaultSettings.log.log(Level.ERROR, "An exception occurred while starting up the game (Post):", e);
        }
        setUp = true;
    }

    //? if neoforge {
    /*public void postInit(FMLLoadCompleteEvent event) {
        //? if <1.21.9 {
        if (FMLLoader.getDist().isClient()) {
        //?} else {
        /^if (FMLLoader.getCurrent().getDist().isClient()) {
        ^///?}
            restoreStart();
        }
        //? if <1.21.9 {
        if (FMLLoader.getDist().isDedicatedServer()) {
        //?} else {
        /^if (FMLLoader.getCurrent().getDist().isDedicatedServer()) {
        ^///?}
            DefaultSettings.log.log(Level.WARN, "DefaultSettings is a client-side mod only! It won't do anything on servers!");
        }
    }
    *///?}

    //? if forge {
    public void postInit(FMLLoadCompleteEvent event) {
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
            restoreStart();
        });

        DistExecutor.runWhenOn(Dist.DEDICATED_SERVER, () -> () -> {
            DefaultSettings.log.log(Level.WARN, "DefaultSettings is a client-side mod only! It won't do anything on servers!");
        });

    }
    //?}

    public static ResourceLocation createResourceLocation(String string, String string2) {
        //? if >=1.21 {
            /*return ResourceLocation.fromNamespaceAndPath(string, string2);
        *///?} else {
            return new ResourceLocation(string, string2);
        //?}
    }

    public static DefaultSettings getInstance() {
        return instance;
    }
}
