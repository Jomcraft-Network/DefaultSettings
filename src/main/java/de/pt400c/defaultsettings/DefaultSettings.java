package de.pt400c.defaultsettings;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(value = DefaultSettings.MODID)
public class DefaultSettings {

	public static final String MODID = "defaultsettings";
	public static final Logger log = LogManager.getLogger(DefaultSettings.MODID);
	public static Map<String, KeyContainer> keyRebinds = new HashMap<String, KeyContainer>();
	public static boolean setUp = false;
	private static final UpdateContainer updateContainer = new UpdateContainer();
	
	public static DefaultSettings instance;
	
	public DefaultSettings() {
		instance = this;
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
