package net.jomcraft.defaultsettings;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import com.mojang.brigadier.arguments.ArgumentType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.impl.util.ManifestUtil;
import net.jomcraft.defaultsettings.commands.CommandDefaultSettings;
import net.jomcraft.defaultsettings.commands.ConfigArguments;
import net.jomcraft.defaultsettings.commands.OperationArguments;
import net.jomcraft.defaultsettings.commands.TypeArguments;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
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

    public static synchronized <A extends ArgumentType<?>, T extends ArgumentTypeInfo.Template<A>, I extends ArgumentTypeInfo<A, T>> I registerByClass(Class<A> infoClass, I argumentTypeInfo) {
        ArgumentTypeInfos.BY_CLASS.put(infoClass, argumentTypeInfo);
        return argumentTypeInfo;
    }

    @Override
    public void onInitialize() {
        instance = this;
        FabricCoreHook core = new FabricCoreHook();
        Core.setInstance(core);

        registerByClass(ConfigArguments.class, new ConfigArguments.Info());
        registerByClass(OperationArguments.class, new OperationArguments.Info());
        registerByClass(TypeArguments.class, new TypeArguments.Info());

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            if (!environment.includeDedicated) {
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