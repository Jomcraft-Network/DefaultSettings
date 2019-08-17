package de.pt400c.defaultsettings;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;
import java.util.regex.Pattern;
import javax.xml.bind.DatatypeConverter;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Level;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraft.client.resources.ResourcePackRepository.Entry;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.fml.client.FMLClientHandler;

public class FileUtil {
	
	public static final Minecraft MC = Minecraft.getMinecraft();
	public static final File mcDataDir = MC.mcDataDir;
	public static final boolean isDev = (Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment"); 
	public static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
	public static MainJSON mainJson;
	public static PersistentJSON persistentJson;
	public static final String persistentLocation = "config/ds_dont_export.json";
	public static final String mainLocation = "config/defaultsettings.json";
	public HashMap<String, String> check = new HashMap<String, String>();
	public static final FileFilter fileFilterModular = new FileFilter() {

		@Override
		public boolean accept(File file) {
			if (!file.getName().equals("defaultsettings") && !file.getName().equals("defaultsettings.json") && !file.getName().equals("ds_dont_export.json") && !file.getName().equals("keys.txt") && !file.getName().equals("options.txt") && !file.getName().equals("optionsof.txt") && !file.getName().equals("servers.dat") && (file.getPath().split("config")[1].split(Pattern.quote("\\")).length > 2 ? true : getActives().contains(file.getName())))
				return true;

			return false;
		}
	};
	
	public static final FileFilter fileFilter = new FileFilter() {

		@Override
		public boolean accept(File file) {

			if (!file.getName().equals("defaultsettings") && !file.getName().equals("defaultsettings.json") && !file.getName().equals("ds_dont_export.json") && !file.getName().equals("keys.txt") && !file.getName().equals("options.txt") && !file.getName().equals("optionsof.txt") && !file.getName().equals("servers.dat"))
				return true;

			return false;
		}
	};
	
	/**
	 * Returning DefaultSettings's main data storage
	 * @category Main storage
	 */
	public static File getMainFolder() {
		final File storeFolder = new File(mcDataDir, "config/defaultsettings");
		storeFolder.mkdir();
		return storeFolder;
	}
	
	public static HashMap<String, String> getOverrides()  {
		return getMainJSON().overrides;
	}
	
	public static List<String> getActives()  {
		return getMainJSON().activeConfigs;
	}
	
	public static void switchState(Byte state, String query) {
		
		FileFilter ff = null;
		if(!query.isEmpty()) {
			ff = new FileFilter() {

				@Override
				public boolean accept(File file) {

					if (!file.getName().equals("defaultsettings") && !file.getName().equals("defaultsettings.json") && !file.getName().equals("ds_dont_export.json") && !file.getName().equals("keys.txt") && !file.getName().equals("options.txt") && !file.getName().equals("optionsof.txt") && !file.getName().equals("servers.dat") && file.getName().toLowerCase().startsWith(query.toLowerCase()))
						return true;

					return false;
				}
			};
		}else {
			ff = FileUtil.fileFilter;
		}
		
		final File main = new File(mcDataDir, mainLocation);
			
		if (state == 1 || state == 2) {
			List<String> list = new ArrayList<String>(getMainJSON().activeConfigs);
			mainJson.activeConfigs.stream().filter(file -> file.toLowerCase().startsWith(query.toLowerCase()) && new File(mcDataDir + "/config", file).exists()).forEach(file -> removeFromLists(list, file));
			mainJson.activeConfigs = list;

		} else if (state == 0) {
			File fileDir = new File(mcDataDir, "config");
			for (File file : fileDir.listFiles(ff))
				getMainJSON().activeConfigs.add(file.getName());

		}

		mainJson.save(main);
	}
	
	private static void removeFromLists(List<String> list, String file) {
		list.remove(file);
		mainJson.overrides.remove(file);
	}
	
	public static PersistentJSON getPersistent() {
		if(persistentJson != null)
			return persistentJson;
		
		final File main = new File(mcDataDir, persistentLocation);
		
		if(main.exists()) {
			
			try (Reader reader = new FileReader(main)) {
				persistentJson = gson.fromJson(reader, PersistentJSON.class);
				
			 } catch (Exception e) {
				 
			 }
			
		}
		
		return persistentJson;
	}
	
	public static void setOverride(String name, boolean actual) {
		final File main = new File(mcDataDir, mainLocation);
		String random = UUID.randomUUID().toString();
		mainJson = getMainJSON();
		if (!actual) {
			mainJson.overrides.remove(name);
		} else if (!mainJson.overrides.containsKey(name))
			mainJson.overrides.put(name, random);

		mainJson.save(main);
	}
	
	public static void setActive(String name, boolean active) {
		final File main = new File(mcDataDir, mainLocation);
		mainJson = getMainJSON();
		if (!active) {
			mainJson.activeConfigs.remove(name);
			mainJson.overrides.remove(name);
		} else if (!mainJson.activeConfigs.contains(name))
			mainJson.activeConfigs.add(name);

		mainJson.save(main);
	}
	
	public static void switchActive(String name) {
		final File main = new File(mcDataDir, mainLocation);
		if (getMainJSON().activeConfigs.contains(name)) {
			mainJson.activeConfigs.remove(name);
			mainJson.overrides.remove(name);
		} else
			mainJson.activeConfigs.add(name);

		mainJson.save(main);
	}
	
	public static void initialSetupJSON() throws UnknownHostException, SocketException, NoSuchAlgorithmException {
		final File main = new File(mcDataDir, mainLocation);
		final String version = getMainJSON().getVersion();
		
		if(!DefaultSettings.VERSION.equals(version)) 
			mainJson.setVersion(DefaultSettings.VERSION).setPrevVersion(version);
		
		final String identifier = mainJson.getIdentifier();
		
		if(!getIdentifier().equals(identifier))
			mainJson.setIdentifier(getIdentifier());
		
		File persFile = new File(mcDataDir, persistentLocation);
		if(persFile.exists()) {
			getPersistent().check.forEach((k, v) -> mainJson.check.put(k, v));
			persFile.delete();
		}
		
		final String created_for = mainJson.created_for;
		
		if(!getUUID(DefaultSettings.UUID).equals(created_for)) {
			mainJson.created_for = getUUID(DefaultSettings.UUID);
			mainJson.check.clear();
			
		}
		
		mainJson.save(main);
	}
	
	/**
	 * Generate or get DefaultSettings' main config JSON object
	 * @category Main storage
	 */
	public static MainJSON getMainJSON() {

		if(mainJson != null)
			return mainJson;
		
		final File main = new File(mcDataDir, mainLocation);
		
		if(main.exists()) {
			try (Reader reader = new FileReader(main)) {
				mainJson = gson.fromJson(reader, MainJSON.class);
				
			 } catch (Exception e) {
				DefaultSettings.log.log(Level.ERROR, "Exception at processing configs: ", e);
		        if(e instanceof JsonSyntaxException) {
		        	main.renameTo(new File(mcDataDir, "config/defaultsettings_malformed.json"));
		        	getMainJSON();
		        }
		        	
		     }
			
		}else {
			Date date = new Date();
			SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
			String identifier = "<UNKNOWN>";
			try {
				identifier = getIdentifier();
			} catch (UnknownHostException | SocketException | NoSuchAlgorithmException e) {
				DefaultSettings.log.log(Level.ERROR, "Exception at processing configs: ", e);
			}
			
			mainJson = new MainJSON().setVersion(DefaultSettings.VERSION).setIdentifier(identifier).setCreated(formatter.format(date) + " (" + TimeZone.getDefault().getDisplayName() + ")");
			
			try {
				mainJson.created_for = getUUID(DefaultSettings.UUID);
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
			mainJson.initPopup = true;
			File fileDir = new File(mcDataDir, "config");
			for (File file : fileDir.listFiles(fileFilter)) 
				mainJson.activeConfigs.add(file.getName());
			
			mainJson.save(main);
		}
		return mainJson;
	}

	public static void setPopup(boolean active) {
		getMainJSON().initPopup = active;
		mainJson.save(new File(mainLocation));
	}
	
	public static void restoreContents() throws NullPointerException, IOException, NoSuchAlgorithmException {
		
		initialSetupJSON();
		
		final File options = new File(mcDataDir, "options.txt");
		boolean firstBoot = !options.exists();
		if (firstBoot) {
			restoreOptions();
			if(!exportMode())
				moveAllConfigs(false);

			restoreConfigs();
		}else if(mainJson.getExportMode()){
			restoreConfigs();
			final File main = new File(mcDataDir, mainLocation);
			mainJson.save(main);
		}else {
			for(String name : getOverrides().keySet()) 
				if(getActives().contains(name) && (!getMainJSON().check.containsKey(name) || !getMainJSON().check.get(name).equals(mainJson.overrides.get(name)))) 
					restoreSingleConfig(name);
			
			final File main = new File(mcDataDir, mainLocation);
			getMainJSON().setExportMode(false);
			mainJson.save(main);
			
		}
		final File optionsOF = new File(mcDataDir, "optionsof.txt");
		if (!optionsOF.exists()) 
			restoreOptionsOF();
		
		final File serversFile = new File(mcDataDir, "servers.dat");
		if (!serversFile.exists()) 
			restoreServers();
		
		if (firstBoot) {
			
			GameSettings gameSettings = MC.gameSettings;
			gameSettings.loadOptions();
			
			ResourcePackRepository resourceRepository = MC.getResourcePackRepository();
			resourceRepository.updateRepositoryEntriesAll();
			List<Entry> repositoryEntries = new ArrayList<Entry>();
			
			for (String resourcePack : gameSettings.resourcePacks) 
				for (Entry entry : resourceRepository.getRepositoryEntriesAll()) 
					if (entry.getResourcePackName().equals(resourcePack)) 
						repositoryEntries.add(entry);

			resourceRepository.setRepositories(repositoryEntries);

			MC.getLanguageManager().currentLanguage = gameSettings.language;

		}
		
		if(!options.exists())
			options.createNewFile();
			
	}
	
	public static boolean optionsFilesExist() {
		final File optionsFile = new File(getMainFolder(), "options.txt");
		final File optionsofFile = new File(getMainFolder(), "optionsof.txt");
		return optionsFile.exists() || optionsofFile.exists();
	}
	
	public static boolean keysFileExist() {
		final File keysFile = new File(getMainFolder(), "keys.txt");
		return keysFile.exists();
	}
	
	public static void restoreOptions() throws NullPointerException, IOException {
		final File optionsFile = new File(getMainFolder(), "options.txt");
		if (optionsFile.exists()) {
			BufferedReader reader = null;
			PrintWriter writer = null;
			try {
				reader = new BufferedReader(new FileReader(optionsFile));	
				writer = new PrintWriter(new FileWriter(new File(mcDataDir, "options.txt")));
				String line;
				while ((line = reader.readLine()) != null) 
					writer.print(line + "\n");
				
			} catch (IOException e) {
				throw e;
			} finally {
				try {
					reader.close();
					writer.close();
				} catch (IOException e) {
					throw e;
				} catch (NullPointerException e) {
					throw e;
				}
			}
		}
	}
	
	public static void restoreKeys() throws NullPointerException, IOException, NumberFormatException {
		if(DefaultSettings.mcVersion.startsWith("1.8"))
			DefaultSettings.keyRebinds_18.clear();
		else
			DefaultSettings.keyRebinds_19.clear();
		
		final File keysFile = new File(getMainFolder(), "keys.txt");
		if (keysFile.exists()) {
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new FileReader(keysFile));
				String line;
				while ((line = reader.readLine()) != null) {
					if (line.isEmpty()) 
						continue;

					if(DefaultSettings.mcVersion.startsWith("1.8"))
						DefaultSettings.keyRebinds_18.put(line.split(":")[0], Integer.parseInt(line.split(":")[1]));
					else
						DefaultSettings.keyRebinds_19.put(line.split(":")[0], new KeyContainer(Integer.parseInt(line.split(":")[1]), line.split(":").length > 2 ? KeyModifier.valueFromString(line.split(":")[2]) : KeyModifier.NONE));
				}
			} catch (IOException e) {
				throw e;
				
			} catch (NullPointerException e) {
				throw e;
			} finally {
				try {
					reader.close();
				} catch (IOException e) {
					throw e;
				} catch (NullPointerException e) {
					throw e;
				}
			}

			for (KeyBinding keyBinding : MC.gameSettings.keyBindings) {
				if(DefaultSettings.mcVersion.startsWith("1.8")) {
					if (DefaultSettings.keyRebinds_18.containsKey(keyBinding.getKeyDescription())) 
						keyBinding.keyCodeDefault = DefaultSettings.keyRebinds_18.get(keyBinding.getKeyDescription());
					
				}else {
					
					if (DefaultSettings.keyRebinds_19.containsKey(keyBinding.getKeyDescription())) {
						KeyContainer container = DefaultSettings.keyRebinds_19.get(keyBinding.getKeyDescription());
						setField("keyModifierDefault", KeyBinding.class, keyBinding, container.modifier);
						keyBinding.keyCodeDefault = container.input;
						keyBinding.setKeyModifierAndCode(keyBinding.getKeyModifierDefault(), keyBinding.keyCodeDefault);
					}
				}
			}
			
			KeyBinding.resetKeyBindingArrayAndHash();
		}
	}
	
