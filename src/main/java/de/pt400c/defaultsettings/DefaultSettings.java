package de.pt400c.defaultsettings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarInputStream;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.toml.TomlParser;
import de.pt400c.defaultsettings.EventHandlers.NewModInfo;
import de.pt400c.defaultsettings.font.FontRendererClass;
import net.minecraft.client.GameSettings;
import net.minecraft.client.resources.ClientResourcePackInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import net.minecraftforge.forgespi.language.IModInfo;

@Mod(value = DefaultSettings.MODID)
public class DefaultSettings {

	public static final String MODID = "defaultsettings";
	public static final Logger log = LogManager.getLogger(DefaultSettings.MODID);
	public static final String VERSION = getModVersion();
	public static Map<String, KeyContainer> keyRebinds = new HashMap<String, KeyContainer>();
	public static boolean setUp = false;
	public static String BUILD_ID = "Unknown";
	public static String BUILD_TIME = "Unknown";
	public static FontRendererClass fontRenderer;
	private static final UpdateContainer updateContainer = new UpdateContainer();
	public static DefaultSettings instance;
	public static final boolean debug = false;
	
	@SuppressWarnings("unchecked")
	public DefaultSettings() {
		instance = this;

		DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
			if (setUp)
				return;
			
			FMLJavaModLoadingContext.get().getModEventBus().addListener(this::postInit);
			
			try {
				Field sortedList = ModList.class.getDeclaredField("sortedList");
				sortedList.setAccessible(true);
				List<ModInfo> editList = (List<ModInfo>) sortedList.get(ModList.get());
				ModInfo prevModInfo = editList.stream().filter(modInfos -> modInfos.getModId().equals(DefaultSettings.MODID)).findFirst().get();
				NewModInfo modInfo = new NewModInfo(prevModInfo);
				editList.set(editList.indexOf(prevModInfo), modInfo);
				ModContainer modContainer = ModList.get().getModContainerById(DefaultSettings.MODID).get();
				Field modInfoField = ModContainer.class.getDeclaredField("modInfo");
				modInfoField.setAccessible(true);
				modInfoField.set(modContainer, (IModInfo) modInfo);

			} catch (NullPointerException | NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {

				e.printStackTrace();
			}

			try {
				FileUtil.restoreContents();
				
			} catch (Exception e) {
				DefaultSettings.log.log(Level.ERROR, "An exception occurred while starting up the game:", e);
			}
			setUp = true;
			MinecraftForge.EVENT_BUS.register(DefaultSettings.class);

			MinecraftForge.EVENT_BUS.register(new EventHandlers());
			MinecraftForge.EVENT_BUS.register(new UnregHandlers());

			ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, () -> GuiConfig::new);
			ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> Pair.of(() -> "ANY", (remote, isServer) -> true));

		});
		
		DistExecutor.runWhenOn(Dist.DEDICATED_SERVER, () -> () -> {
			DefaultSettings.log.log(Level.WARN, "DefaultSettings is a client-side mod only! It won't do anything on servers!");
		});
	}
	
	@SuppressWarnings("unchecked")
	public static String getModVersion() {
		//Stupid FG 3 workaround
		TomlParser parser = new TomlParser();
		InputStream stream = DefaultSettings.class.getClassLoader().getResourceAsStream("META-INF/mods.toml");
		CommentedConfig file = parser.parse(stream);

		return ((ArrayList<CommentedConfig>) file.get("mods")).get(0).get("version");
	}
	
	/*
    public void onFingerprintViolation(FMLFingerprintViolationEvent event) {
		if(event.isDirectory() || FileUtil.isDev)
			return;

		DefaultSettings.log.log(Level.ERROR, "The mod's files have been manipulated! The game will be terminated.");
		System.exit(0);
    }*/

	public void postInit(FMLLoadCompleteEvent event) {
		DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
			
			fontRenderer = new FontRendererClass();
			fontRenderer.readFontTexture();
			
			try {
				getBuildID();
				getBuildTime();
			} catch (NullPointerException | IOException  e) {
				GameSettings gameSettings = FileUtil.MC.gameSettings;
				gameSettings.loadOptions();
				FileUtil.MC.getResourcePackList().reloadPacksFromFinders();
				List<ClientResourcePackInfo> repositoryEntries = new ArrayList<ClientResourcePackInfo>();
				for (String resourcePack : gameSettings.resourcePacks)
					for (ClientResourcePackInfo entry : FileUtil.MC.getResourcePackList().getAllPacks())
						if (entry.getName().equals(resourcePack))
							repositoryEntries.add(entry);

				FileUtil.MC.getResourcePackList().getEnabledPacks().addAll(repositoryEntries);
			}
			
			try {
				FileUtil.restoreKeys();
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
	
	private static void getBuildID() throws FileNotFoundException, IOException {
		File file = FMLLoader.getLoadingModList().getModFileById(DefaultSettings.MODID).getFile().getFilePath().toFile();

		try (JarInputStream jarStream = new JarInputStream(new FileInputStream(file))) {
			BUILD_ID = jarStream.getManifest().getMainAttributes().getValue("Build-ID");
		}
	}
	
	private static void getBuildTime() throws FileNotFoundException, IOException {
		File file = FMLLoader.getLoadingModList().getModFileById(DefaultSettings.MODID).getFile().getFilePath().toFile();

		try (JarInputStream jarStream = new JarInputStream(new FileInputStream(file))) {
			BUILD_TIME = jarStream.getManifest().getMainAttributes().getValue("Implementation-Timestamp");
		}
	}
	
	public static UpdateContainer getUpdater() {
		return updateContainer;
	}
	
	public static DefaultSettings getInstance() {
		return instance;
	}
}