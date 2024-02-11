package net.jomcraft.defaultsettings;

import net.jomcraft.jcplugin.FileUtilNoMC;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;

public class ForgeCoreHook_17 implements ICoreHook {

    @Override
    public File getMCDataDir() {
        return FileUtilNoMC.mcDataDir;
    }

    @Override
    public File getMainFolder() {
        return FileUtilNoMC.getMainFolder();
    }

    @Override
    public String getActiveProfile() {
        return FileUtilNoMC.activeProfile;
    }

    @Override
    public KeyPlaceholder[] getKeyMappings() {
        KeyBinding[] mappings = Minecraft.getMinecraft().gameSettings.keyBindings;
        if (mappings == null || mappings.length == 0)
            return new KeyPlaceholder[0];

        KeyPlaceholder[] keys = new KeyPlaceholder[mappings.length];

        for (int i = 0; i < mappings.length; i++) {
            keys[i] = new KeyPlaceholder(mappings[i].getKeyDescription(), String.valueOf(mappings[i].getKeyCode()), null);
        }
        return keys;
    }

    @Override
    public void resetMappings() {
        KeyBinding.resetKeyBindingArrayAndHash();
    }

    @Override
    public void clearKeyBinds() {
        DefaultSettings_17.keyRebinds.clear();
    }

    @Override
    public void putKeybind(String first, String second, String third) {
        DefaultSettings_17.keyRebinds.put(first, Integer.parseInt(second));
    }

    @Override
    public boolean keybindExists(String key) {
        return DefaultSettings_17.keyRebinds.containsKey(key);
    }

    @Override
    public void setKeybind(KeyPlaceholder key, boolean init) {
        KeyBinding[] mappings = Minecraft.getMinecraft().gameSettings.keyBindings;

        for (int i = 0; i < mappings.length; i++) {
            if (mappings[i].getKeyDescription().equals(key.name)) {
                int container = DefaultSettings_17.keyRebinds.get(key.name);

                if (init)
                    mappings[i].keyCode = container;

                mappings[i].keyCodeDefault = container;
                break;
            }
        }
    }

    @Override
    public void sendSuccess(Object source, String text, int color) {
        if (source instanceof ICommandSender) {
            final ChatComponentText message = new ChatComponentText(text);
            message.func_150256_b().func_150238_a(EnumChatFormatting.values()[color]);
            ((ICommandSender) source).func_145747_a(message);
        }
    }

    @Override
    public Exception throwFailedException() {
        return null;
    }

    @Override
    public boolean hasDSShutDown() {
        return DefaultSettings_17.shutDown;
    }

    @Override
    public Logger getDSLog() {
        return DefaultSettings_17.log;
    }

    @Override
    public String shutdownReason() {
        return DefaultSettings_17.shutdownReason;
    }

    @Override
    public boolean isOtherCreator() {
        return FileUtilNoMC.otherCreator;
    }

    @Override
    public boolean disableCreatorCheck() {
        return FileUtilNoMC.privateJson.disableCreatorCheck;
    }

    @Override
    public boolean checkChangedConfig() {
        return FileUtilNoMC.checkChangedConfig();
    }

    @Override
    public boolean checkForConfigFiles() {
        return FileUtilNoMC.checkForConfigFiles();
    }

    @Override
    public void checkMD5(boolean updateExisting, boolean configs, String file) throws IOException {
        FileUtilNoMC.checkMD5(updateExisting, configs, file);
    }

    @Override
    public void copyAndHashPrivate(boolean options, boolean configs) throws NullPointerException, IOException {
        FileUtilNoMC.copyAndHashPrivate(options, configs);
    }

    @Override
    public boolean keysFileExist() {
        return FileUtilNoMC.keysFileExist();
    }

    @Override
    public boolean optionsFilesExist() {
        return FileUtilNoMC.optionsFilesExist();
    }

    @Override
    public boolean serversFileExists() {
        return FileUtilNoMC.serversFileExists();
    }

    @Override
    public boolean checkChanged() {
        return FileUtil_17.checkChanged();
    }

    @Override
    public void saveKeys() throws IOException {
        FileUtil_17.saveKeys();
    }

    @Override
    public boolean saveOptions() throws IOException {
        return FileUtil_17.saveOptions();
    }

    @Override
    public void saveServers() throws IOException {
        FileUtilNoMC.saveServers();
    }

    @Override
    public void restoreKeys(boolean update, boolean initial) throws IOException {
        FileUtil_17.restoreKeys(update, initial);
    }
}