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
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Pattern;
import javax.xml.bind.DatatypeConverter;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Level;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.LanguageManager;
import net.minecraft.client.resources.ResourcePackInfoClient;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.world.World;
import net.minecraftforge.client.settings.KeyModifier;

public class FileUtil {
	
	public static final Minecraft MC = Minecraft.getInstance();
	public static final File mcDataDir = MC.gameDir;
	public static final boolean devEnv = World.class.getSimpleName().equals("World");
	
	
	
	
	
	//ERROR HERE !!!!!!!!!!
	
	
	
	
	
	
	public static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
	public static MainJSON mainJson;
	public static final FileFilter fileFilterModular = new FileFilter() {

		@Override
		public boolean accept(File file) {
			if (!file.getName().equals("defaultsettings") && !file.getName().equals("defaultsettings.json") && !file.getName().equals("keys.txt") && !file.getName().equals("options.txt") && !file.getName().equals("optionsof.txt") && !file.getName().equals("servers.dat") && (file.getPath().split("config")[1].split(Pattern.quote("\\")).length > 2 ? true : getActives().contains(file.getName())))
				return true;

			return false;
		}
	};
	
	public static final FileFilter fileFilter = new FileFilter() {
		@Override
		public boolean accept(File file) {
			
			//Optifine isn't implemented yet
			
			if (!file.getName().equals("defaultsettings") && !file.getName().equals("defaultsettings.json") && !file.getName().equals("keys.txt") && !file.getName().equals("options.txt") /*&& !file.getName().equals("optionsof.txt")*/ && !file.getName().equals("servers.dat"))
				return true;

			return false;
		}
	};
	
	public static List<String> getActives()  {
		final File main = new File(mcDataDir, "config/defaultsettings.json");
		if(main.exists()) {
			try (Reader reader = new FileReader(main)) {
				mainJson = gson.fromJson(reader, MainJSON.class);
				
			 } catch (Exception e) {
		        e.printStackTrace();
		        return new ArrayList<>();
		     }
			return mainJson.activeConfigs;
			
			
		}else {
			try {
				Date date = new Date();
				SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

				mainJson = new MainJSON().setVersion(DefaultSettings.VERSION).setIdentifier(getIdentifier()).setCreated(formatter.format(date) + " (" + TimeZone.getDefault().getDisplayName() + ")");
				File fileDir = new File(mcDataDir, "config");
				for (File file : fileDir.listFiles(fileFilter)) 
					mainJson.activeConfigs.add(file.getName());
				
				try (FileWriter writer = new FileWriter(main)) {
					gson.toJson(mainJson, writer);
				} catch (IOException e) {
					DefaultSettings.log.log(Level.ERROR, "Exception at processing configs: ", e);
				}
				return mainJson.activeConfigs;
			} catch (UnknownHostException | SocketException | NoSuchAlgorithmException e) {
				DefaultSettings.log.log(Level.ERROR, "Exception at processing configs: ", e);
				return new ArrayList<>();
			}
		}
		
	}

	public static void switchState(Byte state, String query) {
		
		FileFilter ff = null;
		if(!query.isEmpty()) {
			ff = new FileFilter() {

				@Override
				public boolean accept(File file) {

					if (!file.getName().equals("defaultsettings") && !file.getName().equals("defaultsettings.json") && !file.getName().equals("keys.txt") && !file.getName().equals("options.txt") && !file.getName().equals("optionsof.txt") && !file.getName().equals("servers.dat") && file.getName().toLowerCase().startsWith(query.toLowerCase()))
						return true;

					return false;
				}
			};
		}else {
			ff = FileUtil.fileFilter;
		}
		
		final File main = new File(mcDataDir, "config/defaultsettings.json");
		if(main.exists()) {
			try (Reader reader = new FileReader(main)) {
				mainJson = gson.fromJson(reader, MainJSON.class);
				
			 } catch (Exception e) {
				DefaultSettings.log.log(Level.ERROR, "Exception at processing configs: ", e);
		        return;
		        	
		     }
			
			if(state == 1 || state == 2) {
				List<String> list = new ArrayList<String>(mainJson.activeConfigs);
				mainJson.activeConfigs.stream().filter(file -> file.toLowerCase().startsWith(query.toLowerCase()) && new File(mcDataDir + "/config", file).exists()).forEach(file -> list.remove(file));
				mainJson.activeConfigs = list;
				
			}else if (state == 0){
				File fileDir = new File(mcDataDir, "config");
				for(File file : fileDir.listFiles(ff)) 
					mainJson.activeConfigs.add(file.getName());
				
			}
			
			try (FileWriter writer = new FileWriter(main)) {
	            gson.toJson(mainJson, writer);
	        } catch (IOException e) {
	        	DefaultSettings.log.log(Level.ERROR, "Exception at processing configs: ", e);
	        }
			
		}else {
			try {
				Date date = new Date();
				SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
				mainJson = new MainJSON().setVersion(DefaultSettings.VERSION).setIdentifier(getIdentifier()).setCreated(formatter.format(date) + " (" + TimeZone.getDefault().getDisplayName() + ")");
				File fileDir = new File(mcDataDir, "config");
				if (state == 0) 
					for (File file : fileDir.listFiles(ff)) 
						mainJson.activeConfigs.add(file.getName());
					
				try (FileWriter writer = new FileWriter(main)) {
					gson.toJson(mainJson, writer);
				} catch (IOException e) {
					DefaultSettings.log.log(Level.ERROR, "Exception at processing configs: ", e);
				}
			} catch (UnknownHostException | SocketException | NoSuchAlgorithmException e) {
				DefaultSettings.log.log(Level.ERROR, "Exception at processing configs: ", e);
			}
		}
		
	}

