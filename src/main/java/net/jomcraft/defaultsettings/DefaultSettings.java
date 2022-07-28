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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(value = DefaultSettings.MODID)
public class DefaultSettings {

	public static final String MODID = "defaultsettings";
	public static final Logger log = LogManager.getLogger(DefaultSettings.MODID);
	public static final String VERSION = getModVersion();
	public static Map<String, KeyContainer> keyRebinds = new HashMap<String, KeyContainer>();
	public static boolean setUp = false;
	public static RegistryEvent newEvent;
	public static DefaultSettings instance;
	public static boolean init = false;

	@SuppressWarnings({ "deprecation" })
	public DefaultSettings() {
		instance = this;

		DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
			if (setUp)
				return;

			FMLJavaModLoadingContext.get().getModEventBus().addListener(this::postInit);
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