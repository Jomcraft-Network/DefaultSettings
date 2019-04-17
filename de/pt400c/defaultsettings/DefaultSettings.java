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
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.Mod.ServerStarting;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;

@Mod(modid = "defaultsettings", name = "DefaultSettings", version = "1.2.1", dependencies = "before:*")
@NetworkMod(clientSideRequired = true, serverSideRequired = false)
public class DefaultSettings {

	public static final String MODID = "defaultsettings";
    public static final Logger log = LogManager.getLogManager().getLogger(DefaultSettings.MODID);
    public static final boolean isServer = FMLCommonHandler.instance().getSide() == Side.SERVER;
    public static Map<String, Integer> keyRebinds = new HashMap<String, Integer>();
    public static boolean setUp = false;
    private static final UpdateContainer updateContainer = new UpdateContainer();
    
    public DefaultSettings() {
    	instance = this;
    	if(isServer || setUp)
    		return;
        try {
			FileUtil.restoreContents();
		} catch (Exception e) {
			DefaultSettings.log.log(Level.SEVERE, "An exception occurred while starting up the game:", e);
		}
        setUp = true;
	}
    
    @Instance
    public static DefaultSettings instance;
    
    @Init
    public static void init(FMLInitializationEvent event) {
    	if (!isServer)
    		TickRegistry.registerTickHandler(new TickHandlerClient(), Side.CLIENT);
    }
    
    @PreInit
    public static void preInit(FMLPreInitializationEvent event) {
		if (isServer)
			return;
		MinecraftForge.EVENT_BUS.register(DefaultSettings.class);
    }
    
    @ServerStarting
    public static void registerCommand(FMLServerStartingEvent event) {
    	if (!isServer)
    		event.registerServerCommand(new CommandDefaultSettings());
    }

    @PostInit
	public static void postInit(FMLPostInitializationEvent event) {
		if (isServer)
			return;

		try {
			FileUtil.restoreKeys();
		} catch (IOException e) {
			DefaultSettings.log.log(Level.SEVERE, "An exception occurred while starting up the game (Post):", e);
		}catch (NullPointerException e) {
			DefaultSettings.log.log(Level.SEVERE, "An exception occurred while starting up the game (Post):", e);
		}
	}
    
    public static UpdateContainer getUpdater() {
		return updateContainer;
	}
    
    public static DefaultSettings getInstance() {
		return instance;
	}
	
}
