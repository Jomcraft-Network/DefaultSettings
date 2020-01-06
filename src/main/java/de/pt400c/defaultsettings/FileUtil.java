package de.pt400c.defaultsettings;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.lang.reflect.Field;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.xml.bind.DatatypeConverter;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import cpw.mods.fml.client.FMLClientHandler;
import de.pt400c.defaultsettings.gui.MenuArea;
import de.pt400c.defaultsettings.gui.ScrollableSegment;
import de.pt400c.defaultsettings.gui.Segment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.LanguageManager;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraft.client.resources.ResourcePackRepositoryEntry;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.launchwrapper.Launch;

public class FileUtil {
	
	public static final Minecraft MC = Minecraft.getMinecraft();
	public static final File mcDataDir = MC.mcDataDir;
	public static final boolean isDev = (Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment"); 
	public static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
	public static MainJSON mainJson;
	public static PrivateJSON privateJson;
	public static final String privateLocation = "ds_private_storage.json";
	public static final String mainLocation = "config/defaultsettings.json";
	public static final boolean devEnv = (Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");
	public static Thread registryChecker;
	public volatile static boolean options_exists = false;
	public volatile static boolean keys_exists = false;
	public volatile static boolean servers_exists = false;
	public static String activeProfile = "Default";
	public static boolean otherCreator = false;
	public static final FileFilter fileFilterModular = new FileFilter() {

		@Override
		public boolean accept(File file) {
			if (!file.getName().equals("defaultsettings") && !file.getName().equals("defaultsettings.json") && !file.getName().equals("ds_dont_export.json") && !file.getName().equals("keys.txt") && !file.getName().equals("options.txt") && !file.getName().equals("optionsof.txt") && !file.getName().equals("servers.dat") && (file.getPath().split("config")[1].split(Pattern.quote("\\")).length > 2 ? true : getActives().contains(file.getName())))
				return true;

			return false;
		}
	};
	
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
	
	public static final FileFilter fileFilterAnti = new FileFilter() {

		@Override
		public boolean accept(File file) {
			if (!file.getName().equals("defaultsettings") && !file.getName().equals("defaultsettings.json") && !file.getName().equals("ds_dont_export.json") && !file.getName().equals("keys.txt") && !file.getName().equals("options.txt") && !file.getName().equals("optionsof.txt") && !file.getName().equals("servers.dat") && !getActives().contains(file.getName()))
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
	
	public static List<String> getActives()  {
		return getMainJSON().activeConfigs;
	}
	
	public static void switchState(Byte state, final String query) {
		
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
			for(String file : mainJson.activeConfigs) 
				if(file.toLowerCase().startsWith(query.toLowerCase()) && new File(mcDataDir + "/config", file).exists()) 
					removeFromLists(list, file);
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
	}
	
	public static void setActive(String name, boolean active) {
		final File main = new File(mcDataDir, mainLocation);
		mainJson = getMainJSON();
		if (!active) 
			mainJson.activeConfigs.remove(name);
		else if (!mainJson.activeConfigs.contains(name))
			mainJson.activeConfigs.add(name);

		mainJson.save(main);
	}
	
	public static void switchActive(String name) {
		final File main = new File(mcDataDir, mainLocation);
		if (getMainJSON().activeConfigs.contains(name)) 
			mainJson.activeConfigs.remove(name);
		else
			mainJson.activeConfigs.add(name);

		mainJson.save(main);
	}
	
	public static void initialSetupJSON() throws UnknownHostException, SocketException, NoSuchAlgorithmException {
		
		getPrivateJSON();
		final File main = new File(mcDataDir, mainLocation);
		final String version = getMainJSON().getVersion();
		
		if(!DefaultSettings.VERSION.equals(version)) 
			mainJson.setVersion(DefaultSettings.VERSION).setPrevVersion(version);

		if(!privateJson.privateIdentifier.equals(mainJson.generatedBy) && !mainJson.generatedBy.equals("<default>")) {
			otherCreator = true;
		}
		
		mainJson.save(main);
	}
	
	public static PrivateJSON getPrivateJSON() {

		if(privateJson != null)
			return privateJson;
		
		final File main = new File(mcDataDir, privateLocation);
		
		if(main.exists()) {
			try (Reader reader = new FileReader(main)) {
				privateJson = gson.fromJson(reader, PrivateJSON.class);
				
				if(privateJson.privateIdentifier == null || privateJson.privateIdentifier.isEmpty())
					privateJson.privateIdentifier = UUID.randomUUID().toString();
				
				privateJson.save(main);
				
			 } catch (Exception e) {
				DefaultSettings.log.log(Level.SEVERE, "Exception at processing startup: ", e);  	
		     }
			
		}else {
			
			privateJson = new PrivateJSON();
			privateJson.privateIdentifier = UUID.randomUUID().toString();
			privateJson.save(main);
		}
		return privateJson;
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
				DefaultSettings.log.log(Level.SEVERE, "Exception at processing configs: ", e);
		        if(e instanceof JsonSyntaxException) {
		        	main.renameTo(new File(mcDataDir, "config/defaultsettings_malformed.json"));
		        	getMainJSON();
		        }
		        	
		     }
			
		}else {
			Date date = new Date();
			SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
			
			mainJson = new MainJSON().setVersion(DefaultSettings.VERSION)/*.setIdentifier(identifier)*/.setCreated(formatter.format(date));
			
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

		initialToDefaultProfile();
		
		String firstFolder = "<ERROR>";
		
		for(File file : getMainFolder().listFiles()) {
			if(file.isDirectory()) {
				firstFolder = file.getName();
				break;
			}
		}
		
		if(!new File(getMainFolder(), mainJson.mainProfile).exists()) 
			mainJson.mainProfile = firstFolder;
		
		if(getPrivateJSON().targetProfile.equals("!NEW!"))
			privateJson.targetProfile = mainJson.mainProfile;
		
		if(privateJson.currentProfile.equals("!NEW!"))
			privateJson.currentProfile = mainJson.mainProfile;
		
		if(!new File(getMainFolder(), privateJson.targetProfile).exists()) 
			privateJson.targetProfile = firstFolder;
		
		if(!new File(getMainFolder(), privateJson.currentProfile).exists()) 
			privateJson.currentProfile = firstFolder;

		
		File main = new File(mcDataDir, privateLocation);
		
		privateJson.save(main);
		
		main = new File(mcDataDir, mainLocation);
		
		mainJson.save(main);
		
		boolean switchProf = switchProfile();
		
		activeProfile = privateJson.currentProfile;
		
		final File options = new File(mcDataDir, "options.txt");
		boolean firstBoot = !options.exists();
		if (firstBoot) {
			restoreOptions();
			if(!exportMode())
				moveAllConfigs();

			restoreConfigs();
		}else if((mainJson.getExportMode() && !otherCreator) || switchProf){
			restoreConfigs();

			getMainJSON().setExportMode(false);

			main = new File(mcDataDir, mainLocation);
			mainJson.save(main);
		}else {

			copyAndHash();
			
			main = new File(mcDataDir, mainLocation);
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
			ResourcePackRepositoryEntry resEntry = null;
			String resourcePack = gameSettings.skin;
			for (Object entryObj : resourceRepository.getRepositoryEntriesAll()) {
				ResourcePackRepositoryEntry entry = (ResourcePackRepositoryEntry) entryObj;
				if (entry.getResourcePackName().equals(resourcePack)) {
					resEntry = entry;
					break;
				}
			}

			resourceRepository.setRepositoryEntries(resEntry);
			setField(devEnv ? "currentLanguage" : "field_135048_c", LanguageManager.class, MC.getLanguageManager(), gameSettings.language);

		}
		
		if(!options.exists())
			options.createNewFile();
			
	}
	
	private static boolean switchProfile() throws IOException {
		if(!getPrivateJSON().currentProfile.equals(privateJson.targetProfile)) {

			Date date = new Date();
			SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH.mm.ss");
			
			String profileName = formatter.format(date);

			File fileDir = new File(FileUtil.getMainFolder(), profileName);
			fileDir.mkdir();

			activeProfile = profileName;

			FileUtil.moveAllConfigs();
			FileUtil.checkMD5();

			Path pf = new File(FileUtil.getMainFolder(), profileName + ".zip").toPath();
			Files.createFile(pf);

	        try {
				zipFolder(Paths.get(fileDir.getPath()), pf);
			} catch (Exception e2) {
				e2.printStackTrace();
			}
	        
			try {
				FileUtils.deleteDirectory(fileDir);
			} catch (IOException e) {
				try {
					FileUtils.forceDeleteOnExit(fileDir);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				e.printStackTrace();
			}
			
			activeProfile = privateJson.targetProfile;
			privateJson.currentProfile = activeProfile;
			final File main = new File(mcDataDir, privateLocation);
			privateJson.save(main);
			
			return true;
			
		}
		return false;
		
	}
	
	private static void zipFolder(final Path sourceFolderPath, Path zipPath) throws Exception {
        final ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipPath.toFile()));
        Files.walkFileTree(sourceFolderPath, new SimpleFileVisitor<Path>() {
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                zos.putNextEntry(new ZipEntry(sourceFolderPath.relativize(file).toString()));
                Files.copy(file, zos);
                zos.closeEntry();
                return FileVisitResult.CONTINUE;
            }
        });
        zos.close();
    }

	private static void initialToDefaultProfile() {
		if(getMainJSON().mainProfile.equals("!NEW!")) {
			
			new File(getMainFolder(), "Default").mkdir();
			
			FileFilter ffm = new FileFilter() {

				@Override
				public boolean accept(File file) {
					if (!file.getName().equals("Default"))
						return true;

					return false;
				}
			};
			
			try {
				FileUtils.copyDirectory(getMainFolder(), new File(getMainFolder(), "Default"), ffm);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			for (File f : getMainFolder().listFiles(ffm)) {
				try {
				if(f.isDirectory())
					FileUtils.deleteDirectory(f);
				else
					Files.delete(f.toPath());
				}catch(IOException e) {
					DefaultSettings.log.log(Level.SEVERE, "Couldn't move config files: ", e);
				}
			}
			

			privateJson.targetProfile = "Default";
			File main = new File(mcDataDir, privateLocation);

			privateJson.save(main);
			
			getMainJSON().mainProfile = "Default";
			main = new File(mcDataDir, mainLocation);

			mainJson.save(main);
			
		}
		
	}

	private static void copyAndHash() {
		for(String name : mainJson.activeConfigs) {
			File file = new File(mcDataDir, "config");
			File fileInner = new File(file, name);
			try {
				
				File locInDir = new File(getMainFolder(), activeProfile + "/" + name);

				if(locInDir.isDirectory()) {
					
					Collection<File> files = FileUtils.listFilesAndDirs(locInDir, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
					for(File filePers : files) {
						
						if(filePers.isDirectory())
							continue;

						String loc = filePers.getPath().split("defaultsettings")[1].substring(1).split(activeProfile)[1].substring(1);
						
						File configLoc = new File(file, loc);
						
						File newF = new File(getMainFolder(), activeProfile + "/" + loc);
						if(!getPrivateJSON().currentHash.containsKey(activeProfile + "/" + loc) || !getPrivateJSON().currentHash.get(activeProfile + "/" + loc).equals(mainJson.hashes.get(activeProfile + "/" + loc)) && newF.exists()) {
							FileUtils.copyFile(newF, configLoc);
							
							getPrivateJSON().currentHash.put(activeProfile + "/" + loc, mainJson.hashes.get(activeProfile + "/" + loc));
							
							final File main2 = new File(mcDataDir, privateLocation);

							privateJson.save(main2);
						}
					}
					
				}else {
	
					if(!getPrivateJSON().currentHash.containsKey(activeProfile + "/" + name) || !getPrivateJSON().currentHash.get(activeProfile + "/" + name).equals(mainJson.hashes.get(activeProfile + "/" + name)) && locInDir.exists()) {

						FileUtils.copyFile(locInDir, fileInner);
					
						getPrivateJSON().currentHash.put(activeProfile + "/" + name, mainJson.hashes.get(activeProfile + "/" + name));
						
						final File main2 = new File(mcDataDir, privateLocation);

						privateJson.save(main2);
					}
				}
			}catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}

	public static boolean optionsFilesExist() {
		final File optionsFile = new File(getMainFolder(), activeProfile + "/options.txt");
		final File optionsofFile = new File(getMainFolder(), activeProfile + "/optionsof.txt");
		return optionsFile.exists() || optionsofFile.exists();
	}
	
	public static boolean keysFileExist() {
		final File keysFile = new File(getMainFolder(), activeProfile + "/keys.txt");
		return keysFile.exists();
	}
	
	public static void deleteKeys() {
		new File(getMainFolder(), activeProfile + "/keys.txt").delete();
		FileUtil.keys_exists = false;
	}
	
	public static void deleteServers() {
		new File(getMainFolder(), activeProfile + "/servers.dat").delete();
		FileUtil.servers_exists = false;
	}
	
	public static void deleteOptions() {
		new File(getMainFolder(), activeProfile + "/options.txt").delete();
		new File(getMainFolder(), activeProfile + "/optionsof.txt").delete();
		FileUtil.options_exists = false;
	}
	
	public static void restoreOptions() throws NullPointerException, IOException {
		final File optionsFile = new File(getMainFolder(), activeProfile + "/options.txt");
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
		DefaultSettings.keyRebinds.clear();
		final File keysFile = new File(getMainFolder(), activeProfile + "/keys.txt");
		if (keysFile.exists()) {
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new FileReader(keysFile));
				String line;
				while ((line = reader.readLine()) != null) {
					if (line.isEmpty()) 
						continue;
					
					DefaultSettings.keyRebinds.put(line.split(":")[0], Integer.parseInt(line.split(":")[1]));
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

			for (KeyBinding keyBinding : MC.gameSettings.keyBindings)
				if (DefaultSettings.keyRebinds.containsKey(keyBinding.keyDescription)) 
					keyBinding.keyCode = DefaultSettings.keyRebinds.get(keyBinding.keyDescription);

			KeyBinding.resetKeyBindingArrayAndHash();
		}
	}
	
	public static void restoreOptionsOF() throws IOException {
		final File optionsOFFile = new File(getMainFolder(), activeProfile + "/optionsof.txt");
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

	public static void restoreConfigs() throws IOException {
		try {	
			FileUtils.copyDirectory(new File(getMainFolder(), activeProfile), new File(mcDataDir, "config"), fileFilterModular);
		} catch (IOException e) {
			throw e;
		}
		
		Collection<File> list = FileUtils.listFilesAndDirs(new File(getMainFolder(), activeProfile), TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
		for (File file : list) {
			if (!file.isDirectory()) {
				getPrivateJSON().currentHash.put(activeProfile + "/" + file.getPath().split("defaultsettings")[1].substring(1).split(activeProfile)[1].substring(1), fileToHash(new FileInputStream(file)));
			}
		}
		
		
		final File main = new File(mcDataDir, mainLocation);
		getMainJSON().setExportMode(false);

		mainJson.save(main);

		final File main2 = new File(mcDataDir, privateLocation);

		privateJson.save(main2);
	}

	public static void moveAllConfigs() throws IOException {
		mainJson.generatedBy = privateJson.privateIdentifier;
		
		try {
			
			File fileDir = new File(mcDataDir, "config");
			
			FileUtils.copyDirectory(fileDir, new File(getMainFolder(), activeProfile), fileFilterModular);
			for (File f : fileDir.listFiles(fileFilterModular)) {
				try {
				if(f.isDirectory())
					FileUtils.deleteDirectory(f);
				else
					//f.delete() calls updates, not appropriate
					Files.delete(f.toPath());
				}catch(IOException e) {
					DefaultSettings.log.log(Level.SEVERE, "Couldn't move config files: ", e);
				}
			}
			
			FileUtils.copyDirectory(new File(getMainFolder(), activeProfile), fileDir, fileFilterAnti);
			for (File f : new File(getMainFolder(), activeProfile).listFiles(fileFilterAnti)) {
				try {
				if(f.isDirectory())
					FileUtils.deleteDirectory(f);
				else
					//f.delete() calls updates, not appropriate
					Files.delete(f.toPath());
				}catch(IOException e) {
					DefaultSettings.log.log(Level.SEVERE, "Couldn't move config files: ", e);
				}
			}
			
		} catch (IOException e) {
			throw e;
		}
		final File main = new File(mcDataDir, mainLocation);
		
		getMainJSON().setExportMode(true);
		mainJson.save(main);
	}

	public static void restoreServers() throws IOException {
		try {
			File file = new File(getMainFolder(), activeProfile + "/servers.dat");
			if(file.exists())
				FileUtils.copyFile(file, new File(mcDataDir, "servers.dat"));
			else
				DefaultSettings.log.log(Level.WARNING, "Couldn't restore the server config as it's not included");
		} catch (IOException e) {
			DefaultSettings.log.log(Level.SEVERE, "Couldn't restore the server config: ", e);
		}
	}
	
	public static void saveKeys() throws IOException, NullPointerException {
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(new FileWriter(new File(getMainFolder(), "keys.txt")));
			for (KeyBinding keyBinding : MC.gameSettings.keyBindings) 
				writer.print(keyBinding.keyDescription + ":" + keyBinding.keyCode + "\n");

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
			writer = new PrintWriter(new FileWriter(new File(getMainFolder(), activeProfile + "/options.txt")));
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
			writer = new PrintWriter(new FileWriter(new File(getMainFolder(), activeProfile + "/optionsof.txt")));
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
				FileUtils.copyFile(serversFile, new File(getMainFolder(), activeProfile + "/servers.dat"));
			} catch (IOException e) {
				throw e;
			}
		}
	}
	
	public static boolean serversFileExists() {
		final File serversFile = new File(getMainFolder(), activeProfile + "/servers.dat");
		return serversFile.exists();
	}
	
	public static String getUUID(String uuid) throws NoSuchAlgorithmException {
		return stringToHash(uuid);
	}
	
	public static String stringToHash(String string) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(string.getBytes());
		byte[] digest = md.digest();
		return DatatypeConverter.printHexBinary(digest).toUpperCase();
	}
	
	public static String fileToHash(InputStream is) throws IOException {
		return DigestUtils.md5Hex(is).toUpperCase();
	}
	
	public static class RegistryChecker {
		
		public RegistryChecker() {		
			if (FileUtil.registryChecker != null)
				return;
			
			FileUtil.registryChecker = new Thread(new Runnable() {
				
				@Override
				public void run() {
					while (true) {
						if(!(MC.currentScreen instanceof GuiConfig)) {
							FileUtil.registryChecker = null;
							break;
						}

						if(optionsFilesExist())
							FileUtil.options_exists = true;
						else
							FileUtil.options_exists = false;
						if(keysFileExist())
							FileUtil.keys_exists = true;
						else
							FileUtil.keys_exists = false;
						if(serversFileExists())
							FileUtil.servers_exists = true;
						else
							FileUtil.servers_exists = false;
			
						GuiScreen gui = MC.currentScreen;
						
						if(gui instanceof GuiConfig && ((GuiConfig) gui).menu != null) {
							synchronized (((GuiConfig) gui).menu) {
								for(MenuArea variant : ((GuiConfig) gui).menu.getVariants()) {
									for(Segment segment : variant.getChildren()) {
										if(segment instanceof ScrollableSegment)
											segment.guiContentUpdate(((ScrollableSegment) segment).searchbar.query);
									}
								}
							}
						}
						
						try {
							Thread.sleep(10000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			});
			FileUtil.registryChecker.start();
		}
	}
	
	@SuppressWarnings("rawtypes")
	private static void setField(String name, Class clazz, Object obj, Object value) {
		try {
			Field field = clazz.getDeclaredField(name);
			field.setAccessible(true);
			field.set(obj, value);
		} catch (IllegalAccessException e) {
			DefaultSettings.log.log(Level.SEVERE, "Reflection exception: ", e);
		} catch (IllegalArgumentException e) {
			DefaultSettings.log.log(Level.SEVERE, "Reflection exception: ", e);
		} catch (NoSuchFieldException e) {
			DefaultSettings.log.log(Level.SEVERE, "Reflection exception: ", e);
		} catch (SecurityException e) {
			DefaultSettings.log.log(Level.SEVERE, "Reflection exception: ", e);
		}
	}
	
	public static void checkMD5() throws FileNotFoundException, IOException {

		Collection<File> lel = FileUtils.listFilesAndDirs(new File(getMainFolder(), activeProfile), TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
		for (File fef : lel) {
			if (!fef.isDirectory()) {
				getMainJSON().hashes.put(activeProfile + "/" + fef.getPath().split("defaultsettings")[1].substring(1).split(activeProfile)[1].substring(1), fileToHash(new FileInputStream(fef)));
			}
		}

		final File main = new File(mcDataDir, mainLocation);
		mainJson.save(main);
		
	}

}