package de.pt400c.defaultsettings;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLFingerprintViolationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = DefaultSettings.MODID, name = DefaultSettings.NAME, version = DefaultSettings.VERSION, dependencies = "before:*", certificateFingerprint = "@FINGERPRINT@", clientSideOnly = true)
public class DefaultSettings {

	public static final String MODID = "defaultsettings";
	public static final String NAME = "DefaultSettings";
	public static final String VERSION = "@VERSION@";
	public static final Logger log = LogManager.getLogger(DefaultSettings.MODID);
	public static Map<String, KeyContainer> keyRebinds = new HashMap<String, KeyContainer>();

	@Instance
	public static DefaultSettings instance;
	
	public DefaultSettings() {
		instance = this;
	}

	@EventHandler
	public static void construction(FMLConstructionEvent event) {
		try {
			FileUtil.restoreContents();
		} catch (Exception e) {
			DefaultSettings.log.log(Level.ERROR, "An exception occurred while starting up the game:", e);
		}
	}
	
	@EventHandler
    public static void onFingerprintViolation(FMLFingerprintViolationEvent event) {
		if(event.isDirectory())
			return;

		DefaultSettings.log.log(Level.ERROR, "The mod's files have been manipulated! The game will be terminated.");
		FMLCommonHandler.instance().exitJava(0, true);
    }

	@EventHandler
	public static void preInit(FMLPreInitializationEvent event) {
		ClientCommandHandler.instance.registerCommand(new CommandDefaultSettings());
		MinecraftForge.EVENT_BUS.register(DefaultSettings.class);
		MinecraftForge.EVENT_BUS.register(new EventHandlers());
	}
	
	@EventHandler
	public static void postInit(FMLPostInitializationEvent event) {
		try {
			FileUtil.restoreKeys();
		} catch (IOException e) {
			DefaultSettings.log.log(Level.ERROR, "An exception occurred while starting up the game (Post):", e);
		} catch (NullPointerException e) {
			DefaultSettings.log.log(Level.ERROR, "An exception occurred while starting up the game (Post):", e);
		}
	}
	
	public static DefaultSettings getInstance() {
		return instance;
	}

}
