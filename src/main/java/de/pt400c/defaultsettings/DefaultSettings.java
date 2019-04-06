package de.pt400c.defaultsettings;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLConstructionEvent;
import cpw.mods.fml.common.event.FMLFingerprintViolationEvent;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;

@Mod(modid = DefaultSettings.MODID, name = DefaultSettings.NAME, version = DefaultSettings.VERSION, dependencies = "before:*", certificateFingerprint = "@FINGERPRINT@")
public class DefaultSettings {

	public static final String MODID = "defaultsettings";
	public static final String NAME = "DefaultSettings";
	public static final String VERSION = "@VERSION@";
	public static final Logger log = LogManager.getLogManager().getLogger(DefaultSettings.MODID);
	public static final boolean isServer = FMLCommonHandler.instance().getSide() == Side.SERVER;
	public static boolean devEnv = Arrays.asList(MinecraftServer.class.getDeclaredFields()).get(7).getName().equals("hostname");
	public static Map<String, Integer> keyRebinds = new HashMap<String, Integer>();
	public static boolean setUp = false;

	@Instance
	public static DefaultSettings instance;

	@EventHandler
	public static void init(FMLInitializationEvent event) {
		if (!isServer)
			TickRegistry.registerTickHandler(new TickHandlerClient(), Side.CLIENT);
	}
	
	@EventHandler
    public static void onFingerprintViolation(FMLFingerprintViolationEvent event) {
		if(devEnv)
			return;
		
		DefaultSettings.log.log(Level.SEVERE, "The mod's files have been manipulated! The game will be terminated.");
		System.exit(0);
    }

	@EventHandler
	public static void construction(FMLConstructionEvent event) {
		if (isServer || setUp)
			return;
		try {
			FileUtil.restoreContents();
		} catch (Exception e) {
			DefaultSettings.log.log(Level.SEVERE, "An exception occurred while starting up the game:", e);
		}
		setUp = true;
	}

	@EventHandler
	public static void preInit(FMLPreInitializationEvent event) {
		if (isServer)
			return;
		ClientCommandHandler.instance.registerCommand(new CommandDefaultSettings());
		MinecraftForge.EVENT_BUS.register(DefaultSettings.class);
	}

	@EventHandler
	public static void postInit(FMLPostInitializationEvent event) {
		if (isServer)
			return;

		try {
			FileUtil.restoreKeys();
		} catch (IOException e) {
			DefaultSettings.log.log(Level.SEVERE, "An exception occurred while starting up the game (Post):", e);
		} catch (NullPointerException e) {
			DefaultSettings.log.log(Level.SEVERE, "An exception occurred while starting up the game (Post):", e);
		}
	}

}
