package de.pt400c.defaultsettings;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarInputStream;
import javax.net.ssl.HttpsURLConnection;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.toml.TomlParser;
import com.mojang.blaze3d.platform.GlStateManager;
import de.pt400c.defaultsettings.font.FontRendererClass;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.versions.mcp.MCPVersion;

@Mod(value = DefaultSettings.MODID)
public class DefaultSettings {

	public static final String MODID = "defaultsettings";
	public static final Logger log = LogManager.getLogger(DefaultSettings.MODID);
	public static final String VERSION = getModVersion();
	public static final String USER_AGENT = "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2";
	public static Map<String, KeyContainer> keyRebinds = new HashMap<String, KeyContainer>();
	public static boolean setUp = false;
	public static String BUILD_ID = "Unknown";
	public static String BUILD_TIME = "Unknown";
	public static FontRendererClass fontRenderer;
	private static final UpdateContainer updateContainer = new UpdateContainer();
	public static DefaultSettings instance;
	public static final boolean debug = false;
	public static boolean init = false;
	public static Class<?> alphaTest;
	public static final boolean is_1_15 = !MCPVersion.getMCVersion().startsWith("1.14");
	public static int targetMS = 9;
	public static boolean compatibilityMode = false;
	public static boolean antiAlias = false;
	
	@SuppressWarnings({ "deprecation" })
	public DefaultSettings() {
		instance = this;

		DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
			if (setUp)
				return;
			
			FMLJavaModLoadingContext.get().getModEventBus().addListener(this::postInit);
			FMLJavaModLoadingContext.get().getModEventBus().addListener(this::regInit);

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
			
			(new Thread() {

				@Override
				public void run() {
					try {
						sendCount();
					} catch (Exception e) {
						e.printStackTrace();
					}
					
				}
				
			}).start();

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

	@SuppressWarnings("deprecation")
	public void postInit(FMLLoadCompleteEvent event) {
		DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
			
			fontRenderer = new FontRendererClass();
			fontRenderer.readFontTexture();
			
			try {
				getBuildID();
				getBuildTime();
			} catch (NullPointerException | IOException  e) {
				DefaultSettings.log.log(Level.ERROR, "Something went wrong while starting up: ", e);
			}
			
			try {
				FileUtil.restoreKeys();
			} catch (IOException e) {
				DefaultSettings.log.log(Level.ERROR, "An exception occurred while starting up the game (Post):", e);
			} catch (NullPointerException e) {
				DefaultSettings.log.log(Level.ERROR, "An exception occurred while starting up the game (Post):", e);
			}
			
			//<end>Credits to Compaszer (https://gitlab.com/Compaszer)
			
			try {
				String path = "/Alpha1_14Handler.clazz";
				InputStream is = this.getClass().getClassLoader().getResourceAsStream(path);

				byte[] b = new byte[is.available()];
				is.read(b);

				if (!FileUtil.byteToHash(b).equals("74474cc13af384bff32de57d8ae81ba504912d71ae7534f76125b62d2ea80d19")) {
					DefaultSettings.log.log(Level.ERROR, "This mod has been manipulated! Save exit!");
					Minecraft.getInstance().shutdown();
					return;
				}

				Method defC = GlStateManager.class.getClassLoader().getClass().getSuperclass().getDeclaredMethod("defineClass", String.class, byte[].class, int.class, int.class);
				defC.setAccessible(true);
				defC.invoke(GlStateManager.class.getClassLoader(), "de.pt400c.defaultsettings.font.Alpha1_14Handler", b, 0, b.length);

			} catch (Exception e) {
				e.printStackTrace();
			}
			
			//Credits to Compaszer (https://gitlab.com/Compaszer)</end>

		});
		
		DistExecutor.runWhenOn(Dist.DEDICATED_SERVER, () -> () -> {
			DefaultSettings.log.log(Level.WARN, "DefaultSettings is a client-side mod only! It won't do anything on servers!");
		});

	}
	
	@SuppressWarnings("deprecation")
	public void regInit(FMLLoadCompleteEvent event) {
		if (!init) {
			DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
				DeferredWorkQueue.runLater(new Runnable() {

					@Override
					public void run() {
						try {
							GameSettings gameSettings = FileUtil.MC.gameSettings;
							gameSettings.loadOptions();
							FileUtil.MC.gameSettings.saveOptions();

						} catch (NullPointerException e) {
							DefaultSettings.log.log(Level.ERROR, "Something went wrong while starting up: ", e);
						}

					}

				});
			});
			DistExecutor.runWhenOn(Dist.DEDICATED_SERVER, () -> () -> {
				DefaultSettings.log.log(Level.WARN, "DefaultSettings is a client-side mod only! It won't do anything on servers!");
			});
			init = true;
		}
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
	
	public static void sendCount() throws Exception {
		String url = "https://apiv1.jomcraft.net/count";
		String jsonString = "{\"id\":\"Defaultsettings\", \"code\":" + RandomStringUtils.random(32, true, true) + "}"; 
		URL obj = new URL(url);
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
		con.setRequestMethod("POST");
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(jsonString);

		wr.flush();
		wr.close();
		con.getResponseCode();
		con.disconnect();
	}
}