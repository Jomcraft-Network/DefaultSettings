package net.jomcraft.defaultsettings;

import static net.jomcraft.jcplugin.FileUtilNoMC.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import net.minecraft.client.settings.KeyBinding;
import org.apache.logging.log4j.Level;
import net.minecraft.client.Minecraft;

public class FileUtil_17 {

	public static void restoreContents() throws NullPointerException, IOException {

		final String version = getMainJSON().getVersion();

		if (!DefaultSettings_17.VERSION.equals(version))
			mainJson.setVersion(DefaultSettings_17.VERSION).setPrevVersion(version);

		if (mainJson.generatedBy.equals("<default>"))
			mainJson.generatedBy = privateJson.privateIdentifier;

		activeProfile = privateJson.currentProfile;

		if (!privateJson.firstBootUp){
			copyAndHashPrivate(true, true);
		}

		final File optionsOF = new File(mcDataDir, "optionsof.txt");
		if (!optionsOF.exists())
			restoreOptionsOF();

		final File optionsShaders = new File(mcDataDir, "optionsshaders.txt");
		if (!optionsShaders.exists())
			restoreOptionsShaders();

		final File optionsJEK = new File(mcDataDir, "options.justenoughkeys.txt");
		if (!optionsJEK.exists())
			restoreOptionsJEK();

		final File optionsAmecs = new File(mcDataDir, "options.amecsapi.txt");
		if (!optionsAmecs.exists())
			restoreOptionsAmecs();

		final File serversFile = new File(mcDataDir, "servers.dat");
		if (!serversFile.exists())
			restoreServers();

		mainJson.save();
	}

	@SuppressWarnings("resource")
	public static void restoreKeys(boolean update, boolean initial) throws NullPointerException, IOException, NumberFormatException {
		DefaultSettings_17.keyRebinds.clear();
		final File keysFile = new File(getMainFolder(), activeProfile + "/keys.txt");
		if (keysFile.exists()) {
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new FileReader(keysFile));
				String line;
				while ((line = reader.readLine()) != null) {
					if (line.isEmpty())
						continue;

					DefaultSettings_17.keyRebinds.put(line.split(":")[0], Integer.parseInt(line.split(":")[1]));
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

			if (update) {

				ArrayList<String> presentKeys = new ArrayList<String>();

				final File localKeysFile = new File(mcDataDir, "options.txt");
				if (localKeysFile.exists()) {
					BufferedReader localReader = null;
					try {
						localReader = new BufferedReader(new FileReader(localKeysFile));
						String line;
						while ((line = localReader.readLine()) != null) {
							if (line.isEmpty())
								continue;

							if (line.startsWith("key_key.")) {
								final String key = line.split("key_")[1].split(":")[0];
								presentKeys.add(key);
							}
						}
					} catch (IOException e) {
						throw e;

					} catch (NullPointerException e) {
						throw e;
					} finally {
						try {
							localReader.close();
						} catch (IOException e) {
							throw e;
						} catch (NullPointerException e) {
							throw e;
						}
					}
				}

				for (KeyBinding keyBinding : Minecraft.getMinecraft().gameSettings.keyBindings) {
					if (DefaultSettings_17.keyRebinds.containsKey(keyBinding.getKeyDescription())) {
						int container = DefaultSettings_17.keyRebinds.get(keyBinding.getKeyDescription());

						if (initial || !presentKeys.contains(keyBinding.getKeyDescription()))
							keyBinding.keyCode = container;

						keyBinding.keyCodeDefault = container;
						//ObfuscationReflectionHelper.setPrivateValue(KeyBinding.class, keyBinding, container.modifier, "keyModifierDefault");
					}
				}
				KeyBinding.resetKeyBindingArrayAndHash();
			}

		}
	}

	@SuppressWarnings("resource")
	public static void saveKeys() throws IOException, NullPointerException {
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(new FileWriter(new File(getMainFolder(), activeProfile + "/keys.txt")));
			for (KeyBinding keyBinding : Minecraft.getMinecraft().gameSettings.keyBindings)
				writer.print(keyBinding.getKeyDescription() + ":" + keyBinding.getKeyCode() + "\n");

		} catch (IOException e) {
			throw e;
		} catch (NullPointerException e) {
			throw e;
		} finally {
			writer.close();
		}

