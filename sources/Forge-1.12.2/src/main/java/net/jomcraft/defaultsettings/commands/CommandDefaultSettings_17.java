package net.jomcraft.defaultsettings.commands;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.List;
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

    public String getUsage(ICommandSender sender) {
        return "/defaultsettings [arguments]";
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
                    ArrayList<String> files = FileUtilNoMC.listConfigFiles();
                    return getListOfStringsMatchingLastWord(args, files.toArray(new String[0]));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return new ArrayList<String>();
    }

    private static void saveProcessConfigs(ICommandSender sender, String argument, String argument2) {

        //TODO: FIX TEXT COLOR LINES!

        if (tpe.getQueue().size() > 0) {
            sender.func_145747_a(new ChatComponentText(EnumChatFormatting.RED + "Please wait until the last request has finished"));
            return;
        }

        if (DefaultSettings_17.shutDown) {
            sender.func_145747_a(new ChatComponentText(EnumChatFormatting.RED + "DefaultSettings is missing the JCPlugin mod! Shutting down..."));
            return;
        }

        MutableBoolean_17 issue = new MutableBoolean_17(false);

        tpe.execute(new ThreadRunnable_17(sender, issue) {

            @Override
            public void run() {
                try {
                    boolean somethingChanged = FileUtilNoMC.checkChangedConfig();

                    if (somethingChanged && (argument == null || !argument.equals("forceOverride"))) {
                        sender.func_145747_a(new ChatComponentText(EnumChatFormatting.GOLD + "\n\n"));
                        sender.func_145747_a(new ChatComponentText(EnumChatFormatting.GOLD + "You seem to have updated certain config files!"));
                        sender.func_145747_a(new ChatComponentText(EnumChatFormatting.GOLD + "Users who already play your pack won't (!) receive those changes.\n"));
                        sender.func_145747_a(new ChatComponentText(EnumChatFormatting.GOLD + "If you want to ship the new configs to those players too,"));
                        sender.func_145747_a(new ChatComponentText(EnumChatFormatting.GOLD + "append the 'forceOverride' argument"));
                    }
                } catch (Exception e) {
                    DefaultSettings_17.log.log(Level.ERROR, "An exception occurred while saving the server list:", e);
                    sender.func_145747_a(new ChatComponentText(EnumChatFormatting.RED + "Couldn't save the config files!"));
                    issue.setBoolean(true);
                }

                if (issue.getBoolean())
                    sender.func_145747_a(new ChatComponentText(EnumChatFormatting.YELLOW + "Please inspect the log files for further information!"));
                else
                    try {
                        boolean updateExisting = argument != null && argument.equals("forceOverride");

                        FileUtilNoMC.checkMD5(updateExisting, true, argument2 == null ? null : argument2);
                        FileUtilNoMC.copyAndHashPrivate(false, true);
                        sender.func_145747_a(new ChatComponentText(EnumChatFormatting.GREEN + "Successfully saved your mod configuration files" + (argument2 == null ? "" : argument2.contains("*") ? " (wildcard)" : " (single entry)")));
                        boolean noFiles = FileUtilNoMC.checkForConfigFiles();
                        if (noFiles)
                            sender.func_145747_a(new ChatComponentText(EnumChatFormatting.YELLOW + "Warning: No config files will be shipped as the folder is still empty!"));

                    } catch (UncheckedIOException | NullPointerException | IOException e) {
                        sender.func_145747_a(new ChatComponentText(EnumChatFormatting.RED + "Couldn't save the config files!"));
                        if (e instanceof UncheckedIOException && e.getCause() instanceof NoSuchFileException)
                            sender.func_145747_a(new ChatComponentText(EnumChatFormatting.RED + "It seems, no file or folder by that name exists"));
                        DefaultSettings_17.log.log(Level.ERROR, "An exception occurred while saving your configuration:", e);
                    }
            }
        });
    }

    private static void saveProcess(ICommandSender sender, String argument, String argument2) {
        if (tpe.getQueue().size() > 0) {
            sender.func_145747_a(new ChatComponentText(EnumChatFormatting.RED + "Please wait until the last request has finished"));
            return;
        }

        if (DefaultSettings_17.shutDown) {
            sender.func_145747_a(new ChatComponentText(EnumChatFormatting.RED + "DefaultSettings is missing the JCPlugin mod! Shutting down..."));
            return;
        }

        if ((FileUtilNoMC.keysFileExist() || FileUtilNoMC.optionsFilesExist() || FileUtilNoMC.serversFileExists()) && (argument == null || (!argument.equals("override") && !argument.equals("forceOverride")))) {
            sender.func_145747_a(new ChatComponentText(EnumChatFormatting.GOLD + "These files already exist! If you want to overwrite"));
            sender.func_145747_a(new ChatComponentText(EnumChatFormatting.GOLD + "them, add the 'override' argument"));
            return;
        }

        MutableBoolean_17 issue = new MutableBoolean_17(false);

        tpe.execute(new ThreadRunnable_17(sender, issue) {

            @Override
            public void run() {
                try {
                    boolean somethingChanged = FileUtil_17.checkChanged();

                    if (somethingChanged && !argument.equals("forceOverride")) {
                        sender.func_145747_a(new ChatComponentText(EnumChatFormatting.GOLD + "\n\n"));
                        sender.func_145747_a(new ChatComponentText(EnumChatFormatting.GOLD + "You seem to have updated certain config files!"));
                        sender.func_145747_a(new ChatComponentText(EnumChatFormatting.GOLD + "Users who already play your pack won't (!) receive those changes.\n"));
                        sender.func_145747_a(new ChatComponentText(EnumChatFormatting.GOLD + "If you want to ship the new configs to those players too,"));
                        sender.func_145747_a(new ChatComponentText(EnumChatFormatting.GOLD + "append the 'forceOverride' argument instead of 'override'"));
                    }
                } catch (Exception e) {
                    DefaultSettings_17.log.log(Level.ERROR, "An exception occurred while saving the key configuration:", e);
                }
            }
        });

        tpe.execute(new ThreadRunnable_17(sender, issue) {

            @Override
            public void run() {
                try {
                    if (argument2 == null || argument2.equals("keybinds")) {
                        FileUtil_17.saveKeys();
                        sender.func_145747_a(new ChatComponentText(EnumChatFormatting.GREEN + "Successfully saved the key configuration"));
                        FileUtil_17.restoreKeys(true, false);
                    }
                } catch (Exception e) {
                    DefaultSettings_17.log.log(Level.ERROR, "An exception occurred while saving the key configuration:", e);
                    sender.func_145747_a(new ChatComponentText(EnumChatFormatting.RED + "Couldn't save the key configuration!"));
                    issue.setBoolean(true);
                }
            }
        });

        tpe.execute(new ThreadRunnable_17(sender, issue) {

            @Override
            public void run() {
                try {
                    if (argument2 == null || argument2.equals("options")) {
                        boolean optifine = FileUtil_17.saveOptions();
                        sender.func_145747_a(new ChatComponentText(EnumChatFormatting.GREEN + "Successfully saved the default game options" + (optifine ? " (+ Optifine)" : "")));
                    }
                } catch (Exception e) {
                    DefaultSettings_17.log.log(Level.ERROR, "An exception occurred while saving the default game options:", e);
                    sender.func_145747_a(new ChatComponentText(EnumChatFormatting.RED + "Couldn't save the default game options!"));
                    issue.setBoolean(true);
                }
            }
        });

        tpe.execute(new ThreadRunnable_17(sender, issue) {

            @Override
            public void run() {
                try {
                    if (argument2 == null || argument2.equals("servers")) {
                        FileUtilNoMC.saveServers();
                        sender.func_145747_a(new ChatComponentText(EnumChatFormatting.GREEN + "Successfully saved the server list"));
                    }
                } catch (Exception e) {
                    DefaultSettings_17.log.log(Level.ERROR, "An exception occurred while saving the server list:", e);
                    sender.func_145747_a(new ChatComponentText(EnumChatFormatting.RED + "Couldn't save the server list!"));
                    issue.setBoolean(true);
                }

                if (issue.getBoolean())
                    sender.func_145747_a(new ChatComponentText(EnumChatFormatting.YELLOW + "Please inspect the log files for further information!"));
                else
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

    public void func_71515_b(ICommandSender sender, String[] args) {
        if(args.length == 0) {
            sender.func_145747_a(new ChatComponentText(EnumChatFormatting.RED + "Lol!"));
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
                System.out.println("FIRST");
                if(args.length == 2){
                    System.out.println("SECOND");
                    saveProcessConfigs(sender, "forceOverride", null);
                    // /ds saveconfigs forceOverride
                } else if(args.length == 3){
                    saveProcessConfigs(sender, "forceOverride", args[2]);
                    // /ds saveconfigs forceOverride [type]
                }

            }
        }
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