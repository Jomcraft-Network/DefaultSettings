package net.jomcraft.defaultsettings;

import org.apache.logging.log4j.Level;

import java.io.*;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class CoreUtil {

    private static ThreadPoolExecutor tpe = new ThreadPoolExecutor(1, 3, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

    public static void restoreKeys(boolean update, boolean initial) throws IOException, NullPointerException {
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

                    String[] parts = line.split(":");
                    if (parts[parts.length - 1].contains("key.") || numberParsable(parts[parts.length - 1])) {
                        String keyName = "";
                        for (int i = 0; i < parts.length - 1; i++) {
                            keyName += (keyName.isEmpty() ? "" : ":") + parts[i];
                        }

                        String bind = parts[parts.length - 1];
                        Core.getInstance().putKeybind(keyName, bind, null);

                    } else {
                        String modifier = parts[parts.length - 1];
                        String keyName = "";
                        for (int i = 0; i < parts.length - 2; i++) {
                            keyName += (keyName.isEmpty() ? "" : ":") + parts[i];
                        }

                        String bind = parts[parts.length - 2];
                        Core.getInstance().putKeybind(keyName, bind, modifier);
                    }
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

                                String[] parts = line.split(":");
                                if (parts[parts.length - 1].contains("key.") || numberParsable(parts[parts.length - 1])) {
                                    String keyName = "";
                                    for (int i = 0; i < parts.length - 1; i++) {
                                        keyName += (keyName.isEmpty() ? "" : ":") + parts[i];
                                    }

                                    presentKeys.add(keyName.substring(4));

                                } else {
                                    String keyName = "";
                                    for (int i = 0; i < parts.length - 2; i++) {
                                        keyName += (keyName.isEmpty() ? "" : ":") + parts[i];
                                    }

                                    presentKeys.add(keyName.substring(4));
                                }
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
                    if (Core.getInstance().keybindExists(keyBinding.name)) {
                        boolean init = false;
                        if (initial || !presentKeys.contains(keyBinding.name))
                            init = true;

                        Core.getInstance().setKeybind(keyBinding, init);
                    }
                }
                Core.getInstance().resetMappings();
            }
        }
    }

    public static boolean numberParsable(String text) {
        try {
            Integer.parseInt(text);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static InputStream getKeysStream(boolean noModifier) throws IOException, NullPointerException {
        FileInputStream stream;
        PrintWriter writer = null;
        File file = new File(Core.getInstance().getMainFolder(), Core.getInstance().getActiveProfile() + "/keys.txt_temp");
        try {
            writer = new PrintWriter(new FileWriter(file));
            for (KeyPlaceholder keyBinding : Core.getInstance().getKeyMappings())
                writer.print(keyBinding.name + ":" + keyBinding.key + (noModifier ? "" : (":" + keyBinding.modifier)) + "\n");
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

    public static void saveKeys() throws IOException, NullPointerException {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new FileWriter(new File(Core.getInstance().getMainFolder(), Core.getInstance().getActiveProfile() + "/keys.txt")));
            for (KeyPlaceholder keyBinding : Core.getInstance().getKeyMappings()) {
                writer.print(keyBinding.name + ":" + keyBinding.key + (keyBinding.modifier == null ? "" : (":" + keyBinding.modifier)) + "\n");
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

    public static boolean saveOptions() throws NullPointerException, IOException {
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

    public static int saveProcess(Object source, String argument, String argument2) throws Exception {
        if (tpe.getQueue().size() > 0) {
            Exception e = Core.getInstance().throwFailedException();
            if (e != null) {
                throw e;
            } else {
                Core.getInstance().sendSuccess(source, "Please wait until the last request has finished", ChatColors.RED.ordinal());
                return 0;
            }
        }

        if (Core.getInstance().hasDSShutDown()) {
            Core.getInstance().sendSuccess(source, "DefaultSettings is missing the JCPlugin mod! Shutting down...", ChatColors.RED.ordinal());
            if (Core.getInstance().shutdownReason() != null && !Core.getInstance().shutdownReason().isEmpty())
                Core.getInstance().sendSuccess(source, "Reason: " + Core.getInstance().shutdownReason(), ChatColors.RED.ordinal());
            return 0;
        }

        if (!shouldExecute(source)) {
            return 0;
        }

        if ((Core.getInstance().keysFileExist() || Core.getInstance().optionsFilesExist() || Core.getInstance().serversFileExists()) && (argument == null || (!argument.equals("override") && !argument.equals("forceOverride")))) {
            Core.getInstance().sendSuccess(source, "These files already exist! If you want to overwrite", ChatColors.GOLD.ordinal());
            Core.getInstance().sendSuccess(source, "them, add the 'override' argument", ChatColors.GOLD.ordinal());
            return 0;
        }

        MutableBoolean issue = new MutableBoolean(false);

        tpe.execute(new ThreadRunnable(source, issue) {

            @Override
            public void run() {
                try {
                    boolean somethingChanged = Core.getInstance().checkChanged();

                    if (somethingChanged && !argument.equals("forceOverride")) {
                        Core.getInstance().sendSuccess(source, "\n\n", ChatColors.GOLD.ordinal());
                        Core.getInstance().sendSuccess(source, "You seem to have updated certain config files!", ChatColors.GOLD.ordinal());
                        Core.getInstance().sendSuccess(source, "Users who already play your pack won't (!) receive those changes.\n", ChatColors.GOLD.ordinal());
                        Core.getInstance().sendSuccess(source, "If you want to ship the new configs to those players too,", ChatColors.GOLD.ordinal());
                        Core.getInstance().sendSuccess(source, "append the 'forceOverride' argument instead of 'override'", ChatColors.GOLD.ordinal());
                    }
                } catch (Exception e) {
                    Core.getInstance().getDSLog().log(Level.ERROR, "An exception occurred while saving the key configuration:", e);
                }
            }
        });

        tpe.execute(new ThreadRunnable(source, issue) {

            @Override
            public void run() {
                try {
                    if (argument2 == null || argument2.equals("keybinds")) {
                        Core.getInstance().saveKeys();
                        Core.getInstance().sendSuccess(source, "Successfully saved the key configuration", ChatColors.GREEN.ordinal());
                        Core.getInstance().restoreKeys(true, false);
                    }
                } catch (Exception e) {
                    Core.getInstance().getDSLog().log(Level.ERROR, "An exception occurred while saving the key configuration:", e);
                    Core.getInstance().sendSuccess(source, "Couldn't save the key configuration!", ChatColors.RED.ordinal());
                    issue.setBoolean(true);
                }
            }
        });

        tpe.execute(new ThreadRunnable(source, issue) {

            @Override
            public void run() {
                try {
                    if (argument2 == null || argument2.equals("options")) {
                        boolean optifine = Core.getInstance().saveOptions();
                        Core.getInstance().sendSuccess(source, "Successfully saved the default game options" + (optifine ? " (+ Optifine)" : ""), ChatColors.GREEN.ordinal());
                    }
                } catch (Exception e) {
                    Core.getInstance().getDSLog().log(Level.ERROR, "An exception occurred while saving the default game options:", e);
                    Core.getInstance().sendSuccess(source, "Couldn't save the default game options!", ChatColors.RED.ordinal());
                    issue.setBoolean(true);
                }
            }
        });

        tpe.execute(new ThreadRunnable(source, issue) {

            @Override
            public void run() {
                try {
                    if (argument2 == null || argument2.equals("servers")) {
                        Core.getInstance().saveServers();
                        Core.getInstance().sendSuccess(source, "Successfully saved the server list", ChatColors.GREEN.ordinal());
                    }
                } catch (Exception e) {
                    Core.getInstance().getDSLog().log(Level.ERROR, "An exception occurred while saving the server list:", e);
                    Core.getInstance().sendSuccess(source, "Couldn't save the server list!", ChatColors.RED.ordinal());
                    issue.setBoolean(true);
                }

                if (issue.getBoolean())
                    Core.getInstance().sendSuccess(source, "Please inspect the log files for further information!", ChatColors.YELLOW.ordinal());
                else
                    try {
                        boolean updateExisting = argument != null && argument.equals("forceOverride");
                        Core.getInstance().checkMD5(updateExisting, false, null);
                        Core.getInstance().copyAndHashPrivate(true, false);
                    } catch (IOException e) {
                        Core.getInstance().getDSLog().log(Level.ERROR, "An exception occurred while saving your configuration:", e);
                    }
            }
        });

        return 0;
    }

    private static boolean shouldExecute(Object source) {
        if (Core.getInstance().isOtherCreator()) {
            if (!Core.getInstance().disableCreatorCheck()) {
                Core.getInstance().sendSuccess(source, "You're not the creator of this modpack! Using these creator-only commands might come with unforeseen problems.", ChatColors.RED.ordinal());
                Core.getInstance().sendSuccess(source, "If you're fine with those risks, you may change `\"disableCreatorCheck\": \"false\"` in the `ds_private_storage.json` file to `true`", ChatColors.RED.ordinal());
                return false;
            } else {
                Core.getInstance().sendSuccess(source, "Caution! You disabled the creator checker! This might break things!", ChatColors.RED.ordinal());
                return true;
            }
        }
        return true;
    }

    public static int saveProcessConfigs(Object source, String argument, String argument2) throws Exception {

        if (tpe.getQueue().size() > 0) {
            Exception e = Core.getInstance().throwFailedException();
            if (e != null) {
                throw e;
            } else {
                Core.getInstance().sendSuccess(source, "Please wait until the last request has finished", ChatColors.RED.ordinal());
                return 0;
            }
        }

        if (Core.getInstance().hasDSShutDown()) {
            Core.getInstance().sendSuccess(source, "DefaultSettings is missing the JCPlugin mod! Shutting down...", ChatColors.RED.ordinal());
            if (Core.getInstance().shutdownReason() != null && !Core.getInstance().shutdownReason().isEmpty())
                Core.getInstance().sendSuccess(source, "Reason: " + Core.getInstance().shutdownReason(), ChatColors.RED.ordinal());
            return 0;
        }

        if (!shouldExecute(source)) {
            return 0;
        }

        MutableBoolean issue = new MutableBoolean(false);

        tpe.execute(new ThreadRunnable(source, issue) {

            @Override
            public void run() {
                try {
                    boolean somethingChanged = Core.getInstance().checkChangedConfig();
                    if (somethingChanged && (argument == null || !argument.equals("forceOverride"))) {
                        Core.getInstance().sendSuccess(source, "\n\n", ChatColors.GOLD.ordinal());
                        Core.getInstance().sendSuccess(source, "You seem to have updated certain config files!", ChatColors.GOLD.ordinal());
                        Core.getInstance().sendSuccess(source, "Users who already play your pack won't (!) receive those changes.\n", ChatColors.GOLD.ordinal());
                        Core.getInstance().sendSuccess(source, "If you want to ship the new configs to those players too,", ChatColors.GOLD.ordinal());
                        Core.getInstance().sendSuccess(source, "append the 'forceOverride' argument", ChatColors.GOLD.ordinal());
                    }
                } catch (Exception e) {
                    Core.getInstance().getDSLog().log(Level.ERROR, "An exception occurred while saving the server list:", e);
                    Core.getInstance().sendSuccess(source, "Couldn't save the config files!", ChatColors.RED.ordinal());
                    issue.setBoolean(true);
                }

                if (issue.getBoolean())
                    Core.getInstance().sendSuccess(source, "Please inspect the log files for further information!", ChatColors.YELLOW.ordinal());
                else
                    try {
                        boolean updateExisting = argument != null && argument.equals("forceOverride");
                        Core.getInstance().checkMD5(updateExisting, true, argument2 == null ? null : argument2);
                        Core.getInstance().copyAndHashPrivate(false, true);
                        Core.getInstance().sendSuccess(source, "Successfully saved your mod configuration files" + (argument2 == null ? "" : argument2.contains("*") ? " (wildcard)" : " (single entry)"), ChatColors.GREEN.ordinal());
                        boolean noFiles = Core.getInstance().checkForConfigFiles();
                        if (noFiles)
                            Core.getInstance().sendSuccess(source, "Warning: No config files will be shipped as the folder is still empty!", ChatColors.YELLOW.ordinal());

                    } catch (UncheckedIOException | NullPointerException | IOException e) {
                        Core.getInstance().sendSuccess(source, "Couldn't save the config files!", ChatColors.RED.ordinal());
                        if (e instanceof UncheckedIOException && e.getCause() instanceof NoSuchFileException)
                            Core.getInstance().sendSuccess(source, "It seems, no file or folder by that name exists", ChatColors.RED.ordinal());
                        Core.getInstance().getDSLog().log(Level.ERROR, "An exception occurred while saving your configuration:", e);
                    }
            }
        });

        return 0;
    }

}

abstract class ThreadRunnable implements Runnable {

    final Object supply;
    final MutableBoolean issue;

    ThreadRunnable(Object supply, MutableBoolean issue) {
        this.supply = supply;
        this.issue = issue;
    }
}

class MutableBoolean {

    private boolean bool;

    public MutableBoolean(boolean bool) {
        this.bool = bool;
    }

    public boolean getBoolean() {
        return this.bool;
    }

    public void setBoolean(boolean bool) {
        this.bool = bool;
    }

}