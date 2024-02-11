package net.jomcraft.defaultsettings;

import org.apache.logging.log4j.Logger;
import java.io.File;
import java.io.IOException;

public interface ICoreHook {

    File getMCDataDir();
    File getMainFolder();
    String getActiveProfile();
    KeyPlaceholder[] getKeyMappings();
    void resetMappings();
    void clearKeyBinds();
    void putKeybind(String first, String second, String third);
    boolean keybindExists(String bind);
    void setKeybind(KeyPlaceholder key, boolean init);
    void sendSuccess(Object source, String text, int color);
    Exception throwFailedException();
    boolean hasDSShutDown();
    Logger getDSLog();
    String shutdownReason();
    boolean isOtherCreator();
    boolean disableCreatorCheck();
    boolean checkChangedConfig();
    boolean checkForConfigFiles();
    void checkMD5(boolean updateExisting, boolean configs, String file) throws IOException;
    void copyAndHashPrivate(boolean options, boolean configs) throws NullPointerException, IOException;
    boolean keysFileExist();
    boolean optionsFilesExist();
    boolean serversFileExists();
    boolean checkChanged();
    void saveKeys() throws IOException;
    boolean saveOptions() throws IOException;
    void saveServers() throws IOException;
    void restoreKeys(boolean update, boolean initial) throws IOException;
}
