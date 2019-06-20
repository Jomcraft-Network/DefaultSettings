package de.pt400c.defaultsettings;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import de.pt400c.defaultsettings.EventHandlers.NewModInfo;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import net.minecraftforge.forgespi.language.IModInfo;

@Mod(value = DefaultSettings.MODID)
public class DefaultSettings {

	public static final String MODID = "defaultsettings";
	public static final Logger log = LogManager.getLogger(DefaultSettings.MODID);
	public static Map<String, KeyContainer> keyRebinds = new HashMap<String, KeyContainer>();
	public static boolean setUp = false;
	private static final UpdateContainer updateContainer = new UpdateContainer();
	
	public static DefaultSettings instance;
	
	@SuppressWarnings("unchecked")
	public DefaultSettings() {
		instance = this;

		try {
			Field sortedList = ModList.class.getDeclaredField("sortedList");
            sortedList.setAccessible(true);
            List<ModInfo> editList = (List<ModInfo>) sortedList.get(ModList.get());
            ModInfo prevModInfo = editList.stream().filter(modInfos -> modInfos.getModId().equals(DefaultSettings.MODID)).findFirst().get();
			NewModInfo modInfo = new NewModInfo(prevModInfo);
			editList.set(editList.indexOf(prevModInfo), modInfo);
			ModContainer wailaContainer = ModList.get().getModContainerById(DefaultSettings.MODID).get();
			Field modInfoField = ModContainer.class.getDeclaredField("modInfo");
			modInfoField.setAccessible(true);
			modInfoField.set(wailaContainer, (IModInfo) modInfo);
	
		}catch(NullPointerException | NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
		
			e.printStackTrace();
		}
		
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::postInit);
		
		//Not yet implemented by Forge
		
		//FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onFingerprintViolation);
		
		if (setUp)
			return;
		try {
			FileUtil.restoreContents();
		} catch (Exception e) {
			DefaultSettings.log.log(Level.ERROR, "An exception occurred while starting up the game:", e);
		}
		setUp = true;
		MinecraftForge.EVENT_BUS.register(DefaultSettings.class);
		MinecraftForge.EVENT_BUS.register(new EventHandlers());
		
		ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, ()-> (mc, screen) -> new GuiConfig(screen));

	}
	
	/*
    public void onFingerprintViolation(FMLFingerprintViolationEvent event) {
		if(event.isDirectory() || FileUtil.isDev)
			return;

		DefaultSettings.log.log(Level.ERROR, "The mod's files have been manipulated! The game will be terminated.");
		System.exit(0);
    }*/

	public void postInit(FMLLoadCompleteEvent event) {
		try {
			FileUtil.restoreKeys();
		} catch (IOException e) {
			DefaultSettings.log.log(Level.ERROR, "An exception occurred while starting up the game (Post):", e);
		} catch (NullPointerException e) {
			DefaultSettings.log.log(Level.ERROR, "An exception occurred while starting up the game (Post):", e);
		}

	}
	
	public static UpdateContainer getUpdater() {
		return updateContainer;
	}
	
	public static DefaultSettings getInstance() {
		return instance;
	}

}
