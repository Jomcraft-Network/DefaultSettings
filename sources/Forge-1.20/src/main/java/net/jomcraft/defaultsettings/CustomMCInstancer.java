package net.jomcraft.defaultsettings;

import com.mojang.blaze3d.platform.InputConstants;
import net.jomcraft.jcplugin.FileUtilNoMC;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import java.io.File;

public class CustomMCInstancer implements MCInstancer {

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
        if(mappings == null || mappings.length == 0)
            return new KeyPlaceholder[0];

        KeyPlaceholder[] keys = new KeyPlaceholder[mappings.length];

        for(int i = 0; i < mappings.length; i++) {
            keys[i] = new KeyPlaceholder(mappings[i].getName(), mappings[i].getKey().toString(), mappings[i].getKeyModifier().name());
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
        DefaultSettings.keyRebinds.put(first, new KeyContainer(InputConstants.getKey(second), third != null ? KeyModifier.valueFromString(third) : KeyModifier.NONE));
    }

    @Override
    public boolean keybindExists(String key) {
        return DefaultSettings.keyRebinds.containsKey(key);
    }

    @Override
    public void setKeybind(KeyPlaceholder key, boolean init) {
        KeyMapping[] mappings = Minecraft.getInstance().options.keyMappings;
        for(int i = 0; i < mappings.length; i++){
            if(mappings[i].getName().equals(key.name)){
                KeyContainer container = DefaultSettings.keyRebinds.get(key.name);

                if(init)
                    mappings[i].setKey(container.input);

                mappings[i].defaultKey = container.input;

                ObfuscationReflectionHelper.setPrivateValue(KeyMapping.class, mappings[i], container.modifier, "keyModifierDefault");
                mappings[i].setKeyModifierAndCode(mappings[i].getDefaultKeyModifier(), container.input);
                break;
            }
        }
    }
}
