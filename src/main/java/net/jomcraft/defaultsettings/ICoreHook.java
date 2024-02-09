package net.jomcraft.defaultsettings;

import java.io.File;

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

}