	public static void restoreOptionsOF() throws IOException {
		final File optionsOFFile = new File(getMainFolder(), "optionsof.txt");
		if (optionsOFFile.exists()) {
			BufferedReader reader = null;
			PrintWriter writer = null;
			try {
				reader = new BufferedReader(new FileReader(optionsOFFile));
				writer = new PrintWriter(new FileWriter(new File(mcDataDir, "optionsof.txt")));
				String line;
				while ((line = reader.readLine()) != null) {
					writer.print(line + "\n");
				}
			} catch (IOException e) {
				throw e;
			} catch (NullPointerException e) {
				throw e;
			} finally {
				try {
					reader.close();
					writer.close();
				} catch (IOException e) {
					throw e;
				} catch (NullPointerException e) {
					throw e;
				}
			}
		}
	}
	
	public static void restoreSingleConfig(String name) throws IOException {
		try {
			File file = new File(getMainFolder(), name);
			if (file.exists()) {
				if (file.isDirectory())
					FileUtils.copyDirectory(file, new File(mcDataDir, "config/" + name));

				else
					FileUtils.copyFile(file, new File(mcDataDir, "config/" + name));
			}else {
				DefaultSettings.log.log(Level.WARN, "Couldn't restore a config file as it's missing: " + name);
				return;
			}
			
			String random = getOverrides().get(name);
			
			getMainJSON().check.put(name, random);
			mainJson.save(new File(mcDataDir, mainLocation));

		} catch (IOException e) {
			throw e;
		}
	}

