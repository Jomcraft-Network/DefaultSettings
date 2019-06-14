package de.pt400c.defaultsettings;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Level;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.ClientResourcePackInfo;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.world.World;
import net.minecraftforge.client.settings.KeyModifier;

public class FileUtil {
	
	public static final Minecraft MC = Minecraft.getInstance();
	public static final File mcDataDir = MC.gameDir;
	public static final boolean devEnv = World.class.getSimpleName().equals("World");
	public static final FileFilter fileFilter = new FileFilter() {
		@Override
		public boolean accept(File file) {
			
			//Optifine isn't implemented yet
			
			if (!file.getName().equals("defaultsettings") && !file.getName().equals("keys.txt") && !file.getName().equals("options.txt") &&/* !file.getName().equals("optionsof.txt") && */!file.getName().equals("servers.dat"))
				return true;

			return false;
		}
	};
	
	public static File getMainFolder() {
		final File storeFolder = new File(mcDataDir, "config/defaultsettings");
		storeFolder.mkdir();
		return storeFolder;
	}

	public static void restoreContents() throws NullPointerException, IOException {
		
		final File options = new File(mcDataDir, "options.txt");
		boolean firstBoot = !options.exists();
		if (firstBoot) {
			restoreOptions();
			if(!exportMode())
				moveAllConfigs();

			restoreConfigs();
		}
		
		//Optifine isn't implemented yet
		
		/*
		final File optionsOF = new File(mcDataDir, "optionsof.txt");
		if (!optionsOF.exists()) 
			restoreOptionsOF();*/
		
		final File serversFile = new File(mcDataDir, "servers.dat");
		if (!serversFile.exists()) 
			restoreServers();
		
		if (firstBoot) {
			
			GameSettings gameSettings = MC.gameSettings;
			gameSettings.loadOptions();
			MC.getResourcePackList().reloadPacksFromFinders();
			List<ClientResourcePackInfo> repositoryEntries = new ArrayList<ClientResourcePackInfo>();
			for (String resourcePack : gameSettings.resourcePacks) {
				for (ClientResourcePackInfo entry : MC.getResourcePackList().getAllPacks()) {
					
					if (entry.getName().equals(resourcePack)) {
						repositoryEntries.add(entry);
					}
				}
			}
			MC.getResourcePackList().getEnabledPacks().addAll(repositoryEntries);
		}
		
	}
	
	public static boolean optionsFilesExist() {
		final File optionsFile = new File(getMainFolder(), "options.txt");
		return optionsFile.exists();
	}
	
	public static boolean keysFileExist() {
		final File keysFile = new File(getMainFolder(), "keys.txt");
		return keysFile.exists();
	}
	
	public static boolean serversFileExists() {
		final File serversFile = new File(getMainFolder(), "servers.dat");
		return serversFile.exists();
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
				while ((line = reader.readLine()) != null) {
					writer.print(line + "\n");
				}
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
	
	public static void moveAllConfigs() throws IOException {
		try {
			
			File fileDir = new File(mcDataDir, "config");
			FileUtils.copyDirectory(fileDir, getMainFolder(), fileFilter);
			for (File f : fileDir.listFiles(fileFilter)) {
				
				if(f.isDirectory())
					FileUtils.deleteDirectory(f);
				else
					//f.delete() calls updates, not appropriate
					Files.delete(f.toPath());
			}
		} catch (IOException e) {
			throw e;
		}
	}
	
	public static void setExportMode() throws IOException {
		for(File f : new File(mcDataDir, "config").listFiles(fileFilter)) {
			if(f.isDirectory())
				FileUtils.deleteDirectory(f);
			else
				//f.delete() calls updates, not appropriate
				Files.delete(f.toPath());
		}
	}
	
	public static boolean exportMode() {
		return new File(mcDataDir, "config").listFiles(fileFilter).length == 0;
	}
	
	public static void restoreKeys() throws NullPointerException, IOException, NumberFormatException {
		DefaultSettings.keyRebinds.clear();
		final File keysFile = new File(getMainFolder(), "keys.txt");
		if (keysFile.exists()) {
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new FileReader(keysFile));
				String line;
				while ((line = reader.readLine()) != null) {
					if (line.isEmpty()) {
						continue;
					}
					DefaultSettings.keyRebinds.put(line.split(":")[0], new KeyContainer(InputMappings.getInputByName(line.split(":")[1]), KeyModifier.valueFromString(line.split(":")[2])));
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
				if (DefaultSettings.keyRebinds.containsKey(keyBinding.getKeyDescription())) {
					KeyContainer container = DefaultSettings.keyRebinds.get(keyBinding.getKeyDescription());
					setField("keyModifierDefault", KeyBinding.class, keyBinding, container.modifier);
					setField(devEnv ? "keyCodeDefault" : "field_151472_e", KeyBinding.class, keyBinding, container.input);
					keyBinding.setKeyModifierAndCode(keyBinding.getKeyModifierDefault(), container.input);
				}
			}
			
			KeyBinding.resetKeyBindingArrayAndHash();
		}
	}
	
	//Optifine isn't implemented yet
	
	/*
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
	*/
	
	public static void restoreConfigs() throws IOException {
		try {
			FileUtils.copyDirectory(getMainFolder(), new File(mcDataDir, "config"), fileFilter);
		} catch (IOException e) {
			throw e;
		}
	}

	public static void restoreServers() throws IOException {
		try {
			FileUtils.copyFile(new File(getMainFolder(), "servers.dat"), new File(mcDataDir, "servers.dat"));
		} catch (IOException e) {
			DefaultSettings.log.log(Level.ERROR, "Couldn't restore the server config: ", e);
		}
	}
	
	public static void saveKeys() throws IOException, NullPointerException {
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(new FileWriter(new File(getMainFolder(), "keys.txt")));
			for (KeyBinding keyBinding : MC.gameSettings.keyBindings) {
				writer.print(keyBinding.getKeyDescription() + ":" + keyBinding.getKey().toString() + ":" + keyBinding.getKeyModifier().name() + "\n");
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
				if (line.startsWith("key_")) {
					continue;
				}
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
		
		//Optifine isn't implemented yet	

		//if (!FMLClientHandler.instance().hasOptifine()) {
			return;
	//	}
/*
		try {
			writer = new PrintWriter(new FileWriter(new File(getMainFolder(), "optionsof.txt")));
			reader = new BufferedReader(new FileReader(new File(mcDataDir, "optionsof.txt")));
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
		}	*/
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
