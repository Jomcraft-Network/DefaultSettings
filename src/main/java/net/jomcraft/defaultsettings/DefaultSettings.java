package net.jomcraft.defaultsettings;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.toml.TomlParser;
import net.jomcraft.defaultsettings.commands.ConfigArguments;
import net.jomcraft.defaultsettings.commands.OperationArguments;
import net.jomcraft.defaultsettings.commands.TypeArguments;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.core.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;

@Mod(value = DefaultSettings.MODID)
public class DefaultSettings {

	public static final String MODID = "defaultsettings";
	public static final Logger log = LogManager.getLogger(DefaultSettings.MODID);
	public static final String VERSION = getModVersion();
	public static final String USER_AGENT = "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2";
	public static Map<String, KeyContainer> keyRebinds = new HashMap<String, KeyContainer>();
	public static boolean setUp = false;
	public static DefaultSettings instance;
	public static RegistryEvent newEvent;
	public static boolean init = false;
	private static final DeferredRegister<ArgumentTypeInfo<?, ?>> COMMAND_ARGUMENT_TYPES = DeferredRegister.create(Registry.COMMAND_ARGUMENT_TYPE_REGISTRY, DefaultSettings.MODID);

	@SuppressWarnings({ "deprecation" })
	public DefaultSettings() {
		instance = this;

		DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
			if (setUp)
				return;

			FMLJavaModLoadingContext.get().getModEventBus().addListener(this::postInit);

			COMMAND_ARGUMENT_TYPES.register("ds_config", () -> ArgumentTypeInfos.registerByClass(ConfigArguments.class, new ConfigArguments.Info()));
			COMMAND_ARGUMENT_TYPES.register("ds_operation", () -> ArgumentTypeInfos.registerByClass(OperationArguments.class, new OperationArguments.Info()));
			COMMAND_ARGUMENT_TYPES.register("ds_type", () -> ArgumentTypeInfos.registerByClass(TypeArguments.class, new TypeArguments.Info()));

			COMMAND_ARGUMENT_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
			newEvent = new RegistryEvent();
			FMLJavaModLoadingContext.get().getModEventBus().addListener(newEvent::regInitNew);

			try {
				FileUtil.restoreContents();

			} catch (Exception e) {
				DefaultSettings.log.log(Level.ERROR, "An exception occurred while starting up the game:", e);
			}
			setUp = true;
			MinecraftForge.EVENT_BUS.register(DefaultSettings.class);

			MinecraftForge.EVENT_BUS.register(new EventHandlers());

			ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> "ANY", (remote, isServer) -> true));

		});

		DistExecutor.runWhenOn(Dist.DEDICATED_SERVER, () -> () -> {
			DefaultSettings.log.log(Level.WARN, "DefaultSettings is a client-side mod only! It won't do anything on servers!");
		});
	}

	@SuppressWarnings("unchecked")
	public static String getModVersion() {
		// Stupid FG 3 workaround
		TomlParser parser = new TomlParser();
		InputStream stream = DefaultSettings.class.getClassLoader().getResourceAsStream("META-INF/mods.toml");
		CommentedConfig file = parser.parse(stream);
		return ((ArrayList<CommentedConfig>) file.get("mods")).get(0).get("version");
	}

	@SuppressWarnings("deprecation")
	public void postInit(FMLLoadCompleteEvent event) {
		DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {

			try {
				FileUtil.restoreKeys(true, FileUtil.firstBootUp);
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