	public static void blankJson() throws UnknownHostException, SocketException, NoSuchAlgorithmException {
		final File main = new File(mcDataDir, "config/defaultsettings.json");
		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		mainJson = new MainJSON().setVersion(DefaultSettings.VERSION).setIdentifier(getIdentifier()).setCreated(formatter.format(date) + " (" + TimeZone.getDefault().getDisplayName() + ")");
		File fileDir = new File(mcDataDir, "config");
		for (File file : fileDir.listFiles(fileFilter)) {
			mainJson.activeConfigs.add(file.getName());
		}
		try (FileWriter writer = new FileWriter(main)) {
			gson.toJson(mainJson, writer);
		} catch (IOException e) {
			DefaultSettings.log.log(Level.ERROR, "Exception at processing configs: ", e);
		}
	}
	
	public static void switchActive(String name) {
		final File main = new File(mcDataDir, "config/defaultsettings.json");
		if(main.exists()) {
			try (Reader reader = new FileReader(main)) {
				mainJson = gson.fromJson(reader, MainJSON.class);
				
			 } catch (Exception e) {
				 DefaultSettings.log.log(Level.ERROR, "Exception at processing configs: ", e);
		        return;
		        	
		     }
			if(mainJson.activeConfigs.contains(name))
				mainJson.activeConfigs.remove(name);
			else
				mainJson.activeConfigs.add(name);
			
			try (FileWriter writer = new FileWriter(main)) {
	            gson.toJson(mainJson, writer);
	        } catch (IOException e) {
	        	DefaultSettings.log.log(Level.ERROR, "Exception at processing configs: ", e);
	        }
			
		}else {
			try {
				blankJson();
			}catch(UnknownHostException | SocketException | NoSuchAlgorithmException e) {
				DefaultSettings.log.log(Level.ERROR, "Exception at processing configs: ", e);
			}
		}
		
	}
	
	protected static void getMainJSON() throws UnknownHostException, SocketException, NoSuchAlgorithmException {
		final File main = new File(mcDataDir, "config/defaultsettings.json");
		if(main.exists()) {
			try (Reader reader = new FileReader(main)) {
				mainJson = gson.fromJson(reader, MainJSON.class);
				
			 } catch (Exception e) {
				 DefaultSettings.log.log(Level.ERROR, "Exception at processing configs: ", e);
		        if(e instanceof JsonSyntaxException) {
		        	main.renameTo(new File(mcDataDir, "config/defaultsettings_malformed.json"));
		        	getMainJSON();
		        }
		        return;
		        	
		     }
			final String version = mainJson.getVersion();
			
			if(!DefaultSettings.VERSION.equals(version)) 
				mainJson.setVersion(DefaultSettings.VERSION).setPrevVersion(version);
			
			final String identifier = mainJson.getIdentifier();
			
			if(!getIdentifier().equals(identifier))
				mainJson.setIdentifier(identifier);
			
			try (FileWriter writer = new FileWriter(main)) {
	            gson.toJson(mainJson, writer);
	        } catch (IOException e) {
	        	DefaultSettings.log.log(Level.ERROR, "Exception at processing configs: ", e);
	        }
			
		}else {
			blankJson();
		}
	}


	public static File getMainFolder() {
		final File storeFolder = new File(mcDataDir, "config/defaultsettings");
		storeFolder.mkdir();
		return storeFolder;
	}

