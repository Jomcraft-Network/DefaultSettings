package net.jomcraft.defaultsettings.commands;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import net.jomcraft.defaultsettings.DefaultSettings_17;
import net.jomcraft.defaultsettings.FileUtil_17;
import net.jomcraft.jcplugin.FileUtilNoMC;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import org.apache.logging.log4j.Level;

public class CommandDefaultSettings_17 extends CommandBase {

    private static ThreadPoolExecutor tpe = new ThreadPoolExecutor(1, 3, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

    public String getName() {
        return "defaultsettings";
    }

    public List<String> getAliases() {
        return new ArrayList<String>() {
            {
                add("ds");
            }
        };
    }

    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {

    }

    public String getUsage(ICommandSender sender) {
        return "/defaultsettings [arguments]";
    }

    public int getRequiredPermissionLevel() {
        return 0;
    }

    public List func_71516_a(ICommandSender p_71516_1_, String[] args) {
        if(args.length == 1) {
            return getListOfStringsMatchingLastWord(args, new String[]{"save", "saveconfigs"});
        }

        if(args.length == 2) {
            if(args[0].toLowerCase().equals("save")) {
                return getListOfStringsMatchingLastWord(args, new String[]{"override", "forceOverride"});
            } else if (args[0].toLowerCase().equals("saveconfigs")){
                return getListOfStringsMatchingLastWord(args, new String[]{"forceOverride"});
            }
        }

        if(args.length == 3) {
            if(args[0].toLowerCase().equals("save")) {
                return getListOfStringsMatchingLastWord(args, new String[]{"keybinds", "options", "servers"});
            } else if (args[0].toLowerCase().equals("saveconfigs")){
                try {
                    ArrayList<String> filtered = new ArrayList<String>();
                    ArrayList<String> prevList = FileUtilNoMC.listConfigFiles();
                    for(int i = 0; i < prevList.size(); i++){
                        String name = prevList.get(i);
                        if(name.contains(" "))
                            name = "\"" + name + "\"";
                        filtered.add(name);
                    }
                    return getListOfStringsMatchingLastWord(args, filtered.toArray(new String[0]));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return new ArrayList<String>();
    }

    public void func_71515_b(ICommandSender sender, String[] args) {
        if(args.length == 0) {
            sender.func_145747_a(new ChatComponentText("\u00a7c/defaultsettings [arguments]"));
            return;
        }
        if (args[0].toLowerCase().equals("save")) {
            if(args.length == 1){
                // /ds save
                saveProcess(sender, null, null);
            } else if(args[1].toLowerCase().equals("override")){
                if(args.length == 2){
                    // /ds save override
                    saveProcess(sender, "override", null);
                } else if(args.length == 3){
                    if(args[2].toLowerCase().equals("keybinds")){
                        // /ds save override [type]
                        saveProcess(sender, "override", "keybinds");
                    } else if(args[2].toLowerCase().equals("options")){
                        // /ds save override [type]
                        saveProcess(sender, "override", "options");
                    } else if(args[2].toLowerCase().equals("servers")){
                        // /ds save override [type]
                        saveProcess(sender, "override", "servers");
                    }
                }
            } else if(args[1].toLowerCase().equals("forceoverride")){
                if(args.length == 2){
                    // /ds save forceOverride
                    saveProcess(sender, "forceOverride", null);
                } else if(args.length == 3){
                    if(args[2].toLowerCase().equals("keybinds")){
                        // /ds save forceOverride [type]
                        saveProcess(sender, "forceOverride", "keybinds");
                    } else if(args[2].toLowerCase().equals("options")){
                        // /ds save forceOverride [type]
                        saveProcess(sender, "forceOverride", "options");
                    } else if(args[2].toLowerCase().equals("servers")){
                        // /ds save forceOverride [type]
                        saveProcess(sender, "forceOverride", "servers");
                    }
                }
            }

        } else if (args[0].toLowerCase().equals("saveconfigs")) {
            if(args.length == 1){
                saveProcessConfigs(sender, null, null);
                // /ds saveconfigs
            } else if(args[1].toLowerCase().equals("forceoverride")){
                if(args.length == 2){
                    saveProcessConfigs(sender, "forceOverride", null);
                    // /ds saveconfigs forceOverride
                } else if(args.length >= 3){
                    StringJoiner fileJoiner = new StringJoiner(" ");

                    for(int i = 2; i < args.length; i++){
                        fileJoiner.add(args[i]);
                    }

                    String joined = fileJoiner.toString().replaceAll("\"", "");
                    saveProcessConfigs(sender, "forceOverride", joined);
                    // /ds saveconfigs forceOverride [type]
                }

            }
        }
    }

    private static void saveProcessConfigs(ICommandSender sender, String argument, String argument2) {

        if (tpe.getQueue().size() > 0) {
            final ChatComponentText message = new ChatComponentText("Please wait until the last request has finished");
            message.func_150256_b().func_150238_a(EnumChatFormatting.RED);
            sender.func_145747_a(message);
            return;
        }

        if (DefaultSettings_17.shutDown) {
            ChatComponentText message = new ChatComponentText("DefaultSettings is missing the JCPlugin mod! Shutting down...");
            message.func_150256_b().func_150238_a(EnumChatFormatting.RED);
            sender.func_145747_a(message);
            message = new ChatComponentText("Reason: " + DefaultSettings_17.shutdownReason);
            message.func_150256_b().func_150238_a(EnumChatFormatting.RED);
            sender.func_145747_a(message);
            return;
        }

        if(!shouldExecute(sender)){
            return;
        }

        MutableBoolean issue = new MutableBoolean(false);

        tpe.execute(new ThreadRunnable(sender, issue) {

            @Override
            public void run() {
                try {
                    boolean somethingChanged = FileUtilNoMC.checkChangedConfig();

                    if (somethingChanged && (argument == null || !argument.equals("forceOverride"))) {
                        sender.func_145747_a(new ChatComponentText(""));
                        sender.func_145747_a(new ChatComponentText(""));
                        ChatComponentText message = new ChatComponentText("You seem to have updated certain config files!");
                        message.func_150256_b().func_150238_a(EnumChatFormatting.GOLD);
                        sender.func_145747_a(message);
                        message = new ChatComponentText("Users who already play your pack won't (!) receive those changes.");
                        message.func_150256_b().func_150238_a(EnumChatFormatting.GOLD);
                        sender.func_145747_a(message);
                        sender.func_145747_a(new ChatComponentText(""));
                        message = new ChatComponentText("If you want to ship the new configs to those players too,");
                        message.func_150256_b().func_150238_a(EnumChatFormatting.GOLD);
                        sender.func_145747_a(message);
                        message = new ChatComponentText("append the 'forceOverride' argument");
                        message.func_150256_b().func_150238_a(EnumChatFormatting.GOLD);
                        sender.func_145747_a(message);
                    }
                } catch (Exception e) {
                    DefaultSettings_17.log.log(Level.ERROR, "An exception occurred while saving the server list:", e);
                    final ChatComponentText message = new ChatComponentText("Couldn't save the config files!");
                    message.func_150256_b().func_150238_a(EnumChatFormatting.RED);
                    sender.func_145747_a(message);
                    issue.setBoolean(true);
                }

                if (issue.getBoolean()){
                    final ChatComponentText message = new ChatComponentText("Please inspect the log files for further information!");
                    message.func_150256_b().func_150238_a(EnumChatFormatting.YELLOW);
                    sender.func_145747_a(message);
                } else
                    try {
                        boolean updateExisting = argument != null && argument.equals("forceOverride");

                        FileUtilNoMC.checkMD5(updateExisting, true, argument2 == null ? null : argument2);
                        FileUtilNoMC.copyAndHashPrivate(false, true);
                        final ChatComponentText message = new ChatComponentText("Successfully saved your mod configuration files" + (argument2 == null ? "" : argument2.contains("*") ? " (wildcard)" : " (single entry)"));
                        message.func_150256_b().func_150238_a(EnumChatFormatting.GREEN);
                        sender.func_145747_a(message);
                        boolean noFiles = FileUtilNoMC.checkForConfigFiles();
                        if (noFiles){
                            final ChatComponentText message2 = new ChatComponentText("Warning: No config files will be shipped as the folder is still empty!");
                            message2.func_150256_b().func_150238_a(EnumChatFormatting.YELLOW);
                            sender.func_145747_a(message2);
                        }

                    } catch (UncheckedIOException | NullPointerException | IOException e) {
                        final ChatComponentText message = new ChatComponentText("Couldn't save the config files!");
                        message.func_150256_b().func_150238_a(EnumChatFormatting.RED);
                        sender.func_145747_a(message);
                        if (e instanceof UncheckedIOException && e.getCause() instanceof NoSuchFileException){
                            final ChatComponentText message2 = new ChatComponentText("It seems, no file or folder by that name exists");
                            message2.func_150256_b().func_150238_a(EnumChatFormatting.RED);
                            sender.func_145747_a(message2);
                        }

                        DefaultSettings_17.log.log(Level.ERROR, "An exception occurred while saving your configuration:", e);
                    }
            }
        });
    }

    public static boolean shouldExecute(ICommandSender sender){
        if(FileUtilNoMC.otherCreator){
            if(!FileUtilNoMC.privateJson.disableCreatorCheck){
                ChatComponentText message = new ChatComponentText("You're not the creator of this modpack! Using these creator-only commands might come with unforeseen problems.");
                message.func_150256_b().func_150238_a(EnumChatFormatting.RED);
                sender.func_145747_a(message);
                message = new ChatComponentText("If you're fine with those risks, you may change `\"disableCreatorCheck\": \"false\"` in the `ds_private_storage.json` file to `true`");
                message.func_150256_b().func_150238_a(EnumChatFormatting.RED);
                sender.func_145747_a(message);
                return false;
            } else {
                final ChatComponentText message = new ChatComponentText("Caution! You disabled the creator checker! This might break things!");
                message.func_150256_b().func_150238_a(EnumChatFormatting.RED);
                sender.func_145747_a(message);
                return true;
            }
        }
        return true;
    }

    private static void saveProcess(ICommandSender sender, String argument, String argument2) {
        if (tpe.getQueue().size() > 0) {
            final ChatComponentText message = new ChatComponentText("Please wait until the last request has finished");
            message.func_150256_b().func_150238_a(EnumChatFormatting.RED);
            sender.func_145747_a(message);
            return;
        }

        if (DefaultSettings_17.shutDown) {
            ChatComponentText message = new ChatComponentText("DefaultSettings is missing the JCPlugin mod! Shutting down...");
            message.func_150256_b().func_150238_a(EnumChatFormatting.RED);
            sender.func_145747_a(message);
            message = new ChatComponentText("Reason: " + DefaultSettings_17.shutdownReason);
            message.func_150256_b().func_150238_a(EnumChatFormatting.RED);
            sender.func_145747_a(message);
            return;
        }

        if(!shouldExecute(sender)){
            return;
        }

        if ((FileUtilNoMC.keysFileExist() || FileUtilNoMC.optionsFilesExist() || FileUtilNoMC.serversFileExists()) && (argument == null || (!argument.equals("override") && !argument.equals("forceOverride")))) {
            ChatComponentText message = new ChatComponentText("These files already exist! If you want to overwrite");
            message.func_150256_b().func_150238_a(EnumChatFormatting.GOLD);
            sender.func_145747_a(message);
            message = new ChatComponentText("them, add the 'override' argument");
            message.func_150256_b().func_150238_a(EnumChatFormatting.GOLD);
            sender.func_145747_a(message);
            return;
        }

        MutableBoolean issue = new MutableBoolean(false);

        tpe.execute(new ThreadRunnable(sender, issue) {

            @Override
            public void run() {
                try {
                    boolean somethingChanged = FileUtil_17.checkChanged();

                    if (somethingChanged && !argument.equals("forceOverride")) {
                        sender.func_145747_a(new ChatComponentText(""));
                        sender.func_145747_a(new ChatComponentText(""));
                        ChatComponentText message = new ChatComponentText("You seem to have updated certain config files!");
                        message.func_150256_b().func_150238_a(EnumChatFormatting.GOLD);
                        sender.func_145747_a(message);
                        message = new ChatComponentText("Users who already play your pack won't (!) receive those changes.");
                        message.func_150256_b().func_150238_a(EnumChatFormatting.GOLD);
                        sender.func_145747_a(message);
                        sender.func_145747_a(new ChatComponentText(""));
                        message = new ChatComponentText("If you want to ship the new configs to those players too,");
                        message.func_150256_b().func_150238_a(EnumChatFormatting.GOLD);
                        sender.func_145747_a(message);
                        message = new ChatComponentText("append the 'forceOverride' argument instead of 'override'");
                        message.func_150256_b().func_150238_a(EnumChatFormatting.GOLD);
                        sender.func_145747_a(message);
                    }
                } catch (Exception e) {
                    DefaultSettings_17.log.log(Level.ERROR, "An exception occurred while saving the key configuration:", e);
                }
            }
        });

        tpe.execute(new ThreadRunnable(sender, issue) {

            @Override
            public void run() {
                try {
                    if (argument2 == null || argument2.equals("keybinds")) {
                        FileUtil_17.saveKeys();
                        final ChatComponentText message = new ChatComponentText("Successfully saved the key configuration");
                        message.func_150256_b().func_150238_a(EnumChatFormatting.GREEN);
                        sender.func_145747_a(message);
                        FileUtil_17.restoreKeys(true, false);
                    }
                } catch (Exception e) {
                    DefaultSettings_17.log.log(Level.ERROR, "An exception occurred while saving the key configuration:", e);
                    final ChatComponentText message = new ChatComponentText("Couldn't save the key configuration!");
                    message.func_150256_b().func_150238_a(EnumChatFormatting.RED);
                    sender.func_145747_a(message);
                    issue.setBoolean(true);
                }
            }
        });

        tpe.execute(new ThreadRunnable(sender, issue) {

            @Override
            public void run() {
                try {
                    if (argument2 == null || argument2.equals("options")) {
                        boolean optifine = FileUtil_17.saveOptions();
                        final ChatComponentText message = new ChatComponentText("Successfully saved the default game options" + (optifine ? " (+ Optifine)" : ""));
                        message.func_150256_b().func_150238_a(EnumChatFormatting.GREEN);
                        sender.func_145747_a(message);
                    }
                } catch (Exception e) {
                    DefaultSettings_17.log.log(Level.ERROR, "An exception occurred while saving the default game options:", e);
                    final ChatComponentText message = new ChatComponentText("Couldn't save the default game options!");
                    message.func_150256_b().func_150238_a(EnumChatFormatting.RED);
                    sender.func_145747_a(message);
                    issue.setBoolean(true);
                }
            }
        });

        tpe.execute(new ThreadRunnable(sender, issue) {

            @Override
            public void run() {
                try {
                    if (argument2 == null || argument2.equals("servers")) {
                        FileUtilNoMC.saveServers();
                        final ChatComponentText message = new ChatComponentText("Successfully saved the server list");
                        message.func_150256_b().func_150238_a(EnumChatFormatting.GREEN);
                        sender.func_145747_a(message);
                    }
                } catch (Exception e) {
                    DefaultSettings_17.log.log(Level.ERROR, "An exception occurred while saving the server list:", e);
                    final ChatComponentText message = new ChatComponentText("Couldn't save the server list!");
                    message.func_150256_b().func_150238_a(EnumChatFormatting.RED);
                    sender.func_145747_a(message);
                    issue.setBoolean(true);
                }

                if (issue.getBoolean()){
                    final ChatComponentText message = new ChatComponentText("Please inspect the log files for further information!");
                    message.func_150256_b().func_150238_a(EnumChatFormatting.YELLOW);
                    sender.func_145747_a(message);
                } else
                    try {
                        boolean updateExisting = argument != null && argument.equals("forceOverride");
                        FileUtilNoMC.checkMD5(updateExisting, false, null);
                        FileUtilNoMC.copyAndHashPrivate(true, false);
                    } catch (IOException e) {
                        DefaultSettings_17.log.log(Level.ERROR, "An exception occurred while saving your configuration:", e);
                    }
            }
        });
    }
}

abstract class ThreadRunnable_17 implements Runnable {

    final ICommandSender supply;
    final MutableBoolean_17 issue;

    ThreadRunnable_17(ICommandSender supply, MutableBoolean_17 issue) {
        this.supply = supply;
        this.issue = issue;
    }
}

class MutableBoolean_17 {

    private boolean bool;

    public MutableBoolean_17(boolean bool) {
        this.bool = bool;
    }

    public boolean getBoolean() {
        return this.bool;
    }

    public void setBoolean(boolean bool) {
        this.bool = bool;
    }

}