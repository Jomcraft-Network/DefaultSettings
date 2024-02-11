package net.jomcraft.defaultsettings;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.jomcraft.jcplugin.FileUtilNoMC;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.TextComponent;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;

public class FabricCoreHook implements ICoreHook {

    private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(new TextComponent(ChatFormatting.RED + "Please wait until the last request has finished"));

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
        KeyMapping[] mappings = Minecraft.getInstance().options.keyMappings;
        if (mappings == null || mappings.length == 0)
            return new KeyPlaceholder[0];

        KeyPlaceholder[] keys = new KeyPlaceholder[mappings.length];

        for (int i = 0; i < mappings.length; i++) {
            keys[i] = new KeyPlaceholder(mappings[i].getName(), mappings[i].key.toString(), null);
        }
        return keys;
    }

    @Override
    public void resetMappings() {
        KeyMapping.resetMapping();
    }

    @Override
    public void clearKeyBinds() {
        DefaultSettings.keyRebinds.clear();
    }

    @Override
    public void putKeybind(String first, String second, String third) {
        DefaultSettings.keyRebinds.put(first, new KeyContainer(InputConstants.getKey(second), null));
    }

    @Override
    public boolean keybindExists(String key) {
        return DefaultSettings.keyRebinds.containsKey(key);
    }

    @Override
    public void setKeybind(KeyPlaceholder key, boolean init) {
        KeyMapping[] mappings = Minecraft.getInstance().options.keyMappings;
        for (int i = 0; i < mappings.length; i++) {
            if (mappings[i].getName().equals(key.name)) {
                KeyContainer container = DefaultSettings.keyRebinds.get(key.name);

                if (init)
                    mappings[i].setKey(container.input);

                mappings[i].defaultKey = container.input;

                //ObfuscationReflectionHelper.setPrivateValue(KeyMapping.class, mappings[i], container.modifier, "keyModifierDefault");
                //mappings[i].setKeyModifierAndCode(mappings[i].getDefaultKeyModifier(), container.input);
                break;
            }
        }
    }

    @Override
    public void sendSuccess(Object source, String text, int color) {
        if (source instanceof CommandSourceStack) {
            ((CommandSourceStack) source).sendSuccess(new TextComponent(text).withStyle(ChatFormatting.getById(color)), true);
        }
    }

    @Override
    public Exception throwFailedException() {
        return FAILED_EXCEPTION.create();
    }

    @Override
    public boolean hasDSShutDown() {
        return DefaultSettings.shutDown;
    }

    @Override
    public Logger getDSLog() {
        return DefaultSettings.log;
    }

    @Override
    public String shutdownReason() {
        return null;
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
        return FileUtil.checkChanged();
    }

    @Override
    public void saveKeys() throws IOException {
        FileUtil.saveKeys();
    }

    @Override
    public boolean saveOptions() throws IOException {
        return FileUtil.saveOptions();
    }

    @Override
    public void saveServers() throws IOException {
        FileUtilNoMC.saveServers();
    }

    @Override
    public void restoreKeys(boolean update, boolean initial) throws IOException {
        FileUtil.restoreKeys(update, initial);
    }
}