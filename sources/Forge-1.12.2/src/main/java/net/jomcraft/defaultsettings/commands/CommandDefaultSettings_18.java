package net.jomcraft.defaultsettings.commands;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import net.jomcraft.defaultsettings.DefaultSettings_18;
import net.jomcraft.defaultsettings.FileUtil_18;
import net.jomcraft.jcplugin.FileUtilNoMC;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import org.apache.logging.log4j.Level;

public class CommandDefaultSettings_18 extends CommandBase {

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

    public String getUsage(ICommandSender sender) {
        return "/defaultsettings [aaaaaaaaaaaaaaaaaaaaaa]";
    }

    public int getRequiredPermissionLevel() {
        return 0;
    }

    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
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

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if(args.length == 0) {
            sender.sendMessage(new TextComponentString("\u00a7cLol!"));
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

    private static void saveProcessConfigs(ICommandSender sender, String argument, String argument2) {

        if (tpe.getQueue().size() > 0) {
            sender.sendMessage(new TextComponentString(TextFormatting.RED + "Please wait until the last request has finished"));
            return;
        }

        if (DefaultSettings_18.shutDown) {
            sender.sendMessage(new TextComponentString(TextFormatting.RED + "DefaultSettings is missing the JCPlugin mod! Shutting down..."));
            return;
        }

        MutableBoolean issue = new MutableBoolean(false);

        tpe.execute(new ThreadRunnable(sender, issue) {

            @Override
            public void run() {
                try {
                    boolean somethingChanged = FileUtilNoMC.checkChangedConfig();

                    if (somethingChanged && (argument == null || !argument.equals("forceOverride"))) {
                        sender.sendMessage(new TextComponentString(TextFormatting.GOLD + "\n\n"));
                        sender.sendMessage(new TextComponentString(TextFormatting.GOLD + "You seem to have updated certain config files!"));
                        sender.sendMessage(new TextComponentString(TextFormatting.GOLD + "Users who already play your pack won't (!) receive those changes.\n"));
                        sender.sendMessage(new TextComponentString(TextFormatting.GOLD + "If you want to ship the new configs to those players too,"));
                        sender.sendMessage(new TextComponentString(TextFormatting.GOLD + "append the 'forceOverride' argument"));
                    }
                } catch (Exception e) {
                    DefaultSettings_18.log.log(Level.ERROR, "An exception occurred while saving the server list:", e);
                    sender.sendMessage(new TextComponentString(TextFormatting.RED + "Couldn't save the config files!"));
                    issue.setBoolean(true);
                }

                if (issue.getBoolean())
                    sender.sendMessage(new TextComponentString(TextFormatting.YELLOW + "Please inspect the log files for further information!"));
                else
                    try {
                        boolean updateExisting = argument != null && argument.equals("forceOverride");

                        FileUtilNoMC.checkMD5(updateExisting, true, argument2 == null ? null : argument2);
                        FileUtilNoMC.copyAndHashPrivate(false, true);
                        sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Successfully saved your mod configuration files" + (argument2 == null ? "" : " (single entry)")));
                        boolean noFiles = FileUtilNoMC.checkForConfigFiles();
                        if (noFiles)
                            sender.sendMessage(new TextComponentString(TextFormatting.YELLOW + "Warning: No config files will be shipped as the folder is still empty!"));

                    } catch (UncheckedIOException | NullPointerException | IOException e) {
                        sender.sendMessage(new TextComponentString(TextFormatting.RED + "Couldn't save the config files!"));
                        if (e instanceof UncheckedIOException && e.getCause() instanceof NoSuchFileException)
                            sender.sendMessage(new TextComponentString(TextFormatting.RED + "It seems, no file or folder by that name exists"));
                        DefaultSettings_18.log.log(Level.ERROR, "An exception occurred while saving your configuration:", e);
                    }
            }
        });
    }

    private static void saveProcess(ICommandSender sender, String argument, String argument2) {
        if (tpe.getQueue().size() > 0) {
            sender.sendMessage(new TextComponentString(TextFormatting.RED + "Please wait until the last request has finished"));
            return;
        }

        if (DefaultSettings_18.shutDown) {
            sender.sendMessage(new TextComponentString(TextFormatting.RED + "DefaultSettings is missing the JCPlugin mod! Shutting down..."));
            return;
        }

        if ((FileUtilNoMC.keysFileExist() || FileUtilNoMC.optionsFilesExist() || FileUtilNoMC.serversFileExists()) && (argument == null || (!argument.equals("override") && !argument.equals("forceOverride")))) {
            sender.sendMessage(new TextComponentString(TextFormatting.GOLD + "These files already exist! If you want to overwrite"));
            sender.sendMessage(new TextComponentString(TextFormatting.GOLD + "them, add the 'override' argument"));
            return;
        }

        MutableBoolean issue = new MutableBoolean(false);

        tpe.execute(new ThreadRunnable(sender, issue) {

            @Override
            public void run() {
                try {
                    boolean somethingChanged = FileUtil_18.checkChanged();

                    if (somethingChanged && !argument.equals("forceOverride")) {
                        sender.sendMessage(new TextComponentString(TextFormatting.GOLD + "\n\n"));
                        sender.sendMessage(new TextComponentString(TextFormatting.GOLD + "You seem to have updated certain config files!"));
                        sender.sendMessage(new TextComponentString(TextFormatting.GOLD + "Users who already play your pack won't (!) receive those changes.\n"));
                        sender.sendMessage(new TextComponentString(TextFormatting.GOLD + "If you want to ship the new configs to those players too,"));
                        sender.sendMessage(new TextComponentString(TextFormatting.GOLD + "append the 'forceOverride' argument instead of 'override'"));
                    }
                } catch (Exception e) {
                    DefaultSettings_18.log.log(Level.ERROR, "An exception occurred while saving the key configuration:", e);
                }
            }
        });

        tpe.execute(new ThreadRunnable(sender, issue) {

            @Override
            public void run() {
                try {
                    if (argument2 == null || argument2.equals("keybinds")) {
                        FileUtil_18.saveKeys();
                        sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Successfully saved the key configuration"));
                        FileUtil_18.restoreKeys(true, false);
                    }
                } catch (Exception e) {
                    DefaultSettings_18.log.log(Level.ERROR, "An exception occurred while saving the key configuration:", e);
                    sender.sendMessage(new TextComponentString(TextFormatting.RED + "Couldn't save the key configuration!"));
                    issue.setBoolean(true);
                }
            }
        });

        tpe.execute(new ThreadRunnable(sender, issue) {

            @Override
            public void run() {
                try {
                    if (argument2 == null || argument2.equals("options")) {
                        boolean optifine = FileUtil_18.saveOptions();
                        sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Successfully saved the default game options" + (optifine ? " (+ Optifine)" : "")));
                    }
                } catch (Exception e) {
                    DefaultSettings_18.log.log(Level.ERROR, "An exception occurred while saving the default game options:", e);
                    sender.sendMessage(new TextComponentString(TextFormatting.RED + "Couldn't save the default game options!"));
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
                        sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Successfully saved the server list"));
                    }
                } catch (Exception e) {
                    DefaultSettings_18.log.log(Level.ERROR, "An exception occurred while saving the server list:", e);
                    sender.sendMessage(new TextComponentString(TextFormatting.RED + "Couldn't save the server list!"));
                    issue.setBoolean(true);
                }

                if (issue.getBoolean())
                    sender.sendMessage(new TextComponentString(TextFormatting.YELLOW + "Please inspect the log files for further information!"));
                else
                    try {
                        boolean updateExisting = argument != null && argument.equals("forceOverride");
                        FileUtilNoMC.checkMD5(updateExisting, false, null);
                        FileUtilNoMC.copyAndHashPrivate(true, false);
                    } catch (IOException e) {
                        DefaultSettings_18.log.log(Level.ERROR, "An exception occurred while saving your configuration:", e);
                    }
            }
        });
    }
}

abstract class ThreadRunnable implements Runnable {

    final ICommandSender supply;
    final MutableBoolean issue;

    ThreadRunnable(ICommandSender supply, MutableBoolean issue) {
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