	public static void restoreConfigs() throws IOException {
		try {	
			FileUtils.copyDirectory(getMainFolder(), new File(mcDataDir, "config"), fileFilterModular);
		} catch (IOException e) {
			throw e;
		}
		
		final File main = new File(mcDataDir, mainLocation);
		getMainJSON().setExportMode(false);

		mainJson.save(main);
	}

	public static void moveAllConfigs(boolean deletePersistent) throws IOException {
		try {
			
			File fileDir = new File(mcDataDir, "config");
			if(deletePersistent)
				new File(mcDataDir, persistentLocation).delete();
			
			FileUtils.copyDirectory(fileDir, getMainFolder(), fileFilterModular);
			for (File f : fileDir.listFiles(fileFilterModular)) {
				
				if(f.isDirectory())
					FileUtils.deleteDirectory(f);
				else
					//f.delete() calls updates, not appropriate
					Files.delete(f.toPath());
			}
			
		} catch (IOException e) {
			throw e;
		}
		final File main = new File(mcDataDir, mainLocation);
		
		getMainJSON().setExportMode(true);
		mainJson.save(main);
	}
	
	public static void setExportMode() throws IOException {
		for(File f : new File(mcDataDir, "config").listFiles(fileFilterModular)) {
			if(f.isDirectory())
				FileUtils.deleteDirectory(f);
			else
				//f.delete() calls updates, not appropriate
				Files.delete(f.toPath());

		}
		
		final File main = new File(mcDataDir, mainLocation);
		
		getMainJSON().setExportMode(true);
		mainJson.save(main);
	}
	
