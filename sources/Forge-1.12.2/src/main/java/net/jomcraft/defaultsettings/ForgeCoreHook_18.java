package net.jomcraft.defaultsettings;

import net.jomcraft.jcplugin.FileUtilNoMC;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;

public class ForgeCoreHook_18 implements ICoreHook {

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
            keys[i] = new KeyPlaceholder(mappings[i].getKeyDescription(), String.valueOf(mappings[i].getKeyCode()), mappings[i].getKeyModifier().name());
        }
        return keys;
    }

    @Override
    public void resetMappings() {
        KeyBinding.resetKeyBindingArrayAndHash();
    }

    @Override
    public void clearKeyBinds() {
        DefaultSettings_18.keyRebinds.clear();
    }

    @Override
    public void putKeybind(String first, String second, String third) {
        DefaultSettings_18.keyRebinds.put(first, new KeyContainer(Integer.parseInt(second), third != null ? KeyModifier.valueFromString(third) : KeyModifier.NONE));
    }

    @Override
    public boolean keybindExists(String key) {
        return DefaultSettings_18.keyRebinds.containsKey(key);
    }

    @Override
    public void setKeybind(KeyPlaceholder key, boolean init) {
        KeyBinding[] mappings = Minecraft.getMinecraft().gameSettings.keyBindings;
        for (int i = 0; i < mappings.length; i++) {
            if (mappings[i].getKeyDescription().equals(key.name)) {
                KeyContainer container = DefaultSettings_18.keyRebinds.get(key.name);

                if (init)
                    mappings[i].setKeyCode(container.input);

                mappings[i].keyCodeDefault = container.input;

                ObfuscationReflectionHelper.setPrivateValue(KeyBinding.class, mappings[i], container.modifier, "keyModifierDefault");
                mappings[i].setKeyModifierAndCode(mappings[i].getKeyModifierDefault(), container.input);
                break;
            }
        }
    }

    @Override
    public void sendSuccess(Object source, String text, int color) {
        if (source instanceof ICommandSender) {
            final TextComponentString message = new TextComponentString(text);
            message.getStyle().setColor(TextFormatting.fromColorIndex(color));
            ((ICommandSender) source).sendMessage(message);
        }
    }

    @Override
    public Exception throwFailedException() {
        return null;
    }

    @Override
    public boolean hasDSShutDown() {
        return DefaultSettings_18.shutDown;
    }

    @Override
    public Logger getDSLog() {
        return DefaultSettings_18.log;
    }

    @Override
    public String shutdownReason() {
        return DefaultSettings_18.shutdownReason;
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
        return FileUtil_18.checkChanged();
    }

    @Override
    public void saveKeys() throws IOException {
        FileUtil_18.saveKeys();
    }

    @Override
    public boolean saveOptions() throws IOException {
        return FileUtil_18.saveOptions();
    }

    @Override
    public void saveServers() throws IOException {
        FileUtilNoMC.saveServers();
    }

    @Override
    public void restoreKeys(boolean update, boolean initial) throws IOException {
        FileUtil_18.restoreKeys(update, initial);
    }
}