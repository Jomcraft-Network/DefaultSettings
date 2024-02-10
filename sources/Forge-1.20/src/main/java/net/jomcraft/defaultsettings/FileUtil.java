package net.jomcraft.defaultsettings;

import static net.jomcraft.jcplugin.FileUtilNoMC.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import org.apache.logging.log4j.Level;
import net.minecraft.client.Minecraft;

public class FileUtil {

    public static void restoreContents() throws NullPointerException, IOException {

        final String version = getMainJSON().getVersion();

        if (!DefaultSettings.VERSION.equals(version))
            mainJson.setVersion(DefaultSettings.VERSION).setPrevVersion(version);

        if (mainJson.generatedBy.equals("<default>"))
			mainJson.generatedBy = privateJson.privateIdentifier;

        activeProfile = privateJson.currentProfile;

        if (!privateJson.firstBootUp) {
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
        CoreUtil.restoreKeys(update, initial);
    }

    @SuppressWarnings("resource")
    public static void saveKeys() throws IOException, NullPointerException {
        CoreUtil.saveKeys();
    }

    @SuppressWarnings("resource")
    public static boolean saveOptions() throws NullPointerException, IOException {
        Minecraft.getInstance().options.save();
        return CoreUtil.saveOptions();
    }

    public static boolean checkChanged() {
        boolean ret = false;
        try {

            InputStream keys = CoreUtil.getKeysStream();
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

            if (options != null) {
                options.close();
                File fileO = new File(getMainFolder(), activeProfile + "/options.txt_temp");
                Files.delete(fileO.toPath());
            }

            if (keys != null) {
                keys.close();
                File fileK = new File(getMainFolder(), activeProfile + "/keys.txt_temp");
                Files.delete(fileK.toPath());
            }

        } catch (Exception e) {
            DefaultSettings.log.log(Level.ERROR, "Error while saving configs: ", e);
        }

        return ret;
    }
}