		BufferedReader reader = null;
		if (new File(mcDataDir, "options.justenoughkeys.txt").exists()) {

			try {
				writer = new PrintWriter(new FileWriter(new File(getMainFolder(), activeProfile + "/options.justenoughkeys.txt")));
				reader = new BufferedReader(new FileReader(new File(mcDataDir, "options.justenoughkeys.txt")));
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

		if (new File(mcDataDir, "options.amecsapi.txt").exists()) {

			try {
				writer = new PrintWriter(new FileWriter(new File(getMainFolder(), activeProfile + "/options.amecsapi.txt")));
				reader = new BufferedReader(new FileReader(new File(mcDataDir, "options.amecsapi.txt")));
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
	}

	@SuppressWarnings("resource")
	public static boolean saveOptions() throws NullPointerException, IOException {
		Minecraft.getMinecraft().gameSettings.saveOptions();
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

		if (!new File(mcDataDir, "optionsof.txt").exists())
			return false;

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

		if (!new File(mcDataDir, "optionsshaders.txt").exists())
			return false;

		try {
			writer = new PrintWriter(new FileWriter(new File(getMainFolder(), activeProfile + "/optionsshaders.txt")));
			reader = new BufferedReader(new FileReader(new File(mcDataDir, "optionsshaders.txt")));
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

		return true;
	}

	@SuppressWarnings("resource")
	public static InputStream getKeysStream() throws IOException, NullPointerException {
		FileInputStream stream = null;
		PrintWriter writer = null;
		File file = new File(getMainFolder(), activeProfile + "/keys.txt_temp");
		try {
			writer = new PrintWriter(new FileWriter(file));
			for (KeyBinding keyBinding : Minecraft.getMinecraft().gameSettings.keyBindings)
				writer.print(keyBinding.getKeyDescription() + ":" + keyBinding.getKeyCode() + "\n");
			stream = new FileInputStream(file);
		} catch (IOException e) {
			throw e;
		} catch (NullPointerException e) {
			throw e;
		} finally {
			writer.close();
		}

		return stream;
	}

	public static boolean checkChanged() {
		boolean ret = false;
		try {

			InputStream keys = getKeysStream();
			InputStream options = getOptionsStream();
			InputStream optionsOF = getOptionsOFStream();
			InputStream optionsShaders = getOptionsShadersStream();
			InputStream optionsJEK = getOptionsJEKStream();
			InputStream optionsAmecs = getOptionsAmecsStream();
			InputStream servers = getServersStream();

			String hashO = "";
			String writtenHashO = "";

			if (options != null) {
				hashO = fileToHash(options);
				writtenHashO = mainJson.hashes.get(activeProfile + "/options.txt");
			}

			String hashK = "";
			String writtenHashK = "";

			if (keys != null) {
				hashK = fileToHash(keys);
				writtenHashK = mainJson.hashes.get(activeProfile + "/keys.txt");
			}

			String hashOF = "";
			String writtenHashOF = "";

			if (optionsOF != null) {
				hashOF = fileToHash(optionsOF);
				writtenHashOF = mainJson.hashes.get(activeProfile + "/optionsof.txt");
			}

			String hashShaders = "";
			String writtenHashShaders = "";

			if (optionsShaders != null) {
				hashShaders = fileToHash(optionsShaders);
				writtenHashShaders = mainJson.hashes.get(activeProfile + "/optionsshaders.txt");
			}

			String hashJEK = "";
			String writtenHashJEK = "";

			if (optionsJEK != null) {
				hashJEK = fileToHash(optionsJEK);
				writtenHashJEK = mainJson.hashes.get(activeProfile + "/options.justenoughkeys.txt");
			}

			String hashAmecs = "";
			String writtenHashAmecs = "";

			if (optionsAmecs != null) {
				hashAmecs = fileToHash(optionsAmecs);
				writtenHashAmecs = mainJson.hashes.get(activeProfile + "/options.amecsapi.txt");
			}

			String hashS = "";
			String writtenHashS = "";

			if (servers != null) {
				hashS = fileToHash(servers);
				writtenHashS = mainJson.hashes.get(activeProfile + "/servers.dat");
			}

			if (mainJson.hashes.containsKey(activeProfile + "/options.txt") && !hashO.equals(writtenHashO)) {
				ret = true;
			} else if (mainJson.hashes.containsKey(activeProfile + "/keys.txt") && !hashK.equals(writtenHashK)) {
				ret = true;
			} else if (mainJson.hashes.containsKey(activeProfile + "/optionsof.txt") && !hashOF.equals(writtenHashOF)) {
				ret = true;
			} else if (mainJson.hashes.containsKey(activeProfile + "/optionsshaders.txt") && !hashShaders.equals(writtenHashShaders)) {
				ret = true;
			} else if (mainJson.hashes.containsKey(activeProfile + "/options.justenoughkeys.txt") && !hashJEK.equals(writtenHashJEK)) {
				ret = true;
			} else if (mainJson.hashes.containsKey(activeProfile + "/options.amecsapi.txt") && !hashAmecs.equals(writtenHashAmecs)) {
				ret = true;
			} else if (mainJson.hashes.containsKey(activeProfile + "/servers.dat") && !hashS.equals(writtenHashS)) {
				ret = true;
			}

			options.close();
			File fileO = new File(getMainFolder(), activeProfile + "/options.txt_temp");
			Files.delete(fileO.toPath());
			keys.close();
			File fileK = new File(getMainFolder(), activeProfile + "/keys.txt_temp");
			Files.delete(fileK.toPath());

		} catch (Exception e) {
			DefaultSettings_17.log.log(Level.ERROR, "Error while saving configs: ", e);
		}

		return ret;
	}
}