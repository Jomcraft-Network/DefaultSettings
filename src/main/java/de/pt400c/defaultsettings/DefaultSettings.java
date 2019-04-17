package de.pt400c.defaultsettings;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLConstructionEvent;
import cpw.mods.fml.common.event.FMLFingerprintViolationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;

@Mod(modid = DefaultSettings.MODID, name = DefaultSettings.NAME, version = DefaultSettings.VERSION, dependencies = "before:*", certificateFingerprint = "@FINGERPRINT@")
public class DefaultSettings {

	public static final String MODID = "defaultsettings";
	public static final String NAME = "DefaultSettings";
	public static final String VERSION = "@VERSION@";
	public static final Logger log = LogManager.getLogger(DefaultSettings.MODID);
	private static boolean isServer = false;
	public static Map<String, Integer> keyRebinds = new HashMap<String, Integer>();
	private static final UpdateContainer updateContainer = new UpdateContainer();

	@Instance
	public static DefaultSettings instance;
	
	public DefaultSettings() {
		instance = this;
	}

	@EventHandler
	public static void construction(FMLConstructionEvent event) {
		isServer = FMLCommonHandler.instance().getSide() == Side.SERVER;
		if (isServer)
			return;
		try {
			FileUtil.restoreContents();
		} catch (Exception e) {
			DefaultSettings.log.log(Level.ERROR, "An exception occurred while starting up the game:", e);
		}
	}
	
	@EventHandler
    public static void onFingerprintViolation(FMLFingerprintViolationEvent event) {
		if(event.isDirectory)
			return;
		
		DefaultSettings.log.log(Level.ERROR, "The mod's files have been manipulated! The game will be terminated.");
		FMLCommonHandler.instance().exitJava(0, true);
    }

	@EventHandler
	public static void preInit(FMLPreInitializationEvent event) {
		if (isServer)
			return;
		
		ClientCommandHandler.instance.registerCommand(new CommandDefaultSettings());
		MinecraftForge.EVENT_BUS.register(DefaultSettings.class);
		FMLCommonHandler.instance().bus().register(new EventHandlers());
	}
	
	@EventHandler
	public static void postInit(FMLPostInitializationEvent event) {
		if (isServer)
			return;

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