	public static void restoreContents() throws NullPointerException, IOException, NoSuchAlgorithmException {
		getMainJSON();
		
		
		final File options = new File(mcDataDir, "options.txt");
		boolean firstBoot = !options.exists();

		if (firstBoot) {
			restoreOptions();
			if(!exportMode())
				moveAllConfigs();

			restoreConfigs();
		}else if(mainJson.getExportMode()){
			restoreConfigs();
			final File main = new File(mcDataDir, "config/defaultsettings.json");
			try (FileWriter writer = new FileWriter(main)) {
	            gson.toJson(mainJson, writer);
	        } catch (IOException e) {
	        	DefaultSettings.log.log(Level.ERROR, "Exception at processing configs: ", e);
	        }
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
			List<ResourcePackInfoClient> repositoryEntries = new ArrayList<ResourcePackInfoClient>();
			for (String resourcePack : gameSettings.resourcePacks) {
				for (ResourcePackInfoClient entry : MC.getResourcePackList().func_198978_b()) {
					if (entry.getName().equals(resourcePack)) {
						repositoryEntries.add(entry);
					}
				}
			}
			MC.getResourcePackList().getPackInfos().addAll(repositoryEntries);
			setField(devEnv ? "currentLanguage" : "field_135048_c", LanguageManager.class, MC.getLanguageManager(), gameSettings.language);

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
	
	public static void moveAllConfigs() throws IOException {
		try {
			
			File fileDir = new File(mcDataDir, "config");
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
		
		final File main = new File(mcDataDir, "config/defaultsettings.json");
		if(main.exists()) {
			try (Reader reader = new FileReader(main)) {
				mainJson = gson.fromJson(reader, MainJSON.class);
				
			 } catch (Exception e) {
				 DefaultSettings.log.log(Level.ERROR, "Exception at processing configs: ", e);
		        return;
		        	
		     }
			mainJson.setExportMode(true);
			
			try (FileWriter writer = new FileWriter(main)) {
	            gson.toJson(mainJson, writer);
	        } catch (IOException e) {
	        	DefaultSettings.log.log(Level.ERROR, "Exception at processing configs: ", e);
	        }
			
		}else {
			try {
				blankJson();
				mainJson.setExportMode(true);
			}catch(UnknownHostException | SocketException | NoSuchAlgorithmException e) {
				DefaultSettings.log.log(Level.ERROR, "Exception at processing configs: ", e);
			}
		}

	}
	
	public static void setExportMode() throws IOException {
		for(File f : new File(mcDataDir, "config").listFiles(fileFilterModular)) {
			if(f.isDirectory())
				FileUtils.deleteDirectory(f);
			else 
				//f.delete() calls updates, not appropriate
				Files.delete(f.toPath());
		}
		
		final File main = new File(mcDataDir, "config/defaultsettings.json");
		if(main.exists()) {
			try (Reader reader = new FileReader(main)) {
				mainJson = gson.fromJson(reader, MainJSON.class);
				
			 } catch (Exception e) {
				 DefaultSettings.log.log(Level.ERROR, "Exception at processing configs: ", e);
		        return;
		        	
		     }
			mainJson.setExportMode(true);
			
			try (FileWriter writer = new FileWriter(main)) {
	            gson.toJson(mainJson, writer);
	        } catch (IOException e) {
	        	DefaultSettings.log.log(Level.ERROR, "Exception at processing configs: ", e);
	        }
			
		}else {
			try {
				blankJson();
				mainJson.setExportMode(true);
			}catch(UnknownHostException | SocketException | NoSuchAlgorithmException e) {
				DefaultSettings.log.log(Level.ERROR, "Exception at processing configs: ", e);
			}
		}

	}
	
	public static String getIdentifier() throws UnknownHostException, SocketException, NoSuchAlgorithmException {
		InetAddress l = InetAddress.getLocalHost();
		NetworkInterface inter = NetworkInterface.getByInetAddress(l);
		byte[] mac = inter.getHardwareAddress();
		if (mac != null) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < mac.length; i++) {
				sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
			}
			String address = sb.toString();
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(address.getBytes());
			byte[] digest = md.digest();
			return DatatypeConverter.printHexBinary(digest).toUpperCase();
		}
		return "0";
	}
	
	public static boolean exportMode() {
		return new File(mcDataDir, "config").listFiles(fileFilterModular).length == 0;
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
			FileUtils.copyDirectory(getMainFolder(), new File(mcDataDir, "config"), fileFilterModular);
		} catch (IOException e) {
			throw e;
		}
		
		final File main = new File(mcDataDir, "config/defaultsettings.json");
		if(main.exists()) {
			try (Reader reader = new FileReader(main)) {
				mainJson = gson.fromJson(reader, MainJSON.class);
				
			 } catch (Exception e) {
				 DefaultSettings.log.log(Level.ERROR, "Exception at processing configs: ", e);
		        return;
		        	
		     }
			mainJson.setExportMode(false);
			
			try (FileWriter writer = new FileWriter(main)) {
	            gson.toJson(mainJson, writer);
	        } catch (IOException e) {
	        	DefaultSettings.log.log(Level.ERROR, "Exception at processing configs: ", e);
	        }
			
		}else {
			try {
				blankJson();
				mainJson.setExportMode(false);
			}catch(UnknownHostException | SocketException | NoSuchAlgorithmException e) {
				DefaultSettings.log.log(Level.ERROR, "Exception at processing configs: ", e);
			}
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
