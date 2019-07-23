package de.pt400c.defaultsettings;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.toml.TomlParser;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ExtensionPoint;
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
	private static final UpdateContainer updateContainer = new UpdateContainer();
	
	public static DefaultSettings instance;
	
	public DefaultSettings() {
		instance = this;
		
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::postInit);
		DistExecutor.runWhenOn(Dist.CLIENT, new Supplier<Runnable>() {

			@Override
			public Runnable get() {
				
				//Not yet implemented by Forge
				
				//FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onFingerprintViolation);
				
				if (setUp)
					return null;
				return new Runnable() {
					
					@Override
					public void run() {
						try {
							FileUtil.restoreContents();
						} catch (Exception e) {
							DefaultSettings.log.log(Level.ERROR, "An exception occurred while starting up the game:", e);
						}
						setUp = true;
						MinecraftForge.EVENT_BUS.register(DefaultSettings.class);
						MinecraftForge.EVENT_BUS.register(new EventHandlers());
						ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, ()-> (mc, screen) -> new GuiConfig(screen));
						ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST, ()-> Pair.of(()->"ANY", (remote, isServer)-> true));
					}
				};
			}
		});

		DistExecutor.runWhenOn(Dist.DEDICATED_SERVER, new Supplier<Runnable>() {

			@Override
			public Runnable get() {
				return new Runnable() {
					
					@Override
					public void run() {
						DefaultSettings.log.log(Level.WARN, "+++-----------------------------------------------------------------------+++");
						DefaultSettings.log.log(Level.WARN, " ");
						DefaultSettings.log.log(Level.WARN, " ");
						DefaultSettings.log.log(Level.WARN, " ");
						DefaultSettings.log.log(Level.WARN, "DefaultSettings is a client-side mod only! Please uninstall it on the server!");
						DefaultSettings.log.log(Level.WARN, " ");
						DefaultSettings.log.log(Level.WARN, " ");
						DefaultSettings.log.log(Level.WARN, " ");
						DefaultSettings.log.log(Level.WARN, "+++-----------------------------------------------------------------------+++");
					}
				};
			}
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
		
		DistExecutor.runWhenOn(Dist.CLIENT, new Supplier<Runnable>() {

			@Override
			public Runnable get() {
				return new Runnable() {
					
					@Override
					public void run() {
						try {
							FileUtil.restoreKeys();
						} catch (IOException e) {
							DefaultSettings.log.log(Level.ERROR, "An exception occurred while starting up the game (Post):", e);
						} catch (NullPointerException e) {
							DefaultSettings.log.log(Level.ERROR, "An exception occurred while starting up the game (Post):", e);
						}
						
					}
				};
			}
		});
		
		DistExecutor.runWhenOn(Dist.DEDICATED_SERVER, new Supplier<Runnable>() {

			@Override
			public Runnable get() {
				return new Runnable() {
					
					@Override
					public void run() {
						DefaultSettings.log.log(Level.WARN, "+++-----------------------------------------------------------------------+++");
						DefaultSettings.log.log(Level.WARN, " ");
						DefaultSettings.log.log(Level.WARN, " ");
						DefaultSettings.log.log(Level.WARN, " ");
						DefaultSettings.log.log(Level.WARN, "DefaultSettings is a client-side mod only! Please uninstall it on the server!");
						DefaultSettings.log.log(Level.WARN, " ");
						DefaultSettings.log.log(Level.WARN, " ");
						DefaultSettings.log.log(Level.WARN, " ");
						DefaultSettings.log.log(Level.WARN, "+++-----------------------------------------------------------------------+++");
					}
				};
			}
		});

	}
	
	public static UpdateContainer getUpdater() {
		return updateContainer;
	}
	
	public static DefaultSettings getInstance() {
		return instance;
	}

}
