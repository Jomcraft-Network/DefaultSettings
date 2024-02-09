package net.jomcraft.defaultsettings;

import java.io.*;
import java.util.ArrayList;

public class TestR {

    public static void restoreKeysAA(boolean update, boolean initial) throws IOException, NullPointerException {
        //DefaultSettings.keyRebinds.clear();
        Core.getInstance().clearKeyBinds();
        final File keysFile = new File(Core.getInstance().getMainFolder(), Core.getInstance().getActiveProfile() + "/keys.txt");
        if (keysFile.exists()) {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(keysFile));
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.isEmpty())
                        continue;

                    Core.getInstance().putKeybind(line.split(":")[0], line.split(":")[1], line.split(":").length > 2 ? line.split(":")[2] : null);

                    //DefaultSettings.keyRebinds.put(line.split(":")[0], new KeyContainer(InputConstants.getKey(line.split(":")[1]), line.split(":").length > 2 ? KeyModifier.valueFromString(line.split(":")[2]) : KeyModifier.NONE));
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

                final File localKeysFile = new File(Core.getInstance().getMCDataDir(), "options.txt");
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

                for (KeyPlaceholder keyBinding : Core.getInstance().getKeyMappings()) {
                    if (Core.getInstance().keybindExists(keyBinding.name)/*DefaultSettings.keyRebinds.containsKey(keyBinding.name)*/) {
                        //KeyContainer container = DefaultSettings.keyRebinds.get(keyBinding.name);
                        boolean init = false;
                        if (initial || !presentKeys.contains(keyBinding.name))
                            init = true;
                            //keyBinding.setKey(container.input);

                        Core.getInstance().setKeybind(keyBinding, init);

                        /*keyBinding.defaultKey = container.input;

                        ObfuscationReflectionHelper.setPrivateValue(KeyMapping.class, keyBinding, container.modifier, "keyModifierDefault");
                        keyBinding.setKeyModifierAndCode(keyBinding.getDefaultKeyModifier(), container.input);*/

                    }
                }
                //KeyMapping.resetMapping();
                Core.getInstance().resetMappings();
            }
        }
    }

    public static InputStream getKeysStreamAA() throws IOException, NullPointerException {
        FileInputStream stream;
        PrintWriter writer = null;
        File file = new File(Core.getInstance().getMainFolder(), Core.getInstance().getActiveProfile() + "/keys.txt_temp");
        try {
            writer = new PrintWriter(new FileWriter(file));
            for (KeyPlaceholder keyBinding : Core.getInstance().getKeyMappings())
                writer.print(keyBinding.name + ":" + keyBinding.key + ":" + keyBinding.modifier + "\n");
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

    public static void saveKeysAA() throws IOException, NullPointerException {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new FileWriter(new File(Core.getInstance().getMainFolder(), Core.getInstance().getActiveProfile() + "/keys.txt")));
            for (KeyPlaceholder keyBinding : Core.getInstance().getKeyMappings()/*Minecraft.getInstance().options.keyMappings*/){
                writer.print(keyBinding.name + ":" + keyBinding.key + ":" + keyBinding.modifier + "\n");
            }

        } catch (IOException e) {
            throw e;
        } catch (NullPointerException e) {
            throw e;
        } finally {
            writer.close();
        }

        BufferedReader reader = null;
        if (new File(Core.getInstance().getMCDataDir(), "options.justenoughkeys.txt").exists()) {

            try {
                writer = new PrintWriter(new FileWriter(new File(Core.getInstance().getMainFolder(), Core.getInstance().getActiveProfile() + "/options.justenoughkeys.txt")));
                reader = new BufferedReader(new FileReader(new File(Core.getInstance().getMCDataDir(), "options.justenoughkeys.txt")));
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

        if (new File(Core.getInstance().getMCDataDir(), "options.amecsapi.txt").exists()) {

            try {
                writer = new PrintWriter(new FileWriter(new File(Core.getInstance().getMainFolder(), Core.getInstance().getActiveProfile() + "/options.amecsapi.txt")));
                reader = new BufferedReader(new FileReader(new File(Core.getInstance().getMCDataDir(), "options.amecsapi.txt")));
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

    public static boolean saveOptionsAA() throws NullPointerException, IOException {
        PrintWriter writer = null;
        BufferedReader reader = null;
        try {
            writer = new PrintWriter(new FileWriter(new File(Core.getInstance().getMainFolder(), Core.getInstance().getActiveProfile() + "/options.txt")));
            reader = new BufferedReader(new FileReader(new File(Core.getInstance().getMCDataDir(), "options.txt")));
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

        if (!new File(Core.getInstance().getMCDataDir(), "optionsof.txt").exists())
            return false;

        try {
            writer = new PrintWriter(new FileWriter(new File(Core.getInstance().getMainFolder(), Core.getInstance().getActiveProfile() + "/optionsof.txt")));
            reader = new BufferedReader(new FileReader(new File(Core.getInstance().getMCDataDir(), "optionsof.txt")));
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

        if (!new File(Core.getInstance().getMCDataDir(), "optionsshaders.txt").exists())
            return false;

        try {
            writer = new PrintWriter(new FileWriter(new File(Core.getInstance().getMainFolder(), Core.getInstance().getActiveProfile() + "/optionsshaders.txt")));
            reader = new BufferedReader(new FileReader(new File(Core.getInstance().getMCDataDir(), "optionsshaders.txt")));
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

}