	public static boolean exportMode() {
		return new File(mcDataDir, "config").listFiles(fileFilterModular).length == 0;
	}
	
	public static void restoreServers() throws IOException {
		try {
			File file = new File(getMainFolder(), "servers.dat");
			if(file.exists())
				FileUtils.copyFile(file, new File(mcDataDir, "servers.dat"));
			else
				DefaultSettings.log.log(Level.WARN, "Couldn't restore the server config as it's not included");
		} catch (IOException e) {
			DefaultSettings.log.log(Level.ERROR, "Couldn't restore the server config: ", e);
		}
	}
	
	public static void saveKeys() throws IOException, NullPointerException {
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(new FileWriter(new File(getMainFolder(), "keys.txt")));
			for (KeyBinding keyBinding : MC.gameSettings.keyBindings) {
				if(DefaultSettings.mcVersion.startsWith("1.8"))
					writer.print(keyBinding.getKeyDescription() + ":" + keyBinding.getKeyCode() + "\n");
				else
					writer.print(keyBinding.getKeyDescription() + ":" + keyBinding.getKeyCode() + ":" + keyBinding.getKeyModifier().name() + "\n");
			}
		} catch (IOException e) {
			throw e;
		} catch (NullPointerException e) {
			throw e;
		} finally {
			writer.close();
		}
	}

	public static void saveOptions() throws NullPointerException, IOException {
		MC.gameSettings.saveOptions();
		PrintWriter writer = null;
		BufferedReader reader = null;
		try {
			writer = new PrintWriter(new FileWriter(new File(getMainFolder(), "options.txt")));
			reader = new BufferedReader(new FileReader(new File(mcDataDir, "options.txt")));
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.startsWith("key_"))
					continue;
				
				writer.print(line + "\n");
			}
		} catch (IOException e) {
			throw e;
		} catch (NullPointerException e) {
			throw e;
		} finally {
			try {
				reader.close();
				writer.close();
			} catch (IOException e) {
				throw e;
			} catch (NullPointerException e) {
				throw e;
			}
		}

		if (!FMLClientHandler.instance().hasOptifine()) 
			return;

		try {
			writer = new PrintWriter(new FileWriter(new File(getMainFolder(), "optionsof.txt")));
			reader = new BufferedReader(new FileReader(new File(mcDataDir, "optionsof.txt")));
			String line;
			while ((line = reader.readLine()) != null)
				writer.print(line + "\n");
			
		} catch (IOException e) {
			throw e;
		} catch (NullPointerException e) {
			throw e;
		} finally {
			try {
				reader.close();
				writer.close();
			} catch (IOException e) {
				throw e;
			} catch (NullPointerException e) {
				throw e;
			}
		}	
	}

	public static void saveServers() throws IOException {
		final File serversFile = new File(mcDataDir, "servers.dat");
		if (serversFile.exists()) {
			try {
				FileUtils.copyFile(serversFile, new File(getMainFolder(), "servers.dat"));
			} catch (IOException e) {
				throw e;
			}
		}
	}
	
	public static boolean serversFileExists() {
		final File serversFile = new File(getMainFolder(), "servers.dat");
		return serversFile.exists();
	}
	
	public static String getUUID(String uuid) throws NoSuchAlgorithmException {

		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(uuid.getBytes());
		byte[] digest = md.digest();
		return DatatypeConverter.printHexBinary(digest).toUpperCase();

	}
	
	public static String getIdentifier() throws UnknownHostException, SocketException, NoSuchAlgorithmException {
		InetAddress l = InetAddress.getLocalHost();
		NetworkInterface inter = NetworkInterface.getByInetAddress(l);
		byte[] mac = inter.getHardwareAddress();
		if (mac != null) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < mac.length; i++) 
				sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
			
			String address = sb.toString();
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(address.getBytes());
			byte[] digest = md.digest();
			return DatatypeConverter.printHexBinary(digest).toUpperCase();
		}
		return "0";
	}
	
	@SuppressWarnings("rawtypes")
	private static void setField(String name, Class clazz, Object obj, Object value) {
		try {
			Field field = clazz.getDeclaredField(name);
			field.setAccessible(true);
			field.set(obj, value);
		} catch (IllegalAccessException e) {
			DefaultSettings.log.log(Level.ERROR, "Reflection exception: ", e);
		} catch (IllegalArgumentException e) {
			DefaultSettings.log.log(Level.ERROR, "Reflection exception: ", e);
		} catch (NoSuchFieldException e) {
			DefaultSettings.log.log(Level.ERROR, "Reflection exception: ", e);
		} catch (SecurityException e) {
			DefaultSettings.log.log(Level.ERROR, "Reflection exception: ", e);
		}
	}
	

}
