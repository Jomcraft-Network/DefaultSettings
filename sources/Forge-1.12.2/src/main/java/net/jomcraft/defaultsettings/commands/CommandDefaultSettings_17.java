package net.jomcraft.defaultsettings.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import net.jomcraft.defaultsettings.CoreUtil;
import net.jomcraft.defaultsettings.DefaultSettings_17;
import net.jomcraft.jcplugin.FileUtilNoMC;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;

public class CommandDefaultSettings_17 extends CommandBase {

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
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, new String[]{"save", "saveconfigs"});
        }

        if (args.length == 2) {
            if (args[0].toLowerCase().equals("save")) {
                return getListOfStringsMatchingLastWord(args, new String[]{"override", "forceOverride"});
            } else if (args[0].toLowerCase().equals("saveconfigs")) {
                return getListOfStringsMatchingLastWord(args, new String[]{"forceOverride"});
            }
        }

        if (args.length == 3) {
            if (args[0].toLowerCase().equals("save")) {
                return getListOfStringsMatchingLastWord(args, new String[]{"keybinds", "options", "servers"});
            } else if (args[0].toLowerCase().equals("saveconfigs")) {
                try {
                    ArrayList<String> filtered = new ArrayList<String>();
                    ArrayList<String> prevList = FileUtilNoMC.listConfigFiles();
                    for (int i = 0; i < prevList.size(); i++) {
                        String name = prevList.get(i);
                        if (name.contains(" "))
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
        if (args.length == 0) {
            sender.func_145747_a(new ChatComponentText("\u00a7c/defaultsettings [arguments]"));
            return;
        }
        if (args[0].toLowerCase().equals("save")) {
            if (args.length == 1) {
                // /ds save
                saveProcess(sender, null, null);
            } else if (args[1].toLowerCase().equals("override")) {
                if (args.length == 2) {
                    // /ds save override
                    saveProcess(sender, "override", null);
                } else if (args.length == 3) {
                    if (args[2].toLowerCase().equals("keybinds")) {
                        // /ds save override [type]
                        saveProcess(sender, "override", "keybinds");
                    } else if (args[2].toLowerCase().equals("options")) {
                        // /ds save override [type]
                        saveProcess(sender, "override", "options");
                    } else if (args[2].toLowerCase().equals("servers")) {
                        // /ds save override [type]
                        saveProcess(sender, "override", "servers");
                    }
                }
            } else if (args[1].toLowerCase().equals("forceoverride")) {
                if (args.length == 2) {
                    // /ds save forceOverride
                    saveProcess(sender, "forceOverride", null);
                } else if (args.length == 3) {
                    if (args[2].toLowerCase().equals("keybinds")) {
                        // /ds save forceOverride [type]
                        saveProcess(sender, "forceOverride", "keybinds");
                    } else if (args[2].toLowerCase().equals("options")) {
                        // /ds save forceOverride [type]
                        saveProcess(sender, "forceOverride", "options");
                    } else if (args[2].toLowerCase().equals("servers")) {
                        // /ds save forceOverride [type]
                        saveProcess(sender, "forceOverride", "servers");
                    }
                }
            }

        } else if (args[0].toLowerCase().equals("saveconfigs")) {
            if (args.length == 1) {
                saveProcessConfigs(sender, null, null);
                // /ds saveconfigs
            } else if (args[1].toLowerCase().equals("forceoverride")) {
                if (args.length == 2) {
                    saveProcessConfigs(sender, "forceOverride", null);
                    // /ds saveconfigs forceOverride
                } else if (args.length >= 3) {
                    StringJoiner fileJoiner = new StringJoiner(" ");

                    for (int i = 2; i < args.length; i++) {
                        fileJoiner.add(args[i]);
                    }

                    String joined = fileJoiner.toString().replaceAll("\"", "");
                    saveProcessConfigs(sender, "forceOverride", joined);
                    // /ds saveconfigs forceOverride [type]
                }

            }
        }
    }

    private static void saveProcessConfigs(ICommandSender source, String argument, String argument2) {
        try {
            CoreUtil.saveProcessConfigs(source, argument, argument2);
        } catch (Exception e) {
            DefaultSettings_17.log.error("Error while executing command: ", e);
        }
    }

    private static void saveProcess(ICommandSender source, String argument, String argument2) {
        try {
            CoreUtil.saveProcess(source, argument, argument2);
        } catch (Exception e) {
            DefaultSettings_17.log.error("Error while executing command: ", e);
        }
    }
}